// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreBuyingResult;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreSellingResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public class TradeList
{
    private static final Logger LOGGER;
    private final Player owner;
    private final Set<TradeItem> items;
    private Player partner;
    private String title;
    private boolean packaged;
    private boolean confirmed;
    private boolean locked;
    
    public TradeList(final Player owner, final Player partner) {
        this.items = (Set<TradeItem>)ConcurrentHashMap.newKeySet();
        this.confirmed = false;
        this.locked = false;
        this.owner = owner;
        this.partner = partner;
    }
    
    public TradeList(final Player owner) {
        this.items = (Set<TradeItem>)ConcurrentHashMap.newKeySet();
        this.confirmed = false;
        this.locked = false;
        this.owner = owner;
    }
    
    public Player getOwner() {
        return this.owner;
    }
    
    public Player getPartner() {
        return this.partner;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public boolean isConfirmed() {
        return this.confirmed;
    }
    
    public boolean isPackaged() {
        return this.packaged;
    }
    
    public void setPackaged(final boolean value) {
        this.packaged = value;
    }
    
    public TradeItem[] getItems() {
        return this.items.toArray(TradeItem[]::new);
    }
    
    public Collection<TradeItem> getAvailableItems(final PlayerInventory inventory) {
        final List<TradeItem> list = new LinkedList<TradeItem>();
        for (TradeItem item : this.items) {
            item = new TradeItem(item, item.getCount(), item.getPrice());
            inventory.adjustAvailableItem(item);
            list.add(item);
        }
        return list;
    }
    
    public int getItemCount() {
        return this.items.size();
    }
    
    public TradeItem adjustAvailableItem(final Item item) {
        if (item.isStackable()) {
            for (final TradeItem exclItem : this.items) {
                if (exclItem.getItem().getId() == item.getId()) {
                    return (item.getCount() <= exclItem.getCount()) ? null : new TradeItem(item, item.getCount() - exclItem.getCount(), item.getReferencePrice());
                }
            }
        }
        return new TradeItem(item, item.getCount(), item.getReferencePrice());
    }
    
    public TradeItem addItem(final int objectId, final long count) {
        return this.addItem(objectId, count, 0L);
    }
    
    public synchronized TradeItem addItem(final int objectId, final long count, final long price) {
        if (this.locked) {
            TradeList.LOGGER.warn("{} Attempt to modify locked TradeList!", (Object)this.owner);
            return null;
        }
        final WorldObject o = World.getInstance().findObject(objectId);
        if (!GameUtils.isItem(o)) {
            TradeList.LOGGER.warn("{} Trying to add something other than an item!: ObjectId {} ", (Object)this.owner, (Object)objectId);
            return null;
        }
        final Item item = (Item)o;
        if ((!item.isTradeable() && (!this.owner.isGM() || !Config.GM_TRADE_RESTRICTED_ITEMS)) || item.isQuestItem()) {
            TradeList.LOGGER.warn("{} Attempt to add a restricted item!", (Object)this.owner);
            return null;
        }
        if (this.owner.getInventory().isBlocked(item)) {
            TradeList.LOGGER.warn("{} Attempt to add an item that can't manipulate!", (Object)this.owner);
            return null;
        }
        if (count <= 0L || count > item.getCount()) {
            TradeList.LOGGER.warn("{} Attempt to add an item with invalid item count!", (Object)this.owner);
            return null;
        }
        if (!item.isStackable() && count > 1L) {
            TradeList.LOGGER.warn("{} Attempt to add non-stackable item to TradeList with count > 1!", (Object)this.owner);
            return null;
        }
        if (Inventory.MAX_ADENA / count < price) {
            TradeList.LOGGER.warn("{} Attempt to overflow adena !", (Object)this.owner);
            return null;
        }
        for (final TradeItem checkitem : this.items) {
            if (checkitem.getObjectId() == objectId) {
                TradeList.LOGGER.warn("{} Attempt to add an item that is already present!", (Object)this.owner);
                return null;
            }
        }
        final TradeItem titem = new TradeItem(item, count, price);
        this.items.add(titem);
        this.invalidateConfirmation();
        return titem;
    }
    
    public synchronized TradeItem addItemByItemId(final int itemId, final long count, final long price) {
        if (this.locked) {
            TradeList.LOGGER.warn("{} Attempt to modify locked TradeList!", (Object)this.owner);
            return null;
        }
        final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
        if (item == null) {
            TradeList.LOGGER.warn("{} Attempt to add invalid item to TradeList!", (Object)this.owner);
            return null;
        }
        if (!item.isTradeable() || item.isQuestItem()) {
            return null;
        }
        if (!item.isStackable() && count > 1L) {
            TradeList.LOGGER.warn("{} Attempt to add non-stackable item to TradeList with count > 1!", (Object)this.owner);
            return null;
        }
        if (Inventory.MAX_ADENA / count < price) {
            TradeList.LOGGER.warn("{} Attempt to overflow adena !", (Object)this.owner);
            return null;
        }
        final TradeItem titem = new TradeItem(item, count, price);
        this.items.add(titem);
        this.invalidateConfirmation();
        return titem;
    }
    
    private synchronized TradeItem removeItem(final int objectId, final int itemId, final long count) {
        if (this.locked) {
            TradeList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.owner.getName()));
            return null;
        }
        if (count < 0L) {
            TradeList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;J)Ljava/lang/String;, this.owner.getName(), count));
            return null;
        }
        for (final TradeItem titem : this.items) {
            if (titem.getObjectId() == objectId || titem.getItem().getId() == itemId) {
                if (this.partner != null) {
                    final TradeList partnerList = this.partner.getActiveTradeList();
                    if (partnerList == null) {
                        TradeList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.partner.getName(), this.partner.getName()));
                        return null;
                    }
                    partnerList.invalidateConfirmation();
                }
                if (count != -1L && titem.getCount() > count) {
                    titem.setCount(titem.getCount() - count);
                }
                else {
                    this.items.remove(titem);
                }
                return titem;
            }
        }
        return null;
    }
    
    public synchronized void updateItems() {
        for (final TradeItem titem : this.items) {
            final Item item = this.owner.getInventory().getItemByObjectId(titem.getObjectId());
            if (item == null || titem.getCount() < 1L) {
                this.removeItem(titem.getObjectId(), -1, -1L);
            }
            else {
                if (item.getCount() >= titem.getCount()) {
                    continue;
                }
                titem.setCount(item.getCount());
            }
        }
    }
    
    public void lock() {
        this.locked = true;
    }
    
    public synchronized void clear() {
        this.items.clear();
        this.locked = false;
    }
    
    public boolean confirm() {
        if (this.confirmed) {
            return true;
        }
        if (this.partner != null) {
            final TradeList partnerList = this.partner.getActiveTradeList();
            if (partnerList == null) {
                TradeList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.partner.getName(), this.partner.getName()));
                return false;
            }
            TradeList sync1;
            TradeList sync2;
            if (this.getOwner().getObjectId() > partnerList.getOwner().getObjectId()) {
                sync1 = partnerList;
                sync2 = this;
            }
            else {
                sync1 = this;
                sync2 = partnerList;
            }
            synchronized (sync1) {
                synchronized (sync2) {
                    this.confirmed = true;
                    if (partnerList.isConfirmed()) {
                        partnerList.lock();
                        this.lock();
                        if (!partnerList.validate() || !this.validate()) {
                            return false;
                        }
                        this.doExchange(partnerList);
                    }
                    else {
                        this.partner.onTradeConfirm(this.owner);
                    }
                }
            }
        }
        else {
            this.confirmed = true;
        }
        return this.confirmed;
    }
    
    private void invalidateConfirmation() {
        this.confirmed = false;
    }
    
    private boolean validate() {
        if (this.owner == null || World.getInstance().findPlayer(this.owner.getObjectId()) == null) {
            TradeList.LOGGER.warn("Invalid owner of TradeList");
            return false;
        }
        for (final TradeItem titem : this.items) {
            final Item item = this.owner.checkItemManipulation(titem.getObjectId(), titem.getCount(), "transfer");
            if (item == null || item.getCount() < 1L) {
                TradeList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.owner.getName()));
                return false;
            }
        }
        return true;
    }
    
    private boolean TransferItems(final Player partner, final InventoryUpdate ownerIU, final InventoryUpdate partnerIU) {
        for (final TradeItem titem : this.items) {
            final Item oldItem = this.owner.getInventory().getItemByObjectId(titem.getObjectId());
            if (oldItem == null) {
                return false;
            }
            final Item newItem = this.owner.getInventory().transferItem("Trade", titem.getObjectId(), titem.getCount(), partner.getInventory(), this.owner, this.partner);
            if (newItem == null) {
                return false;
            }
            if (ownerIU != null) {
                if (oldItem.getCount() > 0L && oldItem != newItem) {
                    ownerIU.addModifiedItem(oldItem);
                }
                else {
                    ownerIU.addRemovedItem(oldItem);
                }
            }
            if (partnerIU == null) {
                continue;
            }
            if (newItem.getCount() > titem.getCount()) {
                partnerIU.addModifiedItem(newItem);
            }
            else {
                partnerIU.addNewItem(newItem);
            }
        }
        return true;
    }
    
    private int countItemsSlots(final Player partner) {
        int slots = 0;
        for (final TradeItem item : this.items) {
            if (item == null) {
                continue;
            }
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(item.getItem().getId());
            if (template == null) {
                continue;
            }
            if (!template.isStackable()) {
                slots += (int)item.getCount();
            }
            else {
                if (partner.getInventory().getItemByItemId(item.getItem().getId()) != null) {
                    continue;
                }
                ++slots;
            }
        }
        return slots;
    }
    
    private int calcItemsWeight() {
        long weight = 0L;
        for (final TradeItem item : this.items) {
            if (item == null) {
                continue;
            }
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(item.getItem().getId());
            if (template == null) {
                continue;
            }
            weight += item.getCount() * template.getWeight();
        }
        return (int)Math.min(weight, 2147483647L);
    }
    
    private void doExchange(final TradeList partnerList) {
        boolean success = false;
        if (!this.owner.getInventory().validateWeight(partnerList.calcItemsWeight()) || !partnerList.getOwner().getInventory().validateWeight(this.calcItemsWeight())) {
            partnerList.getOwner().sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            this.owner.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
        }
        else if (!this.owner.getInventory().validateCapacity(partnerList.countItemsSlots(this.getOwner())) || !partnerList.getOwner().getInventory().validateCapacity(this.countItemsSlots(partnerList.getOwner()))) {
            partnerList.getOwner().sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            this.owner.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
        }
        else {
            final InventoryUpdate ownerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
            final InventoryUpdate partnerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
            partnerList.TransferItems(this.owner, partnerIU, ownerIU);
            this.TransferItems(partnerList.getOwner(), ownerIU, partnerIU);
            if (ownerIU != null) {
                this.owner.sendInventoryUpdate(ownerIU);
            }
            else {
                this.owner.sendItemList();
            }
            if (partnerIU != null) {
                this.partner.sendInventoryUpdate(partnerIU);
            }
            else {
                this.partner.sendItemList();
            }
            success = true;
        }
        partnerList.getOwner().onTradeFinish(success);
        this.owner.onTradeFinish(success);
    }
    
    public synchronized int privateStoreBuy(final Player player, final Set<ItemRequest> items) {
        if (this.locked) {
            return 1;
        }
        if (!this.validate()) {
            this.lock();
            return 1;
        }
        if (!this.owner.isOnline() || !player.isOnline()) {
            return 1;
        }
        int slots = 0;
        int weight = 0;
        long totalPrice = 0L;
        final PlayerInventory ownerInventory = this.owner.getInventory();
        final PlayerInventory playerInventory = player.getInventory();
        for (final ItemRequest item : items) {
            boolean found = false;
            for (final TradeItem ti : this.items) {
                if (ti.getObjectId() == item.getObjectId()) {
                    if (ti.getPrice() == item.getPrice()) {
                        if (ti.getCount() < item.getCount()) {
                            item.setCount(ti.getCount());
                        }
                        found = true;
                        break;
                    }
                    break;
                }
            }
            if (!found) {
                if (this.packaged) {
                    GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                    return 2;
                }
                item.setCount(0L);
            }
            else {
                if (Inventory.MAX_ADENA / item.getCount() < item.getPrice()) {
                    this.lock();
                    return 1;
                }
                totalPrice += item.getCount() * item.getPrice();
                if (Inventory.MAX_ADENA < totalPrice || totalPrice < 0L) {
                    this.lock();
                    return 1;
                }
                final Item oldItem = this.owner.checkItemManipulation(item.getObjectId(), item.getCount(), "sell");
                if (oldItem == null || !oldItem.isTradeable()) {
                    this.lock();
                    return 2;
                }
                final ItemTemplate template = ItemEngine.getInstance().getTemplate(item.getItemId());
                if (template == null) {
                    continue;
                }
                weight += (int)(item.getCount() * template.getWeight());
                if (!template.isStackable()) {
                    slots += (int)item.getCount();
                }
                else {
                    if (playerInventory.getItemByItemId(item.getItemId()) != null) {
                        continue;
                    }
                    ++slots;
                }
            }
        }
        if (totalPrice > playerInventory.getAdena()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return 1;
        }
        if (!playerInventory.validateWeight(weight)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return 1;
        }
        if (!playerInventory.validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            return 1;
        }
        final InventoryUpdate ownerIU = new InventoryUpdate();
        final InventoryUpdate playerIU = new InventoryUpdate();
        final Item adenaItem = playerInventory.getAdenaInstance();
        if (!playerInventory.reduceAdena("PrivateStore", totalPrice, player, this.owner)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return 1;
        }
        playerIU.addItem(adenaItem);
        ownerInventory.addAdena("PrivateStore", totalPrice, this.owner, player);
        boolean ok = true;
        for (final ItemRequest item2 : items) {
            if (item2.getCount() == 0L) {
                continue;
            }
            final Item oldItem2 = this.owner.checkItemManipulation(item2.getObjectId(), item2.getCount(), "sell");
            if (oldItem2 == null) {
                this.lock();
                ok = false;
                break;
            }
            final Item newItem = ownerInventory.transferItem("PrivateStore", item2.getObjectId(), item2.getCount(), playerInventory, this.owner, player);
            if (newItem == null) {
                ok = false;
                break;
            }
            this.removeItem(item2.getObjectId(), -1, item2.getCount());
            if (oldItem2.getCount() > 0L && oldItem2 != newItem) {
                ownerIU.addModifiedItem(oldItem2);
            }
            else {
                ownerIU.addRemovedItem(oldItem2);
            }
            if (newItem.getCount() > item2.getCount()) {
                playerIU.addModifiedItem(newItem);
            }
            else {
                playerIU.addNewItem(newItem);
            }
            if (newItem.isStackable()) {
                SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S3_S2_S);
                msg.addString(player.getName());
                msg.addItemName(newItem);
                msg.addLong(item2.getCount());
                this.owner.sendPacket(msg);
                msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_PURCHASED_S3_S2_S_FROM_C1);
                msg.addString(this.owner.getName());
                msg.addItemName(newItem);
                msg.addLong(item2.getCount());
                player.sendPacket(msg);
            }
            else {
                SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S2);
                msg.addString(player.getName());
                msg.addItemName(newItem);
                this.owner.sendPacket(msg);
                msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_PURCHASED_S2_FROM_C1);
                msg.addString(this.owner.getName());
                msg.addItemName(newItem);
                player.sendPacket(msg);
            }
            this.owner.sendPacket(new ExPrivateStoreSellingResult(item2.getObjectId(), item2.getCount(), player.getAppearance().getVisibleName()));
        }
        this.owner.sendInventoryUpdate(ownerIU);
        player.sendInventoryUpdate(playerIU);
        return ok ? 0 : 2;
    }
    
    public synchronized boolean privateStoreSell(final Player player, final ItemRequest[] requestedItems) {
        if (this.locked || !this.owner.isOnline() || !player.isOnline()) {
            return false;
        }
        boolean ok = false;
        final PlayerInventory ownerInventory = this.owner.getInventory();
        final PlayerInventory playerInventory = player.getInventory();
        final InventoryUpdate ownerIU = new InventoryUpdate();
        final InventoryUpdate playerIU = new InventoryUpdate();
        long totalPrice = 0L;
        final TradeItem[] sellerItems = this.items.toArray(new TradeItem[0]);
        for (final ItemRequest item : requestedItems) {
            boolean found = false;
            final TradeItem[] array = sellerItems;
            final int length2 = array.length;
            int j = 0;
            while (j < length2) {
                final TradeItem ti = array[j];
                if (ti.getItem().getId() == item.getItemId()) {
                    if (ti.getPrice() == item.getPrice()) {
                        if (ti.getCount() < item.getCount()) {
                            item.setCount(ti.getCount());
                        }
                        found = (item.getCount() > 0L);
                        break;
                    }
                    break;
                }
                else {
                    ++j;
                }
            }
            Label_0834: {
                if (found) {
                    if (Inventory.MAX_ADENA / item.getCount() < item.getPrice()) {
                        this.lock();
                        break;
                    }
                    final long _totalPrice = totalPrice + item.getCount() * item.getPrice();
                    if (Inventory.MAX_ADENA < _totalPrice || _totalPrice < 0L) {
                        this.lock();
                        break;
                    }
                    if (ownerInventory.getAdena() >= _totalPrice) {
                        if (item.getObjectId() >= 1) {
                            if (item.getObjectId() <= sellerItems.length) {
                                final TradeItem tradeItem = sellerItems[item.getObjectId() - 1];
                                if (tradeItem != null) {
                                    if (tradeItem.getItem().getId() == item.getItemId()) {
                                        int objectId = tradeItem.getObjectId();
                                        Item oldItem = player.checkItemManipulation(objectId, item.getCount(), "sell");
                                        if (oldItem == null) {
                                            oldItem = playerInventory.getItemByItemId(item.getItemId());
                                            if (oldItem == null) {
                                                break Label_0834;
                                            }
                                            objectId = oldItem.getObjectId();
                                            oldItem = player.checkItemManipulation(objectId, item.getCount(), "sell");
                                            if (oldItem == null) {
                                                break Label_0834;
                                            }
                                        }
                                        if (oldItem.getId() != item.getItemId()) {
                                            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
                                            return false;
                                        }
                                        if (oldItem.isTradeable()) {
                                            final Item newItem = playerInventory.transferItem("PrivateStore", objectId, item.getCount(), ownerInventory, player, this.owner);
                                            if (newItem != null) {
                                                this.removeItem(-1, item.getItemId(), item.getCount());
                                                ok = true;
                                                totalPrice = _totalPrice;
                                                if (oldItem.getCount() > 0L && oldItem != newItem) {
                                                    playerIU.addModifiedItem(oldItem);
                                                }
                                                else {
                                                    playerIU.addRemovedItem(oldItem);
                                                }
                                                if (newItem.getCount() > item.getCount()) {
                                                    ownerIU.addModifiedItem(newItem);
                                                }
                                                else {
                                                    ownerIU.addNewItem(newItem);
                                                }
                                                if (newItem.isStackable()) {
                                                    SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_PURCHASED_S3_S2_S_FROM_C1);
                                                    msg.addString(player.getName());
                                                    msg.addItemName(newItem);
                                                    msg.addLong(item.getCount());
                                                    this.owner.sendPacket(msg);
                                                    msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S3_S2_S);
                                                    msg.addString(this.owner.getName());
                                                    msg.addItemName(newItem);
                                                    msg.addLong(item.getCount());
                                                    player.sendPacket(msg);
                                                }
                                                else {
                                                    SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_PURCHASED_S2_FROM_C1);
                                                    msg.addString(player.getName());
                                                    msg.addItemName(newItem);
                                                    this.owner.sendPacket(msg);
                                                    msg = SystemMessage.getSystemMessage(SystemMessageId.C1_PURCHASED_S2);
                                                    msg.addString(this.owner.getName());
                                                    msg.addItemName(newItem);
                                                    player.sendPacket(msg);
                                                }
                                                this.owner.sendPacket(new ExPrivateStoreBuyingResult(item.getObjectId(), item.getCount(), player.getAppearance().getVisibleName()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (totalPrice > 0L) {
            if (totalPrice > ownerInventory.getAdena()) {
                return false;
            }
            final Item adenaItem = ownerInventory.getAdenaInstance();
            ownerInventory.reduceAdena("PrivateStore", totalPrice, this.owner, player);
            ownerIU.addItem(adenaItem);
            playerInventory.addAdena("PrivateStore", totalPrice, player, this.owner);
            playerIU.addItem(playerInventory.getAdenaInstance());
        }
        if (ok) {
            this.owner.sendInventoryUpdate(ownerIU);
            player.sendInventoryUpdate(playerIU);
        }
        return ok;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)TradeList.class);
    }
}
