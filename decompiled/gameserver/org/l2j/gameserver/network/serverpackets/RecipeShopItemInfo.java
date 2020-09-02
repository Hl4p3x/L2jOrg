// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class RecipeShopItemInfo extends ServerPacket
{
    private final Player _player;
    private final int _recipeId;
    
    public RecipeShopItemInfo(final Player player, final int recipeId) {
        this._player = player;
        this._recipeId = recipeId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.RECIPE_SHOP_ITEM_INFO);
        this.writeInt(this._player.getObjectId());
        this.writeInt(this._recipeId);
        this.writeInt((int)this._player.getCurrentMp());
        this.writeInt(this._player.getMaxMp());
        this.writeInt(-1);
        this.writeLong(0L);
        this.writeByte((byte)0);
        this.writeLong(0L);
    }
}
