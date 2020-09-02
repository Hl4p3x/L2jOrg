// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.alchemy;

import java.util.Collections;
import java.util.HashSet;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Set;

public class AlchemyCraftData
{
    private final int _id;
    private final int _level;
    private final int _grade;
    private final int _category;
    private Set<ItemHolder> _ingredients;
    private ItemHolder _productionSuccess;
    private ItemHolder _productionFailure;
    
    public AlchemyCraftData(final StatsSet set) {
        this._id = set.getInt("id");
        this._level = set.getInt("level");
        this._grade = set.getInt("grade");
        this._category = set.getInt("category");
    }
    
    public int getId() {
        return this._id;
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public int getGrade() {
        return this._grade;
    }
    
    public int getCategory() {
        return this._category;
    }
    
    public void addIngredient(final ItemHolder ingredient) {
        if (this._ingredients == null) {
            this._ingredients = new HashSet<ItemHolder>();
        }
        this._ingredients.add(ingredient);
    }
    
    public Set<ItemHolder> getIngredients() {
        return (this._ingredients != null) ? this._ingredients : Collections.emptySet();
    }
    
    public ItemHolder getProductionSuccess() {
        return this._productionSuccess;
    }
    
    public void setProductionSuccess(final ItemHolder item) {
        this._productionSuccess = item;
    }
    
    public ItemHolder getProductionFailure() {
        return this._productionFailure;
    }
    
    public void setProductionFailure(final ItemHolder item) {
        this._productionFailure = item;
    }
}
