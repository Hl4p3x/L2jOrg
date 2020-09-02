// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.OlyManager;

import org.slf4j.LoggerFactory;
import java.util.List;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.olympiad.CompetitionType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.handler.BypassHandler;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class OlyManager extends AbstractNpcAI implements IBypassHandler
{
    private static final int MANAGER = 31688;
    private static final int EQUIPMENT_MULTISELL = 3168801;
    private static final String[] BYPASSES;
    private static final Logger LOGGER;
    
    private OlyManager() {
        this.addStartNpc(31688);
        this.addFirstTalkId(31688);
        this.addTalkId(31688);
        BypassHandler.getInstance().registerHandler((IBypassHandler)this);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "OlyManager-info.html":
            case "OlyManager-infoHistory.html":
            case "OlyManager-infoRules.html":
            case "OlyManager-infoPoints.html":
            case "OlyManager-infoPointsCalc.html":
            case "OlyManager-rank.html":
            case "OlyManager-rewards.html": {
                htmltext = event;
                break;
            }
            case "index": {
                htmltext = this.onFirstTalk(npc, player);
                break;
            }
            case "joinMatch": {
                if (OlympiadManager.getInstance().isRegistered(player)) {
                    htmltext = "OlyManager-registred.html";
                    break;
                }
                htmltext = this.getHtml(player, "OlyManager-joinMatch.html");
                htmltext = htmltext.replace("%olympiad_round%", String.valueOf(Olympiad.getInstance().getPeriod()));
                htmltext = htmltext.replace("%olympiad_week%", String.valueOf(Olympiad.getInstance().getCurrentSeason()));
                htmltext = htmltext.replace("%olympiad_participant%", String.valueOf(OlympiadManager.getInstance().getCountOpponents()));
                break;
            }
            case "register1v1": {
                if (player.isSubClassActive()) {
                    htmltext = "OlyManager-subclass.html";
                    break;
                }
                if ((!player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && !player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) || player.getLevel() < 55) {
                    htmltext = "OlyManager-noNoble.html";
                    break;
                }
                if (Olympiad.getInstance().getOlympiadPoints(player) <= 0) {
                    htmltext = "OlyManager-noPoints.html";
                    break;
                }
                if (!player.isInventoryUnder80(false)) {
                    player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                    break;
                }
                OlympiadManager.getInstance().registerNoble(player, CompetitionType.NON_CLASSED);
                break;
            }
            case "unregister": {
                OlympiadManager.getInstance().unRegisterNoble(player);
                break;
            }
            case "calculatePoints": {
                if (player.getUnclaimedOlympiadPoints() > 0) {
                    htmltext = "OlyManager-calculateEnough.html";
                    break;
                }
                htmltext = "OlyManager-calculateNoEnough.html";
                break;
            }
            case "calculatePointsDone": {
                if (player.isInventoryUnder80(false)) {
                    final int tradePoints = player.getUnclaimedOlympiadPoints();
                    if (tradePoints > 0) {
                        player.setUnclaimedOlympiadPoints(0);
                        giveItems(player, Config.ALT_OLY_COMP_RITEM, (long)(tradePoints * Config.ALT_OLY_MARK_PER_POINT));
                    }
                    break;
                }
                player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                break;
            }
            case "showEquipmentReward": {
                MultisellData.getInstance().separateAndSend(3168801, player, npc, false);
                break;
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return ((!player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && !player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) || player.getLevel() < 55) ? "OlyManager-noNoble.html" : "OlyManager-noble.html";
    }
    
    public boolean useBypass(final String command, final Player player, final Creature bypassOrigin) {
        try {
            final Npc olymanager = player.getLastFolkNPC();
            if (command.startsWith(OlyManager.BYPASSES[0])) {
                if (!Olympiad.getInstance().isMatchesInProgress()) {
                    player.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
                    return false;
                }
                player.sendPacket(new ServerPacket[] { (ServerPacket)new ExOlympiadMatchList() });
            }
            else {
                if (olymanager == null || olymanager.getId() != 31688 || (!player.inObserverMode() && !MathUtil.isInsideRadius2D((ILocational)player, (ILocational)olymanager, 300))) {
                    return false;
                }
                if (OlympiadManager.getInstance().isRegisteredInComp(player)) {
                    player.sendPacket(SystemMessageId.YOU_MAY_NOT_OBSERVE_A_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
                    return false;
                }
                if (!Olympiad.getInstance().isMatchesInProgress()) {
                    player.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
                    return false;
                }
                if (player.isOnEvent()) {
                    player.sendMessage("You can not observe games while registered on an event");
                    return false;
                }
                final int arenaId = Integer.parseInt(command.substring(12).trim());
                final OlympiadGameTask nextArena = OlympiadGameManager.getInstance().getOlympiadTask(arenaId);
                if (nextArena != null) {
                    final List<Location> spectatorSpawns = (List<Location>)nextArena.getStadium().getZone().getSpectatorSpawns();
                    if (spectatorSpawns.isEmpty()) {
                        OlyManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/world/zone/type/OlympiadStadiumZone;)Ljava/lang/String;, nextArena.getStadium().getZone()));
                        return false;
                    }
                    final Location loc = spectatorSpawns.get(Rnd.get(spectatorSpawns.size()));
                    player.enterOlympiadObserverMode(loc, arenaId);
                }
            }
            return true;
        }
        catch (Exception e) {
            OlyManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
            return false;
        }
    }
    
    public String[] getBypassList() {
        return OlyManager.BYPASSES;
    }
    
    public static AbstractNpcAI provider() {
        return new OlyManager();
    }
    
    static {
        BYPASSES = new String[] { "watchmatch", "arenachange" };
        LOGGER = LoggerFactory.getLogger((Class)OlyManager.class);
    }
}
