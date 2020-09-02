// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.ensoul;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExShowEnsoulExtractionWindow extends ServerPacket
{
    public static final ExShowEnsoulExtractionWindow STATIC_PACKET;
    
    private ExShowEnsoulExtractionWindow() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_ENSOUL_EXTRACTION_WINDOW);
    }
    
    static {
        STATIC_PACKET = new ExShowEnsoulExtractionWindow();
    }
}
