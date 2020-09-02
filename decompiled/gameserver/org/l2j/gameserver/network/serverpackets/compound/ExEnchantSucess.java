// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.compound;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExEnchantSucess extends ServerPacket
{
    private final int _itemId;
    
    public ExEnchantSucess(final int itemId) {
        this._itemId = itemId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ENCHANT_SUCCESS);
        this.writeInt(this._itemId);
    }
}
