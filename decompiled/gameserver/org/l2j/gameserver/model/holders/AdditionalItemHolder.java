// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

public class AdditionalItemHolder extends ItemHolder
{
    private final boolean _allowed;
    
    public AdditionalItemHolder(final int id, final boolean allowed) {
        super(id, 0L);
        this._allowed = allowed;
    }
    
    public boolean isAllowedToUse() {
        return this._allowed;
    }
}
