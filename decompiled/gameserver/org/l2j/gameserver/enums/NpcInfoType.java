// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;

public enum NpcInfoType implements IUpdateTypeComponent
{
    ID(0, 4), 
    ATTACKABLE(1, 1), 
    RELATIONS(2, 4), 
    NAME(3, 2), 
    POSITION(4, 12), 
    HEADING(5, 4), 
    UNKNOWN2(6, 4), 
    ATK_CAST_SPEED(7, 8), 
    SPEED_MULTIPLIER(8, 8), 
    EQUIPPED(9, 12), 
    ALIVE(10, 1), 
    RUNNING(11, 1), 
    SWIM_OR_FLY(14, 1), 
    TEAM(15, 1), 
    ENCHANT(16, 4), 
    FLYING(17, 4), 
    CLONE(18, 4), 
    COLOR_EFFECT(19, 4), 
    DISPLAY_EFFECT(22, 4), 
    TRANSFORMATION(23, 4), 
    CURRENT_HP(24, 4), 
    CURRENT_MP(25, 4), 
    MAX_HP(26, 4), 
    MAX_MP(27, 4), 
    SUMMONED(28, 1), 
    UNKNOWN12(29, 8), 
    TITLE(30, 2), 
    NAME_NPCSTRINGID(31, 4), 
    TITLE_NPCSTRINGID(32, 4), 
    PVP_FLAG(33, 1), 
    REPUTATION(34, 4), 
    CLAN(35, 20), 
    ABNORMALS(36, 0), 
    VISUAL_STATE(37, 1);
    
    private final int _mask;
    private final int _blockLength;
    
    private NpcInfoType(final int mask, final int blockLength) {
        this._mask = mask;
        this._blockLength = blockLength;
    }
    
    @Override
    public int getMask() {
        return this._mask;
    }
    
    public int getBlockLength() {
        return this._blockLength;
    }
}
