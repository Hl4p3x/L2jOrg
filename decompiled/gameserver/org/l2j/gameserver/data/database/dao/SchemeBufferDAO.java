// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import java.sql.ResultSet;
import java.util.function.Consumer;
import org.l2j.commons.database.DAO;

public interface SchemeBufferDAO extends DAO<Object>
{
    @Query("SELECT * FROM buffer_schemes")
    void loadAll(final Consumer<ResultSet> consumer);
    
    @Query("TRUNCATE TABLE buffer_schemes")
    void deleteAll();
}
