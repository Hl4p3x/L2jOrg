// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;

public enum UserInfoType implements IUpdateTypeComponent
{
    RELATION(0, 4), 
    BASIC_INFO(1, 19), 
    BASE_STATS(2, 18), 
    MAX_HPCPMP(3, 14), 
    CURRENT_HPMPCP_EXP_SP(4, 38), 
    ENCHANTLEVEL(5, 4), 
    APPAREANCE(6, 15), 
    STATUS(7, 6), 
    STATS(8, 64), 
    ELEMENTALS(9, 14), 
    POSITION(10, 18), 
    SPEED(11, 18), 
    MULTIPLIER(12, 18), 
    COL_RADIUS_HEIGHT(13, 18), 
    ATK_ELEMENTAL(14, 5), 
    CLAN(15, 32), 
    SOCIAL(16, 30), 
    VITA_FAME(17, 19), 
    SLOTS(18, 12), 
    MOVEMENTS(19, 4), 
    COLOR(20, 10), 
    INVENTORY_LIMIT(21, 13), 
    TRUE_HERO(22, 9), 
    SPIRITS(23, 26), 
    RANKER(24, 6), 
    STATS_POINTS(25, 16), 
    STATS_ABILITIES(26, 18);
    
    private final int mask;
    private final int blockLength;
    
    private UserInfoType(final int mask, final int blockLength) {
        this.mask = mask;
        this.blockLength = blockLength;
    }
    
    @Override
    public final int getMask() {
        return this.mask;
    }
    
    public int getBlockLength() {
        return this.blockLength;
    }
}
