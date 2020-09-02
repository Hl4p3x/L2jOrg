// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum PrivateStoreType
{
    NONE(0), 
    SELL(1), 
    SELL_MANAGE(2), 
    BUY(3), 
    BUY_MANAGE(4), 
    MANUFACTURE(5), 
    PACKAGE_SELL(8), 
    SELL_BUFFS(9);
    
    private int _id;
    
    private PrivateStoreType(final int id) {
        this._id = id;
    }
    
    public static PrivateStoreType findById(final int id) {
        for (final PrivateStoreType privateStoreType : values()) {
            if (privateStoreType.getId() == id) {
                return privateStoreType;
            }
        }
        return null;
    }
    
    public int getId() {
        return this._id;
    }
}
