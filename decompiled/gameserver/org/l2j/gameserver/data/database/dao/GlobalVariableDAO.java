// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.DAO;

public interface GlobalVariableDAO extends DAO<Object>
{
    @Query("TRUNCATE global_variables")
    boolean deleteAll();
    
    @Query("DELETE FROM global_variables WHERE var like 'MA_C%'")
    void deleteRaidBonus();
}
