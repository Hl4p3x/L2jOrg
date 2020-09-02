// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.DragonValley.Fellow;

import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Fellow extends AbstractNpcAI
{
    private static final int FELLOW = 31713;
    private static final Location TELEPORT_LOC;
    private static final NpcStringId[] TEXT;
    
    private Fellow() {
        this.addFirstTalkId(31713);
        this.addTalkId(31713);
        this.addSpawnId(new int[] { 31713 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "teleToAntharas": {
                if (player.getCommandChannel() == null || player.getCommandChannel().getLeader() != player || player.getCommandChannel().getMemberCount() < 27 || player.getCommandChannel().getMemberCount() > 300) {
                    return "31713-01.html";
                }
                for (final Player member : player.getCommandChannel().getMembers()) {
                    if (member != null && member.getLevel() > 70) {
                        member.teleToLocation((ILocational)Fellow.TELEPORT_LOC);
                    }
                }
                break;
            }
            case "CHAT_TIMER": {
                npc.broadcastPacket((ServerPacket)new NpcSay(npc, ChatType.NPC_GENERAL, Fellow.TEXT[Rnd.get(Fellow.TEXT.length)]));
                this.startQuestTimer("CHAT_TIMER", 30000L, npc, (Player)null);
                break;
            }
        }
        return null;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return "31713.html";
    }
    
    public String onSpawn(final Npc npc) {
        this.startQuestTimer("CHAT_TIMER", 5000L, npc, (Player)null);
        return super.onSpawn(npc);
    }
    
    static {
        TELEPORT_LOC = new Location(154376, 121290, -3807);
        TEXT = new NpcStringId[] { NpcStringId.LET_S_JOIN_OUR_FORCES_AND_FACE_THIS_TOGETHER, NpcStringId.BALTHUS_KNIGHTS_ARE_LOOKING_FOR_MERCENARIES };
    }
}
