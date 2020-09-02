// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;

public enum GroupType implements IUpdateTypeComponent
{
    NONE(1), 
    PARTY(2), 
    COMMAND_CHANNEL(4);
    
    private int _mask;
    
    private GroupType(final int mask) {
        this._mask = mask;
    }
    
    public static GroupType getByMask(final int flag) {
        for (final GroupType type : values()) {
            if (type.getMask() == flag) {
                return type;
            }
        }
        return null;
    }
    
    @Override
    public int getMask() {
        return this._mask;
    }
}
