// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public enum ClanPrivilege
{
    DUMMY, 
    CL_JOIN_CLAN, 
    CL_GIVE_TITLE, 
    CL_VIEW_WAREHOUSE, 
    CL_MANAGE_RANKS, 
    CL_PLEDGE_WAR, 
    CL_DISMISS, 
    CL_REGISTER_CREST, 
    CL_APPRENTICE, 
    CL_TROOPS_FAME, 
    CL_SUMMON_AIRSHIP, 
    CH_OPEN_DOOR, 
    CH_OTHER_RIGHTS, 
    CH_AUCTION, 
    CH_DISMISS, 
    CH_SET_FUNCTIONS, 
    CS_OPEN_DOOR, 
    CS_MANOR_ADMIN, 
    CS_MANAGE_SIEGE, 
    CS_USE_FUNCTIONS, 
    CS_DISMISS, 
    CS_TAXES, 
    CS_MERCENARIES, 
    CS_SET_FUNCTIONS;
    
    public int getBitmask() {
        return 1 << this.ordinal();
    }
}
