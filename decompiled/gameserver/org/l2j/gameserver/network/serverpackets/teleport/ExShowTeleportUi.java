// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.teleport;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExShowTeleportUi extends ServerPacket
{
    public static final ExShowTeleportUi OPEN;
    
    private ExShowTeleportUi() {
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_TELEPORT_UI);
    }
    
    static {
        OPEN = new ExShowTeleportUi();
    }
}
