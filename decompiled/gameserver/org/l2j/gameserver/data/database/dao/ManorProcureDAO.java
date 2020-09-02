// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import java.util.Collection;
import org.l2j.commons.database.annotation.Query;
import java.util.List;
import org.l2j.gameserver.data.database.data.CropProcure;
import org.l2j.commons.database.DAO;

public interface ManorProcureDAO extends DAO<CropProcure>
{
    @Query("SELECT * FROM castle_manor_procure WHERE castle_id=:id:")
    List<CropProcure> findManorProcureByCastle(final int id);
    
    @Query("TRUNCATE castle_manor_procure")
    void deleteManorProcure();
    
    boolean save(final Collection<CropProcure> procures);
}
