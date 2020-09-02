// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.autoplay;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExActivateAutoShortcut extends ServerPacket
{
    private final int room;
    private final boolean activate;
    
    public ExActivateAutoShortcut(final int room, final boolean activate) {
        this.room = room;
        this.activate = activate;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ACTIVATE_AUTO_SHORTCUT);
        this.writeShort(this.room);
        this.writeByte(this.activate);
    }
}
