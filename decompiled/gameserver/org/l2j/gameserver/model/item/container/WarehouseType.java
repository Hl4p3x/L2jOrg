// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.container;

public enum WarehouseType
{
    PRIVATE, 
    CLAN, 
    CASTLE, 
    FREIGHT;
    
    public int clientId() {
        return 1 + this.ordinal();
    }
}
