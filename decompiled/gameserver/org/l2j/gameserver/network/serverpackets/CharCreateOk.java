// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class CharCreateOk extends ServerPacket
{
    public static final CharCreateOk STATIC_PACKET;
    
    private CharCreateOk() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CHARACTER_CREATE_SUCCESS);
        this.writeInt(1);
    }
    
    static {
        STATIC_PACKET = new CharCreateOk();
    }
}
