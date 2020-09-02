// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.gameserver.model.ManufactureItem;
import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.model.actor.instance.Player;

public class RecipeShopManageList extends ServerPacket
{
    private final Player _seller;
    private final boolean _isDwarven;
    private RecipeList[] _recipes;
    
    public RecipeShopManageList(final Player seller, final boolean isDwarven) {
        this._seller = seller;
        this._isDwarven = isDwarven;
        if (this._isDwarven && this._seller.hasDwarvenCraft()) {
            this._recipes = this._seller.getDwarvenRecipeBook();
        }
        else {
            this._recipes = this._seller.getCommonRecipeBook();
        }
        if (this._seller.hasManufactureShop()) {
            final Iterator<ManufactureItem> it = this._seller.getManufactureItems().values().iterator();
            while (it.hasNext()) {
                final ManufactureItem item = it.next();
                if (item.isDwarven() != this._isDwarven || !seller.hasRecipeList(item.getRecipeId())) {
                    it.remove();
                }
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.RECIPE_SHOP_MANAGE_LIST);
        this.writeInt(this._seller.getObjectId());
        this.writeInt((int)this._seller.getAdena());
        this.writeInt((int)(this._isDwarven ? 0 : 1));
        if (this._recipes == null) {
            this.writeInt(0);
        }
        else {
            this.writeInt(this._recipes.length);
            for (int i = 0; i < this._recipes.length; ++i) {
                final RecipeList temp = this._recipes[i];
                this.writeInt(temp.getId());
                this.writeInt(i + 1);
            }
        }
        if (!this._seller.hasManufactureShop()) {
            this.writeInt(0);
        }
        else {
            this.writeInt(this._seller.getManufactureItems().size());
            for (final ManufactureItem item : this._seller.getManufactureItems().values()) {
                this.writeInt(item.getRecipeId());
                this.writeInt(0);
                this.writeLong(item.getCost());
            }
        }
    }
}
