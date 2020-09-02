// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.Collection;
import java.util.List;
import org.l2j.gameserver.model.item.instance.Item;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.model.ItemInfo;
import io.github.joealisson.primitive.IntMap;

public abstract class AbstractInventoryUpdate extends AbstractItemPacket
{
    private final IntMap<ItemInfo> items;
    
    protected AbstractInventoryUpdate() {
        this.items = (IntMap<ItemInfo>)new CHashIntMap();
    }
    
    protected AbstractInventoryUpdate(final Item item) {
        this();
        this.addItem(item);
    }
    
    protected AbstractInventoryUpdate(final List<ItemInfo> items) {
        this.items = streamToMap(items.stream());
    }
    
    protected AbstractInventoryUpdate(final Collection<Item> items) {
        this.items = streamToMap(items.stream().map((Function<? super Item, ? extends ItemInfo>)ItemInfo::new));
    }
    
    private static IntMap<ItemInfo> streamToMap(final Stream<ItemInfo> stream) {
        return (IntMap<ItemInfo>)stream.collect((Supplier<IntMap>)CHashIntMap::new, (map, item) -> map.put(item.getObjectId(), (Object)item), IntMap::putAll);
    }
    
    public final void addItem(final Item item) {
        this.items.put(item.getObjectId(), (Object)new ItemInfo(item));
    }
    
    public final void addNewItem(final Item item) {
        this.items.put(item.getObjectId(), (Object)new ItemInfo(item, 1));
    }
    
    public final void addModifiedItem(final Item item) {
        this.items.put(item.getObjectId(), (Object)new ItemInfo(item, 2));
    }
    
    public final void addRemovedItem(final Item item) {
        this.items.put(item.getObjectId(), (Object)new ItemInfo(item, 3));
    }
    
    public final boolean isEmpty() {
        return this.items.isEmpty();
    }
    
    protected final void writeItems() {
        this.writeByte(0);
        this.writeInt(this.items.size());
        this.writeInt(this.items.size());
        for (final ItemInfo item : this.items.values()) {
            this.writeShort(item.getChange());
            this.writeItem(item);
        }
    }
}
