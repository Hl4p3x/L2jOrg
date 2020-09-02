// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.interfaces.IUniqueId;

public class UniqueItemHolder extends ItemHolder implements IUniqueId
{
    private final int _objectId;
    
    public UniqueItemHolder(final int id, final int objectId) {
        this(id, objectId, 1L);
    }
    
    public UniqueItemHolder(final int id, final int objectId, final long count) {
        super(id, count);
        this._objectId = objectId;
    }
    
    @Override
    public int getObjectId() {
        return this._objectId;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IIJ)Ljava/lang/String;, this.getClass().getSimpleName(), this.getId(), this._objectId, this.getCount());
    }
}
