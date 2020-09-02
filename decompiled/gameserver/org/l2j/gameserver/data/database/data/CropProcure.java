// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("castle_manor_procure")
public final class CropProcure extends SeedProduction
{
    @Column("reward_type")
    private int rewardType;
    
    public CropProcure(final int id, final long amount, final int type, final long startAmount, final long price, final int castleId, final boolean nextPeriod) {
        super(id, amount, price, startAmount, castleId, nextPeriod);
        this.rewardType = type;
    }
    
    public final int getReward() {
        return this.rewardType;
    }
}
