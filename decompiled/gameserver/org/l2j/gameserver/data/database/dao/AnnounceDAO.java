// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import java.util.List;
import org.l2j.gameserver.data.database.data.AnnounceData;
import org.l2j.commons.database.DAO;

public interface AnnounceDAO extends DAO<AnnounceData>
{
    @Query("SELECT * FROM announcements")
    List<AnnounceData> findAll();
    
    @Query("DELETE FROM announcements WHERE id = :id:")
    void deleteById(final int id);
}
