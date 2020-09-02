// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.type;

public enum ArmorType implements ItemType
{
    NONE, 
    LIGHT, 
    HEAVY, 
    MAGIC, 
    SIGIL, 
    SHIELD;
    
    final int _mask;
    
    private ArmorType() {
        this._mask = 1 << this.ordinal() + WeaponType.values().length;
    }
    
    @Override
    public int mask() {
        return this._mask;
    }
}
