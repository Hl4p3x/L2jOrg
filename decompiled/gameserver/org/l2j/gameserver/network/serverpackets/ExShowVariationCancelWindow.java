// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowVariationCancelWindow extends ServerPacket
{
    public static final ExShowVariationCancelWindow STATIC_PACKET;
    
    private ExShowVariationCancelWindow() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_VARIATION_CANCEL_WINDOW);
    }
    
    static {
        STATIC_PACKET = new ExShowVariationCancelWindow();
    }
}
