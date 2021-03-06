// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.punishment;

public enum PunishmentType
{
    JAIL, 
    BAN, 
    PERMANENT_BAN, 
    CHAT_BAN, 
    PARTY_BAN;
    
    public static PunishmentType getByName(final String name) {
        for (final PunishmentType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
