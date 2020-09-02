// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.announce;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public enum AnnouncementType
{
    NORMAL, 
    CRITICAL, 
    EVENT, 
    AUTO_NORMAL, 
    AUTO_CRITICAL;
    
    private static final Logger LOGGER;
    
    public static AnnouncementType findById(final int id) {
        for (final AnnouncementType type : values()) {
            if (type.ordinal() == id) {
                return type;
            }
        }
        AnnouncementType.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, AnnouncementType.class.getSimpleName(), id), (Throwable)new IllegalStateException());
        return AnnouncementType.NORMAL;
    }
    
    public static AnnouncementType findByName(final String name) {
        for (final AnnouncementType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        AnnouncementType.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, AnnouncementType.class.getSimpleName(), name), (Throwable)new IllegalStateException());
        return AnnouncementType.NORMAL;
    }
    
    public static boolean isAutoAnnounce(final Announce announce) {
        final AnnouncementType type = announce.getType();
        return type == AnnouncementType.AUTO_CRITICAL || type == AnnouncementType.AUTO_NORMAL;
    }
    
    public boolean isAutoAnnounce() {
        return this == AnnouncementType.AUTO_CRITICAL || this == AnnouncementType.AUTO_NORMAL;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AnnouncementType.class);
    }
}
