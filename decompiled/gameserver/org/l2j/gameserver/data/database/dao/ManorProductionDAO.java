// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import java.util.Collection;
import org.l2j.commons.database.annotation.Query;
import java.util.List;
import org.l2j.gameserver.data.database.data.SeedProduction;
import org.l2j.commons.database.DAO;

public interface ManorProductionDAO extends DAO<SeedProduction>
{
    @Query("SELECT * FROM castle_manor_production WHERE castle_id=:id:")
    List<SeedProduction> findManorProductionByCastle(final int id);
    
    @Query("TRUNCATE castle_manor_production")
    void deleteManorProduction();
    
    boolean save(final Collection<SeedProduction> productions);
}
