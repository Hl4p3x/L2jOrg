// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo.geodata;

public enum GeoFormat
{
    L2J("%d_%d.l2j"), 
    L2OFF("%d_%d_conv.dat"), 
    L2D("%d_%d.l2d");
    
    private final String _filename;
    
    private GeoFormat(final String filename) {
        this._filename = filename;
    }
    
    public String getFilename() {
        return this._filename;
    }
}
