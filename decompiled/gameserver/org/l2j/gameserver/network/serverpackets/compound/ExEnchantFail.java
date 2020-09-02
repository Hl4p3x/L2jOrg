// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.compound;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExEnchantFail extends ServerPacket
{
    public static final ExEnchantFail STATIC_PACKET;
    private final int _itemOne;
    private final int _itemTwo;
    
    public ExEnchantFail(final int itemOne, final int itemTwo) {
        this._itemOne = itemOne;
        this._itemTwo = itemTwo;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ENCHANT_FAIL);
        this.writeInt(this._itemOne);
        this.writeInt(this._itemTwo);
    }
    
    static {
        STATIC_PACKET = new ExEnchantFail(0, 0);
    }
}
