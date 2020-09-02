// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.function.ToIntFunction;
import java.util.Objects;
import java.util.Iterator;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import io.github.joealisson.primitive.LinkedHashIntSet;
import org.l2j.gameserver.model.stats.BaseStats;
import java.util.Map;
import org.l2j.gameserver.model.holders.ArmorsetSkillHolder;
import java.util.List;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.EnumSet;

public final class ArmorSet
{
    private static final EnumSet<InventorySlot> ARTIFACT_1_SLOTS;
    private static final EnumSet<InventorySlot> ARTIFACT_2_SLOTS;
    private static final EnumSet<InventorySlot> ARTIFACT_3_SLOTS;
    private final int id;
    private final int minimumPieces;
    private final boolean isVisual;
    private final IntSet requiredItems;
    private final IntSet optionalItems;
    private final List<ArmorsetSkillHolder> skills;
    private final Map<BaseStats, Double> _stats;
    
    public ArmorSet(final int id, final int minimumPieces, final boolean isVisual) {
        this.requiredItems = (IntSet)new LinkedHashIntSet();
        this.optionalItems = (IntSet)new LinkedHashIntSet();
        this.skills = new ArrayList<ArmorsetSkillHolder>();
        this._stats = new LinkedHashMap<BaseStats, Double>();
        this.id = id;
        this.minimumPieces = minimumPieces;
        this.isVisual = isVisual;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getMinimumPieces() {
        return this.minimumPieces;
    }
    
    public boolean isVisual() {
        return this.isVisual;
    }
    
    public boolean addRequiredItem(final int item) {
        return this.requiredItems.add(item);
    }
    
    public IntSet getRequiredItems() {
        return this.requiredItems;
    }
    
    public boolean addOptionalItem(final int item) {
        return this.optionalItems.add(item);
    }
    
    public IntSet getOptionalItems() {
        return this.optionalItems;
    }
    
    public void addSkill(final ArmorsetSkillHolder holder) {
        this.skills.add(holder);
    }
    
    public List<ArmorsetSkillHolder> getSkills() {
        return this.skills;
    }
    
    public void addStatsBonus(final BaseStats stat, final double value) {
        this._stats.putIfAbsent(stat, value);
    }
    
    public double getStatsBonus(final BaseStats stat) {
        return this._stats.getOrDefault(stat, 0.0);
    }
    
    public int getLowestSetEnchant(final Player player) {
        if (this.getPiecesCount(player, Item::getId) < this.minimumPieces) {
            return 0;
        }
        final PlayerInventory inv = player.getInventory();
        int enchantLevel = 127;
        for (final InventorySlot armorSlot : InventorySlot.armorset()) {
            final Item itemPart = inv.getPaperdollItem(armorSlot);
            if (itemPart != null && this.requiredItems.contains(itemPart.getId()) && enchantLevel > itemPart.getEnchantLevel()) {
                enchantLevel = itemPart.getEnchantLevel();
            }
        }
        if (enchantLevel == 127) {
            enchantLevel = 0;
        }
        return enchantLevel;
    }
    
    public int getArtifactSlotMask(final Player player, final int bookSlot) {
        int slotMask = 0;
        EnumSet<InventorySlot> set = null;
        switch (bookSlot) {
            case 1: {
                set = ArmorSet.ARTIFACT_1_SLOTS;
                break;
            }
            case 2: {
                set = ArmorSet.ARTIFACT_2_SLOTS;
                break;
            }
            case 3: {
                set = ArmorSet.ARTIFACT_3_SLOTS;
                break;
            }
            default: {
                set = null;
                break;
            }
        }
        final EnumSet<InventorySlot> slots = set;
        if (Objects.nonNull(slots)) {
            final PlayerInventory inv = player.getInventory();
            for (final InventorySlot slot : slots) {
                if (!inv.isPaperdollSlotEmpty(slot) && this.requiredItems.contains(inv.getPaperdollItemId(slot))) {
                    slotMask += slot.getMask();
                }
            }
        }
        return slotMask;
    }
    
    public boolean hasOptionalEquipped(final Player player, final ToIntFunction<Item> idProvider) {
        return player.getInventory().existsEquippedItem(item -> this.optionalItems.contains(idProvider.applyAsInt(item)));
    }
    
    public int getPiecesCount(final Player player, final ToIntFunction<Item> idProvider) {
        return player.getInventory().countEquippedItems(item -> this.requiredItems.contains(idProvider.applyAsInt(item)));
    }
    
    static {
        ARTIFACT_1_SLOTS = EnumSet.of(InventorySlot.ARTIFACT1, new InventorySlot[] { InventorySlot.ARTIFACT2, InventorySlot.ARTIFACT3, InventorySlot.ARTIFACT4, InventorySlot.ARTIFACT13, InventorySlot.ARTIFACT16, InventorySlot.ARTIFACT19 });
        ARTIFACT_2_SLOTS = EnumSet.of(InventorySlot.ARTIFACT5, new InventorySlot[] { InventorySlot.ARTIFACT6, InventorySlot.ARTIFACT7, InventorySlot.ARTIFACT8, InventorySlot.ARTIFACT14, InventorySlot.ARTIFACT17, InventorySlot.ARTIFACT20 });
        ARTIFACT_3_SLOTS = EnumSet.of(InventorySlot.ARTIFACT9, new InventorySlot[] { InventorySlot.ARTIFACT10, InventorySlot.ARTIFACT11, InventorySlot.ARTIFACT12, InventorySlot.ARTIFACT15, InventorySlot.ARTIFACT18, InventorySlot.ARTIFACT21 });
    }
}
