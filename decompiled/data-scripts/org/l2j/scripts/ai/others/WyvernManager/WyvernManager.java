// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.WyvernManager;

import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.Config;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntMap;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class WyvernManager extends AbstractNpcAI
{
    private static final int CRYSTAL_B_GRADE = 1460;
    private static final int WYVERN = 12621;
    private static final int WYVERN_FEE = 25;
    private static final int STRIDER_LVL = 55;
    private static final int[] STRIDERS;
    private static final IntMap<ManagerType> MANAGERS;
    
    private WyvernManager() {
        this.addStartNpc((IntCollection)WyvernManager.MANAGERS.keySet());
        this.addTalkId((IntCollection)WyvernManager.MANAGERS.keySet());
        this.addFirstTalkId((IntCollection)WyvernManager.MANAGERS.keySet());
    }
    
    private String mountWyvern(final Npc npc, final Player player) {
        if (!player.isMounted() || player.getMountLevel() < 55 || !Util.contains(WyvernManager.STRIDERS, player.getMountNpcId())) {
            return this.replacePart(player, "wyvernmanager-05.html");
        }
        if (this.isOwnerClan(npc, player) && getQuestItemsCount(player, 1460) >= 25L) {
            takeItems(player, 1460, 25L);
            player.dismount();
            player.mount(12621, 0, true);
            return "wyvernmanager-04.html";
        }
        return this.replacePart(player, "wyvernmanager-06.html");
    }
    
    private boolean isOwnerClan(final Npc npc, final Player player) {
        switch ((ManagerType)WyvernManager.MANAGERS.get(npc.getId())) {
            case CASTLE: {
                return player.getClan() != null && npc.getCastle() != null && player.isClanLeader() && player.getClanId() == npc.getCastle().getOwnerId();
            }
            case CLAN_HALL: {
                return player.getClan() != null && npc.getClanHall() != null && player.isClanLeader() && player.getClanId() == npc.getClanHall().getOwnerId();
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isInSiege(final Npc npc) {
        switch ((ManagerType)WyvernManager.MANAGERS.get(npc.getId())) {
            case CASTLE: {
                return npc.getCastle().getZone().isActive();
            }
            default: {
                return false;
            }
        }
    }
    
    private String getResidenceName(final Npc npc) {
        switch ((ManagerType)WyvernManager.MANAGERS.get(npc.getId())) {
            case CASTLE: {
                return npc.getCastle().getName();
            }
            case CLAN_HALL: {
                return npc.getClanHall().getName();
            }
            default: {
                return null;
            }
        }
    }
    
    private String replaceAll(final Npc npc, final Player player) {
        return this.replacePart(player, "wyvernmanager-01.html").replace("%residence_name%", this.getResidenceName(npc));
    }
    
    private String replacePart(final Player player, final String htmlFile) {
        return this.getHtml(player, htmlFile).replace("%wyvern_fee%", String.valueOf(25)).replace("%strider_level%", String.valueOf(55));
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "Return": {
                if (!this.isOwnerClan(npc, player)) {
                    htmltext = "wyvernmanager-02.html";
                    break;
                }
                if (Config.ALLOW_WYVERN_ALWAYS) {
                    htmltext = this.replaceAll(npc, player);
                    break;
                }
                if (WyvernManager.MANAGERS.get(npc.getId()) == ManagerType.CASTLE) {
                    htmltext = "wyvernmanager-dusk.html";
                    break;
                }
                htmltext = this.replaceAll(npc, player);
                break;
            }
            case "Help": {
                htmltext = ((WyvernManager.MANAGERS.get(npc.getId()) == ManagerType.CASTLE) ? this.replacePart(player, "wyvernmanager-03.html") : this.replacePart(player, "wyvernmanager-03b.html"));
                break;
            }
            case "RideWyvern": {
                if (Config.ALLOW_WYVERN_ALWAYS) {
                    htmltext = this.mountWyvern(npc, player);
                    break;
                }
                if (!Config.ALLOW_WYVERN_DURING_SIEGE && (this.isInSiege(npc) || player.isInSiege())) {
                    player.sendMessage("You cannot summon wyvern while in siege.");
                    return null;
                }
                if (WyvernManager.MANAGERS.get(npc.getId()) == ManagerType.CASTLE) {
                    htmltext = "wyvernmanager-dusk.html";
                    break;
                }
                htmltext = this.mountWyvern(npc, player);
                break;
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        String htmltext = null;
        if (!this.isOwnerClan(npc, player)) {
            htmltext = "wyvernmanager-02.html";
        }
        else if (Config.ALLOW_WYVERN_ALWAYS) {
            htmltext = this.replaceAll(npc, player);
        }
        else if (WyvernManager.MANAGERS.get(npc.getId()) == ManagerType.CASTLE) {
            htmltext = "wyvernmanager-dusk.html";
        }
        else {
            htmltext = this.replaceAll(npc, player);
        }
        return htmltext;
    }
    
    public static AbstractNpcAI provider() {
        return new WyvernManager();
    }
    
    static {
        STRIDERS = new int[] { 12526, 12527, 12528, 16038, 16039, 16040, 16068, 13197 };
        (MANAGERS = (IntMap)new HashIntMap()).put(35101, (Object)ManagerType.CASTLE);
        WyvernManager.MANAGERS.put(35143, (Object)ManagerType.CASTLE);
        WyvernManager.MANAGERS.put(35185, (Object)ManagerType.CASTLE);
        WyvernManager.MANAGERS.put(35227, (Object)ManagerType.CASTLE);
        WyvernManager.MANAGERS.put(35275, (Object)ManagerType.CASTLE);
        WyvernManager.MANAGERS.put(35317, (Object)ManagerType.CASTLE);
        WyvernManager.MANAGERS.put(35364, (Object)ManagerType.CASTLE);
        WyvernManager.MANAGERS.put(35510, (Object)ManagerType.CASTLE);
        WyvernManager.MANAGERS.put(35536, (Object)ManagerType.CASTLE);
        WyvernManager.MANAGERS.put(35556, (Object)ManagerType.CASTLE);
        WyvernManager.MANAGERS.put(35419, (Object)ManagerType.CLAN_HALL);
    }
    
    private enum ManagerType
    {
        CASTLE, 
        CLAN_HALL;
    }
}
