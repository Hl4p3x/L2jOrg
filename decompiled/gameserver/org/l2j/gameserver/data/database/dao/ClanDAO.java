// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.gameserver.data.database.data.ClanWarData;
import org.l2j.gameserver.data.database.data.CrestData;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.data.SubPledgeData;
import io.github.joealisson.primitive.ConcurrentIntMap;
import org.l2j.gameserver.data.database.data.ClanSkillData;
import java.sql.ResultSet;
import java.util.function.Consumer;
import java.util.List;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.ClanData;
import org.l2j.commons.database.DAO;

public interface ClanDAO extends DAO<ClanData>
{
    @Query("DELETE FROM clan_data WHERE clan_data.clan_id NOT IN (SELECT clanid FROM characters)")
    int deleteWithoutMembers();
    
    @Query("UPDATE clan_data SET auction_bid_at = 0 WHERE auction_bid_at NOT IN (SELECT auctionId FROM auction_bid)")
    void resetAuctionBidWithoutAction();
    
    @Query("UPDATE clan_data SET new_leader_id = 0 WHERE new_leader_id <> 0 AND new_leader_id NOT IN (SELECT charId FROM characters)")
    void resetNewLeaderWithoutCharacter();
    
    @Query("UPDATE clan_subpledges SET leader_id=0 WHERE clan_subpledges.leader_id NOT IN (SELECT charId FROM characters) AND leader_id > 0;")
    void resetSubpledgeLeaderWithoutCharacter();
    
    @Query("SELECT clan_id FROM clan_data WHERE hasCastle = :castleId:")
    int findOwnerClanIdByCastle(final int castleId);
    
    @Query("UPDATE clan_data SET hasCastle = 0 WHERE hasCastle = :castleId:")
    void removeOwnerClanByCastle(final int castleId);
    
    @Query("UPDATE clan_data SET hasCastle = :castleId: WHERE clan_id = :id:")
    void updateOwnedCastle(final int id, final int castleId);
    
    @Query("DELETE FROM clan_wars WHERE (clan1=:clan1: AND clan2=:clan2:) OR (clan1=:clan2: AND clan2=:clan1:)")
    void deleteClanWar(final int clan1, final int clan2);
    
    @Query("SELECT c.clan_name, c.ally_name\nFROM clan_wars w\nINNER JOIN clan_data c ON c.clan_id = w.clan2\nWHERE w.clan1 = :clanId: AND\n    w.clan2 NOT IN ( SELECT clan1 FROM clan_wars WHERE clan2 = :clanId:)\n")
    List<ClanData> findAttackList(final int clanId);
    
    @Query("SELECT c.clan_name, c.ally_name\nFROM clan_wars w\nINNER JOIN clan_data c ON c.clan_id = w.clan1\nWHERE w.clan2 = :clanId: AND\n    w.clan1 NOT IN ( SELECT clan2 FROM clan_wars WHERE clan1 = :clanId:)\n")
    List<ClanData> findUnderAttackList(final int clanId);
    
    @Query("SELECT c.clan_name, c.ally_name\nFROM clan_wars w\nINNER JOIN clan_data c ON c.clan_id = w.clan2\nWHERE w.clan1 = :clanId: AND\n    w.clan2 IN ( SELECT clan1 FROM clan_wars WHERE clan2 = :clanId:)\n")
    List<ClanData> findWarList(final int clanId);
    
    @Query("SELECT * FROM clan_data")
    List<ClanData> findAll();
    
    @Query("SELECT enabled, notice FROM clan_notices WHERE clan_id=:id:")
    void withNoticesDo(final int id, final Consumer<ResultSet> action);
    
    @Query("REPLACE INTO clan_notices (clan_id, notice, enabled) values (:id:,:notice:,:enabled:)")
    void saveNotice(final int id, final String notice, final boolean enabled);
    
    @Query("SELECT skill_id, skill_level, sub_pledge_id FROM clan_skills WHERE clan_id=:id:")
    List<ClanSkillData> findSkillsByClan(final int id);
    
    @Query("UPDATE clan_skills SET skill_level= :level: WHERE skill_id=:skillId: AND clan_id=:id:")
    void updateClanSkill(final int id, final int skillId, final int level);
    
    @Query("REPLACE INTO clan_skills (clan_id,skill_id,skill_level ,sub_pledge_id) VALUES (:id:, :skillId:, :level:, :subType:)")
    void saveClanSkill(final int id, final int skillId, final int level, final int subType);
    
    @Query("SELECT sub_pledge_id, name, leader_id FROM clan_subpledges WHERE clan_id=:id:")
    ConcurrentIntMap<SubPledgeData> findClanSubPledges(final int id);
    
    void save(final SubPledgeData subPledgeData);
    
    @Query("SELECT privs,`rank` FROM clan_privs WHERE clan_id=:id:")
    void withClanPrivs(final int id, final Consumer<ResultSet> action);
    
    @Query("REPLACE INTO clan_privs (clan_id,`rank`, privs) VALUES (:id:,:rank:,:privs:)")
    void saveClanPrivs(final int id, final int rank, final int privs);
    
    @Query("UPDATE clan_data SET clan_level = :level: WHERE clan_id = :id:")
    void updateClanLevel(final int id, final int level);
    
    @Query("UPDATE clan_data SET crest_id = :crestId: WHERE clan_id = :id:")
    void updateClanCrest(final int id, final int crestId);
    
    @Query("UPDATE clan_data SET ally_crest_id = :crestId: WHERE ally_id = :allyId:")
    void updateAllyCrestByAlly(final int allyId, final int crestId);
    
    @Query("UPDATE clan_data SET ally_crest_id = :crestId: WHERE clan_id = :id:")
    void updateAllyCrest(final int id, final int crestId);
    
    @Query("UPDATE clan_data SET crest_large_id = :crestId: WHERE clan_id = :id:")
    void updateClanCrestLarge(final int id, final int crestId);
    
    @Query("DELETE FROM crests WHERE id NOT IN (\n    SELECT crest_id AS id FROM clan_data\n    UNION ALL\n    SELECT ally_crest_id AS id FROM clan_data\n    UNION ALL\n    SELECT crest_large_id AS id FROM clan_data)\n")
    void removeUnusedCrests();
    
    @Query("SELECT * FROM crests")
    IntMap<CrestData> findAllCrests();
    
    void save(final CrestData crest);
    
    @Query("DELETE FROM crests where id = :crestId:")
    void deleteCrest(final int crestId);
    
    @Query("DELETE FROM clan_data WHERE clan_id = :clanId:")
    void deleteClan(final int clanId);
    
    @Query("SELECT * FROM clan_wars")
    List<ClanWarData> findAllWars();
    
    void save(final ClanWarData war);
    
    @Query("DELETE FROM clan_skills WHERE clan_id=:clanId: AND skill_id=:skillId:")
    void removeSkill(final int clanId, final int skillId);
    
    @Query("DELETE FROM clan_variables WHERE clanId = :clanId:")
    void deleteVariables(final int clanId);
}
