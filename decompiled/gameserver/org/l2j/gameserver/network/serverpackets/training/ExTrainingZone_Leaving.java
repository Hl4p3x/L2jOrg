// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.training;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExTrainingZone_Leaving extends ServerPacket
{
    public static ExTrainingZone_Leaving STATIC_PACKET;
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_TRAINING_ZONE_LEAVING);
    }
    
    static {
        ExTrainingZone_Leaving.STATIC_PACKET = new ExTrainingZone_Leaving();
    }
}
