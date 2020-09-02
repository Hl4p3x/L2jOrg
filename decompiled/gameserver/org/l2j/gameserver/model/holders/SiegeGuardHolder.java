// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.SiegeGuardType;

public class SiegeGuardHolder
{
    private final int _castleId;
    private final int _itemId;
    private final SiegeGuardType _type;
    private final boolean _stationary;
    private final int _npcId;
    private final int _maxNpcAmount;
    
    public SiegeGuardHolder(final int castleId, final int itemId, final SiegeGuardType type, final boolean stationary, final int npcId, final int maxNpcAmount) {
        this._castleId = castleId;
        this._itemId = itemId;
        this._type = type;
        this._stationary = stationary;
        this._npcId = npcId;
        this._maxNpcAmount = maxNpcAmount;
    }
    
    public final int getCastleId() {
        return this._castleId;
    }
    
    public final int getItemId() {
        return this._itemId;
    }
    
    public final SiegeGuardType getType() {
        return this._type;
    }
    
    public final boolean isStationary() {
        return this._stationary;
    }
    
    public final int getNpcId() {
        return this._npcId;
    }
    
    public final int getMaxNpcAmout() {
        return this._maxNpcAmount;
    }
}
