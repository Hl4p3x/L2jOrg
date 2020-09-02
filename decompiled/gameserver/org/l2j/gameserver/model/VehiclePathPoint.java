// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public final class VehiclePathPoint extends Location
{
    private final int _moveSpeed;
    private final int _rotationSpeed;
    
    public VehiclePathPoint(final Location loc) {
        this(loc.getX(), loc.getY(), loc.getZ());
    }
    
    public VehiclePathPoint(final int x, final int y, final int z) {
        super(x, y, z);
        this._moveSpeed = 350;
        this._rotationSpeed = 4000;
    }
    
    public VehiclePathPoint(final int x, final int y, final int z, final int moveSpeed, final int rotationSpeed) {
        super(x, y, z);
        this._moveSpeed = moveSpeed;
        this._rotationSpeed = rotationSpeed;
    }
    
    public int getMoveSpeed() {
        return this._moveSpeed;
    }
    
    public int getRotationSpeed() {
        return this._rotationSpeed;
    }
}
