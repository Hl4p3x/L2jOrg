// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum IllegalActionPunishmentType
{
    NONE, 
    BROADCAST, 
    KICK, 
    KICKBAN, 
    JAIL;
    
    public static IllegalActionPunishmentType findByName(final String name) {
        for (final IllegalActionPunishmentType type : values()) {
            if (type.name().toLowerCase().equals(name.toLowerCase())) {
                return type;
            }
        }
        return IllegalActionPunishmentType.NONE;
    }
}
