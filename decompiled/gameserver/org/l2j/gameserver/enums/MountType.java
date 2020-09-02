// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import org.l2j.gameserver.data.xml.CategoryManager;

public enum MountType
{
    NONE, 
    STRIDER, 
    WYVERN, 
    WOLF;
    
    public static MountType findByNpcId(final int npcId) {
        if (CategoryManager.getInstance().isInCategory(CategoryType.STRIDER, npcId)) {
            return MountType.STRIDER;
        }
        if (CategoryManager.getInstance().isInCategory(CategoryType.WYVERN_GROUP, npcId)) {
            return MountType.WYVERN;
        }
        if (CategoryManager.getInstance().isInCategory(CategoryType.WOLF_GROUP, npcId)) {
            return MountType.WOLF;
        }
        return MountType.NONE;
    }
}
