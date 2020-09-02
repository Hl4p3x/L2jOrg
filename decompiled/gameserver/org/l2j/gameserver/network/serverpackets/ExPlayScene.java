// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPlayScene extends ServerPacket
{
    public static final ExPlayScene STATIC_PACKET;
    
    private ExPlayScene() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLAY_SCENE);
    }
    
    static {
        STATIC_PACKET = new ExPlayScene();
    }
}
