// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.trade;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.TradeItem;

public final class TradeOtherAdd extends TradeAdd
{
    public TradeOtherAdd(final int type, final TradeItem item) {
        super(type, item);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TRADE_OTHER_ADD);
        this.writeItemAdd();
    }
}
