// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver;

import java.util.Iterator;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.commons.threading.ThreadPool;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.List;

public final class ItemsAutoDestroy
{
    private final List<Item> _items;
    
    private ItemsAutoDestroy() {
        this._items = new LinkedList<Item>();
        ThreadPool.scheduleAtFixedRate(this::removeItems, ChronoUnit.MINUTES.getDuration(), ChronoUnit.MINUTES.getDuration());
    }
    
    public synchronized void addItem(final Item item) {
        item.setDropTime(System.currentTimeMillis());
        this._items.add(item);
    }
    
    private synchronized void removeItems() {
        if (this._items.isEmpty()) {
            return;
        }
        final long curtime = System.currentTimeMillis();
        final Iterator<Item> itemIterator = this._items.iterator();
        while (itemIterator.hasNext()) {
            final Item item = itemIterator.next();
            if (item.getDropTime() == 0L || item.getItemLocation() != ItemLocation.VOID) {
                itemIterator.remove();
            }
            else {
                final GeneralSettings generalSettings = (GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class);
                long autoDestroyTime;
                if (item.getTemplate().getAutoDestroyTime() > 0) {
                    autoDestroyTime = item.getTemplate().getAutoDestroyTime();
                }
                else if (item.getTemplate().hasExImmediateEffect()) {
                    autoDestroyTime = generalSettings.autoDestroyHerbTime();
                }
                else if ((autoDestroyTime = generalSettings.autoDestroyItemTime()) == 0L) {
                    autoDestroyTime = 3600000L;
                }
                if (curtime - item.getDropTime() <= autoDestroyTime) {
                    continue;
                }
                item.decayMe();
                itemIterator.remove();
                if (!generalSettings.saveDroppedItems()) {
                    continue;
                }
                ItemsOnGroundManager.getInstance().removeObject(item);
            }
        }
    }
    
    public static ItemsAutoDestroy getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final ItemsAutoDestroy INSTANCE;
        
        static {
            INSTANCE = new ItemsAutoDestroy();
        }
    }
}
