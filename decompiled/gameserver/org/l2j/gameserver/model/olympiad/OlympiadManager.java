// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

import java.util.Collections;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.engine.olympiad.OlympiadEngine;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import java.util.Iterator;
import java.util.ArrayList;
import org.l2j.gameserver.Config;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

public class OlympiadManager
{
    private final Set<Integer> _nonClassBasedRegisters;
    private final Map<Integer, Set<Integer>> _classBasedRegisters;
    
    private OlympiadManager() {
        this._nonClassBasedRegisters = (Set<Integer>)ConcurrentHashMap.newKeySet();
        this._classBasedRegisters = new ConcurrentHashMap<Integer, Set<Integer>>();
    }
    
    public final Set<Integer> getRegisteredNonClassBased() {
        return this._nonClassBasedRegisters;
    }
    
    public final Map<Integer, Set<Integer>> getRegisteredClassBased() {
        return this._classBasedRegisters;
    }
    
    protected final List<Set<Integer>> hasEnoughRegisteredClassed() {
        List<Set<Integer>> result = null;
        for (final Map.Entry<Integer, Set<Integer>> classList : this._classBasedRegisters.entrySet()) {
            if (classList.getValue() != null && classList.getValue().size() >= Config.ALT_OLY_CLASSED) {
                if (result == null) {
                    result = new ArrayList<Set<Integer>>();
                }
                result.add(classList.getValue());
            }
        }
        return result;
    }
    
    protected final boolean hasEnoughRegisteredNonClassed() {
        return this._nonClassBasedRegisters.size() >= Config.ALT_OLY_NONCLASSED;
    }
    
    protected final void clearRegistered() {
        this._nonClassBasedRegisters.clear();
        this._classBasedRegisters.clear();
        AntiFeedManager.getInstance().clear(1);
    }
    
    public final boolean isRegistered(final Player noble) {
        return this.isRegistered(noble, noble, false);
    }
    
    private boolean isRegistered(final Player noble, final Player player, final boolean showMessage) {
        final int objId = noble.getObjectId();
        if (this._nonClassBasedRegisters.contains(objId)) {
            if (showMessage) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_ALL_CLASS_BATTLE);
                sm.addPcName(noble);
                player.sendPacket(sm);
            }
            return true;
        }
        final Set<Integer> classed = this._classBasedRegisters.get(this.getClassGroup(noble));
        if (classed != null && classed.contains(objId)) {
            if (showMessage) {
                final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST);
                sm2.addPcName(noble);
                player.sendPacket(sm2);
            }
            return true;
        }
        return false;
    }
    
    public final boolean isRegisteredInComp(final Player noble) {
        return this.isRegistered(noble, noble, false) || this.isInCompetition(noble, noble, false);
    }
    
    private boolean isInCompetition(final Player noble, final Player player, final boolean showMessage) {
        if (!OlympiadEngine.getInstance().isMatchInProgress()) {
            return false;
        }
        int i = OlympiadGameManager.getInstance().getNumberOfStadiums();
        while (--i >= 0) {
            final AbstractOlympiadGame game = OlympiadGameManager.getInstance().getOlympiadTask(i).getGame();
            if (game == null) {
                continue;
            }
            if (!game.containsParticipant(noble.getObjectId())) {
                continue;
            }
            if (!showMessage) {
                return true;
            }
            switch (game.getType()) {
                case CLASSED: {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST);
                    sm.addPcName(noble);
                    player.sendPacket(sm);
                    break;
                }
                case NON_CLASSED: {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_ALL_CLASS_BATTLE);
                    sm.addPcName(noble);
                    player.sendPacket(sm);
                    break;
                }
            }
            return true;
        }
        return false;
    }
    
    public final boolean registerNoble(final Player player, final CompetitionType type) {
        if (!OlympiadEngine.getInstance().isMatchInProgress()) {
            player.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }
        final int charId = player.getObjectId();
        if (OlympiadEngine.getInstance().getRemainingDailyMatches(player) < 1) {
            player.sendPacket(SystemMessageId.YOU_CAN_PARTICIPATE_IN_UP_TO_30_MATCHES_PER_WEEK);
            return false;
        }
        switch (type) {
            case CLASSED: {
                if (player.isOnEvent()) {
                    player.sendMessage("You can't join olympiad while participating on an Event.");
                    return false;
                }
                if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0 && !AntiFeedManager.getInstance().tryAddPlayer(1, player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP)) {
                    final NpcHtmlMessage message = new NpcHtmlMessage(player.getLastHtmlActionOriginId());
                    message.setFile(player, "data/html/mods/OlympiadIPRestriction.htm");
                    message.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP)));
                    player.sendPacket(message);
                    return false;
                }
                this._classBasedRegisters.computeIfAbsent(Integer.valueOf(this.getClassGroup(player)), k -> ConcurrentHashMap.newKeySet()).add(charId);
                player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REGISTERED_FOR_THE_OLYMPIAD_WAITING_LIST_FOR_A_CLASS_BATTLE);
                break;
            }
            case NON_CLASSED: {
                if (player.isOnEvent()) {
                    player.sendMessage("You can't join olympiad while participating on TvT Event.");
                    return false;
                }
                if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0 && !AntiFeedManager.getInstance().tryAddPlayer(1, player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP)) {
                    final NpcHtmlMessage message = new NpcHtmlMessage(player.getLastHtmlActionOriginId());
                    message.setFile(player, "data/html/mods/OlympiadIPRestriction.htm");
                    message.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP)));
                    player.sendPacket(message);
                    return false;
                }
                this._nonClassBasedRegisters.add(charId);
                player.sendPacket(SystemMessageId.YOU_VE_BEEN_REGISTERED_IN_THE_WAITING_LIST_OF_ALL_CLASS_BATTLE);
                break;
            }
        }
        return true;
    }
    
    public final boolean unRegisterNoble(final Player noble) {
        if (!OlympiadEngine.getInstance().isMatchInProgress()) {
            noble.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }
        if ((!noble.isInCategory(CategoryType.THIRD_CLASS_GROUP) && !noble.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) || noble.getLevel() < 55) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_ONLY_CHARACTERS_THAT_COMPLETED_THE_2ND_CLASS_TRANSFER_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
            sm.addString(noble.getName());
            noble.sendPacket(sm);
            return false;
        }
        if (!this.isRegistered(noble, noble, false)) {
            noble.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_REGISTERED_FOR_THE_OLYMPIAD);
            return false;
        }
        if (this.isInCompetition(noble, noble, false)) {
            return false;
        }
        final int objId = noble.getObjectId();
        if (this._nonClassBasedRegisters.remove(objId)) {
            if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0) {
                AntiFeedManager.getInstance().removePlayer(1, noble);
            }
            noble.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REMOVED_FROM_THE_OLYMPIAD_WAITING_LIST);
            return true;
        }
        final Set<Integer> classed = this._classBasedRegisters.get(this.getClassGroup(noble));
        if (classed != null && classed.remove(objId)) {
            if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0) {
                AntiFeedManager.getInstance().removePlayer(1, noble);
            }
            noble.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REMOVED_FROM_THE_OLYMPIAD_WAITING_LIST);
            return true;
        }
        return false;
    }
    
    public final void removeDisconnectedCompetitor(final Player player) {
        final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(player.getOlympiadGameId());
        if (task != null && task.isGameStarted()) {
            task.getGame().handleDisconnect(player);
        }
        final int objId = player.getObjectId();
        if (this._nonClassBasedRegisters.remove(objId)) {
            return;
        }
        this._classBasedRegisters.getOrDefault(this.getClassGroup(player), Collections.emptySet()).remove(objId);
    }
    
    public int getCountOpponents() {
        return this._nonClassBasedRegisters.size() + this._classBasedRegisters.size();
    }
    
    private int getClassGroup(final Player player) {
        return player.getBaseClass();
    }
    
    public static OlympiadManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final OlympiadManager INSTANCE;
        
        static {
            INSTANCE = new OlympiadManager();
        }
    }
}
