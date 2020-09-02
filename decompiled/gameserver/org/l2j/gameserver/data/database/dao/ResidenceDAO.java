// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import java.util.List;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.ResidenceFunctionData;
import org.l2j.commons.database.DAO;

public interface ResidenceDAO extends DAO<ResidenceFunctionData>
{
    @Query("REPLACE INTO residence_functions (id, level, expiration, residenceId) VALUES (:id:, :level:, :expiration:, :residence:)")
    void saveFunction(final int id, final int level, final long expiration, final int residence);
    
    @Query("SELECT * FROM residence_functions WHERE residenceId = :residenceId:")
    List<ResidenceFunctionData> findFunctionsByResidence(final int residenceId);
    
    @Query("DELETE FROM residence_functions WHERE residenceId = :residenceId: and id = :functionId:")
    void deleteFunction(final int functionId, final int residenceId);
    
    @Query("DELETE FROM residence_functions WHERE residenceId = :id:")
    void deleteFunctionsByResidence(final int id);
}
