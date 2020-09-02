// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.commission;

import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledFuture;
import java.time.Instant;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.item.instance.Item;

public class CommissionItem
{
    private final long _commissionId;
    private final Item _itemInstance;
    private final ItemInfo _itemInfo;
    private final long _pricePerUnit;
    private final Instant _startTime;
    private final byte _durationInDays;
    private ScheduledFuture<?> _saleEndTask;
    
    public CommissionItem(final long commissionId, final Item itemInstance, final long pricePerUnit, final Instant startTime, final byte durationInDays) {
        this._commissionId = commissionId;
        this._itemInstance = itemInstance;
        this._itemInfo = new ItemInfo(this._itemInstance);
        this._pricePerUnit = pricePerUnit;
        this._startTime = startTime;
        this._durationInDays = durationInDays;
    }
    
    public long getCommissionId() {
        return this._commissionId;
    }
    
    public Item getItemInstance() {
        return this._itemInstance;
    }
    
    public ItemInfo getItemInfo() {
        return this._itemInfo;
    }
    
    public long getPricePerUnit() {
        return this._pricePerUnit;
    }
    
    public Instant getStartTime() {
        return this._startTime;
    }
    
    public byte getDurationInDays() {
        return this._durationInDays;
    }
    
    public Instant getEndTime() {
        return this._startTime.plus((long)this._durationInDays, (TemporalUnit)ChronoUnit.DAYS);
    }
    
    public ScheduledFuture<?> getSaleEndTask() {
        return this._saleEndTask;
    }
    
    public void setSaleEndTask(final ScheduledFuture<?> saleEndTask) {
        this._saleEndTask = saleEndTask;
    }
}
