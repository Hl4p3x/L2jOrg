// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.buylist;

import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.EquipableItem;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.BuyListDAO;
import java.util.concurrent.TimeUnit;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.item.type.EtcItemType;
import java.util.Objects;
import org.l2j.gameserver.data.database.data.BuyListInfo;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import org.l2j.gameserver.model.item.ItemTemplate;

public final class Product
{
    private final ItemTemplate template;
    private final long restockDelay;
    private AtomicLong count;
    private final long price;
    private final long maxCount;
    private final double baseTax;
    private ScheduledFuture<?> restockTask;
    private BuyListInfo info;
    
    public Product(final int buyListId, final ItemTemplate item, final long price, final long restockDelay, final long maxCount, final int baseTax) {
        this.count = null;
        this.restockTask = null;
        Objects.requireNonNull(item);
        this.template = item;
        this.maxCount = maxCount;
        if (this.hasLimitedStock()) {
            this.count = new AtomicLong(maxCount);
        }
        this.restockDelay = restockDelay * 60000L;
        this.baseTax = baseTax / 100.0;
        this.price = ((price < 0L) ? item.getReferencePrice() : price);
        this.info = BuyListInfo.of(item.getId(), buyListId);
    }
    
    public ItemTemplate getTemplate() {
        return this.template;
    }
    
    public int getItemId() {
        return this.template.getId();
    }
    
    public long getPrice() {
        long price = this.price;
        if (this.template.getItemType().equals(EtcItemType.CASTLE_GUARD)) {
            price *= (long)Config.RATE_SIEGE_GUARDS_PRICE;
        }
        return price;
    }
    
    public double getBaseTaxRate() {
        return this.baseTax;
    }
    
    public long getMaxCount() {
        return this.maxCount;
    }
    
    public long getCount() {
        if (this.count == null) {
            return 0L;
        }
        final long count = this.count.get();
        return (count > 0L) ? count : 0L;
    }
    
    public void setCount(final long currentCount) {
        if (this.count == null) {
            this.count = new AtomicLong();
        }
        this.count.set(currentCount);
    }
    
    public boolean decreaseCount(final long val) {
        if (this.count == null) {
            return false;
        }
        if (this.restockTask == null || this.restockTask.isDone()) {
            this.restockTask = (ScheduledFuture<?>)ThreadPool.schedule(this::restock, this.restockDelay);
        }
        final boolean result = this.count.addAndGet(-val) >= 0L;
        this.save();
        return result;
    }
    
    public boolean hasLimitedStock() {
        return this.maxCount > -1L;
    }
    
    public void restartRestockTask(final long nextRestockTime) {
        final long remainTime = nextRestockTime - System.currentTimeMillis();
        if (remainTime > 0L) {
            this.restockTask = (ScheduledFuture<?>)ThreadPool.schedule(this::restock, remainTime);
        }
        else {
            this.restock();
        }
    }
    
    public void restock() {
        this.setCount(this.maxCount);
        this.save();
    }
    
    private void save() {
        if (this.hasLimitedStock()) {
            this.info.setCount(this.getCount());
            this.info.setNextRestock(Objects.isNull(this.restockTask) ? 0L : (this.restockTask.getDelay(TimeUnit.MILLISECONDS) + System.currentTimeMillis()));
            ((BuyListDAO)DatabaseAccess.getDAO((Class)BuyListDAO.class)).save((Object)this.info);
        }
    }
    
    public boolean isStackable() {
        return this.template.isStackable();
    }
    
    public long getWeight() {
        return this.template.getWeight();
    }
    
    public CrystalType getCrystalType() {
        return this.template.getCrystalType();
    }
    
    public boolean isEquipable() {
        return this.template instanceof EquipableItem;
    }
    
    public int getType2() {
        return this.template.getType2();
    }
    
    public int getType1() {
        return this.template.getType1();
    }
    
    public BodyPart getBodyPart() {
        return this.template.getBodyPart();
    }
    
    public void updateInfo(final BuyListInfo info) {
        this.info = info;
        if (info.getCount() < this.maxCount) {
            this.setCount(info.getCount());
            this.restartRestockTask(info.getNextRestock());
        }
    }
}
