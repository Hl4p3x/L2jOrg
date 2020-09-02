// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;

public class RequestRecipeShopMessageSet extends ClientPacket
{
    private static final int MAX_MSG_LENGTH = 29;
    private String _name;
    
    public void readImpl() {
        this._name = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (this._name != null && this._name.length() > 29) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            return;
        }
        if (player.hasManufactureShop()) {
            player.setStoreName(this._name);
        }
    }
}
