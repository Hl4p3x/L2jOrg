// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.events.EveTheFortuneTeller;

import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.network.serverpackets.luckygame.ExStartLuckyGame;
import org.l2j.gameserver.enums.LuckyGameType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.quest.LongTimeEvent;

public final class EveTheFortuneTeller extends LongTimeEvent
{
    private static final int EVE = 31855;
    private static final int FORTUNE_READING_TICKET = 23767;
    private static final int LUXURY_FORTUNE_READING_TICKET = 23768;
    
    private EveTheFortuneTeller() {
        this.addStartNpc(31855);
        this.addFirstTalkId(31855);
        this.addTalkId(31855);
        this.addSpawnId(new int[] { 31855 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "31855.htm":
            case "31855-1.htm": {
                htmltext = event;
                break;
            }
            case "FortuneReadingGame": {
                player.sendPacket(new ServerPacket[] { (ServerPacket)new ExStartLuckyGame(LuckyGameType.NORMAL, player.getInventory().getInventoryItemCount(23767, -1)) });
                break;
            }
            case "LuxuryFortuneReadingGame": {
                player.sendPacket(new ServerPacket[] { (ServerPacket)new ExStartLuckyGame(LuckyGameType.LUXURY, player.getInventory().getInventoryItemCount(23768, -1)) });
                break;
            }
        }
        return htmltext;
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        return "31855.htm";
    }
    
    public static AbstractScript provider() {
        return (AbstractScript)new EveTheFortuneTeller();
    }
}
