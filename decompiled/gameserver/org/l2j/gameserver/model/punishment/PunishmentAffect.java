// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.punishment;

public enum PunishmentAffect
{
    ACCOUNT, 
    CHARACTER, 
    IP;
    
    public static PunishmentAffect getByName(final String name) {
        for (final PunishmentAffect type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
