// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.commission;

import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

public enum CommissionTreeType
{
    WEAPON(0, new CommissionItemType[] { CommissionItemType.ONE_HAND_SWORD, CommissionItemType.ONE_HAND_MAGIC_SWORD, CommissionItemType.DAGGER, CommissionItemType.RAPIER, CommissionItemType.TWO_HAND_SWORD, CommissionItemType.ANCIENT_SWORD, CommissionItemType.DUALSWORD, CommissionItemType.DUAL_DAGGER, CommissionItemType.BLUNT_WEAPON, CommissionItemType.ONE_HAND_MAGIC_BLUNT_WEAPON, CommissionItemType.TWO_HAND_BLUNT_WEAPON, CommissionItemType.TWO_HAND_MAGIC_BLUNT_WEAPON, CommissionItemType.DUAL_BLUNT_WEAPON, CommissionItemType.BOW, CommissionItemType.CROSSBOW, CommissionItemType.FIST_WEAPON, CommissionItemType.SPEAR, CommissionItemType.OTHER_WEAPON }), 
    ARMOR(1, new CommissionItemType[] { CommissionItemType.HELMET, CommissionItemType.ARMOR_TOP, CommissionItemType.ARMOR_PANTS, CommissionItemType.FULL_BODY, CommissionItemType.GLOVES, CommissionItemType.FEET, CommissionItemType.SHIELD, CommissionItemType.SIGIL, CommissionItemType.UNDERWEAR, CommissionItemType.CLOAK }), 
    ACCESSORY(2, new CommissionItemType[] { CommissionItemType.RING, CommissionItemType.EARRING, CommissionItemType.NECKLACE, CommissionItemType.BELT, CommissionItemType.BRACELET, CommissionItemType.HAIR_ACCESSORY }), 
    SUPPLIES(3, new CommissionItemType[] { CommissionItemType.POTION, CommissionItemType.SCROLL_ENCHANT_WEAPON, CommissionItemType.SCROLL_ENCHANT_ARMOR, CommissionItemType.SCROLL_OTHER, CommissionItemType.SOULSHOT, CommissionItemType.SPIRITSHOT }), 
    PET_GOODS(4, new CommissionItemType[] { CommissionItemType.PET_EQUIPMENT, CommissionItemType.PET_SUPPLIES }), 
    MISC(5, new CommissionItemType[] { CommissionItemType.CRYSTAL, CommissionItemType.RECIPE, CommissionItemType.MAJOR_CRAFTING_INGREDIENTS, CommissionItemType.LIFE_STONE, CommissionItemType.SOUL_CRYSTAL, CommissionItemType.ATTRIBUTE_STONE, CommissionItemType.WEAPON_ENCHANT_STONE, CommissionItemType.ARMOR_ENCHANT_STONE, CommissionItemType.SPELLBOOK, CommissionItemType.GEMSTONE, CommissionItemType.POUCH, CommissionItemType.PIN, CommissionItemType.MAGIC_RUNE_CLIP, CommissionItemType.MAGIC_ORNAMENT, CommissionItemType.DYES, CommissionItemType.OTHER_ITEM });
    
    private final int _clientId;
    private final Set<CommissionItemType> _commissionItemTypes;
    
    private CommissionTreeType(final int clientId, final CommissionItemType[] commissionItemTypes) {
        this._clientId = clientId;
        this._commissionItemTypes = Collections.unmodifiableSet((Set<? extends CommissionItemType>)new HashSet<CommissionItemType>(Arrays.asList(commissionItemTypes)));
    }
    
    public static CommissionTreeType findByClientId(final int clientId) {
        for (final CommissionTreeType value : values()) {
            if (value.getClientId() == clientId) {
                return value;
            }
        }
        return null;
    }
    
    public int getClientId() {
        return this._clientId;
    }
    
    public Set<CommissionItemType> getCommissionItemTypes() {
        return this._commissionItemTypes;
    }
}
