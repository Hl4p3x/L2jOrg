// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.gameserver.data.database.data.CropProcure;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import java.util.HashMap;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Map;

public class SellListProcure extends ServerPacket
{
    private final long _money;
    private final Map<Item, Long> _sellList;
    
    public SellListProcure(final Player player, final int castleId) {
        this._sellList = new HashMap<Item, Long>();
        this._money = player.getAdena();
        for (final CropProcure c : CastleManorManager.getInstance().getCropProcure(castleId, false)) {
            final Item item = player.getInventory().getItemByItemId(c.getSeedId());
            if (item != null && c.getAmount() > 0L) {
                this._sellList.put(item, c.getAmount());
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SELL_LIST_PROCURE);
        this.writeLong(this._money);
        this.writeInt(0);
        this.writeShort((short)this._sellList.size());
        for (final Item item : this._sellList.keySet()) {
            this.writeShort((short)item.getTemplate().getType1());
            this.writeInt(item.getObjectId());
            this.writeInt(item.getDisplayId());
            this.writeLong((long)this._sellList.get(item));
            this.writeShort((short)item.getTemplate().getType2());
            this.writeShort((short)0);
            this.writeLong(0L);
        }
    }
}
