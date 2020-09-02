// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.data.xml.impl.RecipeData;

public class ManufactureItem
{
    private final int _recipeId;
    private final long _cost;
    private final boolean _isDwarven;
    
    public ManufactureItem(final int recipeId, final long cost) {
        this._recipeId = recipeId;
        this._cost = cost;
        this._isDwarven = RecipeData.getInstance().getRecipeList(this._recipeId).isDwarvenRecipe();
    }
    
    public int getRecipeId() {
        return this._recipeId;
    }
    
    public long getCost() {
        return this._cost;
    }
    
    public boolean isDwarven() {
        return this._isDwarven;
    }
}
