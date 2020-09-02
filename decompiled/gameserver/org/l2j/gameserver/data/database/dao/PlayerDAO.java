// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.gameserver.data.database.data.CostumeCollectionData;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.data.CostumeData;
import java.util.Collection;
import org.l2j.gameserver.data.database.data.PlayerStatsData;
import java.sql.ResultSet;
import java.util.function.Consumer;
import org.l2j.gameserver.data.database.data.KillerData;
import io.github.joealisson.primitive.IntSet;
import java.util.List;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.commons.database.DAO;

public interface PlayerDAO extends DAO<PlayerData>
{
    @Query("UPDATE characters SET online = 0")
    void setAllCharactersOffline();
    
    @Query("SELECT * FROM characters WHERE account_name = :account: ORDER BY createDate")
    List<PlayerData> findPlayersByAccount(final String account);
    
    @Query("SELECT * FROM characters WHERE charId = :objectId:")
    PlayerData findById(final int objectId);
    
    @Query("UPDATE characters SET clanid=0, clan_privs=0, wantspeace=0, subpledge=0, title='', lvl_joined_academy=0, apprentice=0, sponsor=0, clan_join_expiry_time=0, clan_create_expiry_time=0 WHERE characters.clanid > 0 AND characters.clanid NOT IN (SELECT clan_id FROM clan_data)")
    void resetClanInfoOfNonexistentClan();
    
    @Query("UPDATE characters SET clanid=0, clan_privs=0, wantspeace=0, subpledge=0, title='', lvl_joined_academy=0, apprentice=0, sponsor=0, clan_join_expiry_time=:clanJoinExpiryTime:, clan_create_expiry_time=:clanCreateExpiryTime: WHERE charId = :playerId:")
    void deleteClanInfoOfMember(final int playerId, final long clanJoinExpiryTime, final long clanCreateExpiryTime);
    
    @Query("DELETE FROM character_instance_time WHERE time <= :timestamp:")
    void deleteExpiredInstances(final long timestamp);
    
    @Query("DELETE FROM character_skills_save WHERE restore_type = 1 AND systime <= :timestamp:")
    void deleteExpiredSavedSkills(final long timestamp);
    
    @Query("SELECT charId, createDate FROM characters WHERE DAYOFMONTH(createDate) = :day: AND MONTH(createDate) = :month: AND YEAR(createDate) < :year:")
    List<PlayerData> findBirthdayCharacters(final int year, final int month, final int day);
    
    @Query("SELECT charId, accesslevel FROM characters WHERE char_name=:name:")
    PlayerData findIdAndAccessLevelByName(final String name);
    
    @Query("REPLACE INTO character_relationship (char_id, friend_id) VALUES (:playerId:, :otherId:), (:otherId:, :playerId:)")
    void saveFriendship(final int playerId, final int otherId);
    
    @Query("SELECT friend_id FROM character_relationship WHERE char_id=:playerId: AND relation='FRIEND'")
    IntSet findFriendsById(final int playerId);
    
    @Query("DELETE FROM character_relationship WHERE (char_id=:playerId: AND friend_id=:friendId:) OR (char_id=:friendId: AND friend_id=:playerId:)")
    void deleteFriendship(final int playerId, final int friendId);
    
    @Query("SELECT friend_id FROM character_relationship WHERE char_id=:playerId: AND relation='BLOCK'")
    IntSet findBlockListById(final int playerId);
    
    @Query("REPLACE INTO character_relationship (char_id, friend_id, relation) VALUES (:playerId:, :blockedId:, 'BLOCK')")
    void saveBlockedPlayer(final int playerId, final int blockedId);
    
    @Query("DELETE FROM character_relationship WHERE char_id=:playerId: AND friend_id=:blockedId: AND relation='BLOCK'")
    void deleteBlockedPlayer(final int playerId, final int blockedId);
    
    @Query("SELECT char_name, classid, level, lastAccess, clanid, createDate FROM characters WHERE charId = :friendId:")
    PlayerData findFriendData(final int friendId);
    
    @Query("UPDATE characters SET clan_create_expiry_time = 0, clan_join_expiry_time = 0 WHERE char_name=:name:")
    void removeClanPenalty(final String name);
    
    @Query("UPDATE characters SET accesslevel=:level: WHERE char_name=:name:")
    boolean updateAccessLevelByName(final String name, final int level);
    
    @Query("UPDATE characters SET x=-84318, y=244579, z=-3730 WHERE charId=:objectId:")
    void updateToValidLocation(final int objectId);
    
    @Query("REPLACE INTO player_killers (player_id, killer_id, kill_time) VALUES (:player:, :killer:, :time: )")
    void updatePlayerKiller(final int player, final int killer, final long time);
    
    @Query("SELECT pk.killer_id, pk.kill_time, c.char_name as name, IFNULL(cd.clan_name, '') AS clan, c.level, c.race, c.classid as active_class, c.online\nFROM player_killers pk\nJOIN characters c on pk.killer_id = c.charId\nLEFT JOIN  clan_data cd on c.clanid = cd.clan_id\nWHERE pk.player_id = :player: AND pk.kill_time >= :since:\n")
    List<KillerData> findKillersByPlayer(final int player, final long since);
    
    @Query("UPDATE characters SET accesslevel=:level: WHERE charId=:playerId:")
    void updateAccessLevel(final int playerId, final int level);
    
    @Query("UPDATE characters SET x=:x:, y=:y:, z=:z: WHERE char_name=:name:")
    boolean updateLocationByName(final String name, final int x, final int y, final int z);
    
    @Query("SELECT char_name,accesslevel FROM characters WHERE charId=:id:")
    PlayerData findNameAndAccessLevelById(final int id);
    
    @Query("SELECT EXISTS(SELECT 1 FROM characters WHERE char_name=:name:)")
    boolean existsByName(final String name);
    
    @Query("SELECT COUNT(1) as count FROM characters WHERE account_name=:account:")
    int playerCountByAccount(final String account);
    
    @Query("SELECT classid FROM characters WHERE charId=:id: ")
    int findClassIdById(final int id);
    
    @Query("SELECT charId, char_name, accesslevel FROM characters")
    void withPlayersDataDo(final Consumer<ResultSet> action);
    
    @Query("SELECT char_name,level,classid,charId,title,power_grade,subpledge,apprentice,sponsor,sex,race FROM characters WHERE clanid=:clanId:")
    List<PlayerData> findClanMembers(final int clanId);
    
    @Query("UPDATE characters SET apprentice=0 WHERE apprentice=:playerId:")
    void deleteApprentice(final int playerId);
    
    @Query("UPDATE characters SET sponsor=0 WHERE sponsor=:playerId:")
    void deleteSponsor(final int playerId);
    
    @Query("UPDATE characters SET clan_privs = :privs: WHERE charId = :id:")
    void updateClanPrivs(final int id, final int privs);
    
    void save(final PlayerStatsData statsData);
    
    @Query("SELECT * FROM player_stats_points WHERE player_id=:playerId:")
    PlayerStatsData findPlayerStatsData(final int playerId);
    
    void save(final Collection<CostumeData> costumes);
    
    @Query("SELECT id, player_id, amount, locked FROM player_costumes WHERE player_id = :playerId:")
    IntMap<CostumeData> findCostumes(final int playerId);
    
    @Query("DELETE FROM player_costumes WHERE player_id = :playerId: AND id = :costumeId:")
    void removeCostume(final int playerId, final int costumeId);
    
    @Query("SELECT * FROM player_costume_collection WHERE player_id = :playerId:")
    CostumeCollectionData findPlayerCostumeCollection(final int playerId);
    
    void save(final CostumeCollectionData activeCostumesCollection);
    
    @Query("DELETE FROM player_costume_collection WHERE player_id = :playerId:")
    void deleteCostumeCollection(final int playerId);
    
    @Query("SELECT teleport_id FROM player_teleports WHERE player_id = :playerId:")
    IntSet findTeleportFavorites(final int playerId);
    
    @Query(value = "REPLACE INTO player_teleports VALUES (:playerId:, :teleports: )", batchIndex = 1)
    void saveTeleportFavorites(final int playerId, final IntSet teleports);
    
    @Query("DELETE FROM player_teleports WHERE player_id = :playerId:")
    void removeTeleportFavorites(final int playerId);
    
    @Query("DELETE FROM characters WHERE charId=:playerId:")
    void deleteById(final int playerId);
    
    @Query("UPDATE characters SET deletetime=:deleteTime: WHERE charId=:playerId:")
    void updateDeleteTime(final int playerId, final long deleteTime);
    
    @Query("DELETE FROM character_skills_save WHERE skill_id=:skillId:")
    void deleteSkillSave(final int skillId);
    
    @Query("UPDATE character_reco_bonus SET rec_left = 20, rec_have = GREATEST(CAST(rec_have AS SIGNED)  -20 , 0)")
    void resetRecommends();
    
    @Query("UPDATE characters SET vitality_points = :points:")
    void resetVitality(final int points);
    
    @Query("DELETE FROM character_recipebook WHERE charId=:playerId: AND id=:recipeId: AND classIndex=:classIndex:")
    void deleteRecipe(final int playerId, final int recipeId, final int classIndex);
    
    @Query("UPDATE characters SET online=:online:, lastAccess=:lastAccess: WHERE charId=:playerId:")
    void updateOnlineStatus(final int playerId, final boolean online, final long lastAccess);
    
    @Query("DELETE FROM character_skills WHERE skill_id=:skillId: AND charId=:playerId: AND class_index=:classIndex:")
    void deleteSkill(final int playerId, final int skillId, final int classIndex);
    
    @Query("DELETE FROM character_hennas WHERE charId=:playerId: AND slot=:slot: AND class_index=:classIndex:")
    void deleteHenna(final int playerId, final int slot, final int classIndex);
    
    @Query("DELETE FROM character_hennas WHERE charId=:playerId: AND class_index=:classIndex:")
    void deleteHennas(final int playerId, final int classIndex);
    
    @Query("DELETE FROM character_skills_save WHERE charId=:playerId: AND class_index=:classIndex:")
    void deleteSkillsSave(final int playerId, final int classIndex);
    
    @Query("DELETE FROM character_skills WHERE charId=? AND class_index=?")
    void deleteSkills(final int playerId, final int classIndex);
    
    @Query("DELETE FROM character_subclasses WHERE charId=:playerId: AND class_index=:classIndex:")
    void deleteSubClass(final int playerId, final int classIndex);
    
    @Query("UPDATE character_tpbookmark SET icon=:icon:,tag=:tag:,name=:name: where charId=:playerId: AND Id=:id:")
    void updateTeleportBookMark(final int playerId, final int id, final int icon, final String tag, final String name);
    
    @Query("DELETE FROM character_tpbookmark WHERE charId=:playerId: AND Id=:id:")
    void deleteTeleportBookMark(final int playerId, final int id);
    
    @Query("DELETE FROM character_recipeshoplist WHERE charId=:playerId:")
    void deleteRecipeShop(final int playerId);
    
    @Query("UPDATE characters SET subpledge=:pledgeType: WHERE charId=:playerId:")
    void updateSubpledge(final int playerId, final int pledgeType);
    
    @Query("UPDATE characters SET power_grade=:powerGrade: WHERE charId=:playerId:")
    void updatePowerGrade(final int playerId, final int powerGrade);
    
    @Query("UPDATE characters SET apprentice=:apprentice:,sponsor=:sponsor: WHERE charId= :playerId:")
    void updateApprenticeAndSponsor(final int playerId, final int apprentice, final int sponsor);
    
    @Query("DELETE FROM character_contacts WHERE charId =:playeId: and contactId = :contactId:")
    void deleteContact(final int playerId, final int contactId);
    
    @Query("DELETE FROM character_macroses WHERE charId=:playerId: AND id=:id:")
    void deleteMacro(final int playerId, final int id);
    
    @Query("DELETE FROM character_instance_time WHERE charId=:playerId: AND instanceId=:id:")
    void deleteInstanceTime(final int playerId, final int id);
}
