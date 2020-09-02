// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pvpbook;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExKillerLocation extends ServerPacket
{
    private final Player killer;
    
    public ExKillerLocation(final Player killer) {
        this.killer = killer;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PVPBOOK_KILLER_LOCATION);
        this.writeSizedString((CharSequence)this.killer.getName());
        final Location location = this.killer.getLocation();
        this.writeInt(location.getX());
        this.writeInt(location.getY());
        this.writeInt(location.getZ());
    }
}
