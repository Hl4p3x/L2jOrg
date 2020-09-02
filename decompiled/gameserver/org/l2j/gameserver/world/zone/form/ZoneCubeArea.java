// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.form;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Location;
import java.awt.Rectangle;
import org.l2j.gameserver.world.zone.ZoneArea;

public class ZoneCubeArea extends ZoneArea
{
    private final int _z1;
    private final int _z2;
    private final Rectangle _r;
    
    public ZoneCubeArea(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
        final int _x1 = Math.min(x1, x2);
        final int _x2 = Math.max(x1, x2);
        final int _y1 = Math.min(y1, y2);
        final int _y2 = Math.max(y1, y2);
        this._r = new Rectangle(_x1, _y1, _x2 - _x1, _y2 - _y1);
        this._z1 = Math.min(z1, z2);
        this._z2 = Math.max(z1, z2);
    }
    
    @Override
    public boolean isInsideZone(final int x, final int y, final int z) {
        return this._r.contains(x, y) && z >= this._z1 && z <= this._z2;
    }
    
    @Override
    public boolean intersectsRectangle(final int ax1, final int ax2, final int ay1, final int ay2) {
        return this._r.intersects(Math.min(ax1, ax2), Math.min(ay1, ay2), Math.abs(ax2 - ax1), Math.abs(ay2 - ay1));
    }
    
    @Override
    public double getDistanceToZone(final int x, final int y) {
        final int _x1 = this._r.x;
        final int _x2 = this._r.x + this._r.width;
        final int _y1 = this._r.y;
        final int _y2 = this._r.y + this._r.height;
        double test = Math.pow(_x1 - x, 2.0) + Math.pow(_y2 - y, 2.0);
        double shortestDist = Math.pow(_x1 - x, 2.0) + Math.pow(_y1 - y, 2.0);
        if (test < shortestDist) {
            shortestDist = test;
        }
        test = Math.pow(_x2 - x, 2.0) + Math.pow(_y1 - y, 2.0);
        if (test < shortestDist) {
            shortestDist = test;
        }
        test = Math.pow(_x2 - x, 2.0) + Math.pow(_y2 - y, 2.0);
        if (test < shortestDist) {
            shortestDist = test;
        }
        return Math.sqrt(shortestDist);
    }
    
    @Override
    public int getLowZ() {
        return this._z1;
    }
    
    @Override
    public int getHighZ() {
        return this._z2;
    }
    
    @Override
    public void visualizeZone(final int z) {
        final int _x1 = this._r.x;
        final int _x2 = this._r.x + this._r.width;
        final int _y1 = this._r.y;
        final int _y2 = this._r.y + this._r.height;
        for (int x = _x1; x < _x2; x += 10) {
            this.dropDebugItem(57, 1, x, _y1, z);
            this.dropDebugItem(57, 1, x, _y2, z);
        }
        for (int y = _y1; y < _y2; y += 10) {
            this.dropDebugItem(57, 1, _x1, y, z);
            this.dropDebugItem(57, 1, _x2, y, z);
        }
    }
    
    @Override
    public Location getRandomPoint() {
        final int x = Rnd.get(this._r.x, this._r.x + this._r.width);
        final int y = Rnd.get(this._r.y, this._r.y + this._r.height);
        return new Location(x, y, GeoEngine.getInstance().getHeight(x, y, this._z1));
    }
}
