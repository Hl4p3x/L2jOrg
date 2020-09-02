// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.VehiclePathPoint;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.Config;
import java.util.HashMap;
import org.l2j.gameserver.model.actor.instance.Boat;
import java.util.Map;

public class BoatManager
{
    public static final int TALKING_ISLAND = 1;
    public static final int GLUDIN_HARBOR = 2;
    public static final int RUNE_HARBOR = 3;
    private final Map<Integer, Boat> _boats;
    private final boolean[] _docksBusy;
    
    private BoatManager() {
        this._boats = new HashMap<Integer, Boat>();
        this._docksBusy = new boolean[3];
        for (int i = 0; i < this._docksBusy.length; ++i) {
            this._docksBusy[i] = false;
        }
    }
    
    public Boat getNewBoat(final int boatId, final int x, final int y, final int z, final int heading) {
        if (!Config.ALLOW_BOAT) {
            return null;
        }
        final StatsSet npcDat = new StatsSet();
        npcDat.set("npcId", boatId);
        npcDat.set("level", 0);
        npcDat.set("jClass", "boat");
        npcDat.set("baseSTR", 0);
        npcDat.set("baseCON", 0);
        npcDat.set("baseDEX", 0);
        npcDat.set("baseINT", 0);
        npcDat.set("baseWIT", 0);
        npcDat.set("baseMEN", 0);
        npcDat.set("baseShldDef", 0);
        npcDat.set("baseShldRate", 0);
        npcDat.set("baseAccCombat", 38);
        npcDat.set("baseEvasRate", 38);
        npcDat.set("baseCritRate", 38);
        npcDat.set("collision_radius", 0);
        npcDat.set("collision_height", 0);
        npcDat.set("sex", "male");
        npcDat.set("type", "");
        npcDat.set("baseAtkRange", 0);
        npcDat.set("baseMpMax", 0);
        npcDat.set("baseCpMax", 0);
        npcDat.set("rewardExp", 0);
        npcDat.set("rewardSp", 0);
        npcDat.set("basePAtk", 0);
        npcDat.set("baseMAtk", 0);
        npcDat.set("basePAtkSpd", 0);
        npcDat.set("aggroRange", 0);
        npcDat.set("baseMAtkSpd", 0);
        npcDat.set("rhand", 0);
        npcDat.set("lhand", 0);
        npcDat.set("armor", 0);
        npcDat.set("baseWalkSpd", 0);
        npcDat.set("baseRunSpd", 0);
        npcDat.set("baseHpMax", 50000);
        npcDat.set("baseHpReg", 0.003f);
        npcDat.set("baseMpReg", 0.003f);
        npcDat.set("basePDef", 100);
        npcDat.set("baseMDef", 100);
        final CreatureTemplate template = new CreatureTemplate(npcDat);
        final Boat boat = new Boat(template);
        this._boats.put(boat.getObjectId(), boat);
        boat.setHeading(heading);
        boat.setXYZInvisible(x, y, z);
        boat.spawnMe();
        return boat;
    }
    
    public Boat getBoat(final int boatId) {
        return this._boats.get(boatId);
    }
    
    public void dockShip(final int h, final boolean value) {
        try {
            this._docksBusy[h] = value;
        }
        catch (ArrayIndexOutOfBoundsException ex) {}
    }
    
    public boolean dockBusy(final int h) {
        try {
            return this._docksBusy[h];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
    
    public void broadcastPacket(final VehiclePathPoint point1, final VehiclePathPoint point2, final ServerPacket packet) {
        this.broadcastPacketsToPlayers(point1, point2, packet);
    }
    
    public void broadcastPackets(final VehiclePathPoint point1, final VehiclePathPoint point2, final ServerPacket... packets) {
        this.broadcastPacketsToPlayers(point1, point2, packets);
    }
    
    private void broadcastPacketsToPlayers(final VehiclePathPoint point1, final VehiclePathPoint point2, final ServerPacket... packets) {
        for (final Player player : World.getInstance().getPlayers()) {
            if (Math.hypot(player.getX() - point1.getX(), player.getY() - point1.getY()) < Config.BOAT_BROADCAST_RADIUS) {
                for (final ServerPacket p : packets) {
                    player.sendPacket(p);
                }
            }
            else {
                if (Math.hypot(player.getX() - point2.getX(), player.getY() - point2.getY()) >= Config.BOAT_BROADCAST_RADIUS) {
                    continue;
                }
                for (final ServerPacket p : packets) {
                    player.sendPacket(p);
                }
            }
        }
    }
    
    public static BoatManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final BoatManager INSTANCE;
        
        static {
            INSTANCE = new BoatManager();
        }
    }
}
