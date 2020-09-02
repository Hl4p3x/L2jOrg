// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.trade;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.TradeItem;

public final class TradeOwnAdd extends TradeAdd
{
    public TradeOwnAdd(final int type, final TradeItem item) {
        super(type, item);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TRADE_OWN_ADD);
        this.writeItemAdd();
    }
}
