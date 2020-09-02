// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.instancemanager.SellBuffsManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class PrivateStoreListSell extends AbstractItemPacket
{
    private final Player _player;
    private final Player _seller;
    
    public PrivateStoreListSell(final Player player, final Player seller) {
        this._player = player;
        this._seller = seller;
    }
    
    public void writeImpl(final GameClient client) {
        if (this._seller.isSellingBuffs()) {
            SellBuffsManager.getInstance().sendBuffMenu(this._player, this._seller, 0);
        }
        else {
            this.writeId(ServerPacketId.PRIVATE_STORE_LIST);
            this.writeInt(this._seller.getObjectId());
            this.writeInt((int)(this._seller.getSellList().isPackaged() ? 1 : 0));
            this.writeLong(this._player.getAdena());
            this.writeInt(0);
            this.writeInt(this._seller.getSellList().getItems().length);
            for (final TradeItem item : this._seller.getSellList().getItems()) {
                this.writeItem(item);
                this.writeLong(item.getPrice());
                this.writeLong(item.getItem().getReferencePrice() * 2L);
            }
        }
    }
}
