// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.awt.Color;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.ArrayList;
import java.util.List;

public class ExServerPrimitive extends ServerPacket
{
    private final String _name;
    private final int _x;
    private final int _y;
    private final int _z;
    private final List<Point> _points;
    private final List<Line> _lines;
    
    public ExServerPrimitive(final String name, final int x, final int y, final int z) {
        this._points = new ArrayList<Point>();
        this._lines = new ArrayList<Line>();
        this._name = name;
        this._x = x;
        this._y = y;
        this._z = z;
    }
    
    public ExServerPrimitive(final String name, final ILocational locational) {
        this(name, locational.getX(), locational.getY(), locational.getZ());
    }
    
    public void addPoint(final String name, final int color, final boolean isNameColored, final int x, final int y, final int z) {
        this._points.add(new Point(name, color, isNameColored, x, y, z));
    }
    
    public void addPoint(final String name, final int color, final boolean isNameColored, final ILocational locational) {
        this.addPoint(name, color, isNameColored, locational.getX(), locational.getY(), locational.getZ());
    }
    
    public void addPoint(final int color, final int x, final int y, final int z) {
        this.addPoint("", color, false, x, y, z);
    }
    
    public void addPoint(final int color, final ILocational locational) {
        this.addPoint("", color, false, locational);
    }
    
    public void addPoint(final String name, final Color color, final boolean isNameColored, final int x, final int y, final int z) {
        this.addPoint(name, color.getRGB(), isNameColored, x, y, z);
    }
    
    public void addPoint(final String name, final Color color, final boolean isNameColored, final ILocational locational) {
        this.addPoint(name, color.getRGB(), isNameColored, locational);
    }
    
    public void addPoint(final Color color, final int x, final int y, final int z) {
        this.addPoint("", color, false, x, y, z);
    }
    
    public void addPoint(final Color color, final ILocational locational) {
        this.addPoint("", color, false, locational);
    }
    
    public void addLine(final String name, final int color, final boolean isNameColored, final int x, final int y, final int z, final int x2, final int y2, final int z2) {
        this._lines.add(new Line(name, color, isNameColored, x, y, z, x2, y2, z2));
    }
    
    public void addLine(final String name, final int color, final boolean isNameColored, final ILocational locational, final int x2, final int y2, final int z2) {
        this.addLine(name, color, isNameColored, locational.getX(), locational.getY(), locational.getZ(), x2, y2, z2);
    }
    
    public void addLine(final String name, final int color, final boolean isNameColored, final int x, final int y, final int z, final ILocational locational2) {
        this.addLine(name, color, isNameColored, x, y, z, locational2.getX(), locational2.getY(), locational2.getZ());
    }
    
    public void addLine(final String name, final int color, final boolean isNameColored, final ILocational locational, final ILocational locational2) {
        this.addLine(name, color, isNameColored, locational, locational2.getX(), locational2.getY(), locational2.getZ());
    }
    
    public void addLine(final int color, final int x, final int y, final int z, final int x2, final int y2, final int z2) {
        this.addLine("", color, false, x, y, z, x2, y2, z2);
    }
    
    public void addLine(final int color, final ILocational locational, final int x2, final int y2, final int z2) {
        this.addLine("", color, false, locational, x2, y2, z2);
    }
    
    public void addLine(final int color, final int x, final int y, final int z, final ILocational locational2) {
        this.addLine("", color, false, x, y, z, locational2);
    }
    
    public void addLine(final int color, final ILocational locational, final ILocational locational2) {
        this.addLine("", color, false, locational, locational2);
    }
    
    public void addLine(final String name, final Color color, final boolean isNameColored, final int x, final int y, final int z, final int x2, final int y2, final int z2) {
        this.addLine(name, color.getRGB(), isNameColored, x, y, z, x2, y2, z2);
    }
    
    public void addLine(final String name, final Color color, final boolean isNameColored, final ILocational locational, final int x2, final int y2, final int z2) {
        this.addLine(name, color.getRGB(), isNameColored, locational, x2, y2, z2);
    }
    
    public void addLine(final String name, final Color color, final boolean isNameColored, final int x, final int y, final int z, final ILocational locational2) {
        this.addLine(name, color.getRGB(), isNameColored, x, y, z, locational2);
    }
    
    public void addLine(final String name, final Color color, final boolean isNameColored, final ILocational locational, final ILocational locational2) {
        this.addLine(name, color.getRGB(), isNameColored, locational, locational2);
    }
    
    public void addLine(final Color color, final int x, final int y, final int z, final int x2, final int y2, final int z2) {
        this.addLine("", color, false, x, y, z, x2, y2, z2);
    }
    
    public void addLine(final Color color, final ILocational locational, final int x2, final int y2, final int z2) {
        this.addLine("", color, false, locational, x2, y2, z2);
    }
    
    public void addLine(final Color color, final int x, final int y, final int z, final ILocational locational2) {
        this.addLine("", color, false, x, y, z, locational2);
    }
    
    public void addLine(final Color color, final ILocational locational, final ILocational locational2) {
        this.addLine("", color, false, locational, locational2);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SERVER_PRIMITIVE);
        this.writeString((CharSequence)this._name);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
        this.writeInt(65535);
        this.writeInt(65535);
        this.writeInt(this._points.size() + this._lines.size());
        for (final Point point : this._points) {
            this.writeByte((byte)1);
            this.writePoint(point);
        }
        for (final Line line : this._lines) {
            this.writeByte((byte)2);
            this.writePoint(line);
            this.writeInt(line.getX2());
            this.writeInt(line.getY2());
            this.writeInt(line.getZ2());
        }
    }
    
    private void writePoint(final Point line) {
        this.writeString((CharSequence)line.getName());
        final int color = line.getColor();
        this.writeInt(color >> 16 & 0xFF);
        this.writeInt(color >> 8 & 0xFF);
        this.writeInt(color & 0xFF);
        this.writeInt((int)(line.isNameColored() ? 1 : 0));
        this.writeInt(line.getX());
        this.writeInt(line.getY());
        this.writeInt(line.getZ());
    }
    
    private static class Point
    {
        private final String _name;
        private final int _color;
        private final boolean _isNameColored;
        private final int _x;
        private final int _y;
        private final int _z;
        
        public Point(final String name, final int color, final boolean isNameColored, final int x, final int y, final int z) {
            this._name = name;
            this._color = color;
            this._isNameColored = isNameColored;
            this._x = x;
            this._y = y;
            this._z = z;
        }
        
        public String getName() {
            return this._name;
        }
        
        public int getColor() {
            return this._color;
        }
        
        public boolean isNameColored() {
            return this._isNameColored;
        }
        
        public int getX() {
            return this._x;
        }
        
        public int getY() {
            return this._y;
        }
        
        public int getZ() {
            return this._z;
        }
    }
    
    private static class Line extends Point
    {
        private final int _x2;
        private final int _y2;
        private final int _z2;
        
        public Line(final String name, final int color, final boolean isNameColored, final int x, final int y, final int z, final int x2, final int y2, final int z2) {
            super(name, color, isNameColored, x, y, z);
            this._x2 = x2;
            this._y2 = y2;
            this._z2 = z2;
        }
        
        public int getX2() {
            return this._x2;
        }
        
        public int getY2() {
            return this._y2;
        }
        
        public int getZ2() {
            return this._z2;
        }
    }
}
