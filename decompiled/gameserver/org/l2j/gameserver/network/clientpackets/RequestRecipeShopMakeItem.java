// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public final class RequestRecipeShopMakeItem extends ClientPacket
{
    private int _id;
    private int _recipeId;
    private long _unknown;
    
    public void readImpl() {
        this._id = this.readInt();
        this._recipeId = this.readInt();
        this._unknown = this.readLong();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getManufacture().tryPerformAction("RecipeShopMake")) {
            return;
        }
        final Player manufacturer = World.getInstance().findPlayer(this._id);
        if (manufacturer == null) {
            return;
        }
        if (manufacturer.getInstanceWorld() != activeChar.getInstanceWorld()) {
            return;
        }
        if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE) {
            activeChar.sendMessage("You cannot create items while trading.");
            return;
        }
        if (manufacturer.getPrivateStoreType() != PrivateStoreType.MANUFACTURE) {
            return;
        }
        if (activeChar.isCrafting() || manufacturer.isCrafting()) {
            activeChar.sendMessage("You are currently in Craft Mode.");
            return;
        }
        if (GameUtils.checkIfInRange(150, activeChar, manufacturer, true)) {
            RecipeController.getInstance().requestManufactureItem(manufacturer, this._recipeId, activeChar);
        }
    }
}
