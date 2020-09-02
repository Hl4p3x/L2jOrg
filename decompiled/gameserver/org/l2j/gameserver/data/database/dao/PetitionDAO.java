// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.DAO;

public interface PetitionDAO extends DAO<Object>
{
    @Query("INSERT INTO petition_feedback VALUES (:name:,:gmName:,:rate:,:message:,:time:)")
    void saveFeedback(final String name, final String gmName, final int rate, final String message, final long time);
}
