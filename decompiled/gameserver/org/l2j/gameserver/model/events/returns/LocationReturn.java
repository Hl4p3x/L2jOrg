// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.returns;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.instancezone.Instance;

public class LocationReturn extends TerminateReturn
{
    private final boolean _overrideLocation;
    private int _x;
    private int _y;
    private int _z;
    private int _heading;
    private Instance _instance;
    
    public LocationReturn(final boolean terminate, final boolean overrideLocation) {
        super(terminate, false, false);
        this._overrideLocation = overrideLocation;
    }
    
    public LocationReturn(final boolean terminate, final boolean overrideLocation, final ILocational targetLocation, final Instance instance) {
        super(terminate, false, false);
        this._overrideLocation = overrideLocation;
        if (targetLocation != null) {
            this.setX(targetLocation.getX());
            this.setY(targetLocation.getY());
            this.setZ(targetLocation.getZ());
            this.setHeading(targetLocation.getHeading());
            this.setInstance(instance);
        }
    }
    
    public boolean overrideLocation() {
        return this._overrideLocation;
    }
    
    public int getX() {
        return this._x;
    }
    
    public void setX(final int x) {
        this._x = x;
    }
    
    public int getY() {
        return this._y;
    }
    
    public void setY(final int y) {
        this._y = y;
    }
    
    public int getZ() {
        return this._z;
    }
    
    public void setZ(final int z) {
        this._z = z;
    }
    
    public int getHeading() {
        return this._heading;
    }
    
    public void setHeading(final int heading) {
        this._heading = heading;
    }
    
    public Instance getInstance() {
        return this._instance;
    }
    
    public void setInstance(final Instance instance) {
        this._instance = instance;
    }
}
