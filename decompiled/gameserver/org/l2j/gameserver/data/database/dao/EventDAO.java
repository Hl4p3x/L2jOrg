// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.DAO;

public interface EventDAO extends DAO<Void>
{
    @Query("REPLACE INTO event_schedulers (eventName, schedulerName, lastRun) VALUES (:event:, :scheduler:, :lastRun:)")
    void updateLastRun(final String event, final String scheduler, final long lastRun);
    
    @Query("SELECT lastRun FROM event_schedulers WHERE eventName = :event: AND schedulerName = :scheduler:")
    long findLastRun(final String event, final String scheduler);
}
