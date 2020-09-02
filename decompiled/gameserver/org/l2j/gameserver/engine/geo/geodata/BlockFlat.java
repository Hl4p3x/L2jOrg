// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo.geodata;

import java.io.IOException;
import java.io.BufferedOutputStream;
import java.nio.ByteBuffer;

public class BlockFlat extends ABlock
{
    protected final short _height;
    protected byte _nswe;
    
    public BlockFlat(final ByteBuffer bb, final GeoFormat format) {
        this._height = bb.getShort();
        this._nswe = (byte)((format != GeoFormat.L2D) ? 15 : -1);
        if (format == GeoFormat.L2OFF) {
            bb.getShort();
        }
    }
    
    @Override
    public final boolean hasGeoPos() {
        return true;
    }
    
    @Override
    public final short getHeightNearest(final int geoX, final int geoY, final int worldZ) {
        return this._height;
    }
    
    @Override
    public final short getHeightNearestOriginal(final int geoX, final int geoY, final int worldZ) {
        return this._height;
    }
    
    @Override
    public final short getHeightAbove(final int geoX, final int geoY, final int worldZ) {
        return (short)((this._height > worldZ) ? this._height : -32768);
    }
    
    @Override
    public final short getHeightBelow(final int geoX, final int geoY, final int worldZ) {
        return (short)((this._height < worldZ) ? this._height : 32767);
    }
    
    @Override
    public final byte getNsweNearest(final int geoX, final int geoY, final int worldZ) {
        return this._nswe;
    }
    
    @Override
    public final byte getNsweNearestOriginal(final int geoX, final int geoY, final int worldZ) {
        return this._nswe;
    }
    
    @Override
    public final byte getNsweAbove(final int geoX, final int geoY, final int worldZ) {
        return (byte)((this._height > worldZ) ? this._nswe : 0);
    }
    
    @Override
    public final byte getNsweBelow(final int geoX, final int geoY, final int worldZ) {
        return (byte)((this._height < worldZ) ? this._nswe : 0);
    }
    
    @Override
    public final int getIndexNearest(final int geoX, final int geoY, final int worldZ) {
        return 0;
    }
    
    @Override
    public final int getIndexAbove(final int geoX, final int geoY, final int worldZ) {
        return (this._height > worldZ) ? 0 : -1;
    }
    
    @Override
    public final int getIndexAboveOriginal(final int geoX, final int geoY, final int worldZ) {
        return this.getIndexAbove(geoX, geoY, worldZ);
    }
    
    @Override
    public final int getIndexBelow(final int geoX, final int geoY, final int worldZ) {
        return (this._height < worldZ) ? 0 : -1;
    }
    
    @Override
    public final int getIndexBelowOriginal(final int geoX, final int geoY, final int worldZ) {
        return this.getIndexBelow(geoX, geoY, worldZ);
    }
    
    @Override
    public final short getHeight(final int index) {
        return this._height;
    }
    
    @Override
    public final short getHeightOriginal(final int index) {
        return this._height;
    }
    
    @Override
    public final byte getNswe(final int index) {
        return this._nswe;
    }
    
    @Override
    public final byte getNsweOriginal(final int index) {
        return this._nswe;
    }
    
    @Override
    public final void setNswe(final int index, final byte nswe) {
        this._nswe = nswe;
    }
    
    @Override
    public final void saveBlock(final BufferedOutputStream stream) throws IOException {
        stream.write(-48);
        stream.write((byte)(this._height & 0xFF));
        stream.write((byte)(this._height >> 8));
    }
}
