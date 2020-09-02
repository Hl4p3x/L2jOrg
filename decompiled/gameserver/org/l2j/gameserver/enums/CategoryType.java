// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum CategoryType
{
    FIGHTER_GROUP, 
    MAGE_GROUP, 
    WIZARD_GROUP, 
    CLERIC_GROUP, 
    ATTACKER_GROUP, 
    FIRST_CLASS_GROUP, 
    SECOND_CLASS_GROUP, 
    THIRD_CLASS_GROUP, 
    FOURTH_CLASS_GROUP, 
    BOUNTY_HUNTER_GROUP, 
    WARSMITH_GROUP, 
    STRIDER, 
    WOLF_GROUP, 
    WYVERN_GROUP, 
    SUBJOB_GROUP_KNIGHT, 
    HUMAN_FALL_CLASS, 
    HUMAN_MALL_CLASS, 
    HUMAN_CALL_CLASS, 
    ELF_FALL_CLASS, 
    ELF_MALL_CLASS, 
    ELF_CALL_CLASS, 
    ORC_FALL_CLASS, 
    ORC_MALL_CLASS, 
    BEGINNER_MAGE, 
    SUB_GROUP_ROGUE, 
    SUB_GROUP_KNIGHT, 
    SUB_GROUP_HEC, 
    SUB_GROUP_HEW, 
    SUB_GROUP_HEF, 
    SUB_GROUP_ORC, 
    SUB_GROUP_WARE, 
    SUB_GROUP_BLACK, 
    SUB_GROUP_DE;
    
    public static CategoryType findByName(final String categoryName) {
        for (final CategoryType type : values()) {
            if (type.name().equalsIgnoreCase(categoryName)) {
                return type;
            }
        }
        return null;
    }
}