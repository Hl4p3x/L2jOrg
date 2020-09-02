// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

public final class LinePointIterator
{
    private final int _dstX;
    private final int _dstY;
    private final long _dx;
    private final long _dy;
    private final long _sx;
    private final long _sy;
    private int _srcX;
    private int _srcY;
    private long _error;
    private boolean _first;
    
    public LinePointIterator(final int srcX, final int srcY, final int dstX, final int dstY) {
        this._srcX = srcX;
        this._srcY = srcY;
        this._dstX = dstX;
        this._dstY = dstY;
        this._dx = Math.abs(dstX - (long)srcX);
        this._dy = Math.abs(dstY - (long)srcY);
        this._sx = ((srcX < dstX) ? 1L : -1L);
        this._sy = ((srcY < dstY) ? 1L : -1L);
        if (this._dx >= this._dy) {
            this._error = this._dx / 2L;
        }
        else {
            this._error = this._dy / 2L;
        }
        this._first = true;
    }
    
    public boolean next() {
        if (this._first) {
            this._first = false;
            return true;
        }
        if (this._dx >= this._dy) {
            if (this._srcX != this._dstX) {
                this._srcX += (int)this._sx;
                this._error += this._dy;
                if (this._error >= this._dx) {
                    this._srcY += (int)this._sy;
                    this._error -= this._dx;
                }
                return true;
            }
        }
        else if (this._srcY != this._dstY) {
            this._srcY += (int)this._sy;
            this._error += this._dx;
            if (this._error >= this._dy) {
                this._srcX += (int)this._sx;
                this._error -= this._dy;
            }
            return true;
        }
        return false;
    }
    
    public int x() {
        return this._srcX;
    }
    
    public int y() {
        return this._srcY;
    }
}
