// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.api.item;

public enum UpgradeType
{
    RARE, 
    NORMAL, 
    SPECIAL;
    
    public static UpgradeType ofId(final int type) {
        UpgradeType upgradeType = null;
        switch (type) {
            case 0: {
                upgradeType = UpgradeType.RARE;
                break;
            }
            case 1: {
                upgradeType = UpgradeType.NORMAL;
                break;
            }
            case 2: {
                upgradeType = UpgradeType.SPECIAL;
                break;
            }
            default: {
                upgradeType = null;
                break;
            }
        }
        return upgradeType;
    }
}
