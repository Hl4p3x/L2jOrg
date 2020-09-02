// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.auction;

public enum ItemAuctionState
{
    CREATED((byte)0), 
    STARTED((byte)1), 
    FINISHED((byte)2);
    
    private final byte _stateId;
    
    private ItemAuctionState(final byte stateId) {
        this._stateId = stateId;
    }
    
    public static ItemAuctionState stateForStateId(final byte stateId) {
        for (final ItemAuctionState state : values()) {
            if (state.getStateId() == stateId) {
                return state;
            }
        }
        return null;
    }
    
    public byte getStateId() {
        return this._stateId;
    }
}
