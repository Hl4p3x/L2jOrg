// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PrivateStoreMsgSell;
import org.l2j.gameserver.util.GameUtils;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;

public class SetPrivateStoreMsgSell extends ClientPacket
{
    private static final int MAX_MSG_LENGTH = 29;
    private String _storeMsg;
    
    public void readImpl() {
        this._storeMsg = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player) || Objects.isNull(player.getSellList())) {
            return;
        }
        if (Objects.nonNull(this._storeMsg) && this._storeMsg.length() > 29) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return;
        }
        player.getSellList().setTitle(this._storeMsg);
        ((GameClient)this.client).sendPacket(new PrivateStoreMsgSell(player));
    }
}
