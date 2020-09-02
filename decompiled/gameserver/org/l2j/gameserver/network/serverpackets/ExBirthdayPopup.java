// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExBirthdayPopup extends ServerPacket
{
    public static final ExBirthdayPopup STATIC_PACKET;
    
    private ExBirthdayPopup() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_NOTIFY_BIRTHDAY);
    }
    
    static {
        STATIC_PACKET = new ExBirthdayPopup();
    }
}
