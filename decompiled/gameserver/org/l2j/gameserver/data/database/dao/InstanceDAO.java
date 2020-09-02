// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import java.sql.ResultSet;
import java.util.function.Consumer;
import org.l2j.commons.database.annotation.Query;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.database.DAO;

public interface InstanceDAO extends DAO<Object>
{
    @Query(value = "DELETE FROM character_instance_time WHERE charId=:playerId: AND instanceId=:instances:", batchIndex = 1)
    void deleteInstanceTime(final int playerId, final IntSet instances);
    
    @Query("SELECT * FROM character_instance_time")
    void findAllInstancesTime(final Consumer<ResultSet> consumer);
}
