// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.DAO;

public interface SiegeDAO extends DAO<Object>
{
    @Query("DELETE FROM castle_siege_guards WHERE npcId = :npcId: AND x = :x: AND y = :y: AND z = :z: AND isHired = 1")
    void deleteGuard(final int npcId, final int x, final int y, final int z);
    
    @Query("DELETE FROM castle_siege_guards WHERE castleId = :castleId: AND isHired = 1")
    void deleteGuardsOfCastle(final int castleId);
    
    @Query("SELECT EXISTS(SELECT 1 FROM siege_clans WHERE clan_id=:clanId: AND castle_id=:castleId:)")
    boolean isRegistered(final int clanId, final int castleId);
}
