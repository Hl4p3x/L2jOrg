// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.item.type.CrystalType;

public enum ItemGrade
{
    NONE, 
    D, 
    C, 
    B, 
    A, 
    S;
    
    public static ItemGrade valueOf(final CrystalType type) {
        ItemGrade itemGrade = null;
        switch (type) {
            case D: {
                itemGrade = ItemGrade.D;
                break;
            }
            case C: {
                itemGrade = ItemGrade.C;
                break;
            }
            case B: {
                itemGrade = ItemGrade.B;
                break;
            }
            case A: {
                itemGrade = ItemGrade.A;
                break;
            }
            case S: {
                itemGrade = ItemGrade.S;
                break;
            }
            default: {
                itemGrade = ItemGrade.NONE;
                break;
            }
        }
        return itemGrade;
    }
}
