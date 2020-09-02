// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.DAO;

public interface PunishmentDAO extends DAO<Object>
{
    @Query("UPDATE punishments SET expiration = :expiration: WHERE id =:id:")
    void updateExpiration(final int id, final long expiration);
}
