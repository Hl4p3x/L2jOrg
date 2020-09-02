// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public class RecipeList
{
    private final int _id;
    private final int _level;
    private final int _recipeId;
    private final String _recipeName;
    private final int _successRate;
    private final int _itemId;
    private final int _count;
    private final boolean _isDwarvenRecipe;
    private Recipe[] _recipes;
    private RecipeStat[] _statUse;
    private RecipeStat[] _altStatChange;
    private int _rareItemId;
    private int _rareCount;
    private int _rarity;
    
    public RecipeList(final StatsSet set, final boolean haveRare) {
        this._recipes = new Recipe[0];
        this._statUse = new RecipeStat[0];
        this._altStatChange = new RecipeStat[0];
        this._id = set.getInt("id");
        this._level = set.getInt("craftLevel");
        this._recipeId = set.getInt("recipeId");
        this._recipeName = set.getString("recipeName");
        this._successRate = set.getInt("successRate");
        this._itemId = set.getInt("itemId");
        this._count = set.getInt("count");
        if (haveRare) {
            this._rareItemId = set.getInt("rareItemId");
            this._rareCount = set.getInt("rareCount");
            this._rarity = set.getInt("rarity");
        }
        this._isDwarvenRecipe = set.getBoolean("isDwarvenRecipe");
    }
    
    public void addRecipe(final Recipe recipe) {
        final int len = this._recipes.length;
        final Recipe[] tmp = new Recipe[len + 1];
        System.arraycopy(this._recipes, 0, tmp, 0, len);
        tmp[len] = recipe;
        this._recipes = tmp;
    }
    
    public void addStatUse(final RecipeStat statUse) {
        final int len = this._statUse.length;
        final RecipeStat[] tmp = new RecipeStat[len + 1];
        System.arraycopy(this._statUse, 0, tmp, 0, len);
        tmp[len] = statUse;
        this._statUse = tmp;
    }
    
    public void addAltStatChange(final RecipeStat statChange) {
        final int len = this._altStatChange.length;
        final RecipeStat[] tmp = new RecipeStat[len + 1];
        System.arraycopy(this._altStatChange, 0, tmp, 0, len);
        tmp[len] = statChange;
        this._altStatChange = tmp;
    }
    
    public int getId() {
        return this._id;
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public int getRecipeId() {
        return this._recipeId;
    }
    
    public String getRecipeName() {
        return this._recipeName;
    }
    
    public int getSuccessRate() {
        return this._successRate;
    }
    
    public int getItemId() {
        return this._itemId;
    }
    
    public int getCount() {
        return this._count;
    }
    
    public int getRareItemId() {
        return this._rareItemId;
    }
    
    public int getRareCount() {
        return this._rareCount;
    }
    
    public int getRarity() {
        return this._rarity;
    }
    
    public boolean isDwarvenRecipe() {
        return this._isDwarvenRecipe;
    }
    
    public Recipe[] getRecipes() {
        return this._recipes;
    }
    
    public RecipeStat[] getStatUse() {
        return this._statUse;
    }
    
    public RecipeStat[] getAltStatChange() {
        return this._altStatChange;
    }
}
