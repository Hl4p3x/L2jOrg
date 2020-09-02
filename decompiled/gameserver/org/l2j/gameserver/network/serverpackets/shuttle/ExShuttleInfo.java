// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.shuttle;

import java.util.Iterator;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.shuttle.ShuttleStop;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExShuttleInfo extends ServerPacket
{
    private final Shuttle _shuttle;
    private final List<ShuttleStop> _stops;
    
    public ExShuttleInfo(final Shuttle shuttle) {
        this._shuttle = shuttle;
        this._stops = shuttle.getStops();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHUTTLE_INFO);
        this.writeInt(this._shuttle.getObjectId());
        this.writeInt(this._shuttle.getX());
        this.writeInt(this._shuttle.getY());
        this.writeInt(this._shuttle.getZ());
        this.writeInt(this._shuttle.getHeading());
        this.writeInt(this._shuttle.getId());
        this.writeInt(this._stops.size());
        for (final ShuttleStop stop : this._stops) {
            this.writeInt(stop.getId());
            for (final Location loc : stop.getDimensions()) {
                this.writeInt(loc.getX());
                this.writeInt(loc.getY());
                this.writeInt(loc.getZ());
            }
            this.writeInt((int)(stop.isDoorOpen() ? 1 : 0));
            this.writeInt((int)(stop.hasDoorChanged() ? 1 : 0));
        }
    }
}
