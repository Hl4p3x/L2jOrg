// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreSetWholeMsg;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;

public class SetPrivateStoreWholeMsg extends ClientPacket
{
    private static final int MAX_MSG_LENGTH = 29;
    private String _msg;
    
    public void readImpl() {
        this._msg = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.getSellList() == null) {
            return;
        }
        if (this._msg != null && this._msg.length() > 29) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            return;
        }
        player.getSellList().setTitle(this._msg);
        ((GameClient)this.client).sendPacket(new ExPrivateStoreSetWholeMsg(player));
    }
}
