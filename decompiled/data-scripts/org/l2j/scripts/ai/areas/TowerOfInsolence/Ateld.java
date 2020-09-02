// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.TowerOfInsolence;

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

public class Ateld extends AbstractNpcAI
{
    private static final int ATELD = 31714;
    private static final Location TELEPORT_LOC;
    private static final NpcStringId[] TEXT;
    
    private Ateld() {
        this.addFirstTalkId(31714);
        this.addTalkId(31714);
        this.addSpawnId(new int[] { 31714 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "teleToBaium": {
                if (player.getCommandChannel() == null || player.getCommandChannel().getLeader() != player || player.getCommandChannel().getMemberCount() < 27 || player.getCommandChannel().getMemberCount() > 300) {
                    return "31714-01.html";
                }
                for (final Player member : player.getCommandChannel().getMembers()) {
                    if (member != null && member.getLevel() > 70) {
                        member.teleToLocation((ILocational)Ateld.TELEPORT_LOC);
                    }
                }
                break;
            }
            case "CHAT_TIMER": {
                npc.broadcastPacket((ServerPacket)new NpcSay(npc, ChatType.NPC_GENERAL, Ateld.TEXT[Rnd.get(Ateld.TEXT.length)]));
                this.startQuestTimer("CHAT_TIMER", 30000L, npc, (Player)null);
                break;
            }
        }
        return null;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return "31714.html";
    }
    
    public String onSpawn(final Npc npc) {
        this.startQuestTimer("CHAT_TIMER", 5000L, npc, (Player)null);
        return super.onSpawn(npc);
    }
    
    public static AbstractNpcAI provider() {
        return new Ateld();
    }
    
    static {
        TELEPORT_LOC = new Location(115322, 16756, 9012);
        TEXT = new NpcStringId[] { NpcStringId.LET_S_JOIN_OUR_FORCES_AND_FACE_THIS_TOGETHER, NpcStringId.BALTHUS_KNIGHTS_ARE_LOOKING_FOR_MERCENARIES };
    }
}
