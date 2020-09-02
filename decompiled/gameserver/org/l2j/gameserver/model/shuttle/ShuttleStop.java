// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.shuttle;

import java.util.ArrayList;
import org.l2j.gameserver.model.Location;
import java.util.List;

public class ShuttleStop
{
    private final int _id;
    private final List<Location> _dimensions;
    private boolean _isOpen;
    private long _lastDoorStatusChanges;
    
    public ShuttleStop(final int id) {
        this._dimensions = new ArrayList<Location>(3);
        this._isOpen = true;
        this._lastDoorStatusChanges = System.currentTimeMillis();
        this._id = id;
    }
    
    public int getId() {
        return this._id;
    }
    
    public boolean isDoorOpen() {
        return this._isOpen;
    }
    
    public void addDimension(final Location loc) {
        this._dimensions.add(loc);
    }
    
    public List<Location> getDimensions() {
        return this._dimensions;
    }
    
    public void openDoor() {
        if (this._isOpen) {
            return;
        }
        this._isOpen = true;
        this._lastDoorStatusChanges = System.currentTimeMillis();
    }
    
    public void closeDoor() {
        if (!this._isOpen) {
            return;
        }
        this._isOpen = false;
        this._lastDoorStatusChanges = System.currentTimeMillis();
    }
    
    public boolean hasDoorChanged() {
        return System.currentTimeMillis() - this._lastDoorStatusChanges <= 1000L;
    }
}
