// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.model;

import org.l2j.gameserver.model.Location;

public class TeleportData
{
    private final byte castleId;
    private final long price;
    private final Location location;
    
    public TeleportData(final long price, final Location location, final byte castleId) {
        this.price = price;
        this.location = location;
        this.castleId = castleId;
    }
    
    public long getPrice() {
        return this.price;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public byte getCastleId() {
        return this.castleId;
    }
}
