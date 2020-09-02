// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.TryMixCubeResultType;

public class AlchemyResult extends ItemHolder
{
    private final TryMixCubeResultType _type;
    
    public AlchemyResult(final int id, final long count, final TryMixCubeResultType type) {
        super(id, count);
        this._type = type;
    }
    
    public TryMixCubeResultType getType() {
        return this._type;
    }
}
