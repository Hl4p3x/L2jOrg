// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.api.item;

import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.InventorySlot;

public interface InventoryListener
{
    void notifyEquiped(final InventorySlot slot, final Item inst, final Inventory inventory);
    
    void notifyUnequiped(final InventorySlot slot, final Item inst, final Inventory inventory);
}
