// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class PrivateStoreMsgSell extends ServerPacket
{
    private final int _objId;
    private String _storeMsg;
    
    public PrivateStoreMsgSell(final Player player) {
        this._objId = player.getObjectId();
        if (player.getSellList() != null || player.isSellingBuffs()) {
            this._storeMsg = player.getSellList().getTitle();
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PRIVATE_STORE_MSG);
        this.writeInt(this._objId);
        this.writeString((CharSequence)this._storeMsg);
    }
}
