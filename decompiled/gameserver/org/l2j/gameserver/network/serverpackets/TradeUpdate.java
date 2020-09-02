// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.TradeItem;

public class TradeUpdate extends AbstractItemPacket
{
    private final int sendType;
    private final TradeItem item;
    private final long newCount;
    private final long count;
    
    public TradeUpdate(final int sendType, final Player player, final TradeItem item, final long count) {
        this.sendType = sendType;
        this.count = count;
        this.item = item;
        this.newCount = ((player == null) ? 0L : (player.getInventory().getItemByObjectId(item.getObjectId()).getCount() - item.getCount()));
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TRADE_UPDATE);
        this.writeByte((byte)this.sendType);
        this.writeInt(1);
        if (this.sendType == 2) {
            this.writeInt(1);
            this.writeShort((short)((this.newCount > 0L && this.item.getItem().isStackable()) ? 3 : 2));
            this.writeItem(this.item, this.count);
        }
    }
}
