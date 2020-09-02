// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.announce;

public interface Announce
{
    boolean isValid();
    
    AnnouncementType getType();
    
    String getContent();
    
    boolean canBeStored();
    
    int getId();
    
    String getAuthor();
    
    void setType(final AnnouncementType type);
    
    void setContent(final String content);
    
    void setAuthor(final String name);
}
