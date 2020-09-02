// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import java.util.HashSet;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.ItemRequest;
import java.util.Set;
import org.slf4j.Logger;

public final class RequestPrivateStoreBuy extends ClientPacket
{
    private static final Logger LOGGER;
    private static final int BATCH_LENGTH = 20;
    private int _storePlayerId;
    private Set<ItemRequest> _items;
    
    public RequestPrivateStoreBuy() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        this._storePlayerId = this.readInt();
        final int count = this.readInt();
        if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * 20 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this._items = new HashSet<ItemRequest>();
        for (int i = 0; i < count; ++i) {
            final int objectId = this.readInt();
            final long cnt = this.readLong();
            final long price = this.readLong();
            if (objectId < 1 || cnt < 1L || price < 0L) {
                this._items = null;
                throw new InvalidDataPacketException();
            }
            this._items.add(new ItemRequest(objectId, cnt, price));
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
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("privatestorebuy")) {
            player.sendMessage("You are buying items too fast.");
            return;
        }
        final WorldObject object = World.getInstance().findPlayer(this._storePlayerId);
        if (object == null) {
            return;
        }
        final Player storePlayer = (Player)object;
        if (!MathUtil.isInsideRadius3D(player, storePlayer, 250)) {
            return;
        }
        if (player.getInstanceWorld() != storePlayer.getInstanceWorld()) {
            return;
        }
        if (storePlayer.getPrivateStoreType() != PrivateStoreType.SELL && storePlayer.getPrivateStoreType() != PrivateStoreType.PACKAGE_SELL) {
            return;
        }
        final TradeList storeList = storePlayer.getSellList();
        if (storeList == null) {
            return;
        }
        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (storePlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL && storeList.getItemCount() > this._items.size()) {
            final String msgErr = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ((GameClient)this.client).getPlayer().getName());
            GameUtils.handleIllegalPlayerAction(((GameClient)this.client).getPlayer(), msgErr);
            return;
        }
        final int result = storeList.privateStoreBuy(player, this._items);
        if (result > 0) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            if (result > 1) {
                RequestPrivateStoreBuy.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), storePlayer.getName()));
            }
            return;
        }
        if (storeList.getItemCount() == 0) {
            storePlayer.setPrivateStoreType(PrivateStoreType.NONE);
            storePlayer.broadcastUserInfo();
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestPrivateStoreBuy.class);
    }
}
