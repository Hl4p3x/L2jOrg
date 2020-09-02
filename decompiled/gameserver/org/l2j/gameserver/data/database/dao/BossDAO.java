// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.DAO;

public interface BossDAO extends DAO<Object>
{
    @Query("UPDATE grandboss_data set status = :status: where boss_id = :bossId:")
    void updateStatus(final int bossId, final int status);
}
