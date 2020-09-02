// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats;

import java.util.EnumSet;

public enum TraitType
{
    NONE(0), 
    SWORD(1), 
    BLUNT(1), 
    DAGGER(1), 
    SPEAR(1), 
    FIST(1), 
    BOW(1), 
    HAMMER(1), 
    ETC(1), 
    UNK_8(0), 
    POISON(3), 
    HOLD(3), 
    BLEED(3), 
    SLEEP(3), 
    SHOCK(3), 
    DERANGEMENT(3), 
    BUG_WEAKNESS(2), 
    ANIMAL_WEAKNESS(2), 
    PLANT_WEAKNESS(2), 
    BEAST_WEAKNESS(2), 
    DRAGON_WEAKNESS(2), 
    PARALYZE(3), 
    DUAL(1), 
    TWO_HAND_SWORD(1), 
    MAGIC_SWORD(1), 
    DUALFIST(1), 
    BOSS(3), 
    GIANT_WEAKNESS(2), 
    CONSTRUCT_WEAKNESS(2), 
    DEATH(3), 
    VALAKAS(2), 
    ANESTHESIA(2), 
    CRITICAL_POISON(3), 
    ROOT_PHYSICALLY(3), 
    ROOT_MAGICALLY(3), 
    RAPIER(1), 
    CROSSBOW(1), 
    ANCIENT_SWORD(1), 
    TURN_STONE(3), 
    GUST(3), 
    PHYSICAL_BLOCKADE(3), 
    TARGET(3), 
    PHYSICAL_WEAKNESS(3), 
    MAGICAL_WEAKNESS(3), 
    DUAL_DAGGER(1), 
    DEMONIC_WEAKNESS(2), 
    DIVINE_WEAKNESS(2), 
    ELEMENTAL_WEAKNESS(2), 
    FAIRY_WEAKNESS(2), 
    HUMAN_WEAKNESS(2), 
    HUMANOID_WEAKNESS(2), 
    UNDEAD_WEAKNESS(2), 
    DUAL_BLUNT(1), 
    KNOCKBACK(3), 
    KNOCKDOWN(3), 
    PULL(3), 
    HATE(3), 
    AGGRESSION(3), 
    AIRBIND(3), 
    DISARM(3), 
    DEPORT(3), 
    CHANGEBODY(3), 
    TWO_HAND_CROSSBOW(1), 
    ZONE(3), 
    PSYCHIC(3), 
    EMBRYO_WEAKNESS(2), 
    SPIRIT_WEAKNESS(2), 
    ROD(1), 
    STAFF(1);
    
    private final int _type;
    private static final EnumSet<TraitType> WEAKNESSES;
    
    private TraitType(final int type) {
        this._type = type;
    }
    
    public int getType() {
        return this._type;
    }
    
    public static EnumSet<TraitType> getAllWeakness() {
        return TraitType.WEAKNESSES;
    }
    
    static {
        WEAKNESSES = EnumSet.of(TraitType.BUG_WEAKNESS, TraitType.ANIMAL_WEAKNESS, TraitType.PLANT_WEAKNESS, TraitType.BEAST_WEAKNESS, TraitType.DRAGON_WEAKNESS, TraitType.GIANT_WEAKNESS, TraitType.CONSTRUCT_WEAKNESS, TraitType.VALAKAS, TraitType.ANESTHESIA, TraitType.DEMONIC_WEAKNESS, TraitType.DIVINE_WEAKNESS, TraitType.ELEMENTAL_WEAKNESS, TraitType.FAIRY_WEAKNESS, TraitType.HUMAN_WEAKNESS, TraitType.HUMANOID_WEAKNESS, TraitType.UNDEAD_WEAKNESS, TraitType.EMBRYO_WEAKNESS, TraitType.SPIRIT_WEAKNESS);
    }
}
