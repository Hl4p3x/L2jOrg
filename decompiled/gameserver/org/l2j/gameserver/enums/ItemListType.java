// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;

public enum ItemListType implements IUpdateTypeComponent
{
    AUGMENT_BONUS(1), 
    ELEMENTAL_ATTRIBUTE(2), 
    ENCHANT_EFFECT(4), 
    VISUAL_ID(8), 
    SOUL_CRYSTAL(16), 
    REUSE_DELAY(64);
    
    private final int _mask;
    
    private ItemListType(final int mask) {
        this._mask = mask;
    }
    
    @Override
    public int getMask() {
        return this._mask;
    }
}
