// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.announce;

import java.time.LocalDateTime;
import org.l2j.commons.util.DateRange;
import java.util.concurrent.atomic.AtomicInteger;

public class EventAnnouncement implements Announce
{
    private static final AtomicInteger virtualId;
    private final int id;
    private final DateRange range;
    private String content;
    
    public EventAnnouncement(final DateRange range, final String content) {
        this.id = EventAnnouncement.virtualId.decrementAndGet();
        this.range = range;
        this.content = content;
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public AnnouncementType getType() {
        return AnnouncementType.EVENT;
    }
    
    @Override
    public boolean isValid() {
        return this.range.isWithinRange(LocalDateTime.now());
    }
    
    @Override
    public String getContent() {
        return this.content;
    }
    
    @Override
    public void setContent(final String content) {
        this.content = content;
    }
    
    @Override
    public String getAuthor() {
        return "System";
    }
    
    @Override
    public void setType(final AnnouncementType type) {
    }
    
    @Override
    public void setAuthor(final String author) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean canBeStored() {
        return false;
    }
    
    static {
        virtualId = new AtomicInteger(-1);
    }
}
