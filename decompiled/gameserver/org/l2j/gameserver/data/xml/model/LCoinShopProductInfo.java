// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.model;

import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;

public class LCoinShopProductInfo
{
    private int id;
    private Category category;
    private int limitPerDay;
    private int minLevel;
    private boolean isEvent;
    private List<ItemHolder> ingredients;
    private ItemHolder production;
    private int remainServerItemAmount;
    
    public LCoinShopProductInfo(final int id, final Category category, final int limitPerDay, final int minLevel, final boolean isEvent, final List<ItemHolder> ingredients, final ItemHolder production, final int remainServerItemAmount) {
        this.id = id;
        this.category = category;
        this.limitPerDay = limitPerDay;
        this.minLevel = minLevel;
        this.isEvent = isEvent;
        this.ingredients = ingredients;
        this.production = production;
        this.remainServerItemAmount = remainServerItemAmount;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getRemainAmount() {
        return this.limitPerDay;
    }
    
    public int getRemainTime() {
        return -1;
    }
    
    public int getRemainServerItemAmount() {
        return this.remainServerItemAmount;
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public int getLimitPerDay() {
        return this.limitPerDay;
    }
    
    public int getMinLevel() {
        return this.minLevel;
    }
    
    public boolean isEvent() {
        return this.isEvent;
    }
    
    public List<ItemHolder> getIngredients() {
        return this.ingredients;
    }
    
    public ItemHolder getProduction() {
        return this.production;
    }
    
    public enum Category
    {
        Equip, 
        Special, 
        Supplies, 
        Other;
    }
}
