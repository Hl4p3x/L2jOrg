// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.GameClient;

public final class RequestRecipeItemMakeSelf extends ClientPacket
{
    private int _id;
    
    public void readImpl() {
        this._id = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getManufacture().tryPerformAction("RecipeMakeSelf")) {
            return;
        }
        if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE) {
            activeChar.sendMessage("You cannot create items while trading.");
            return;
        }
        if (activeChar.isCrafting()) {
            activeChar.sendMessage("You are currently in Craft Mode.");
            return;
        }
        RecipeController.getInstance().requestMakeItem(activeChar, this._id);
    }
}
