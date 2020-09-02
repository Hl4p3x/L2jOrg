// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.RecipeShopItemInfo;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public final class RequestRecipeShopMakeInfo extends ClientPacket
{
    private int _playerObjectId;
    private int _recipeId;
    
    public void readImpl() {
        this._playerObjectId = this.readInt();
        this._recipeId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Player shop = World.getInstance().findPlayer(this._playerObjectId);
        if (shop == null || shop.getPrivateStoreType() != PrivateStoreType.MANUFACTURE) {
            return;
        }
        ((GameClient)this.client).sendPacket(new RecipeShopItemInfo(shop, this._recipeId));
    }
}
