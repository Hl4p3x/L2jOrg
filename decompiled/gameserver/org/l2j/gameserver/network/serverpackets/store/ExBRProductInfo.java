// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.store;

import java.util.Iterator;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreItem;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreProduct;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExBRProductInfo extends ServerPacket
{
    private final L2StoreProduct item;
    private final int points;
    private final long adenas;
    private final long coins;
    
    public ExBRProductInfo(final L2StoreProduct item, final Player player) {
        this.item = item;
        this.points = player.getNCoins();
        this.adenas = player.getAdena();
        this.coins = player.getInventory().getInventoryItemCount(23805, -1);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BR_PRODUCT_INFO_ACK);
        this.writeInt(this.item.getId());
        this.writeInt(this.item.getPrice());
        this.writeInt(this.item.getItems().size());
        for (final L2StoreItem item : this.item.getItems()) {
            this.writeInt(item.getId());
            this.writeInt((int)item.getCount());
            this.writeInt(item.getWeight());
            this.writeInt(item.isTradable());
        }
        this.writeLong(this.adenas);
        this.writeLong((long)this.points);
        this.writeLong(this.coins);
    }
}
