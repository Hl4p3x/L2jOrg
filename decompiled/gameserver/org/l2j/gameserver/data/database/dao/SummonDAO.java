// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import java.util.List;
import org.l2j.gameserver.data.database.data.SummonData;
import org.l2j.commons.database.DAO;

public interface SummonDAO extends DAO<SummonData>
{
    @Query("SELECT ownerId, summonId FROM character_summons")
    List<SummonData> findAllSummonOwners();
    
    @Query("DELETE FROM character_summons WHERE ownerId = :objectId: and summonId = :id:")
    void deleteByIdAndOwner(final int id, final int objectId);
    
    @Query("SELECT summonSkillId, summonId, curHp, curMp, time FROM character_summons WHERE ownerId = :ownerId:")
    List<SummonData> findSummonsByOwner(final int ownerId);
    
    @Query("REPLACE INTO character_summons (ownerId,summonId,summonSkillId,curHp,curMp,time) VALUES (:ownerId:,:id:,:skill:,:currentHp:,:currentMp:,:lifeTime:)")
    void save(final int ownerId, final int id, final int skill, final int currentHp, final int currentMp, final int lifeTime);
}
