// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;

public final class SetPrivateStoreMsgBuy extends ClientPacket
{
    private static final int MAX_MSG_LENGTH = 29;
    private String _storeMsg;
    
    public void readImpl() {
        this._storeMsg = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.getBuyList() == null) {
            return;
        }
        if (this._storeMsg != null && this._storeMsg.length() > 29) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            return;
        }
        player.getBuyList().setTitle(this._storeMsg);
        ((GameClient)this.client).sendPacket(new PrivateStoreMsgBuy(player));
    }
}
