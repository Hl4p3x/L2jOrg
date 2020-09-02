// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.events.HappyHours;

import org.l2j.gameserver.model.events.AbstractScript;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.quest.LongTimeEvent;

public class HappyHours extends LongTimeEvent
{
    private static final int SIBI = 34262;
    private static final int SUPPLY_BOX = 49782;
    private static final int SIBIS_COIN = 49783;
    private static final int TRANSFORMATION_SKILL = 39171;
    private static final int MIN_LEVEL = 20;
    private static final int REWARD_INTERVAL = 3600000;
    private static long _lastRewardTime;
    
    private HappyHours() {
        this.addStartNpc(34262);
        this.addFirstTalkId(34262);
        this.addTalkId(34262);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "34262-1.htm": {
                htmltext = event;
                break;
            }
            case "giveSupplyBox": {
                if (player.getLevel() < 20) {
                    return "34262-2.htm";
                }
                if (hasQuestItems(player, 49782)) {
                    return "34262-3.htm";
                }
                giveItems(player, 49782, 1L);
                break;
            }
            case "REWARD_SIBI_COINS": {
                if (!this.isEventPeriod()) {
                    this.cancelQuestTimers("REWARD_SIBI_COINS");
                    break;
                }
                if (System.currentTimeMillis() - (HappyHours._lastRewardTime + 3600000L) > 0L) {
                    HappyHours._lastRewardTime = System.currentTimeMillis();
                    final ExShowScreenMessage screenMsg = new ExShowScreenMessage("You obtained 20 Oriana's coins.", 2, 7000, 0, true, true);
                    final SystemMessage systemMsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_OBTAINED_S1_ORIANA_S_COINS);
                    systemMsg.addInt(20);
                    for (final Player plr : World.getInstance().getPlayers()) {
                        if (plr != null && plr.isOnlineInt() == 1 && plr.isAffectedBySkill(39171)) {
                            plr.addItem("HappyHours", 49783, 20L, (WorldObject)player, false);
                            plr.sendPacket(new ServerPacket[] { (ServerPacket)screenMsg });
                            plr.sendPacket(new ServerPacket[] { (ServerPacket)systemMsg });
                        }
                    }
                    break;
                }
                break;
            }
        }
        return htmltext;
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        return "34262.htm";
    }
    
    protected void startEvent() {
        super.startEvent();
        this.cancelQuestTimers("REWARD_SIBI_COINS");
        this.startQuestTimer("REWARD_SIBI_COINS", 3601000L, (Npc)null, (Player)null, true);
    }
    
    public static AbstractScript provider() {
        return (AbstractScript)new HappyHours();
    }
    
    static {
        HappyHours._lastRewardTime = System.currentTimeMillis();
    }
}
