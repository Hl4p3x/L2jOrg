// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum PartyDistributionType
{
    FINDERS_KEEPERS(0, 487), 
    RANDOM(1, 488), 
    RANDOM_INCLUDING_SPOIL(2, 798), 
    BY_TURN(3, 799), 
    BY_TURN_INCLUDING_SPOIL(4, 800);
    
    private final int _id;
    private final int _sysStringId;
    
    private PartyDistributionType(final int id, final int sysStringId) {
        this._id = id;
        this._sysStringId = sysStringId;
    }
    
    public static PartyDistributionType findById(final int id) {
        for (final PartyDistributionType partyDistributionType : values()) {
            if (partyDistributionType.getId() == id) {
                return partyDistributionType;
            }
        }
        return null;
    }
    
    public int getId() {
        return this._id;
    }
    
    public int getSysStringId() {
        return this._sysStringId;
    }
}
