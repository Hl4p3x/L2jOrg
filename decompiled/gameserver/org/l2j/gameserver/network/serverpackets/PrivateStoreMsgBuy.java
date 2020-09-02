// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class PrivateStoreMsgBuy extends ServerPacket
{
    private final int _objId;
    private String _storeMsg;
    
    public PrivateStoreMsgBuy(final Player player) {
        this._objId = player.getObjectId();
        if (player.getBuyList() != null) {
            this._storeMsg = player.getBuyList().getTitle();
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PRIVATE_STORE_BUY_MSG);
        this.writeInt(this._objId);
        this.writeString((CharSequence)this._storeMsg);
    }
}
