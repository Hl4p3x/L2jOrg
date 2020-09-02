// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.Iterator;
import java.util.Map;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.data.database.data.SeedProduction;
import java.util.HashMap;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;

public class RequestBuySeed extends ClientPacket
{
    private static final int BATCH_LENGTH = 12;
    private int _manorId;
    private List<ItemHolder> _items;
    
    public RequestBuySeed() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        this._manorId = this.readInt();
        final int count = this.readInt();
        if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * 12 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this._items = new ArrayList<ItemHolder>(count);
        for (int i = 0; i < count; ++i) {
            final int itemId = this.readInt();
            final long cnt = this.readLong();
            if (cnt < 1L || itemId < 1) {
                this._items = null;
                throw new InvalidDataPacketException();
            }
            this._items.add(new ItemHolder(itemId, cnt));
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getManor().tryPerformAction("BuySeed")) {
            player.sendMessage("You are buying seeds too fast!");
            return;
        }
        if (this._items == null) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final CastleManorManager manor = CastleManorManager.getInstance();
        if (manor.isUnderMaintenance()) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Castle castle = CastleManager.getInstance().getCastleById(this._manorId);
        if (castle == null) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Npc manager = player.getLastFolkNPC();
        if (!(manager instanceof Merchant) || !manager.canInteract(player) || manager.getParameters().getInt("manor_id", -1) != this._manorId) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        long totalPrice = 0L;
        int slots = 0;
        int totalWeight = 0;
        final Map<Integer, SeedProduction> _productInfo = new HashMap<Integer, SeedProduction>();
        for (final ItemHolder ih : this._items) {
            final SeedProduction sp = manor.getSeedProduct(this._manorId, ih.getId(), false);
            if (sp == null || sp.getPrice() <= 0L || sp.getAmount() < ih.getCount() || Inventory.MAX_ADENA / ih.getCount() < sp.getPrice()) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            totalPrice += sp.getPrice() * ih.getCount();
            if (totalPrice > Inventory.MAX_ADENA) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;, player.getName(), player.getAccountName(), Inventory.MAX_ADENA));
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(ih.getId());
            totalWeight += (int)(ih.getCount() * template.getWeight());
            if (!template.isStackable()) {
                slots += (int)ih.getCount();
            }
            else if (player.getInventory().getItemByItemId(ih.getId()) == null) {
                ++slots;
            }
            _productInfo.put(ih.getId(), sp);
        }
        if (!player.getInventory().validateWeight(totalWeight)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return;
        }
        if (!player.getInventory().validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            return;
        }
        if (totalPrice < 0L || player.getAdena() < totalPrice) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return;
        }
        for (final ItemHolder i : this._items) {
            final SeedProduction sp = _productInfo.get(i.getId());
            final long price = sp.getPrice() * i.getCount();
            if (!sp.decreaseAmount(i.getCount()) || !player.reduceAdena("Buy", price, player, false)) {
                totalPrice -= price;
            }
            else {
                player.addItem("Buy", i.getId(), i.getCount(), manager, true);
            }
        }
        if (totalPrice > 0L) {
            castle.addToTreasuryNoTax(totalPrice);
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_ADENA_DISAPPEARED);
            sm.addLong(totalPrice);
            player.sendPacket(sm);
        }
    }
}
