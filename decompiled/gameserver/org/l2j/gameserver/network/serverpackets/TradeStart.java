// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.PcCondOverride;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;

public final class TradeStart extends AbstractItemPacket
{
    private static final byte PARTNER_INFO = 1;
    private static final byte ITEMS_INFO = 2;
    private Player partner;
    private Collection<Item> items;
    private final int type;
    private int mask;
    
    private TradeStart(final int type, final Player player, final Player partner) {
        this.mask = 0;
        this.type = type;
        this.partner = partner;
        if (Objects.nonNull(partner)) {
            if (player.isFriend(partner)) {
                this.mask |= 0x1;
            }
            if (player.isInSameClan(partner)) {
                this.mask |= 0x2;
            }
            if (player.hasMentorRelationship(partner)) {
                this.mask |= 0x4;
            }
            if (player.isInSameAlly(partner)) {
                this.mask |= 0x8;
            }
            if (partner.isGM()) {
                this.mask |= 0x10;
            }
        }
    }
    
    private TradeStart(final byte type, final Player player) {
        this.mask = 0;
        this.type = type;
        this.items = player.getInventory().getAvailableItems(true, player.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && Config.GM_TRADE_RESTRICTED_ITEMS, false);
    }
    
    public void writeImpl(final GameClient client) throws InvalidDataPacketException {
        if (client.getPlayer().getActiveTradeList() == null) {
            throw new InvalidDataPacketException();
        }
        this.writeId(ServerPacketId.TRADE_START);
        this.writeByte(this.type);
        if (this.type == 2) {
            this.writeItems();
        }
        else {
            this.writePatner();
        }
    }
    
    private void writePatner() {
        this.writeInt(this.partner.getObjectId());
        this.writeByte(this.mask);
        if ((this.mask & 0x10) == 0x0) {
            this.writeByte(this.partner.getLevel());
        }
    }
    
    private void writeItems() {
        this.writeInt(this.items.size());
        this.writeInt(this.items.size());
        for (final Item item : this.items) {
            this.writeItem(item);
        }
    }
    
    public static TradeStart partnerInfo(final Player player, final Player partner) {
        return new TradeStart(1, player, partner);
    }
    
    public static TradeStart itemsInfo(final Player player) {
        return new TradeStart((byte)2, player);
    }
}
