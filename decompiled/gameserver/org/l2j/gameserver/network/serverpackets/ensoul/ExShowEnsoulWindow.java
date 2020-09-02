// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.ensoul;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExShowEnsoulWindow extends ServerPacket
{
    public static final ExShowEnsoulWindow STATIC_PACKET;
    
    private ExShowEnsoulWindow() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_ENSOUL_WINDOW);
    }
    
    static {
        STATIC_PACKET = new ExShowEnsoulWindow();
    }
}
