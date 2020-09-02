// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.gameserver.data.database.data.RankHistoryData;
import io.github.joealisson.primitive.IntMap;
import java.util.List;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.RankData;
import org.l2j.commons.database.DAO;

public interface RankDAO extends DAO<RankData>
{
    @Query("TRUNCATE rankers_snapshot")
    void clearSnapshot();
    
    @Query("INSERT INTO rankers_snapshot SELECT id, exp, `rank`, rank_race FROM rankers LIMIT 150")
    void updateSnapshot();
    
    @Query("SELECT * FROM rankers")
    List<RankData> findAll();
    
    @Query("SELECT * FROM rankers_snapshot")
    IntMap<RankData> findAllSnapshot();
    
    @Query("SELECT * FROM rankers  WHERE id = :playerId:")
    RankData findPlayerRank(final int playerId);
    
    @Query("SELECT * FROM rankers_race WHERE race = :race:")
    List<RankData> findAllByRace(final int race);
    
    @Query("SELECT * FROM rankers WHERE clan_id = :clanId:")
    List<RankData> findByClan(final int clanId);
    
    @Query("SELECT * FROM rankers WHERE id IN (:playerId:, (SELECT friend_id FROM character_relationship WHERE char_id = :playerId: AND relation = 'FRIEND'))")
    List<RankData> findFriendRankers(final int playerId);
    
    @Query("SELECT rankers.*\nFROM rankers, ( SELECT @base_rank := CONVERT( (SELECT rankers.`rank` FROM rankers WHERE id = :playerId:), SIGNED ) ) dummy\nWHERE @base_rank IS NOT NULL AND rankers.`rank` BETWEEN  @base_rank - 10 AND @base_rank + 10\n")
    List<RankData> findRankersNextToPlayer(final int playerId);
    
    @Query("SELECT rankers_race.*\nFROM rankers_race, ( SELECT @base_rank := CONVERT( (SELECT rankers_race.`rank` FROM rankers_race WHERE id = :playerId:), SIGNED ) ) dummy\nWHERE @base_rank IS NOT NULL AND  rankers_race.race = :race:  AND rankers_race.`rank` BETWEEN  @base_rank - 10 AND @base_rank + 10\n")
    List<RankData> findRaceRankersNextToPlayer(final int playerId, final int race);
    
    @Query("INSERT INTO rankers_history SELECT  id, exp, `rank`, :date: FROM rankers_snapshot")
    void updateRankersHistory(final long date);
    
    @Query("DELETE FROM rankers_history WHERE date < :baseDate:")
    void removeOldRankersHistory(final long baseDate);
    
    @Query("SELECT * FROM rankers_history WHERE id = :playerId: ORDER BY date")
    List<RankHistoryData> findPlayerHistory(final int playerId);
}
