// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.container;

import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.type.ItemType;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.List;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemDrop;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemDestroy;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemTransfer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemAdd;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.Config;
import java.util.stream.Stream;
import java.util.function.Function;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.Pet;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collection;
import org.l2j.gameserver.enums.ItemLocation;
import java.util.function.Consumer;
import java.util.ServiceLoader;
import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.enums.InventoryBlockType;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;

public class PlayerInventory extends Inventory
{
    private final Player owner;
    private Item _adena;
    private Item _beautyTickets;
    private Item silverCoin;
    private Item goldCoin;
    private Item l2Coin;
    private Item currentAmmunition;
    private IntCollection blockItems;
    private InventoryBlockType blockMode;
    
    public PlayerInventory(final Player owner) {
        this.blockItems = null;
        this.blockMode = InventoryBlockType.NONE;
        this.owner = owner;
        ServiceLoader.load(PlayerInventoryListener.class).forEach(this::addPaperdollListener);
    }
    
    @Override
    public Player getOwner() {
        return this.owner;
    }
    
    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.INVENTORY;
    }
    
    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.PAPERDOLL;
    }
    
    public Item getAdenaInstance() {
        return this._adena;
    }
    
    @Override
    public long getAdena() {
        return (this._adena != null) ? this._adena.getCount() : 0L;
    }
    
    public Item getBeautyTicketsInstance() {
        return this._beautyTickets;
    }
    
    @Override
    public long getBeautyTickets() {
        return (this._beautyTickets != null) ? this._beautyTickets.getCount() : 0L;
    }
    
    public Collection<Item> getUniqueItems(final boolean allowAdena) {
        return this.getUniqueItems(allowAdena, true);
    }
    
    public Collection<Item> getUniqueItems(final boolean allowAdena, final boolean onlyAvailable) {
        final Collection<Item> list = new LinkedList<Item>();
        for (final Item item : this.items.values()) {
            if (!allowAdena && item.getId() == 57) {
                continue;
            }
            boolean isDuplicate = false;
            for (final Item litem : list) {
                if (litem.getId() == item.getId()) {
                    isDuplicate = true;
                    break;
                }
            }
            if (isDuplicate || (onlyAvailable && (!item.isSellable() || !item.isAvailable(this.owner, false, false)))) {
                continue;
            }
            list.add(item);
        }
        return list;
    }
    
    public Collection<Item> getAllItemsByItemId(final int itemId, final int enchantment) {
        return this.getAllItemsByItemId(itemId, enchantment, true);
    }
    
    public Collection<Item> getAllItemsByItemId(final int itemId, final int enchantment, final boolean includeEquipped) {
        return this.getItems(i -> i.getId() == itemId && i.getEnchantLevel() == enchantment && (includeEquipped || !i.isEquipped()), (Predicate<Item>[])new Predicate[0]);
    }
    
    public Collection<Item> getAvailableItems(final boolean allowAdena, final boolean allowNonTradeable, final boolean feightable) {
        return this.getItems(i -> {
            if (!i.isAvailable(this.owner, allowAdena, allowNonTradeable) || this.isBlocked(i)) {
                return false;
            }
            else if (feightable) {
                return i.getItemLocation() == ItemLocation.INVENTORY && i.isFreightable();
            }
            else {
                return true;
            }
        }, (Predicate<Item>[])new Predicate[0]);
    }
    
    public Collection<Item> getDepositableItems(final WarehouseType type) {
        return this.items.values().stream().filter(item -> this.isDepositable(item, type)).collect((Collector<? super Object, ?, Collection<Item>>)Collectors.toSet());
    }
    
    private boolean isDepositable(final Item item, final WarehouseType type) {
        return item.isDepositable(type) && !this.isBlocked(item) && this.isNotInUse(item);
    }
    
    public boolean isNotInUse(final Item item) {
        final Pet pet = this.owner.getPet();
        return !item.isEquipped() && !this.owner.isProcessingItem(item.getObjectId()) && (Objects.isNull(pet) || pet.getControlObjectId() != item.getObjectId()) && !this.owner.isCastingNow(s -> s.getSkill().getItemConsumeId() != item.getId());
    }
    
    public final boolean haveItemForSelfResurrection() {
        return this.items.values().stream().anyMatch(Item::isSelfResurrection);
    }
    
    public Collection<TradeItem> getAvailableItems(final TradeList tradeList) {
        final Stream<Object> filter = this.items.values().stream().filter(i -> i.isAvailable(this.owner, false, false));
        Objects.requireNonNull(tradeList);
        return filter.map((Function<? super Object, ?>)tradeList::adjustAvailableItem).filter(Objects::nonNull).collect((Collector<? super Object, ?, Collection<TradeItem>>)Collectors.toCollection(LinkedList::new));
    }
    
    public void adjustAvailableItem(final TradeItem item) {
        boolean notAllEquipped = false;
        for (final Item adjItem : this.getItemsByItemId(item.getItem().getId())) {
            if (!adjItem.isEquipable()) {
                notAllEquipped = true;
                break;
            }
            if (adjItem.isEquipped()) {
                continue;
            }
            notAllEquipped = true;
        }
        if (notAllEquipped) {
            final Item adjItem2 = this.getItemByItemId(item.getItem().getId());
            item.setObjectId(adjItem2.getObjectId());
            item.setEnchant(adjItem2.getEnchantLevel());
            if (adjItem2.getCount() < item.getCount()) {
                item.setCount(adjItem2.getCount());
            }
            return;
        }
        item.setCount(0L);
    }
    
    public void addAdena(final String process, final long count, final Player actor, final Object reference) {
        if (count > 0L) {
            this.addItem(process, 57, count, actor, reference);
        }
    }
    
    public boolean reduceAdena(final String process, final long count, final Player actor, final Object reference) {
        return count > 0L && this.destroyItemByItemId(process, 57, count, actor, reference) != null;
    }
    
    public boolean reduceBeautyTickets(final String process, final long count, final Player actor, final Object reference) {
        return count > 0L && this.destroyItemByItemId(process, 36308, count, actor, reference) != null;
    }
    
    @Override
    public Item addItem(final String process, Item item, final Player actor, final Object reference) {
        item = super.addItem(process, item, actor, reference);
        if (item != null) {
            if (item.getId() == 57 && !item.equals(this._adena)) {
                this._adena = item;
            }
            else if (item.getId() == 36308 && !item.equals(this._beautyTickets)) {
                this._beautyTickets = item;
            }
            else if (item.getId() == 29983 && !item.equals(this.silverCoin)) {
                this.silverCoin = item;
            }
            else if (item.getId() == 29984 && !item.equals(this.goldCoin)) {
                this.goldCoin = item;
            }
            else if (item.getId() == 91663 && !item.equals(this.l2Coin)) {
                this.l2Coin = item;
            }
            if (actor != null) {
                if (!Config.FORCE_INVENTORY_UPDATE) {
                    final InventoryUpdate playerIU = new InventoryUpdate();
                    playerIU.addItem(item);
                    actor.sendInventoryUpdate(playerIU);
                }
                else {
                    actor.sendItemList();
                }
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemAdd(actor, item), actor, item.getTemplate());
            }
        }
        return item;
    }
    
    @Override
    public Item addItem(final String process, final int itemId, final long count, final Player actor, final Object reference) {
        return this.addItem(process, itemId, count, actor, reference, true);
    }
    
    public Item addItem(final String process, final int itemId, final long count, final Player actor, final Object reference, final boolean update) {
        final Item item = super.addItem(process, itemId, count, actor, reference);
        if (item != null) {
            if (item.getId() == 57 && !item.equals(this._adena)) {
                this._adena = item;
            }
            else if (item.getId() == 36308 && !item.equals(this._beautyTickets)) {
                this._beautyTickets = item;
            }
            else if (item.getId() == 29983 && !item.equals(this.silverCoin)) {
                this.silverCoin = item;
            }
            else if (item.getId() == 29984 && !item.equals(this.goldCoin)) {
                this.goldCoin = item;
            }
            else if (item.getId() == 91663 && !item.equals(this.l2Coin)) {
                this.l2Coin = item;
            }
        }
        if (item != null && actor != null) {
            if (update) {
                final InventoryUpdate playerIU = new InventoryUpdate();
                playerIU.addItem(item);
                actor.sendInventoryUpdate(playerIU);
            }
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemAdd(actor, item), actor, item.getTemplate());
        }
        return item;
    }
    
    @Override
    public Item transferItem(final String process, final int objectId, final long count, final ItemContainer target, final Player actor, final Object reference) {
        final Item item = super.transferItem(process, objectId, count, target, actor, reference);
        if (this._adena != null && (this._adena.getCount() <= 0L || this._adena.getOwnerId() != this.getOwnerId())) {
            this._adena = null;
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemTransfer(actor, item, target), item.getTemplate());
        return item;
    }
    
    @Override
    public Item detachItem(final String process, Item item, final long count, final ItemLocation newLocation, final Player actor, final Object reference) {
        item = super.detachItem(process, item, count, newLocation, actor, reference);
        if (item != null && actor != null) {
            actor.sendItemList();
        }
        return item;
    }
    
    @Override
    public Item destroyItem(final String process, final Item item, final Player actor, final Object reference) {
        return this.destroyItem(process, item, item.getCount(), actor, reference);
    }
    
    @Override
    public Item destroyItem(final String process, Item item, final long count, final Player actor, final Object reference) {
        item = super.destroyItem(process, item, count, actor, reference);
        if (this._adena != null && this._adena.getCount() <= 0L) {
            this._adena = null;
        }
        if (Objects.nonNull(item)) {
            if (item.isEquipped()) {
                this.unEquipItemInBodySlot(item.getBodyPart());
            }
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemDestroy(actor, item), item.getTemplate());
        }
        return item;
    }
    
    @Override
    public Item destroyItem(final String process, final int objectId, final long count, final Player actor, final Object reference) {
        final Item item = this.getItemByObjectId(objectId);
        if (item == null) {
            return null;
        }
        return this.destroyItem(process, item, count, actor, reference);
    }
    
    @Override
    public Item destroyItemByItemId(final String process, final int itemId, final long count, final Player actor, final Object reference) {
        final Item item = this.getItemByItemId(itemId);
        if (item == null) {
            return null;
        }
        return this.destroyItem(process, item, count, actor, reference);
    }
    
    @Override
    public Item dropItem(final String process, Item item, final Player actor, final Object reference) {
        item = super.dropItem(process, item, actor, reference);
        if (this._adena != null && (this._adena.getCount() <= 0L || this._adena.getOwnerId() != this.getOwnerId())) {
            this._adena = null;
        }
        if (item != null) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemDrop(actor, item, item.getLocation()), item.getTemplate());
        }
        return item;
    }
    
    @Override
    public Item dropItem(final String process, final int objectId, final long count, final Player actor, final Object reference) {
        final Item item = super.dropItem(process, objectId, count, actor, reference);
        if (this._adena != null && (this._adena.getCount() <= 0L || this._adena.getOwnerId() != this.getOwnerId())) {
            this._adena = null;
        }
        if (item != null) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemDrop(actor, item, item.getLocation()), item.getTemplate());
        }
        return item;
    }
    
    @Override
    protected boolean removeItem(final Item item) {
        this.owner.removeItemFromShortCut(item.getObjectId());
        if (this.owner.isProcessingItem(item.getObjectId())) {
            this.owner.removeRequestsThatProcessesItem(item.getObjectId());
        }
        if (item.getId() == 57) {
            this._adena = null;
        }
        else if (item.getId() == 36308) {
            this._beautyTickets = null;
        }
        return super.removeItem(item);
    }
    
    public void refreshWeight() {
        super.refreshWeight();
        this.owner.refreshOverloaded(true);
    }
    
    @Override
    public void restore() {
        super.restore();
        this._adena = this.getItemByItemId(57);
        this._beautyTickets = this.getItemByItemId(36308);
        this.goldCoin = this.getItemByItemId(29984);
        this.silverCoin = this.getItemByItemId(29983);
        this.l2Coin = this.getItemByItemId(91663);
    }
    
    public boolean checkInventorySlotsAndWeight(final List<ItemTemplate> itemList, final boolean sendMessage, final boolean sendSkillMessage) {
        int lootWeight = 0;
        int requiredSlots = 0;
        if (itemList != null) {
            for (final ItemTemplate item : itemList) {
                if (!item.isStackable() || this.getInventoryItemCount(item.getId(), -1) <= 0L) {
                    ++requiredSlots;
                }
                lootWeight += item.getWeight();
            }
        }
        final boolean inventoryStatusOK = this.validateCapacity(requiredSlots) && this.validateWeight(lootWeight);
        if (!inventoryStatusOK && sendMessage) {
            this.owner.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            if (sendSkillMessage) {
                this.owner.sendPacket(SystemMessageId.WEIGHT_AND_VOLUME_LIMIT_HAVE_BEEN_EXCEEDED_THAT_SKILL_IS_CURRENTLY_UNAVAILABLE);
            }
        }
        return inventoryStatusOK;
    }
    
    public boolean validateCapacity(final Item item) {
        int slots = 0;
        if (!item.isStackable() || (this.getInventoryItemCount(item.getId(), -1) <= 0L && !item.getTemplate().hasExImmediateEffect())) {
            ++slots;
        }
        return this.validateCapacity(slots, item.isQuestItem());
    }
    
    public boolean validateCapacityByItemId(final int itemId) {
        int slots = 0;
        final Item invItem = this.getItemByItemId(itemId);
        if (invItem == null || !invItem.isStackable()) {
            ++slots;
        }
        return this.validateCapacity(slots, ItemEngine.getInstance().getTemplate(itemId).isQuestItem());
    }
    
    @Override
    public boolean validateCapacity(final long slots) {
        return this.validateCapacity(slots, false);
    }
    
    public boolean validateCapacity(final long slots, final boolean questItem) {
        return ((slots == 0L && !Config.AUTO_LOOT_SLOT_LIMIT) || questItem) ? (this.getSize(Item::isQuestItem, (Predicate<Item>[])new Predicate[0]) + slots <= this.owner.getQuestInventoryLimit()) : (this.getSize(item -> !item.isQuestItem(), (Predicate<Item>[])new Predicate[0]) + slots <= this.owner.getInventoryLimit());
    }
    
    @Override
    public boolean validateWeight(final long weight) {
        return (this.owner.isGM() && this.owner.getDietMode() && this.owner.getAccessLevel().allowTransaction()) || this._totalWeight + weight <= this.owner.getMaxLoad();
    }
    
    public void setInventoryBlock(final IntCollection items, final InventoryBlockType mode) {
        this.blockMode = mode;
        this.blockItems = items;
    }
    
    public void unblock() {
        this.blockMode = InventoryBlockType.NONE;
        this.blockItems = null;
    }
    
    public boolean hasInventoryBlock() {
        return this.blockMode != InventoryBlockType.NONE && Objects.nonNull(this.blockItems) && !this.blockItems.isEmpty();
    }
    
    public InventoryBlockType getBlockMode() {
        return this.blockMode;
    }
    
    public IntCollection getBlockItems() {
        return this.blockItems;
    }
    
    public boolean isBlocked(final Item item) {
        if (Objects.nonNull(this.blockItems)) {
            boolean b = false;
            switch (this.blockMode) {
                case NONE: {
                    b = false;
                    break;
                }
                case WHITELIST: {
                    b = this.blockItems.stream().noneMatch(id -> id == item.getId());
                    break;
                }
                case BLACKLIST: {
                    b = this.blockItems.stream().anyMatch(id -> id == item.getId());
                    break;
                }
                default: {
                    throw new IncompatibleClassChangeError();
                }
            }
            return b;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this.getClass().getSimpleName(), this.owner);
    }
    
    public void applyItemSkills() {
        for (final Item item : this.items.values()) {
            item.giveSkillsToOwner();
            item.applyEnchantStats();
            if (item.isEquipped()) {
                item.applySpecialAbilities();
            }
        }
    }
    
    public void reduceAmmunitionCount() {
        if (Objects.isNull(this.currentAmmunition) || this.currentAmmunition.isInfinite()) {
            return;
        }
        this.updateItemCountNoDbUpdate(null, this.currentAmmunition, -1L, this.owner, null);
    }
    
    public boolean updateItemCountNoDbUpdate(final String process, final Item item, final long countDelta, final Player creator, final Object reference) {
        final long left = item.getCount() + countDelta;
        if (left > 0L) {
            if (Util.isNotEmpty(process)) {
                item.changeCount(process, countDelta, creator, reference);
            }
            else {
                item.changeCountWithoutTrace(countDelta, creator, reference);
            }
            item.setLastChange(2);
            this.refreshWeight();
        }
        else {
            this.destroyItem(process, item, this.owner, null);
        }
        this.owner.sendInventoryUpdate(new InventoryUpdate(item));
        return true;
    }
    
    public boolean updateItemCount(final String process, final Item item, final long countDelta, final Player creator, final Object reference) {
        if (item != null) {
            try {
                return this.updateItemCountNoDbUpdate(process, item, countDelta, creator, reference);
            }
            finally {
                if (item.getCount() > 0L) {
                    item.updateDatabase();
                }
            }
        }
        return false;
    }
    
    public long getGoldCoin() {
        return Objects.nonNull(this.goldCoin) ? this.goldCoin.getCount() : 0L;
    }
    
    public long getSilverCoin() {
        return Objects.nonNull(this.silverCoin) ? this.silverCoin.getCount() : 0L;
    }
    
    public long getLCoin() {
        return Objects.nonNull(this.l2Coin) ? this.l2Coin.getCount() : 0L;
    }
    
    public void addLCoin(final long count) {
        this.l2Coin.setCount(this.getLCoin() + count);
    }
    
    public boolean findAmmunitionForCurrentWeapon() {
        final Item currentWeapon = this.getPaperdollItem(InventorySlot.RIGHT_HAND);
        if (Objects.isNull(currentWeapon)) {
            return false;
        }
        if (!this.matchesAmmunition(this.currentAmmunition, currentWeapon)) {
            final Item ammunition = this.findAmmunition(currentWeapon);
            this.currentAmmunition = ammunition;
            if (!Objects.nonNull(ammunition)) {
                return false;
            }
        }
        return true;
    }
    
    private Item findAmmunition(final Item currentWeapon) {
        return this.items.values().stream().filter(i -> this.matchesAmmunition(i, currentWeapon)).findFirst().orElse(null);
    }
    
    private boolean matchesAmmunition(final Item ammunition, final Item weapon) {
        if (Objects.isNull(ammunition) || ammunition.getCrystalType() != weapon.getCrystalType() || ammunition.getCount() < 1L || !this.items.containsKey(ammunition.getObjectId())) {
            return false;
        }
        final ItemType itemType = weapon.getItemType();
        return (ammunition.getItemType() == EtcItemType.ARROW && itemType == WeaponType.BOW) || (ammunition.getItemType() == EtcItemType.BOLT && itemType == WeaponType.CROSSBOW) || itemType == WeaponType.TWO_HAND_CROSSBOW;
    }
}
