// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.shuttle;

import java.util.ArrayList;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.VehiclePathPoint;
import java.util.List;
import org.l2j.gameserver.model.Location;

public final class ShuttleData
{
    private final int _id;
    private final Location _loc;
    private final List<Integer> _doors;
    private final List<ShuttleStop> _stops;
    private final List<VehiclePathPoint[]> _routes;
    
    public ShuttleData(final StatsSet set) {
        this._doors = new ArrayList<Integer>(2);
        this._stops = new ArrayList<ShuttleStop>(2);
        this._routes = new ArrayList<VehiclePathPoint[]>(2);
        this._id = set.getInt("id");
        this._loc = new Location(set);
    }
    
    public int getId() {
        return this._id;
    }
    
    public Location getLocation() {
        return this._loc;
    }
    
    public void addDoor(final int id) {
        this._doors.add(id);
    }
    
    public List<Integer> getDoors() {
        return this._doors;
    }
    
    public void addStop(final ShuttleStop stop) {
        this._stops.add(stop);
    }
    
    public List<ShuttleStop> getStops() {
        return this._stops;
    }
    
    public void addRoute(final VehiclePathPoint[] route) {
        this._routes.add(route);
    }
    
    public List<VehiclePathPoint[]> getRoutes() {
        return this._routes;
    }
}
