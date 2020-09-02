// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.RecipeBookItemList;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.network.GameClient;

public final class RequestRecipeBookDestroy extends ClientPacket
{
    private int _recipeID;
    
    public void readImpl() {
        this._recipeID = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("RecipeDestroy")) {
            return;
        }
        final RecipeList rp = RecipeData.getInstance().getRecipeList(this._recipeID);
        if (rp == null) {
            return;
        }
        activeChar.unregisterRecipeList(this._recipeID);
        final RecipeBookItemList response = new RecipeBookItemList(rp.isDwarvenRecipe(), activeChar.getMaxMp());
        if (rp.isDwarvenRecipe()) {
            response.addRecipes(activeChar.getDwarvenRecipeBook());
        }
        else {
            response.addRecipes(activeChar.getCommonRecipeBook());
        }
        activeChar.sendPacket(response);
    }
}
