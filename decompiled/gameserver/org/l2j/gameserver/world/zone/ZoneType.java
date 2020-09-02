// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone;

public enum ZoneType
{
    PVP, 
    PEACE, 
    SIEGE, 
    MOTHER_TREE, 
    CLAN_HALL, 
    LANDING, 
    NO_LANDING, 
    WATER, 
    JAIL, 
    MONSTER_TRACK, 
    CASTLE, 
    SWAMP, 
    NO_SUMMON_FRIEND, 
    FORT, 
    NO_STORE, 
    SCRIPT, 
    HQ, 
    DANGER_AREA, 
    ALTERED, 
    NO_BOOKMARK, 
    NO_ITEM_DROP, 
    NO_RESTART, 
    FISHING, 
    UNDYING, 
    TAX;
    
    public static int getZoneCount() {
        return values().length;
    }
}
