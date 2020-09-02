// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import java.sql.ResultSet;
import java.util.function.Consumer;
import org.l2j.commons.database.DAO;

public interface ClanHallDAO extends DAO<Object>
{
    @Query("SELECT * FROM clanhall WHERE id=:id:")
    void findById(final int id, final Consumer<ResultSet> action);
    
    @Query("REPLACE INTO clanhall (id, owner_id, paid_until) VALUES (:id:,:owner:,:paidUntil:)")
    void save(final int id, final int owner, final long paidUntil);
}
