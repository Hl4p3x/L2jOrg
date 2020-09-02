// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.model.stats.functions.FuncTemplate;
import org.l2j.gameserver.model.actor.stat.CreatureStats;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.api.item.InventoryListener;

public final class StatsListener implements InventoryListener
{
    private StatsListener() {
    }
    
    @Override
    public void notifyUnequiped(final InventorySlot slot, final Item item, final Inventory inventory) {
        final CreatureStats charStat = inventory.getOwner().getStats();
        final CreatureStats creatureStats;
        item.getTemplate().getFunctionTemplates().forEach(func -> {
            if (func.getStat().hasDefaultFinalizer()) {
                creatureStats.removeAddAdditionalStat(func.getStat(), func.getValue());
            }
            return;
        });
        inventory.getOwner().getStats().recalculateStats(true);
    }
    
    @Override
    public void notifyEquiped(final InventorySlot slot, final Item item, final Inventory inventory) {
        final CreatureStats charStat = inventory.getOwner().getStats();
        final CreatureStats creatureStats;
        item.getTemplate().getFunctionTemplates().forEach(func -> {
            if (func.getStat().hasDefaultFinalizer()) {
                creatureStats.addAdditionalStat(func.getStat(), func.getValue());
            }
            return;
        });
        inventory.getOwner().getStats().recalculateStats(true);
    }
    
    public static StatsListener provider() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final StatsListener INSTANCE;
        
        static {
            INSTANCE = new StatsListener();
        }
    }
}
