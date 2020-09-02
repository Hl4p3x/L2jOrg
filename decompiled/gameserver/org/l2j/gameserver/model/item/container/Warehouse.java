// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.container;

public abstract class Warehouse extends ItemContainer
{
    public boolean isPrivate() {
        return this.getType() == WarehouseType.PRIVATE;
    }
    
    public abstract WarehouseType getType();
}
