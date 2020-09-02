// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.interfaces.IPositionable;

public class Location implements IPositionable
{
    protected int _x;
    protected int _y;
    protected int _z;
    private int _heading;
    
    public Location(final int x, final int y, final int z) {
        this(x, y, z, 0);
    }
    
    public Location(final int x, final int y, final int z, final int heading) {
        this._x = x;
        this._y = y;
        this._z = z;
        this._heading = heading;
    }
    
    public Location(final WorldObject obj) {
        this(obj.getX(), obj.getY(), obj.getZ(), obj.getHeading());
    }
    
    public Location(final StatsSet set) {
        this._x = set.getInt("x");
        this._y = set.getInt("y");
        this._z = set.getInt("z");
        this._heading = set.getInt("heading", 0);
    }
    
    @Override
    public int getX() {
        return this._x;
    }
    
    @Override
    public int getY() {
        return this._y;
    }
    
    @Override
    public int getZ() {
        return this._z;
    }
    
    @Override
    public void setXYZ(final int x, final int y, final int z) {
        this._x = x;
        this._y = y;
        this._z = z;
    }
    
    @Override
    public void setXYZ(final ILocational loc) {
        this.setXYZ(loc.getX(), loc.getY(), loc.getZ());
    }
    
    @Override
    public int getHeading() {
        return this._heading;
    }
    
    @Override
    public void setHeading(final int heading) {
        this._heading = heading;
    }
    
    @Override
    public IPositionable getLocation() {
        return this;
    }
    
    @Override
    public void setLocation(final Location loc) {
        this._x = loc.getX();
        this._y = loc.getY();
        this._z = loc.getZ();
        this._heading = loc.getHeading();
    }
    
    @Override
    public boolean equals(final Object obj) {
        final Location loc;
        return obj instanceof Location && (loc = (Location)obj) == obj && this.getX() == loc.getX() && this.getY() == loc.getY() && this.getZ() == loc.getZ() && this.getHeading() == loc.getHeading();
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IIII)Ljava/lang/String;, this.getClass().getSimpleName(), this._x, this._y, this._z, this._heading);
    }
}
