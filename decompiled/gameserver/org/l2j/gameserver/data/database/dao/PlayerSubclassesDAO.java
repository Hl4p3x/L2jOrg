// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.PlayerSubclassesData;
import org.l2j.commons.database.DAO;

public interface PlayerSubclassesDAO extends DAO<PlayerSubclassesData>
{
    @Query("SELECT exp, sp, level, vitality_points FROM character_subclasses WHERE charId = :charId: AND class_id = :classId: ORDER BY charId")
    PlayerSubclassesData findByIdAndClassId(final int charId, final int classId);
}
