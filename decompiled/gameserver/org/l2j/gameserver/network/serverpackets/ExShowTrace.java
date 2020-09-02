// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.ArrayList;
import org.l2j.gameserver.model.Location;
import java.util.List;

public final class ExShowTrace extends ServerPacket
{
    private final List<Location> _locations;
    
    public ExShowTrace() {
        this._locations = new ArrayList<Location>();
    }
    
    public void addLocation(final int x, final int y, final int z) {
        this._locations.add(new Location(x, y, z));
    }
    
    public void addLocation(final ILocational loc) {
        this.addLocation(loc.getX(), loc.getY(), loc.getZ());
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_TRACE);
        this.writeShort((short)0);
        this.writeInt(0);
        this.writeShort((short)this._locations.size());
        for (final Location loc : this._locations) {
            this.writeInt(loc.getX());
            this.writeInt(loc.getY());
            this.writeInt(loc.getZ());
        }
    }
}
