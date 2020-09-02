// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.attributechange;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExChangeAttributeFail extends ServerPacket
{
    public static final ServerPacket STATIC;
    
    private ExChangeAttributeFail() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHANGE_ATTRIBUTE_FAIL);
    }
    
    static {
        STATIC = new ExChangeAttributeFail();
    }
}
