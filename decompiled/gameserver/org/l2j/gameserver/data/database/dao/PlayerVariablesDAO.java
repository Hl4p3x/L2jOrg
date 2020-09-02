// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.PlayerVariableData;
import org.l2j.commons.database.DAO;

public interface PlayerVariablesDAO extends DAO<PlayerVariableData>
{
    @Query("SELECT * FROM player_variables WHERE player_id = :playerId:")
    PlayerVariableData findById(final int playerId);
    
    @Query("UPDATE player_variables SET revenge_locations = 5, revenge_teleports = 5")
    void resetRevengeData();
    
    @Query("UPDATE player_variables SET world_chat_used = 1")
    void resetWorldChatPoint();
    
    @Query("UPDATE player_variables SET extend_drop = ''")
    void resetExtendDrop();
    
    @Query("UPDATE player_variables SET claimed_clan_rewards = 0")
    void resetClaimedClanReward();
}
