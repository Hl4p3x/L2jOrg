// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.network.NpcStringId;

public class NpcLogListHolder
{
    private final int _id;
    private final boolean _isNpcString;
    private final int _count;
    
    public NpcLogListHolder(final NpcStringId npcStringId, final int count) {
        this._id = npcStringId.getId();
        this._isNpcString = true;
        this._count = count;
    }
    
    public NpcLogListHolder(final int id, final boolean isNpcString, final int count) {
        this._id = id;
        this._isNpcString = isNpcString;
        this._count = count;
    }
    
    public int getId() {
        return this._id;
    }
    
    public boolean isNpcString() {
        return this._isNpcString;
    }
    
    public int getCount() {
        return this._count;
    }
}
