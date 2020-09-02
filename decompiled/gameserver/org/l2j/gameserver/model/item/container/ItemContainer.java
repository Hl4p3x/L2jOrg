// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.container;

import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.enums.ItemLocation;
import java.util.Objects;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Iterator;
import org.l2j.commons.util.StreamUtil;
import java.util.function.ToIntFunction;
import io.github.joealisson.primitive.IntSet;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.LinkedList;
import java.util.Collection;
import java.util.function.Predicate;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.WorldObject;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.model.item.instance.Item;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public abstract class ItemContainer
{
    protected static final Logger LOGGER;
    protected final IntMap<Item> items;
    
    protected ItemContainer() {
        this.items = (IntMap<Item>)new CHashIntMap();
    }
    
    public int getOwnerId() {
        return Util.zeroIfNullOrElse((Object)this.getOwner(), WorldObject::getObjectId);
    }
    
    public int getSize() {
        return this.items.size();
    }
    
    @SafeVarargs
    public final int getSize(Predicate<Item> filter, final Predicate<Item>... filters) {
        for (final Predicate<Item> additionalFilter : filters) {
            filter = filter.and(additionalFilter);
        }
        return (int)this.items.values().stream().filter(filter).count();
    }
    
    public Collection<Item> getItems() {
        return this.getItems(i -> true, (Predicate<Item>[])new Predicate[0]);
    }
    
    @SafeVarargs
    public final Collection<Item> getItems(Predicate<Item> filter, final Predicate<Item>... filters) {
        for (final Predicate<Item> additionalFilter : filters) {
            filter = filter.and(additionalFilter);
        }
        return this.items.values().stream().filter((Predicate<? super Object>)filter).collect((Collector<? super Object, ?, Collection<Item>>)Collectors.toCollection(LinkedList::new));
    }
    
    public void forEachItem(final Predicate<Item> filter, final Consumer<Item> action) {
        this.items.values().stream().filter(filter).forEach(action);
    }
    
    public final IntSet getItemsId(final Predicate<Item> filter) {
        return StreamUtil.collectToSet(this.items.entrySet().stream().filter(e -> filter.test((Item)e.getValue())).mapToInt(IntMap.Entry::getKey));
    }
    
    public Item getItemByItemId(final int itemId) {
        return this.items.values().stream().filter(item -> item.getId() == itemId).findFirst().orElse(null);
    }
    
    public Collection<Item> getItemsByItemId(final int itemId) {
        return this.getItems(i -> i.getId() == itemId, (Predicate<Item>[])new Predicate[0]);
    }
    
    public Item getItemByObjectId(final int objectId) {
        return (Item)this.items.get(objectId);
    }
    
    public long getInventoryItemCount(final int itemId, final int enchantLevel) {
        return this.getInventoryItemCount(itemId, enchantLevel, true);
    }
    
    public long getInventoryItemCount(final int itemId, final int enchantLevel, final boolean includeEquipped) {
        long count = 0L;
        for (final Item item : this.items.values()) {
            if (item.getId() == itemId && (item.getEnchantLevel() == enchantLevel || enchantLevel < 0) && (includeEquipped || !item.isEquipped())) {
                if (item.isStackable()) {
                    return item.getCount();
                }
                ++count;
            }
        }
        return count;
    }
    
    public Item addItem(final String process, Item item, final Player actor, final Object reference) {
        final Item olditem = this.getItemByItemId(item.getId());
        if (olditem != null && olditem.isStackable()) {
            final long count = item.getCount();
            olditem.changeCount(process, count, actor, reference);
            olditem.setLastChange(2);
            ItemEngine.getInstance().destroyItem(process, item, actor, reference);
            item.updateDatabase();
            item = olditem;
            final float adenaRate = Config.RATE_DROP_AMOUNT_BY_ID.getOrDefault(57, 1.0f);
            if (item.getId() == 57 && count < 10000.0f * adenaRate) {
                if (WorldTimeController.getInstance().getGameTicks() % 5 == 0) {
                    item.updateDatabase();
                }
            }
            else {
                item.updateDatabase();
            }
        }
        else {
            item.setOwnerId(process, this.getOwnerId(), actor, reference);
            item.setItemLocation(this.getBaseLocation());
            item.setLastChange(1);
            this.addItem(item);
            item.updateDatabase();
        }
        this.refreshWeight();
        return item;
    }
    
    public Item addItem(final String process, final int itemId, final long count, final Player actor, final Object reference) {
        Item item = this.getItemByItemId(itemId);
        if (item != null && item.isStackable()) {
            item.changeCount(process, count, actor, reference);
            item.setLastChange(2);
            final float adenaRate = Config.RATE_DROP_AMOUNT_BY_ID.getOrDefault(57, 1.0f);
            if (itemId == 57 && count < 10000.0f * adenaRate) {
                if (WorldTimeController.getInstance().getGameTicks() % 5 == 0) {
                    item.updateDatabase();
                }
            }
            else {
                item.updateDatabase();
            }
        }
        else {
            for (int i = 0; i < count; ++i) {
                final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);
                if (template == null) {
                    ItemContainer.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (actor != null) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, actor.getName()) : ""), (Object)itemId);
                    return null;
                }
                item = ItemEngine.getInstance().createItem(process, itemId, template.isStackable() ? count : 1L, actor, reference);
                item.setOwnerId(this.getOwnerId());
                item.setItemLocation(this.getBaseLocation());
                item.setLastChange(1);
                this.addItem(item);
                item.updateDatabase();
                if (template.isStackable()) {
                    break;
                }
                if (!Config.MULTIPLE_ITEM_DROP) {
                    break;
                }
            }
        }
        this.refreshWeight();
        return item;
    }
    
    public Item transferItem(final String process, final int objectId, long count, final ItemContainer target, final Player actor, final Object reference) {
        if (Objects.isNull(target)) {
            return null;
        }
        final Item sourceItem = this.getItemByObjectId(objectId);
        if (Objects.isNull(sourceItem)) {
            return null;
        }
        if (count > sourceItem.getCount()) {
            count = sourceItem.getCount();
        }
        Item targetItem = sourceItem.isStackable() ? target.getItemByItemId(sourceItem.getId()) : null;
        if (sourceItem.getCount() == count && Objects.isNull(targetItem) && !sourceItem.isStackable()) {
            this.removeItem(sourceItem);
            target.addItem(process, sourceItem, actor, reference);
            targetItem = sourceItem;
        }
        else {
            if (sourceItem.getCount() > count) {
                sourceItem.changeCount(process, -count, actor, reference);
            }
            else {
                this.removeItem(sourceItem);
                ItemEngine.getInstance().destroyItem(process, sourceItem, actor, reference);
            }
            if (Objects.nonNull(targetItem)) {
                targetItem.changeCount(process, count, actor, reference);
            }
            else {
                targetItem = target.addItem(process, sourceItem.getId(), count, actor, reference);
            }
        }
        sourceItem.updateDatabase(true);
        if (targetItem != sourceItem && targetItem != null) {
            targetItem.updateDatabase();
        }
        if (sourceItem.isAugmented()) {
            sourceItem.getAugmentation().removeBonus(actor);
        }
        this.refreshWeight();
        target.refreshWeight();
        return targetItem;
    }
    
    public Item detachItem(final String process, Item item, final long count, final ItemLocation newLocation, final Player actor, final Object reference) {
        if (item == null) {
            return null;
        }
        synchronized (item) {
            if (!this.items.containsKey(item.getObjectId())) {
                return null;
            }
            if (count > item.getCount()) {
                return null;
            }
            if (count == item.getCount()) {
                this.removeItem(item);
            }
            else {
                item.changeCount(process, -count, actor, reference);
                item.updateDatabase(true);
                item = ItemEngine.getInstance().createItem(process, item.getId(), count, actor, reference);
                item.setOwnerId(this.getOwnerId());
            }
            item.setItemLocation(newLocation);
            item.updateDatabase(true);
        }
        this.refreshWeight();
        return item;
    }
    
    public Item destroyItem(final String process, final Item item, final Player actor, final Object reference) {
        return this.destroyItem(process, item, item.getCount(), actor, reference);
    }
    
    public Item destroyItem(final String process, final Item item, final long count, final Player actor, final Object reference) {
        synchronized (item) {
            if (item.getCount() > count) {
                item.changeCount(process, -count, actor, reference);
                item.setLastChange(2);
                if (process != null || WorldTimeController.getInstance().getGameTicks() % 10 == 0) {
                    item.updateDatabase();
                }
                this.refreshWeight();
            }
            else {
                if (item.getCount() < count) {
                    return null;
                }
                final boolean removed = this.removeItem(item);
                if (!removed) {
                    return null;
                }
                ItemEngine.getInstance().destroyItem(process, item, actor, reference);
                item.updateDatabase();
                this.refreshWeight();
                item.deleteMe();
                item.setLastChange(3);
            }
        }
        return item;
    }
    
    public Item destroyItem(final String process, final int objectId, final long count, final Player actor, final Object reference) {
        final Item item = this.getItemByObjectId(objectId);
        if (item == null) {
            return null;
        }
        return this.destroyItem(process, item, count, actor, reference);
    }
    
    public Item destroyItemByItemId(final String process, final int itemId, final long count, final Player actor, final Object reference) {
        final Item item = this.getItemByItemId(itemId);
        if (item == null) {
            return null;
        }
        return this.destroyItem(process, item, count, actor, reference);
    }
    
    public void destroyAllItems(final String process, final Player actor, final Object reference) {
        for (final Item item : this.items.values()) {
            this.destroyItem(process, item, actor, reference);
        }
    }
    
    public long getAdena() {
        for (final Item item : this.items.values()) {
            if (item.getId() == 57) {
                return item.getCount();
            }
        }
        return 0L;
    }
    
    public long getBeautyTickets() {
        for (final Item item : this.items.values()) {
            if (item.getId() == 36308) {
                return item.getCount();
            }
        }
        return 0L;
    }
    
    protected void addItem(final Item item) {
        this.items.put(item.getObjectId(), (Object)item);
    }
    
    protected boolean removeItem(final Item item) {
        return this.items.remove(item.getObjectId()) != null;
    }
    
    protected void refreshWeight() {
    }
    
    public void deleteMe() {
        if (this.getOwner() != null) {
            for (final Item item : this.items.values()) {
                item.updateDatabase(true);
                item.deleteMe();
                World.getInstance().removeObject(item);
            }
        }
        this.items.clear();
    }
    
    public void updateDatabase() {
        if (this.getOwner() != null) {
            for (final Item item : this.items.values()) {
                item.updateDatabase(true);
            }
        }
    }
    
    public void restore() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT * FROM items WHERE owner_id=? AND (loc=?)");
                try {
                    ps.setInt(1, this.getOwnerId());
                    ps.setString(2, this.getBaseLocation().name());
                    final ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            final Item item = new Item(rs);
                            World.getInstance().addObject(item);
                            final Player owner = (this.getOwner() != null) ? this.getOwner().getActingPlayer() : null;
                            if (item.isStackable() && this.getItemByItemId(item.getId()) != null) {
                                this.addItem("Restore", item, owner, null);
                            }
                            else {
                                this.addItem(item);
                            }
                        }
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    this.refreshWeight();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t2) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            ItemContainer.LOGGER.warn("could not restore container:", (Throwable)e);
        }
    }
    
    public boolean validateCapacity(final long slots) {
        return true;
    }
    
    public boolean validateWeight(final long weight) {
        return true;
    }
    
    public boolean validateCapacityByItemId(final int itemId, final long count) {
        final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);
        return template == null || (template.isStackable() ? this.validateCapacity(1L) : this.validateCapacity(count));
    }
    
    public boolean validateWeightByItemId(final int itemId, final long count) {
        final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);
        return template == null || this.validateWeight(template.getWeight() * count);
    }
    
    public String getName() {
        return "ItemContainer";
    }
    
    public abstract Creature getOwner();
    
    protected abstract ItemLocation getBaseLocation();
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ItemContainer.class);
    }
}
