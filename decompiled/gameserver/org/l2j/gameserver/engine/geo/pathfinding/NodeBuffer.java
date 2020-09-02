// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo.pathfinding;

import org.slf4j.LoggerFactory;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;

public class NodeBuffer
{
    private static final Logger LOGGER;
    private static final int BASE_WEIGHT = 10;
    private static final int DIAGONAL_WEIGHT = 14;
    private static final int HEURISTIC_WEIGHT = 20;
    private static final int OBSTACLE_MULTIPLIER = 10;
    private static final int MAX_ITERATIONS = 3500;
    private final ReentrantLock _lock;
    private final int _size;
    private final Node[][] _buffer;
    private int _cx;
    private int _cy;
    private int _gtx;
    private int _gty;
    private short _gtz;
    private Node _current;
    
    public NodeBuffer(final int size) {
        this._lock = new ReentrantLock();
        this._cx = 0;
        this._cy = 0;
        this._gtx = 0;
        this._gty = 0;
        this._gtz = 0;
        this._current = null;
        this._size = size;
        this._buffer = new Node[size][size];
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                this._buffer[x][y] = new Node();
            }
        }
    }
    
    public final Node findPath(final int gox, final int goy, final short goz, final int gtx, final int gty, final short gtz) {
        this._cx = gox + (gtx - gox - this._size) / 2;
        this._cy = goy + (gty - goy - this._size) / 2;
        this._gtx = gtx;
        this._gty = gty;
        this._gtz = gtz;
        (this._current = this.getNode(gox, goy, goz)).setCost(this.getCostH(gox, goy, goz));
        int count = 0;
        while (this._current.getLoc().getGeoX() != this._gtx || this._current.getLoc().getGeoY() != this._gty || Math.abs(this._current.getLoc().getZ() - this._gtz) >= 8) {
            this.expand();
            this._current = this._current.getChild();
            if (this._current == null || ++count >= 3500) {
                return null;
            }
        }
        return this._current;
    }
    
    public final boolean isLocked() {
        return this._lock.tryLock();
    }
    
    public final void free() {
        this._current = null;
        for (final Node[] array : this._buffer) {
            final Node[] nodes = array;
            for (final Node node : array) {
                if (node.getLoc() != null) {
                    node.free();
                }
            }
        }
        this._lock.unlock();
    }
    
    private final void expand() {
        final byte nswe = this._current.getLoc().getNSWE();
        if (nswe == 0) {
            return;
        }
        final int x = this._current.getLoc().getGeoX();
        final int y = this._current.getLoc().getGeoY();
        final short z = (short)this._current.getLoc().getZ();
        if ((nswe & 0x8) != 0x0) {
            this.addNode(x, y - 1, z, 10);
        }
        if ((nswe & 0x4) != 0x0) {
            this.addNode(x, y + 1, z, 10);
        }
        if ((nswe & 0x2) != 0x0) {
            this.addNode(x - 1, y, z, 10);
        }
        if ((nswe & 0x1) != 0x0) {
            this.addNode(x + 1, y, z, 10);
        }
        if ((nswe & 0xFFFFFF80) != 0x0) {
            this.addNode(x - 1, y - 1, z, 14);
        }
        if ((nswe & 0x40) != 0x0) {
            this.addNode(x + 1, y - 1, z, 14);
        }
        if ((nswe & 0x20) != 0x0) {
            this.addNode(x - 1, y + 1, z, 14);
        }
        if ((nswe & 0x10) != 0x0) {
            this.addNode(x + 1, y + 1, z, 14);
        }
    }
    
    private final Node getNode(final int x, final int y, final short z) {
        final int ix = x - this._cx;
        if (ix < 0 || ix >= this._size) {
            return null;
        }
        final int iy = y - this._cy;
        if (iy < 0 || iy >= this._size) {
            return null;
        }
        final Node result = this._buffer[ix][iy];
        if (result.getLoc() == null) {
            result.setLoc(x, y, z);
        }
        return result;
    }
    
    private final void addNode(final int x, final int y, final short z, final int weight) {
        final Node node = this.getNode(x, y, z);
        if (node == null) {
            return;
        }
        if (node.getLoc().getZ() > z + 16) {
            return;
        }
        if (node.getCost() >= 0.0) {
            return;
        }
        node.setParent(this._current);
        if (node.getLoc().getNSWE() != -1) {
            node.setCost(this.getCostH(x, y, node.getLoc().getZ()) + weight * 10);
        }
        else {
            node.setCost(this.getCostH(x, y, node.getLoc().getZ()) + weight);
        }
        Node current;
        int count;
        for (current = this._current, count = 0; current.getChild() != null && count < 14000; current = current.getChild()) {
            ++count;
            if (current.getChild().getCost() > node.getCost()) {
                node.setChild(current.getChild());
                break;
            }
        }
        if (count >= 14000) {
            NodeBuffer.LOGGER.warn("Too long loop detected, cost: {}", (Object)node.getCost());
        }
        current.setChild(node);
    }
    
    private final double getCostH(final int x, final int y, final int i) {
        final int dX = x - this._gtx;
        final int dY = y - this._gty;
        final int dZ = (i - this._gtz) / 8;
        return Math.sqrt(dX * dX + dY * dY + dZ * dZ) * 20.0;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)NodeBuffer.class);
    }
}
