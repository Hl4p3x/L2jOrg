// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.RecipeItemMakeInfo;
import org.l2j.gameserver.network.GameClient;

public final class RequestRecipeItemMakeInfo extends ClientPacket
{
    private int _id;
    
    public void readImpl() {
        this._id = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new RecipeItemMakeInfo(this._id, player));
    }
}
