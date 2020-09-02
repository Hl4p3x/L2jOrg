// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo;

import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.ListIterator;
import org.l2j.gameserver.engine.geo.pathfinding.NodeBuffer;
import org.l2j.gameserver.engine.geo.geodata.GeoLocation;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.LinkedList;
import org.l2j.gameserver.model.Location;
import java.util.List;
import org.l2j.gameserver.engine.geo.pathfinding.Node;
import org.slf4j.Logger;

final class GeoEnginePathFinding extends GeoEngine
{
    private static final Logger LOGGER;
    private final BufferHolder[] buffers;
    
    GeoEnginePathFinding() {
        this.buffers = new BufferHolder[] { new BufferHolder(100, 6), new BufferHolder(128, 6), new BufferHolder(192, 6), new BufferHolder(256, 4), new BufferHolder(320, 4), new BufferHolder(384, 4), new BufferHolder(500, 2) };
        GeoEnginePathFinding.LOGGER.info("Loaded {} path node buffers.", (Object)this.buffers.length);
    }
    
    private static List<Location> constructPath(Node target) {
        final LinkedList<Location> list = new LinkedList<Location>();
        int dx = 0;
        int dy = 0;
        for (Node parent = target.getParent(); parent != null; parent = target.getParent()) {
            final int nx = parent.getLoc().getGeoX() - target.getLoc().getGeoX();
            final int ny = parent.getLoc().getGeoY() - target.getLoc().getGeoY();
            if (dx != nx || dy != ny) {
                list.addFirst(target.getLoc());
                dx = nx;
                dy = ny;
            }
            target = parent;
        }
        return list;
    }
    
    @Override
    public List<Location> findPath(final int ox, final int oy, final int oz, final int tx, final int ty, final int tz, final Instance instance) {
        final int gox = GeoEngine.getGeoX(ox);
        final int goy = GeoEngine.getGeoY(oy);
        if (!this.hasGeoPos(gox, goy)) {
            return null;
        }
        final short goz = this.getHeightNearest(gox, goy, oz);
        final int gtx = GeoEngine.getGeoX(tx);
        final int gty = GeoEngine.getGeoY(ty);
        if (!this.hasGeoPos(gtx, gty)) {
            return null;
        }
        final short gtz = this.getHeightNearest(gtx, gty, tz);
        final NodeBuffer buffer = this.getBuffer(64 + 2 * Math.max(Math.abs(gox - gtx), Math.abs(goy - gty)));
        if (buffer == null) {
            return null;
        }
        List<Location> path = null;
        try {
            final Node result = buffer.findPath(gox, goy, goz, gtx, gty, gtz);
            if (result == null) {
                return null;
            }
            path = constructPath(result);
        }
        catch (Exception e) {
            GeoEnginePathFinding.LOGGER.warn(e.getMessage());
            return null;
        }
        finally {
            buffer.free();
        }
        if (path.size() < 3) {
            return path;
        }
        final ListIterator<Location> point = path.listIterator();
        int nodeAx = gox;
        int nodeAy = goy;
        short nodeAz = goz;
        GeoLocation nodeB = point.next();
        while (point.hasNext()) {
            final GeoLocation nodeC = path.get(point.nextIndex());
            final GeoLocation loc = this.checkMove(nodeAx, nodeAy, nodeAz, nodeC.getGeoX(), nodeC.getGeoY(), nodeC.getZ(), instance);
            if (loc.getGeoX() == nodeC.getGeoX() && loc.getGeoY() == nodeC.getGeoY()) {
                point.remove();
            }
            else {
                nodeAx = nodeB.getGeoX();
                nodeAy = nodeB.getGeoY();
                nodeAz = (short)nodeB.getZ();
            }
            nodeB = point.next();
        }
        return path;
    }
    
    private final NodeBuffer getBuffer(final int size) {
        NodeBuffer current = null;
        for (final BufferHolder holder : this.buffers) {
            if (holder._size >= size) {
                for (final NodeBuffer buffer : holder._buffer) {
                    if (!buffer.isLocked()) {
                        continue;
                    }
                    return buffer;
                }
                current = new NodeBuffer(holder._size);
                current.isLocked();
            }
        }
        return current;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)GeoEnginePathFinding.class);
    }
    
    private static final class BufferHolder
    {
        final int _size;
        List<NodeBuffer> _buffer;
        
        public BufferHolder(final int size, final int count) {
            this._size = size;
            this._buffer = new ArrayList<NodeBuffer>(count);
            for (int i = 0; i < count; ++i) {
                this._buffer.add(new NodeBuffer(size));
            }
        }
    }
}
