// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.ItemRequest;
import org.slf4j.Logger;

public final class RequestPrivateStoreSell extends ClientPacket
{
    private static final Logger LOGGER;
    private int _storePlayerId;
    private ItemRequest[] _items;
    
    public RequestPrivateStoreSell() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        this._storePlayerId = this.readInt();
        final int itemsCount = this.readInt();
        if (itemsCount <= 0 || itemsCount > Config.MAX_ITEM_IN_PACKET) {
            throw new InvalidDataPacketException();
        }
        this._items = new ItemRequest[itemsCount];
        for (int i = 0; i < itemsCount; ++i) {
            final int slot = this.readInt();
            final int itemId = this.readInt();
            this.readShort();
            this.readShort();
            final long count = this.readLong();
            final long price = this.readLong();
            this.readInt();
            this.readInt();
            this.readInt();
            for (int soulCrystals = this.readByte(), s = 0; s < soulCrystals; ++s) {
                this.readInt();
            }
            for (int soulCrystals2 = this.readByte(), s2 = 0; s2 < soulCrystals2; ++s2) {
                this.readInt();
            }
            if (itemId < 1 || count < 1L || price < 0L) {
                this._items = null;
                throw new InvalidDataPacketException();
            }
            this._items[i] = new ItemRequest(slot, itemId, count, price);
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (this._items == null) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (player.isOnEvent()) {
            player.sendMessage("You cannot open a private store while participating in an event.");
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("privatestoresell")) {
            player.sendMessage("You are selling items too fast.");
            return;
        }
        final Player storePlayer = World.getInstance().findPlayer(this._storePlayerId);
        if (storePlayer == null || !MathUtil.isInsideRadius3D(player, storePlayer, 250)) {
            return;
        }
        if (player.getInstanceWorld() != storePlayer.getInstanceWorld()) {
            return;
        }
        if (storePlayer.getPrivateStoreType() != PrivateStoreType.BUY) {
            return;
        }
        final TradeList storeList = storePlayer.getBuyList();
        if (storeList == null) {
            return;
        }
        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (!storeList.privateStoreSell(player, this._items)) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            RequestPrivateStoreSell.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), storePlayer.getName()));
            return;
        }
        if (storeList.getItemCount() == 0) {
            storePlayer.setPrivateStoreType(PrivateStoreType.NONE);
            storePlayer.broadcastUserInfo();
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestPrivateStoreSell.class);
    }
}
