// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.api.item.PlayerInventoryListener;

public final class BowCrossListener implements PlayerInventoryListener
{
    private BowCrossListener() {
    }
    
    @Override
    public void notifyUnequiped(final InventorySlot slot, final Item item, final Inventory inv) {
    }
    
    @Override
    public void notifyEquiped(final InventorySlot slot, final Item item, final Inventory inv) {
        if (slot != InventorySlot.RIGHT_HAND && slot != InventorySlot.TWO_HAND) {
            return;
        }
        final PlayerInventory inventory;
        if (inv instanceof PlayerInventory && (inventory = (PlayerInventory)inv) == inv) {
            inventory.findAmmunitionForCurrentWeapon();
        }
    }
    
    public static BowCrossListener provider() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final BowCrossListener INSTANCE;
        
        static {
            INSTANCE = new BowCrossListener();
        }
    }
}
