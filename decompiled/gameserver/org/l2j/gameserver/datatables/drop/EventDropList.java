// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.datatables.drop;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;
import org.l2j.commons.util.DateRange;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

public class EventDropList
{
    private final Collection<DateDrop> allNpcEventsDrops;
    
    private EventDropList() {
        this.allNpcEventsDrops = (Collection<DateDrop>)ConcurrentHashMap.newKeySet();
    }
    
    public void addGlobalDrop(final EventDropHolder drop, final DateRange period) {
        this.allNpcEventsDrops.add(new DateDrop(period, drop));
    }
    
    public List<DateDrop> getAllDrops() {
        return this.allNpcEventsDrops.stream().filter(d -> d.dateRange.isWithinRange(LocalDateTime.now())).collect((Collector<? super DateDrop, ?, List<DateDrop>>)Collectors.toList());
    }
    
    public static EventDropList getInstance() {
        return Singleton.INSTANCE;
    }
    
    public static class DateDrop
    {
        private final DateRange dateRange;
        private final EventDropHolder dropHolder;
        
        private DateDrop(final DateRange dateRange, final EventDropHolder eventDrop) {
            this.dateRange = dateRange;
            this.dropHolder = eventDrop;
        }
        
        public boolean monsterCanDrop(final int id, final int level) {
            return this.dropHolder.checkLevel(level) && this.dropHolder.hasMonster(id);
        }
        
        public double getChance() {
            return this.dropHolder.getChance();
        }
        
        public int getItemId() {
            return this.dropHolder.getItemId();
        }
        
        public long getMin() {
            return this.dropHolder.getMin();
        }
        
        public long getMax() {
            return this.dropHolder.getMax();
        }
    }
    
    private static class Singleton
    {
        private static final EventDropList INSTANCE;
        
        static {
            INSTANCE = new EventDropList();
        }
    }
}
