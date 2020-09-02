// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public class ChanceLocation extends Location
{
    private final double _chance;
    
    public ChanceLocation(final int x, final int y, final int z, final int heading, final double chance) {
        super(x, y, z, heading);
        this._chance = chance;
    }
    
    public double getChance() {
        return this._chance;
    }
}
