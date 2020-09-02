// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.enchant.attribute;

import org.l2j.gameserver.enums.AttributeType;

public class AttributeHolder
{
    private final AttributeType _type;
    private int _value;
    
    public AttributeHolder(final AttributeType type, final int value) {
        this._type = type;
        this._value = value;
    }
    
    public AttributeType getType() {
        return this._type;
    }
    
    public int getValue() {
        return this._value;
    }
    
    public void setValue(final int value) {
        this._value = value;
    }
    
    public void incValue(final int with) {
        this._value += with;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this._type.name(), this._value);
    }
}
