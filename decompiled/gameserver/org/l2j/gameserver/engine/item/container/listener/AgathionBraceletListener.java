// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.item.container.listener;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.Objects;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.api.item.PlayerInventoryListener;

public final class AgathionBraceletListener implements PlayerInventoryListener
{
    private AgathionBraceletListener() {
    }
    
    @Override
    public void notifyUnequiped(final InventorySlot slot, final Item item, final Inventory inventory) {
        if (item.getBodyPart() == BodyPart.LEFT_BRACELET) {
            final EnumSet<InventorySlot> agathions = InventorySlot.agathions();
            Objects.requireNonNull(inventory);
            agathions.forEach(inventory::unEquipItemInSlot);
        }
    }
    
    @Override
    public void notifyEquiped(final InventorySlot slot, final Item item, final Inventory inventory) {
    }
    
    public static AgathionBraceletListener provider() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final AgathionBraceletListener INSTANCE;
        
        static {
            INSTANCE = new AgathionBraceletListener();
        }
    }
}
