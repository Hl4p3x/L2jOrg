// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.ManufactureItem;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class RecipeShopSellList extends ServerPacket
{
    private final Player _buyer;
    private final Player _manufacturer;
    
    public RecipeShopSellList(final Player buyer, final Player manufacturer) {
        this._buyer = buyer;
        this._manufacturer = manufacturer;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.RECIPE_SHOP_SELL_LIST);
        this.writeInt(this._manufacturer.getObjectId());
        this.writeInt((int)this._manufacturer.getCurrentMp());
        this.writeInt(this._manufacturer.getMaxMp());
        this.writeLong(this._buyer.getAdena());
        if (!this._manufacturer.hasManufactureShop()) {
            this.writeInt(0);
        }
        else {
            this.writeInt(this._manufacturer.getManufactureItems().size());
            for (final ManufactureItem temp : this._manufacturer.getManufactureItems().values()) {
                this.writeInt(temp.getRecipeId());
                this.writeInt(0);
                this.writeLong(temp.getCost());
                this.writeLong(0L);
                this.writeLong(0L);
                this.writeByte(0);
            }
        }
    }
}
