// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.item.container.listener;

import java.util.Iterator;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.enums.ItemSkillType;
import java.util.EnumSet;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.BodyPart;
import java.util.function.Consumer;
import java.util.Objects;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.api.item.PlayerInventoryListener;

public final class BroochListener implements PlayerInventoryListener
{
    private BroochListener() {
    }
    
    @Override
    public void notifyUnequiped(final InventorySlot slot, final Item item, final Inventory inventory) {
        if (slot == InventorySlot.BROOCH) {
            final EnumSet<InventorySlot> brochesJewel = InventorySlot.brochesJewel();
            Objects.requireNonNull(inventory);
            brochesJewel.forEach(inventory::unEquipItemInSlot);
        }
        else {
            final PlayerInventory inv;
            if (item.getBodyPart() == BodyPart.BROOCH_JEWEL && inventory instanceof PlayerInventory && (inv = (PlayerInventory)inventory) == inventory) {
                this.updateAdditionalSoulshot(inv);
            }
        }
    }
    
    private void updateAdditionalSoulshot(final PlayerInventory inventory) {
        int jewel = 0;
        int currentLevel = -1;
        for (final InventorySlot slot : InventorySlot.brochesJewel()) {
            final Item item = inventory.getPaperdollItem(slot);
            if (Objects.nonNull(item)) {
                final int itemLevel = item.getSkills(ItemSkillType.NORMAL).stream().mapToInt(SkillHolder::getLevel).max().orElse(-1);
                if (jewel != 0 && itemLevel <= currentLevel) {
                    continue;
                }
                jewel = item.getId();
                currentLevel = itemLevel;
            }
        }
        inventory.getOwner().setAdditionalSoulshot(jewel);
    }
    
    @Override
    public void notifyEquiped(final InventorySlot slot, final Item item, final Inventory inventory) {
        final PlayerInventory inv;
        if (item.getBodyPart() == BodyPart.BROOCH_JEWEL && inventory instanceof PlayerInventory && (inv = (PlayerInventory)inventory) == inventory) {
            this.updateAdditionalSoulshot(inv);
        }
    }
    
    public static BroochListener provider() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final BroochListener INSTANCE;
        
        static {
            INSTANCE = new BroochListener();
        }
    }
}
