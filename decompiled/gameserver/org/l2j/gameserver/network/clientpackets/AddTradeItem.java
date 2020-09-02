// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.network.serverpackets.trade.TradeOtherAdd;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.TradeUpdate;
import org.l2j.gameserver.network.serverpackets.trade.TradeOwnAdd;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.World;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class AddTradeItem extends ClientPacket
{
    private static final Logger LOGGER;
    private int tradeId;
    private int objectId;
    private long count;
    
    public void readImpl() {
        this.tradeId = this.readInt();
        this.objectId = this.readInt();
        this.count = this.readLong();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        final TradeList trade = player.getActiveTradeList();
        if (Objects.isNull(trade)) {
            AddTradeItem.LOGGER.warn("Character: {} requested item: {}  add without active trade: {}", new Object[] { player, this.objectId, this.tradeId });
            return;
        }
        final Player partner = trade.getPartner();
        if (Objects.isNull(partner) || Objects.isNull(World.getInstance().findPlayer(partner.getObjectId())) || Objects.isNull(partner.getActiveTradeList())) {
            if (Objects.nonNull(partner)) {
                AddTradeItem.LOGGER.warn("Character: {} requested invalid trade object: {}", (Object)player, (Object)this.objectId);
            }
            player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
            player.cancelActiveTrade();
            return;
        }
        if (trade.isConfirmed() || partner.getActiveTradeList().isConfirmed()) {
            player.sendPacket(SystemMessageId.YOU_MAY_NO_LONGER_ADJUST_ITEMS_IN_THE_TRADE_BECAUSE_THE_TRADE_HAS_BEEN_CONFIRMED);
            return;
        }
        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            player.cancelActiveTrade();
            return;
        }
        if (!player.validateItemManipulation(this.objectId, "trade")) {
            player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
            return;
        }
        final Item item1 = player.getInventory().getItemByObjectId(this.objectId);
        final TradeItem item2 = trade.addItem(this.objectId, this.count);
        if (item2 != null) {
            player.sendPacket(new TradeOwnAdd(1, item2));
            player.sendPacket(new TradeOwnAdd(2, item2));
            player.sendPacket(new TradeUpdate(1, null, null, 0L));
            player.sendPacket(new TradeUpdate(2, player, item2, item1.getCount() - item2.getCount()));
            partner.sendPacket(new TradeOtherAdd(1, item2));
            partner.sendPacket(new TradeOtherAdd(2, item2));
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AddTradeItem.class);
    }
}
