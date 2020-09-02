// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.classchange;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExRequestClassChangeUi extends ServerPacket
{
    public static final ExRequestClassChangeUi STATIC_PACKET;
    
    private ExRequestClassChangeUi() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CLASS_CHANGE_SET_ALARM);
    }
    
    static {
        STATIC_PACKET = new ExRequestClassChangeUi();
    }
}
