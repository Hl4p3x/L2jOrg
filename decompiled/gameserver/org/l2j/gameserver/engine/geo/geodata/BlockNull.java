// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo.geodata;

import java.io.BufferedOutputStream;

public class BlockNull extends ABlock
{
    private final byte _nswe;
    
    public BlockNull() {
        this._nswe = -1;
    }
    
    @Override
    public final boolean hasGeoPos() {
        return false;
    }
    
    @Override
    public final short getHeightNearest(final int geoX, final int geoY, final int worldZ) {
        return (short)worldZ;
    }
    
    @Override
    public final short getHeightNearestOriginal(final int geoX, final int geoY, final int worldZ) {
        return (short)worldZ;
    }
    
    @Override
    public final short getHeightAbove(final int geoX, final int geoY, final int worldZ) {
        return (short)worldZ;
    }
    
    @Override
    public final short getHeightBelow(final int geoX, final int geoY, final int worldZ) {
        return (short)worldZ;
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
        return this._nswe;
    }
    
    @Override
    public final byte getNsweBelow(final int geoX, final int geoY, final int worldZ) {
        return this._nswe;
    }
    
    @Override
    public final int getIndexNearest(final int geoX, final int geoY, final int worldZ) {
        return 0;
    }
    
    @Override
    public final int getIndexAbove(final int geoX, final int geoY, final int worldZ) {
        return 0;
    }
    
    @Override
    public final int getIndexAboveOriginal(final int geoX, final int geoY, final int worldZ) {
        return 0;
    }
    
    @Override
    public final int getIndexBelow(final int geoX, final int geoY, final int worldZ) {
        return 0;
    }
    
    @Override
    public final int getIndexBelowOriginal(final int geoX, final int geoY, final int worldZ) {
        return 0;
    }
    
    @Override
    public final short getHeight(final int index) {
        return 0;
    }
    
    @Override
    public final short getHeightOriginal(final int index) {
        return 0;
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
    }
    
    @Override
    public final void saveBlock(final BufferedOutputStream stream) {
    }
}
