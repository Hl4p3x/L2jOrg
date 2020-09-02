// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.attributechange;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExChangeAttributeOk extends ServerPacket
{
    public static final ServerPacket STATIC;
    
    private ExChangeAttributeOk() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHANGE_ATTRIBUTE_OK);
    }
    
    static {
        STATIC = new ExChangeAttributeOk();
    }
}
