// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

public class AttendanceInfoHolder
{
    private final int _rewardIndex;
    private final boolean _rewardAvailable;
    
    public AttendanceInfoHolder(final int rewardIndex, final boolean rewardAvailable) {
        this._rewardIndex = rewardIndex;
        this._rewardAvailable = rewardAvailable;
    }
    
    public int getRewardIndex() {
        return this._rewardIndex;
    }
    
    public boolean isRewardAvailable() {
        return this._rewardAvailable;
    }
}
