// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.PrivateStoreManageListBuy;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.Collection;
import java.util.Arrays;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.data.xml.impl.EnsoulData;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.TradeItem;

public final class SetPrivateStoreListBuy extends ClientPacket
{
    private TradeItem[] _items;
    
    public SetPrivateStoreListBuy() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        final int count = this.readInt();
        if (count < 1 || count > Config.MAX_ITEM_IN_PACKET) {
            throw new InvalidDataPacketException();
        }
        this._items = new TradeItem[count];
        for (int i = 0; i < count; ++i) {
            final int itemId = this.readInt();
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);
            if (template == null) {
                this._items = null;
                throw new InvalidDataPacketException();
            }
            final int enchantLevel = this.readShort();
            this.readShort();
            final long cnt = this.readLong();
            final long price = this.readLong();
            if (itemId < 1 || cnt < 1L || price < 0L) {
                this._items = null;
                throw new InvalidDataPacketException();
            }
            final int option1 = this.readInt();
            final int option2 = this.readInt();
            final short attackAttributeId = this.readShort();
            final int attackAttributeValue = this.readShort();
            final int defenceFire = this.readShort();
            final int defenceWater = this.readShort();
            final int defenceWind = this.readShort();
            final int defenceEarth = this.readShort();
            final int defenceHoly = this.readShort();
            final int defenceDark = this.readShort();
            this.readInt();
            final EnsoulOption[] soulCrystalOptions = new EnsoulOption[this.readByte()];
            for (int k = 0; k < soulCrystalOptions.length; ++k) {
                soulCrystalOptions[k] = EnsoulData.getInstance().getOption(this.readInt());
            }
            final EnsoulOption[] soulCrystalSpecialOptions = new EnsoulOption[this.readByte()];
            for (int j = 0; j < soulCrystalSpecialOptions.length; ++j) {
                soulCrystalSpecialOptions[j] = EnsoulData.getInstance().getOption(this.readInt());
            }
            final TradeItem item = new TradeItem(template, cnt, price);
            item.setEnchant(enchantLevel);
            item.setAugmentation(option1, option2);
            item.setAttackElementType(AttributeType.findByClientId(attackAttributeId));
            item.setAttackElementPower(attackAttributeValue);
            item.setElementDefAttr(AttributeType.FIRE, defenceFire);
            item.setElementDefAttr(AttributeType.WATER, defenceWater);
            item.setElementDefAttr(AttributeType.WIND, defenceWind);
            item.setElementDefAttr(AttributeType.EARTH, defenceEarth);
            item.setElementDefAttr(AttributeType.HOLY, defenceHoly);
            item.setElementDefAttr(AttributeType.DARK, defenceDark);
            item.setSoulCrystalOptions(Arrays.asList(soulCrystalOptions));
            item.setSoulCrystalSpecialOptions(Arrays.asList(soulCrystalSpecialOptions));
            this._items[i] = item;
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (this._items == null) {
            player.setPrivateStoreType(PrivateStoreType.NONE);
            player.broadcastUserInfo();
            return;
        }
        if (!player.getAccessLevel().allowTransaction()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel()) {
            player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            player.sendPacket(new PrivateStoreManageListBuy(1, player));
            player.sendPacket(new PrivateStoreManageListBuy(2, player));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (player.isInsideZone(ZoneType.NO_STORE)) {
            player.sendPacket(new PrivateStoreManageListBuy(1, player));
            player.sendPacket(new PrivateStoreManageListBuy(2, player));
            player.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final TradeList tradeList = player.getBuyList();
        tradeList.clear();
        if (this._items.length > player.getPrivateBuyStoreLimit()) {
            player.sendPacket(new PrivateStoreManageListBuy(1, player));
            player.sendPacket(new PrivateStoreManageListBuy(2, player));
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        long totalCost = 0L;
        for (final TradeItem i : this._items) {
            if (Inventory.MAX_ADENA / i.getCount() < i.getPrice()) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;, player.getName(), player.getAccountName(), Inventory.MAX_ADENA));
                return;
            }
            tradeList.addItemByItemId(i.getItem().getId(), i.getCount(), i.getPrice());
            totalCost += i.getCount() * i.getPrice();
            if (totalCost > Inventory.MAX_ADENA) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;, player.getName(), player.getAccountName(), Inventory.MAX_ADENA));
                return;
            }
        }
        if (totalCost > player.getAdena()) {
            player.sendPacket(new PrivateStoreManageListBuy(1, player));
            player.sendPacket(new PrivateStoreManageListBuy(2, player));
            player.sendPacket(SystemMessageId.THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE);
            return;
        }
        player.sitDown();
        player.setPrivateStoreType(PrivateStoreType.BUY);
        player.broadcastUserInfo();
        player.broadcastPacket(new PrivateStoreMsgBuy(player));
    }
}
