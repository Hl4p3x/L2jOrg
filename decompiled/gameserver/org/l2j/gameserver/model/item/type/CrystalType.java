// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.type;

import java.util.function.Consumer;

public enum CrystalType
{
    NONE(0, 0, 0, 0), 
    D(1, 1458, 11, 90), 
    C(2, 1459, 6, 45), 
    B(3, 1460, 11, 67), 
    A(4, 1461, 20, 145), 
    S(5, 1462, 25, 250), 
    EVENT(11, 0, 0, 0);
    
    private final int id;
    private final int crystalId;
    private final int crystalEnchantBonusArmor;
    private final int crystalEnchantBonusWeapon;
    private static final CrystalType[] CACHED;
    
    private CrystalType(final int id, final int crystalId, final int crystalEnchantBonusArmor, final int crystalEnchantBonusWeapon) {
        this.id = id;
        this.crystalId = crystalId;
        this.crystalEnchantBonusArmor = crystalEnchantBonusArmor;
        this.crystalEnchantBonusWeapon = crystalEnchantBonusWeapon;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getCrystalId() {
        return this.crystalId;
    }
    
    public int getCrystalEnchantBonusArmor() {
        return this.crystalEnchantBonusArmor;
    }
    
    public int getCrystalEnchantBonusWeapon() {
        return this.crystalEnchantBonusWeapon;
    }
    
    public static void forEach(final Consumer<CrystalType> action) {
        for (final CrystalType crystalType : CrystalType.CACHED) {
            action.accept(crystalType);
        }
    }
    
    static {
        CACHED = values();
    }
}
