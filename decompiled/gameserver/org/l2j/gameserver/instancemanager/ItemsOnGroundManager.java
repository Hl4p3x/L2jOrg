// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.gameserver.data.database.data.ItemOnGroundData;
import java.util.List;
import org.l2j.gameserver.ItemsAutoDestroy;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import java.util.function.Function;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.commons.threading.ThreadPool;
import java.time.Duration;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Set;

public final class ItemsOnGroundManager implements Runnable
{
    private final Set<Item> items;
    
    private ItemsOnGroundManager() {
        this.items = (Set<Item>)ConcurrentHashMap.newKeySet();
        final Duration saveDropItemInterval = ((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).saveDroppedItemInterval();
        if (saveDropItemInterval.compareTo(Duration.ZERO) > 0) {
            ThreadPool.scheduleAtFixedRate((Runnable)this, saveDropItemInterval, saveDropItemInterval);
        }
    }
    
    private void load() {
        final ItemDAO itemDAO = (ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class);
        final GeneralSettings generalSettings = (GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class);
        if (!generalSettings.saveDroppedItems()) {
            if (generalSettings.clearDroppedItems()) {
                itemDAO.deleteItemsOnGround();
            }
            return;
        }
        if (generalSettings.destroyPlayerDroppedItem()) {
            if (!generalSettings.destroyEquipableItem()) {
                itemDAO.updateNonEquipDropTimeByNonDestroyable(System.currentTimeMillis());
            }
            else {
                itemDAO.updateDropTimeByNonDestroyable(System.currentTimeMillis());
            }
        }
        final GeneralSettings generalSettings2;
        itemDAO.findAllItemsOnGround().stream().map((Function<? super Object, ?>)Item::new).forEach(item -> {
            World.getInstance().addObject(item);
            this.items.add(item);
            if (!generalSettings2.isProtectedItem(item.getId()) && !item.isProtected() && ((generalSettings2.autoDestroyItemTime() > 0 && !item.getTemplate().hasExImmediateEffect()) || (generalSettings2.autoDestroyHerbTime() > 0 && item.getTemplate().hasExImmediateEffect()))) {
                ItemsAutoDestroy.getInstance().addItem(item);
            }
            return;
        });
        if (generalSettings.clearDroppedItemsAfterLoad()) {
            itemDAO.deleteItemsOnGround();
        }
    }
    
    public void save(final Item item) {
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).saveDroppedItems()) {
            this.items.add(item);
        }
    }
    
    public void removeObject(final Item item) {
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).saveDroppedItems()) {
            this.items.remove(item);
        }
    }
    
    public void saveInDb() {
        this.run();
    }
    
    public void cleanUp() {
        this.items.clear();
    }
    
    @Override
    public synchronized void run() {
        if (!((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).saveDroppedItems()) {
            return;
        }
        final ItemDAO itemDAO = (ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class);
        itemDAO.deleteItemsOnGround();
        if (this.items.isEmpty()) {
            return;
        }
        itemDAO.save((Collection<ItemOnGroundData>)this.items.stream().map((Function<? super Object, ?>)ItemOnGroundData::of).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static ItemsOnGroundManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final ItemsOnGroundManager INSTANCE;
        
        static {
            INSTANCE = new ItemsOnGroundManager();
        }
    }
}
