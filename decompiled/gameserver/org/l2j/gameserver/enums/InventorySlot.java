// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import java.util.EnumSet;
import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;

public enum InventorySlot implements IUpdateTypeComponent
{
    PENDANT, 
    RIGHT_EAR, 
    LEFT_EAR, 
    NECK, 
    RIGHT_FINGER, 
    LEFT_FINGER, 
    HEAD, 
    RIGHT_HAND, 
    LEFT_HAND, 
    GLOVES, 
    CHEST, 
    LEGS, 
    FEET, 
    CLOAK, 
    TWO_HAND, 
    HAIR, 
    HAIR2, 
    RIGHT_BRACELET, 
    LEFT_BRACELET, 
    AGATHION1, 
    AGATHION2, 
    AGATHION3, 
    AGATHION4, 
    AGATHION5, 
    TALISMAN1, 
    TALISMAN2, 
    TALISMAN3, 
    TALISMAN4, 
    TALISMAN5, 
    TALISMAN6, 
    BELT, 
    BROOCH, 
    BROOCH_JEWEL1, 
    BROOCH_JEWEL2, 
    BROOCH_JEWEL3, 
    BROOCH_JEWEL4, 
    BROOCH_JEWEL5, 
    BROOCH_JEWEL6, 
    ARTIFACT_BOOK, 
    ARTIFACT1, 
    ARTIFACT2, 
    ARTIFACT3, 
    ARTIFACT4, 
    ARTIFACT5, 
    ARTIFACT6, 
    ARTIFACT7, 
    ARTIFACT8, 
    ARTIFACT9, 
    ARTIFACT10, 
    ARTIFACT11, 
    ARTIFACT12, 
    ARTIFACT13, 
    ARTIFACT14, 
    ARTIFACT15, 
    ARTIFACT16, 
    ARTIFACT17, 
    ARTIFACT18, 
    ARTIFACT19, 
    ARTIFACT20, 
    ARTIFACT21;
    
    private static final EnumSet<InventorySlot> accessories;
    private static final EnumSet<InventorySlot> armors;
    private static final EnumSet<InventorySlot> balanceArtifacts;
    private static final EnumSet<InventorySlot> spiritArtifacts;
    private static final EnumSet<InventorySlot> protectionArtifacts;
    private static final EnumSet<InventorySlot> supportArtifact;
    private static final EnumSet<InventorySlot> agathions;
    private static final EnumSet<InventorySlot> broochesJewel;
    private static final EnumSet<InventorySlot> talismans;
    private static final EnumSet<InventorySlot> armorset;
    public static final int TOTAL_SLOTS = 60;
    public static final InventorySlot[] CACHE;
    
    public int getId() {
        return this.ordinal();
    }
    
    @Override
    public int getMask() {
        return this.ordinal();
    }
    
    public static InventorySlot fromId(final int id) {
        if (id < 0 || id > InventorySlot.ARTIFACT21.ordinal()) {
            return null;
        }
        return InventorySlot.CACHE[id];
    }
    
    public static InventorySlot[] cachedValues() {
        return InventorySlot.CACHE;
    }
    
    public static EnumSet<InventorySlot> accessories() {
        return InventorySlot.accessories;
    }
    
    public static EnumSet<InventorySlot> armors() {
        return InventorySlot.armors;
    }
    
    public static EnumSet<InventorySlot> balanceArtifacts() {
        return InventorySlot.balanceArtifacts;
    }
    
    public static EnumSet<InventorySlot> spiritArtifacts() {
        return InventorySlot.spiritArtifacts;
    }
    
    public static EnumSet<InventorySlot> protectionArtifacts() {
        return InventorySlot.protectionArtifacts;
    }
    
    public static EnumSet<InventorySlot> supportArtifact() {
        return InventorySlot.supportArtifact;
    }
    
    public static EnumSet<InventorySlot> agathions() {
        return InventorySlot.agathions;
    }
    
    public static EnumSet<InventorySlot> brochesJewel() {
        return InventorySlot.broochesJewel;
    }
    
    public static EnumSet<InventorySlot> talismans() {
        return InventorySlot.talismans;
    }
    
    public static EnumSet<InventorySlot> armorset() {
        return InventorySlot.armorset;
    }
    
    static {
        accessories = EnumSet.of(InventorySlot.LEFT_FINGER, InventorySlot.RIGHT_FINGER, InventorySlot.LEFT_EAR, InventorySlot.RIGHT_EAR, InventorySlot.NECK);
        armors = EnumSet.of(InventorySlot.CHEST, InventorySlot.LEGS, InventorySlot.HEAD, InventorySlot.FEET, InventorySlot.GLOVES, InventorySlot.LEFT_HAND, InventorySlot.PENDANT, InventorySlot.CLOAK, InventorySlot.BELT, InventorySlot.HAIR, InventorySlot.HAIR2);
        balanceArtifacts = EnumSet.range(InventorySlot.ARTIFACT1, InventorySlot.ARTIFACT12);
        spiritArtifacts = EnumSet.range(InventorySlot.ARTIFACT13, InventorySlot.ARTIFACT15);
        protectionArtifacts = EnumSet.range(InventorySlot.ARTIFACT16, InventorySlot.ARTIFACT18);
        supportArtifact = EnumSet.range(InventorySlot.ARTIFACT19, InventorySlot.ARTIFACT21);
        agathions = EnumSet.range(InventorySlot.AGATHION1, InventorySlot.AGATHION5);
        broochesJewel = EnumSet.range(InventorySlot.BROOCH_JEWEL1, InventorySlot.BROOCH_JEWEL6);
        talismans = EnumSet.range(InventorySlot.TALISMAN1, InventorySlot.TALISMAN6);
        armorset = EnumSet.of(InventorySlot.CHEST, InventorySlot.LEGS, InventorySlot.HEAD, InventorySlot.GLOVES, InventorySlot.FEET);
        CACHE = values();
    }
}
