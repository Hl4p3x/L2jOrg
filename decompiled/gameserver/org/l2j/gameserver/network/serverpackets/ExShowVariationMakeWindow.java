// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowVariationMakeWindow extends ServerPacket
{
    public static final ExShowVariationMakeWindow STATIC_PACKET;
    
    private ExShowVariationMakeWindow() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_VARIATION_MAKE_WINDOW);
    }
    
    static {
        STATIC_PACKET = new ExShowVariationMakeWindow();
    }
}
