// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.type;

public interface ItemType
{
    int mask();
    
    default boolean isRanged() {
        return false;
    }
}
