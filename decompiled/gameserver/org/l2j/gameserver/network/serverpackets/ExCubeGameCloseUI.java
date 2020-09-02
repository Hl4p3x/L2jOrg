// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExCubeGameCloseUI extends ServerPacket
{
    public static final ExCubeGameCloseUI STATIC_PACKET;
    
    private ExCubeGameCloseUI() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BLOCK_UPSET_LIST);
        this.writeInt(-1);
    }
    
    static {
        STATIC_PACKET = new ExCubeGameCloseUI();
    }
}
