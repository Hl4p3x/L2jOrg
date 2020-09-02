// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;

public final class RequestRecipeBookOpen extends ClientPacket
{
    private boolean _isDwarvenCraft;
    
    public void readImpl() {
        this._isDwarvenCraft = (this.readInt() == 0);
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.isCastingNow()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOUR_RECIPE_BOOK_MAY_NOT_BE_ACCESSED_WHILE_USING_A_SKILL);
            return;
        }
        if (activeChar.getActiveRequester() != null) {
            activeChar.sendMessage("You may not alter your recipe book while trading.");
            return;
        }
        RecipeController.getInstance().requestBookOpen(activeChar, this._isDwarvenCraft);
    }
}
