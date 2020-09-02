// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo.geodata;

import java.io.IOException;
import java.io.BufferedOutputStream;
import java.nio.ByteBuffer;

public class BlockComplex extends ABlock
{
    protected byte[] _buffer;
    
    protected BlockComplex() {
        this._buffer = null;
    }
    
    public BlockComplex(final ByteBuffer bb, final GeoFormat format) {
        this._buffer = new byte[192];
        for (int i = 0; i < 64; ++i) {
            if (format != GeoFormat.L2D) {
                short data = bb.getShort();
                this._buffer[i * 3] = (byte)(data & 0xF);
                data = (short)((short)(data & 0xFFF0) >> 1);
                this._buffer[i * 3 + 1] = (byte)(data & 0xFF);
                this._buffer[i * 3 + 2] = (byte)(data >> 8);
            }
            else {
                final byte nswe = bb.get();
                this._buffer[i * 3] = nswe;
                final short height = bb.getShort();
                this._buffer[i * 3 + 1] = (byte)(height & 0xFF);
                this._buffer[i * 3 + 2] = (byte)(height >> 8);
            }
        }
    }
    
    @Override
    public final boolean hasGeoPos() {
        return true;
    }
    
    @Override
    public final short getHeightNearest(final int geoX, final int geoY, final int worldZ) {
        final int index = (geoX % 8 * 8 + geoY % 8) * 3;
        return (short)((this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8);
    }
    
    @Override
    public short getHeightNearestOriginal(final int geoX, final int geoY, final int worldZ) {
        return this.getHeightNearest(geoX, geoY, worldZ);
    }
    
    @Override
    public final short getHeightAbove(final int geoX, final int geoY, final int worldZ) {
        final int index = (geoX % 8 * 8 + geoY % 8) * 3;
        final short height = (short)((this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8);
        return (short)((height > worldZ) ? height : -32768);
    }
    
    @Override
    public final short getHeightBelow(final int geoX, final int geoY, final int worldZ) {
        final int index = (geoX % 8 * 8 + geoY % 8) * 3;
        final short height = (short)((this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8);
        return (short)((height < worldZ) ? height : 32767);
    }
    
    @Override
    public final byte getNsweNearest(final int geoX, final int geoY, final int worldZ) {
        final int index = (geoX % 8 * 8 + geoY % 8) * 3;
        return this._buffer[index];
    }
    
    @Override
    public byte getNsweNearestOriginal(final int geoX, final int geoY, final int worldZ) {
        return this.getNsweNearest(geoX, geoY, worldZ);
    }
    
    @Override
    public final byte getNsweAbove(final int geoX, final int geoY, final int worldZ) {
        final int index = (geoX % 8 * 8 + geoY % 8) * 3;
        final int height = (this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8;
        return (byte)((height > worldZ) ? this._buffer[index] : 0);
    }
    
    @Override
    public final byte getNsweBelow(final int geoX, final int geoY, final int worldZ) {
        final int index = (geoX % 8 * 8 + geoY % 8) * 3;
        final int height = (this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8;
        return (byte)((height < worldZ) ? this._buffer[index] : 0);
    }
    
    @Override
    public final int getIndexNearest(final int geoX, final int geoY, final int worldZ) {
        return (geoX % 8 * 8 + geoY % 8) * 3;
    }
    
    @Override
    public final int getIndexAbove(final int geoX, final int geoY, final int worldZ) {
        final int index = (geoX % 8 * 8 + geoY % 8) * 3;
        final int height = (this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8;
        return (height > worldZ) ? index : -1;
    }
    
    @Override
    public int getIndexAboveOriginal(final int geoX, final int geoY, final int worldZ) {
        return this.getIndexAbove(geoX, geoY, worldZ);
    }
    
    @Override
    public final int getIndexBelow(final int geoX, final int geoY, final int worldZ) {
        final int index = (geoX % 8 * 8 + geoY % 8) * 3;
        final int height = (this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8;
        return (height < worldZ) ? index : -1;
    }
    
    @Override
    public int getIndexBelowOriginal(final int geoX, final int geoY, final int worldZ) {
        return this.getIndexBelow(geoX, geoY, worldZ);
    }
    
    @Override
    public final short getHeight(final int index) {
        return (short)((this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8);
    }
    
    @Override
    public short getHeightOriginal(final int index) {
        return (short)((this._buffer[index + 1] & 0xFF) | this._buffer[index + 2] << 8);
    }
    
    @Override
    public final byte getNswe(final int index) {
        return this._buffer[index];
    }
    
    @Override
    public byte getNsweOriginal(final int index) {
        return this._buffer[index];
    }
    
    @Override
    public final void setNswe(final int index, final byte nswe) {
        this._buffer[index] = nswe;
    }
    
    @Override
    public final void saveBlock(final BufferedOutputStream stream) throws IOException {
        stream.write(-47);
        stream.write(this._buffer, 0, 192);
    }
}
