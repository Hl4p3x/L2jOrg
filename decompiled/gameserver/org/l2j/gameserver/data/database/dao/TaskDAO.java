// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import java.util.List;
import org.l2j.gameserver.data.database.data.TaskData;
import org.l2j.commons.database.DAO;

public interface TaskDAO extends DAO<TaskData>
{
    @Query("SELECT * FROM global_tasks")
    List<TaskData> findAll();
    
    @Query("UPDATE global_tasks SET last_activation=:lastActivation: WHERE id=:id:")
    void updateLastActivation(final int id, final long lastActivation);
    
    @Query("SELECT EXISTS (SELECT 1 FROM global_tasks WHERE name=:task:)")
    boolean existsWithName(final String task);
}
