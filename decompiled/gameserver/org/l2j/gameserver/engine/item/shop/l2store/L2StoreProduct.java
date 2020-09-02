// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.item.shop.l2store;

import org.l2j.gameserver.engine.item.ItemEngine;
import java.util.List;

public class L2StoreProduct
{
    private final int id;
    private byte category;
    private byte paymentType;
    private int price;
    private byte panelType;
    private byte recommended;
    private int start;
    private int end;
    private byte daysOfWeek;
    private byte startHour;
    private byte startMinute;
    private byte stopHour;
    private byte stopMinute;
    private byte stock;
    private byte maxStock;
    private byte salePercent;
    private byte minLevel;
    private byte maxLevel;
    private byte minBirthday;
    private byte maxBirthday;
    private byte restrictionAmount;
    private byte availableCount;
    private final List<L2StoreItem> items;
    private byte vipTier;
    private int silverCoin;
    private boolean isVipGift;
    private RestrictionPeriod restrictionPeriod;
    
    public L2StoreProduct(final int id, final List<L2StoreItem> items) {
        this.id = id;
        this.items = items;
    }
    
    public int getId() {
        return this.id;
    }
    
    public byte getCategory() {
        return this.category;
    }
    
    public byte getPaymentType() {
        return this.paymentType;
    }
    
    public int getPrice() {
        return this.price;
    }
    
    public long getCount() {
        return this.items.stream().mapToLong(item -> ItemEngine.getInstance().getTemplate(item.getId()).isStackable() ? 1L : item.getCount()).sum();
    }
    
    public int getWeight() {
        return this.items.stream().mapToInt(L2StoreItem::getWeight).sum();
    }
    
    public byte getPanelType() {
        return this.panelType;
    }
    
    public byte getRecommended() {
        return this.recommended;
    }
    
    public int getStartSale() {
        return this.start;
    }
    
    public int getEndSale() {
        return this.end;
    }
    
    public byte getDaysOfWeek() {
        return this.daysOfWeek;
    }
    
    public byte getStartHour() {
        return this.startHour;
    }
    
    public byte getStartMinute() {
        return this.startMinute;
    }
    
    public byte getStopHour() {
        return this.stopHour;
    }
    
    public byte getStopMinute() {
        return this.stopMinute;
    }
    
    public int getStock() {
        return this.stock;
    }
    
    public byte getMaxStock() {
        return this.maxStock;
    }
    
    public byte getSalePercent() {
        return this.salePercent;
    }
    
    public byte getMinLevel() {
        return this.minLevel;
    }
    
    public byte getMaxLevel() {
        return this.maxLevel;
    }
    
    public byte getMinBirthday() {
        return this.minBirthday;
    }
    
    public byte getMaxBirthday() {
        return this.maxBirthday;
    }
    
    public byte getRestrictionAmount() {
        return this.restrictionAmount;
    }
    
    public byte getAvailableCount() {
        return this.availableCount;
    }
    
    public List<L2StoreItem> getItems() {
        return this.items;
    }
    
    public void setCategory(final byte category) {
        this.category = category;
    }
    
    public void setPaymentType(final byte paymentType) {
        this.paymentType = paymentType;
    }
    
    public void setPrice(final int price) {
        this.price = price;
    }
    
    public void setPanelType(final byte panelType) {
        this.panelType = panelType;
    }
    
    public void setRecommended(final byte recommended) {
        this.recommended = recommended;
    }
    
    public void setStart(final int start) {
        this.start = start;
    }
    
    public void setEnd(final int end) {
        this.end = end;
    }
    
    public void setDaysOfWeek(final byte daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }
    
    public void setStartHour(final byte startHour) {
        this.startHour = startHour;
    }
    
    public void setStartMinute(final byte startMinute) {
        this.startMinute = startMinute;
    }
    
    public void setStopHour(final byte stopHour) {
        this.stopHour = stopHour;
    }
    
    public void setStopMinute(final byte stopMinute) {
        this.stopMinute = stopMinute;
    }
    
    public void setStock(final byte stock) {
        this.stock = stock;
    }
    
    public void setMaxStock(final byte maxStock) {
        this.maxStock = maxStock;
    }
    
    public void setSalePercent(final byte salePercent) {
        this.salePercent = salePercent;
    }
    
    public void setMinLevel(final byte minLevel) {
        this.minLevel = minLevel;
    }
    
    public void setMaxLevel(final byte maxLevel) {
        this.maxLevel = maxLevel;
    }
    
    public void setMinBirthday(final byte minBirthday) {
        this.minBirthday = minBirthday;
    }
    
    public void setMaxBirthday(final byte maxBirthday) {
        this.maxBirthday = maxBirthday;
    }
    
    public void setRestrictionAmount(final byte restrictionAmount) {
        this.restrictionAmount = restrictionAmount;
    }
    
    public void setAvailableCount(final byte availableCount) {
        this.availableCount = availableCount;
    }
    
    public void setVipTier(final byte vipTier) {
        this.vipTier = vipTier;
    }
    
    public byte getVipTier() {
        return this.vipTier;
    }
    
    public void setSilverCoin(final int silverCoin) {
        this.silverCoin = silverCoin;
    }
    
    public int getSilverCoin() {
        return this.silverCoin;
    }
    
    public void setVipGift(final boolean isVipGift) {
        this.isVipGift = isVipGift;
    }
    
    public boolean isVipGift() {
        return this.isVipGift;
    }
    
    public void setRestrictionPeriod(final RestrictionPeriod restrictionPeriod) {
        this.restrictionPeriod = restrictionPeriod;
    }
    
    public RestrictionPeriod getRestrictionPeriod() {
        return this.restrictionPeriod;
    }
}
