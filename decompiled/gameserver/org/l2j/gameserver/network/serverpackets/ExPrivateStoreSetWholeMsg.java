// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExPrivateStoreSetWholeMsg extends ServerPacket
{
    private final int _objectId;
    private final String _msg;
    
    public ExPrivateStoreSetWholeMsg(final Player player, final String msg) {
        this._objectId = player.getObjectId();
        this._msg = msg;
    }
    
    public ExPrivateStoreSetWholeMsg(final Player player) {
        this(player, player.getSellList().getTitle());
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PRIVATE_STORE_WHOLE_MSG);
        this.writeInt(this._objectId);
        this.writeString((CharSequence)this._msg);
    }
}
