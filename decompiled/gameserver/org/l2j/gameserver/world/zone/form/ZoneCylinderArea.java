// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.form;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.world.zone.ZoneArea;

public class ZoneCylinderArea extends ZoneArea
{
    private final int centerX;
    private final int centerY;
    private final int minZ;
    private final int maxZ;
    private final int radius;
    private final int _radS;
    
    public ZoneCylinderArea(final int x, final int y, final int minZ, final int maxZ, final int radius) {
        this.centerX = x;
        this.centerY = y;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.radius = radius;
        this._radS = radius * radius;
    }
    
    @Override
    public boolean isInsideZone(final int x, final int y, final int z) {
        return Math.pow(this.centerX - x, 2.0) + Math.pow(this.centerY - y, 2.0) <= this._radS && z >= this.minZ && z <= this.maxZ;
    }
    
    @Override
    public boolean intersectsRectangle(final int ax1, final int ax2, final int ay1, final int ay2) {
        if (this.centerX > ax1 && this.centerX < ax2 && this.centerY > ay1 && this.centerY < ay2) {
            return true;
        }
        if (Math.pow(ax1 - this.centerX, 2.0) + Math.pow(ay1 - this.centerY, 2.0) < this._radS) {
            return true;
        }
        if (Math.pow(ax1 - this.centerX, 2.0) + Math.pow(ay2 - this.centerY, 2.0) < this._radS) {
            return true;
        }
        if (Math.pow(ax2 - this.centerX, 2.0) + Math.pow(ay1 - this.centerY, 2.0) < this._radS) {
            return true;
        }
        if (Math.pow(ax2 - this.centerX, 2.0) + Math.pow(ay2 - this.centerY, 2.0) < this._radS) {
            return true;
        }
        if (this.centerX > ax1 && this.centerX < ax2) {
            if (Math.abs(this.centerY - ay2) < this.radius) {
                return true;
            }
            if (Math.abs(this.centerY - ay1) < this.radius) {
                return true;
            }
        }
        if (this.centerY > ay1 && this.centerY < ay2) {
            if (Math.abs(this.centerX - ax2) < this.radius) {
                return true;
            }
            if (Math.abs(this.centerX - ax1) < this.radius) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public double getDistanceToZone(final int x, final int y) {
        return Math.hypot(this.centerX - x, this.centerY - y) - this.radius;
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
        final int count = (int)(6.283185307179586 * this.radius / 10.0);
        final double angle = 6.283185307179586 / count;
        for (int i = 0; i < count; ++i) {
            this.dropDebugItem(57, 1, this.centerX + (int)(Math.cos(angle * i) * this.radius), this.centerY + (int)(Math.sin(angle * i) * this.radius), z);
        }
    }
    
    @Override
    public Location getRandomPoint() {
        final int q = (int)(Rnd.nextDouble() * 2.0 * 3.141592653589793);
        final int r = (int)Math.sqrt(Rnd.nextDouble());
        final int x = (int)(this.radius * r * Math.cos(q) + this.centerX);
        final int y = (int)(this.radius * r * Math.sin(q) + this.centerY);
        return new Location(x, y, GeoEngine.getInstance().getHeight(x, y, this.minZ));
    }
}
