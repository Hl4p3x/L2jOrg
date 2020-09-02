// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.TeleportWithCharm;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class TeleportWithCharm extends AbstractNpcAI
{
    private static final int WHIRPY = 30540;
    private static final int TAMIL = 30576;
    private static final int ORC_GATEKEEPER_CHARM = 1658;
    private static final int DWARF_GATEKEEPER_TOKEN = 1659;
    private static final Location ORC_TELEPORT;
    private static final Location DWARF_TELEPORT;
    
    private TeleportWithCharm() {
        this.addStartNpc(new int[] { 30540, 30576 });
        this.addTalkId(new int[] { 30540, 30576 });
    }
    
    public String onTalk(final Npc npc, final Player player) {
        switch (npc.getId()) {
            case 30540: {
                if (hasQuestItems(player, 1659)) {
                    takeItems(player, 1659, 1L);
                    player.teleToLocation((ILocational)TeleportWithCharm.DWARF_TELEPORT);
                    break;
                }
                return "30540-01.htm";
            }
            case 30576: {
                if (hasQuestItems(player, 1658)) {
                    takeItems(player, 1658, 1L);
                    player.teleToLocation((ILocational)TeleportWithCharm.ORC_TELEPORT);
                    break;
                }
                return "30576-01.htm";
            }
        }
        return super.onTalk(npc, player);
    }
    
    public static AbstractNpcAI provider() {
        return new TeleportWithCharm();
    }
    
    static {
        ORC_TELEPORT = new Location(-80826, 149775, -3043);
        DWARF_TELEPORT = new Location(-80826, 149775, -3043);
    }
}
