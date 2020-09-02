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

public final class ArtifactBookListener implements PlayerInventoryListener
{
    private ArtifactBookListener() {
    }
    
    @Override
    public void notifyUnequiped(final InventorySlot slot, final Item item, final Inventory inventory) {
        if (item.getBodyPart() == BodyPart.ARTIFACT_BOOK) {
            final EnumSet<InventorySlot> balanceArtifacts = InventorySlot.balanceArtifacts();
            Objects.requireNonNull(inventory);
            balanceArtifacts.forEach(inventory::unEquipItemInSlot);
            final EnumSet<InventorySlot> spiritArtifacts = InventorySlot.spiritArtifacts();
            Objects.requireNonNull(inventory);
            spiritArtifacts.forEach(inventory::unEquipItemInSlot);
            final EnumSet<InventorySlot> supportArtifact = InventorySlot.supportArtifact();
            Objects.requireNonNull(inventory);
            supportArtifact.forEach(inventory::unEquipItemInSlot);
            final EnumSet<InventorySlot> protectionArtifacts = InventorySlot.protectionArtifacts();
            Objects.requireNonNull(inventory);
            protectionArtifacts.forEach(inventory::unEquipItemInSlot);
        }
    }
    
    @Override
    public void notifyEquiped(final InventorySlot slot, final Item item, final Inventory inventory) {
    }
    
    public static ArtifactBookListener provider() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final ArtifactBookListener INSTANCE;
        
        static {
            INSTANCE = new ArtifactBookListener();
        }
    }
}
