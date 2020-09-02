// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.teleport;

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExTeleportFavoritesList extends ServerPacket
{
    private final boolean open;
    
    public ExTeleportFavoritesList(final boolean open) {
        this.open = open;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_TELEPORT_FAVORITES_LIST);
        this.writeByte(this.open);
        final IntSet teleports = client.getPlayer().getTeleportFavorites();
        this.writeInt(teleports.size());
        teleports.forEach(x$0 -> this.writeInt(x$0));
    }
}
