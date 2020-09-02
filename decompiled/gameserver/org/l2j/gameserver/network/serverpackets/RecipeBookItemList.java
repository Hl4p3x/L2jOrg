// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.RecipeList;

public class RecipeBookItemList extends ServerPacket
{
    private final boolean _isDwarvenCraft;
    private final int _maxMp;
    private RecipeList[] _recipes;
    
    public RecipeBookItemList(final boolean isDwarvenCraft, final int maxMp) {
        this._isDwarvenCraft = isDwarvenCraft;
        this._maxMp = maxMp;
    }
    
    public void addRecipes(final RecipeList[] recipeBook) {
        this._recipes = recipeBook;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.RECIPE_BOOK_ITEM_LIST);
        this.writeInt((int)(this._isDwarvenCraft ? 0 : 1));
        this.writeInt(this._maxMp);
        if (this._recipes == null) {
            this.writeInt(0);
        }
        else {
            this.writeInt(this._recipes.length);
            for (int i = 0; i < this._recipes.length; ++i) {
                this.writeInt(this._recipes[i].getId());
                this.writeInt(i + 1);
            }
        }
    }
}
