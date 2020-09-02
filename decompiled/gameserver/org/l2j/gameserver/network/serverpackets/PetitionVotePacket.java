// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class PetitionVotePacket extends ServerPacket
{
    public static final PetitionVotePacket STATIC_PACKET;
    
    private PetitionVotePacket() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PETITION_VOTE);
    }
    
    static {
        STATIC_PACKET = new PetitionVotePacket();
    }
}
