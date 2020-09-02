// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.DAO;

public interface SpawnDAO extends DAO<Object>
{
    @Query("DELETE FROM npc_respawns WHERE id = :npcId:")
    void deleteRespawn(final int npcId);
}
