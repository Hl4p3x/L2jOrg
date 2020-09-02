// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo.geodata;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.Location;

public class GeoLocation extends Location
{
    private byte _nswe;
    
    public GeoLocation(final int x, final int y, final int z) {
        super(x, y, GeoEngine.getInstance().getHeightNearest(x, y, z));
        this._nswe = GeoEngine.getInstance().getNsweNearest(x, y, z);
    }
    
    public void set(final int x, final int y, final short z) {
        super.setXYZ(x, y, GeoEngine.getInstance().getHeightNearest(x, y, z));
        this._nswe = GeoEngine.getInstance().getNsweNearest(x, y, z);
    }
    
    public int getGeoX() {
        return this._x;
    }
    
    public int getGeoY() {
        return this._y;
    }
    
    @Override
    public int getX() {
        return GeoEngine.getWorldX(this._x);
    }
    
    @Override
    public int getY() {
        return GeoEngine.getWorldY(this._y);
    }
    
    public byte getNSWE() {
        return this._nswe;
    }
}
