// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.autoplay;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public final class ExAutoPlayDoMacro extends ServerPacket
{
    public static ExAutoPlayDoMacro STATIC;
    
    private ExAutoPlayDoMacro() {
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_AUTOPLAY_DO_MACRO);
        this.writeInt(276);
    }
    
    static {
        ExAutoPlayDoMacro.STATIC = new ExAutoPlayDoMacro();
    }
}
