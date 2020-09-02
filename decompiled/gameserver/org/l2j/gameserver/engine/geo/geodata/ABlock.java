// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo.geodata;

import java.io.IOException;
import java.io.BufferedOutputStream;

public abstract class ABlock
{
    public abstract boolean hasGeoPos();
    
    public abstract short getHeightNearest(final int geoX, final int geoY, final int worldZ);
    
    public abstract short getHeightNearestOriginal(final int geoX, final int geoY, final int worldZ);
    
    public abstract short getHeightAbove(final int geoX, final int geoY, final int worldZ);
    
    public abstract short getHeightBelow(final int geoX, final int geoY, final int worldZ);
    
    public abstract byte getNsweNearest(final int geoX, final int geoY, final int worldZ);
    
    public abstract byte getNsweNearestOriginal(final int geoX, final int geoY, final int worldZ);
    
    public abstract byte getNsweAbove(final int geoX, final int geoY, final int worldZ);
    
    public abstract byte getNsweBelow(final int geoX, final int geoY, final int worldZ);
    
    public abstract int getIndexNearest(final int geoX, final int geoY, final int worldZ);
    
    public abstract int getIndexAbove(final int geoX, final int geoY, final int worldZ);
    
    public abstract int getIndexAboveOriginal(final int geoX, final int geoY, final int worldZ);
    
    public abstract int getIndexBelow(final int geoX, final int geoY, final int worldZ);
    
    public abstract int getIndexBelowOriginal(final int geoX, final int geoY, final int worldZ);
    
    public abstract short getHeight(final int index);
    
    public abstract short getHeightOriginal(final int index);
    
    public abstract byte getNswe(final int index);
    
    public abstract byte getNsweOriginal(final int index);
    
    public abstract void setNswe(final int index, final byte nswe);
    
    public abstract void saveBlock(final BufferedOutputStream stream) throws IOException;
}
