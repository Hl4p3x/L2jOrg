// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.type;

import org.l2j.gameserver.model.stats.TraitType;

public enum WeaponType implements ItemType
{
    NONE(TraitType.NONE), 
    SWORD(TraitType.SWORD), 
    TWO_HAND_SWORD(TraitType.TWO_HAND_SWORD), 
    MAGIC_SWORD(TraitType.MAGIC_SWORD), 
    BLUNT(TraitType.BLUNT), 
    HAMMER(TraitType.HAMMER), 
    ROD(TraitType.ROD), 
    STAFF(TraitType.STAFF), 
    DAGGER(TraitType.DAGGER), 
    SPEAR(TraitType.SPEAR), 
    FIST(TraitType.FIST), 
    BOW(TraitType.BOW), 
    ETC(TraitType.ETC), 
    DUAL(TraitType.DUAL), 
    DUAL_FIST(TraitType.DUALFIST), 
    FISHING_ROD(TraitType.NONE), 
    RAPIER(TraitType.RAPIER), 
    CROSSBOW(TraitType.CROSSBOW), 
    ANCIENT_SWORD(TraitType.ANCIENT_SWORD), 
    FLAG(TraitType.NONE), 
    DUAL_DAGGER(TraitType.DUAL_DAGGER), 
    OWN_THING(TraitType.NONE), 
    TWO_HAND_CROSSBOW(TraitType.TWO_HAND_CROSSBOW), 
    DUAL_BLUNT(TraitType.DUAL_BLUNT);
    
    private final int _mask;
    private final TraitType _traitType;
    
    private WeaponType(final TraitType traitType) {
        this._mask = 1 << this.ordinal();
        this._traitType = traitType;
    }
    
    @Override
    public int mask() {
        return this._mask;
    }
    
    public TraitType getTraitType() {
        return this._traitType;
    }
    
    @Override
    public boolean isRanged() {
        return this == WeaponType.BOW || this == WeaponType.CROSSBOW || this == WeaponType.TWO_HAND_CROSSBOW;
    }
    
    public boolean isCrossbow() {
        return this == WeaponType.CROSSBOW || this == WeaponType.TWO_HAND_CROSSBOW;
    }
    
    public boolean isDual() {
        return this == WeaponType.FIST || this == WeaponType.DUAL || this == WeaponType.DUAL_DAGGER || this == WeaponType.DUAL_BLUNT;
    }
}
