// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.l2j.commons.util.Rnd;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class Territory
{
    private static final Logger LOGGER;
    private final List<Point> _points;
    private final int _terr;
    private int _xMin;
    private int _xMax;
    private int _yMin;
    private int _yMax;
    private int _zMin;
    private int _zMax;
    private int _procMax;
    
    public Territory(final int terr) {
        this._points = Collections.synchronizedList(new ArrayList<Point>());
        this._terr = terr;
        this._xMin = 999999;
        this._xMax = -999999;
        this._yMin = 999999;
        this._yMax = -999999;
        this._zMin = 999999;
        this._zMax = -999999;
        this._procMax = 0;
    }
    
    public void add(final int x, final int y, final int zmin, final int zmax, final int proc) {
        this._points.add(new Point(x, y, zmin, zmax, proc));
        if (x < this._xMin) {
            this._xMin = x;
        }
        if (y < this._yMin) {
            this._yMin = y;
        }
        if (x > this._xMax) {
            this._xMax = x;
        }
        if (y > this._yMax) {
            this._yMax = y;
        }
        if (zmin < this._zMin) {
            this._zMin = zmin;
        }
        if (zmax > this._zMax) {
            this._zMax = zmax;
        }
        this._procMax += proc;
    }
    
    public boolean isIntersect(final int x, final int y, final Point p1, final Point p2) {
        final double dy1 = p1._y - y;
        final double dy2 = p2._y - y;
        if (Math.abs(Math.signum(dy1) - Math.signum(dy2)) <= 1.0E-6) {
            return false;
        }
        final double dx1 = p1._x - x;
        final double dx2 = p2._x - x;
        if (dx1 >= 0.0 && dx2 >= 0.0) {
            return true;
        }
        if (dx1 < 0.0 && dx2 < 0.0) {
            return false;
        }
        final double dx3 = dy1 * (p1._x - p2._x) / (p1._y - p2._y);
        return dx3 <= dx1;
    }
    
    public boolean isInside(final int x, final int y) {
        int intersect_count = 0;
        for (int i = 0; i < this._points.size(); ++i) {
            final Point p1 = this._points.get((i > 0) ? (i - 1) : (this._points.size() - 1));
            final Point p2 = this._points.get(i);
            if (this.isIntersect(x, y, p1, p2)) {
                ++intersect_count;
            }
        }
        return intersect_count % 2 == 1;
    }
    
    public Location getRandomPoint() {
        if (this._procMax > 0) {
            int pos = 0;
            final int rnd = Rnd.get(this._procMax);
            for (final Point p1 : this._points) {
                pos += p1._proc;
                if (rnd <= pos) {
                    return new Location(p1._x, p1._y, Rnd.get(p1._zmin, p1._zmax));
                }
            }
        }
        for (int i = 0; i < 100; ++i) {
            final int x = Rnd.get(this._xMin, this._xMax);
            final int y = Rnd.get(this._yMin, this._yMax);
            if (this.isInside(x, y)) {
                double curdistance = 0.0;
                int zmin = this._zMin;
                for (final Point p2 : this._points) {
                    final double distance = Math.hypot(p2._x - x, p2._y - y);
                    if (curdistance == 0.0 || distance < curdistance) {
                        curdistance = distance;
                        zmin = p2._zmin;
                    }
                }
                return new Location(x, y, Rnd.get(zmin, this._zMax));
            }
        }
        Territory.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._terr));
        return null;
    }
    
    public int getProcMax() {
        return this._procMax;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Territory.class);
    }
    
    protected static class Point
    {
        protected int _x;
        protected int _y;
        protected int _zmin;
        protected int _zmax;
        protected int _proc;
        
        Point(final int x, final int y, final int zmin, final int zmax, final int proc) {
            this._x = x;
            this._y = y;
            this._zmin = zmin;
            this._zmax = zmax;
            this._proc = proc;
        }
    }
}
