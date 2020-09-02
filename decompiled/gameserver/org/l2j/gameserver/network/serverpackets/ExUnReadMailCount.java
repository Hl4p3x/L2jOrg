// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExUnReadMailCount extends ServerPacket
{
    private final int count;
    
    public ExUnReadMailCount(final int count) {
        this.count = count;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_UNREAD_MAIL_COUNT);
        this.writeInt(this.count);
    }
}
