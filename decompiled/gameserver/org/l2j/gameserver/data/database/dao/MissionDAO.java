// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.commons.database.DAO;

public interface MissionDAO extends DAO<MissionPlayerData>
{
    @Query("DELETE FROM character_missions WHERE mission_id = :id:")
    void deleteById(final int id);
    
    @Query("SELECT * FROM character_missions WHERE char_id = :playerId: AND mission_id = :missionId:")
    MissionPlayerData findById(final int playerId, final int missionId);
}
