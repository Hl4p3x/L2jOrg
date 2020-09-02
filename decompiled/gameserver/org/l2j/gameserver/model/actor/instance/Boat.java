// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.network.serverpackets.VehicleInfo;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.VehicleStarted;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.VehicleDeparture;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.ai.BoatAI;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.actor.Vehicle;

public class Boat extends Vehicle
{
    public Boat(final CreatureTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2BoatInstance);
        this.setAI(new BoatAI(this));
    }
    
    @Override
    public boolean isBoat() {
        return true;
    }
    
    @Override
    public int getId() {
        return 0;
    }
    
    @Override
    public boolean moveToNextRoutePoint() {
        final boolean result = super.moveToNextRoutePoint();
        if (result) {
            this.broadcastPacket(new VehicleDeparture(this));
        }
        return result;
    }
    
    @Override
    public void oustPlayer(final Player player) {
        super.oustPlayer(player);
        final Location loc = this.getOustLoc();
        if (player.isOnline()) {
            player.teleToLocation(loc.getX(), loc.getY(), loc.getZ());
        }
        else {
            player.setXYZInvisible(loc.getX(), loc.getY(), loc.getZ());
        }
    }
    
    @Override
    public void stopMove(final Location loc) {
        super.stopMove(loc);
        this.broadcastPacket(new VehicleStarted(this, 0));
        this.broadcastPacket(new VehicleInfo(this));
    }
    
    @Override
    public void sendInfo(final Player activeChar) {
        activeChar.sendPacket(new VehicleInfo(this));
    }
}
