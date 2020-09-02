// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class ClientSetTime extends ServerPacket
{
    public static final ClientSetTime STATIC_PACKET;
    
    private ClientSetTime() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CLIENT_SETTIME);
        this.writeInt(WorldTimeController.getInstance().getGameTime());
        this.writeInt(6);
    }
    
    static {
        STATIC_PACKET = new ClientSetTime();
    }
}
