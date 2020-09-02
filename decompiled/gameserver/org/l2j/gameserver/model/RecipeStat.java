// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.StatType;

public class RecipeStat
{
    private final StatType _type;
    private final int _value;
    
    public RecipeStat(final String type, final int value) {
        this._type = Enum.valueOf(StatType.class, type);
        this._value = value;
    }
    
    public StatType getType() {
        return this._type;
    }
    
    public int getValue() {
        return this._value;
    }
}
