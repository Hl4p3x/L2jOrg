// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class NewCharacterSuccess extends ServerPacket
{
    public static final NewCharacterSuccess STATIC_PACKET;
    
    private NewCharacterSuccess() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.NEW_CHARACTER_SUCCESS);
    }
    
    static {
        STATIC_PACKET = new NewCharacterSuccess();
    }
}
