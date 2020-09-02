// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.form;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Location;
import java.awt.Polygon;
import org.l2j.gameserver.world.zone.ZoneArea;

public class ZonePolygonArea extends ZoneArea
{
    private final Polygon polygon;
    private final int minZ;
    private final int maxZ;
    
    public ZonePolygonArea(final int[] x, final int[] y, final int minZ, final int maxZ) {
        this.polygon = new Polygon(x, y, x.length);
        this.minZ = Math.min(minZ, maxZ);
        this.maxZ = Math.max(minZ, maxZ);
    }
    
    @Override
    public boolean isInsideZone(final int x, final int y, final int z) {
        return this.polygon.contains(x, y) && z >= this.minZ && z <= this.maxZ;
    }
    
    @Override
    public boolean intersectsRectangle(final int ax1, final int ax2, final int ay1, final int ay2) {
        return this.polygon.intersects(Math.min(ax1, ax2), Math.min(ay1, ay2), Math.abs(ax2 - ax1), Math.abs(ay2 - ay1));
    }
    
    @Override
    public double getDistanceToZone(final int x, final int y) {
        final int[] _x = this.polygon.xpoints;
        final int[] _y = this.polygon.ypoints;
        double shortestDist = Math.pow(_x[0] - x, 2.0) + Math.pow(_y[0] - y, 2.0);
        for (int i = 1; i < this.polygon.npoints; ++i) {
            final double test = Math.pow(_x[i] - x, 2.0) + Math.pow(_y[i] - y, 2.0);
            if (test < shortestDist) {
                shortestDist = test;
            }
        }
        return Math.sqrt(shortestDist);
    }
    
    @Override
    public int getLowZ() {
        return this.minZ;
    }
    
    @Override
    public int getHighZ() {
        return this.maxZ;
    }
    
    @Override
    public void visualizeZone(final int z) {
        for (int i = 0; i < this.polygon.npoints; ++i) {
            final int nextIndex = (i + 1 == this.polygon.xpoints.length) ? 0 : (i + 1);
            final int vx = this.polygon.xpoints[nextIndex] - this.polygon.xpoints[i];
            final int vy = this.polygon.ypoints[nextIndex] - this.polygon.ypoints[i];
            final float length = (float)Math.sqrt(vx * vx + vy * vy) / 10.0f;
            for (int o = 1; o <= length; ++o) {
                this.dropDebugItem(57, 1, (int)(this.polygon.xpoints[i] + o / length * vx), (int)(this.polygon.ypoints[i] + o / length * vy), z);
            }
        }
    }
    
    @Override
    public Location getRandomPoint() {
        final int _minX = this.polygon.getBounds().x;
        final int _maxX = this.polygon.getBounds().x + this.polygon.getBounds().width;
        final int _minY = this.polygon.getBounds().y;
        final int _maxY = this.polygon.getBounds().y + this.polygon.getBounds().height;
        int x = Rnd.get(_minX, _maxX);
        int y = Rnd.get(_minY, _maxY);
        for (int antiBlocker = 0; !this.polygon.contains(x, y) && antiBlocker++ < 1000; x = Rnd.get(_minX, _maxX), y = Rnd.get(_minY, _maxY)) {}
        return new Location(x, y, GeoEngine.getInstance().getHeight(x, y, this.minZ));
    }
    
    public int[] getX() {
        return this.polygon.xpoints;
    }
    
    public int[] getY() {
        return this.polygon.ypoints;
    }
}
