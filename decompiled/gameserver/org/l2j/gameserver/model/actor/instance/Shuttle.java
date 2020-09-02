// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.network.serverpackets.shuttle.ExShuttleInfo;
import org.l2j.gameserver.network.serverpackets.shuttle.ExShuttleGetOff;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.shuttle.ExShuttleGetOn;
import org.l2j.gameserver.model.Location;
import java.util.Iterator;
import org.l2j.gameserver.model.shuttle.ShuttleStop;
import java.util.List;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.ai.ShuttleAI;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.shuttle.ShuttleData;
import org.l2j.gameserver.model.actor.Vehicle;

public class Shuttle extends Vehicle
{
    private ShuttleData _shuttleData;
    
    public Shuttle(final CreatureTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2ShuttleInstance);
        this.setAI(new ShuttleAI(this));
    }
    
    public List<ShuttleStop> getStops() {
        return this._shuttleData.getStops();
    }
    
    public void closeDoor(final int id) {
        for (final ShuttleStop stop : this._shuttleData.getStops()) {
            if (stop.getId() == id) {
                stop.closeDoor();
                break;
            }
        }
    }
    
    public void openDoor(final int id) {
        for (final ShuttleStop stop : this._shuttleData.getStops()) {
            if (stop.getId() == id) {
                stop.openDoor();
                break;
            }
        }
    }
    
    @Override
    public int getId() {
        return this._shuttleData.getId();
    }
    
    @Override
    public boolean addPassenger(final Player player) {
        if (!super.addPassenger(player)) {
            return false;
        }
        player.setVehicle(this);
        player.setInVehiclePosition(new Location(0, 0, 0));
        player.broadcastPacket(new ExShuttleGetOn(player, this));
        player.setXYZ(this.getX(), this.getY(), this.getZ());
        player.revalidateZone(true);
        return true;
    }
    
    public void removePassenger(final Player player, final int x, final int y, final int z) {
        this.oustPlayer(player);
        if (player.isOnline()) {
            player.broadcastPacket(new ExShuttleGetOff(player, this, x, y, z));
            player.setXYZ(x, y, z);
            player.revalidateZone(true);
        }
        else {
            player.setXYZInvisible(x, y, z);
        }
    }
    
    @Override
    public void oustPlayers() {
        final Iterator<Player> iter = this._passengers.iterator();
        while (iter.hasNext()) {
            final Player player = iter.next();
            iter.remove();
            if (player != null) {
                this.oustPlayer(player);
            }
        }
    }
    
    @Override
    public void sendInfo(final Player activeChar) {
        activeChar.sendPacket(new ExShuttleInfo(this));
    }
    
    public void broadcastShuttleInfo() {
        this.broadcastPacket(new ExShuttleInfo(this));
    }
    
    public void setData(final ShuttleData data) {
        this._shuttleData = data;
    }
    
    public ShuttleData getShuttleData() {
        return this._shuttleData;
    }
}
