// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum SpecialItemType
{
    PC_CAFE_POINTS(-100, "Player Commendation Points"), 
    CLAN_REPUTATION(-200, "Clan Reputation Points"), 
    FAME(-300, "Fame"), 
    FIELD_CYCLE_POINTS(-400, "Field Cycle Points"), 
    RAIDBOSS_POINTS(-500, "Raid Points");
    
    private final String description;
    private int clientId;
    
    private SpecialItemType(final int clientId, final String description) {
        this.clientId = clientId;
        this.description = description;
    }
    
    public static SpecialItemType getByClientId(final int clientId) {
        for (final SpecialItemType type : values()) {
            if (type.getClientId() == clientId) {
                return type;
            }
        }
        return null;
    }
    
    public int getClientId() {
        return this.clientId;
    }
    
    public String getDescription() {
        return this.description;
    }
}
