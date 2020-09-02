// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.RecipeShopSellList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;

public final class RequestRecipeShopManagePrev extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (player.isAlikeDead() || !GameUtils.isPlayer(player.getTarget())) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        player.sendPacket(new RecipeShopSellList(player, player.getTarget().getActingPlayer()));
    }
}
