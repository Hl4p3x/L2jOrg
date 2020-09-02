// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.gameserver.data.database.data.CommunityFavorite;
import org.l2j.gameserver.data.database.data.CommunityMemo;
import java.util.List;
import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.DAO;

public interface CommunityDAO extends DAO<Object>
{
    @Query("SELECT COUNT(1) FROM `bbs_favorites` WHERE playerId=:playerId:")
    int getFavoritesCount(final int playerId);
    
    @Query("SELECT id, title, date FROM community_memos WHERE owner_id = :playerId:")
    List<CommunityMemo> findMemosBasicInfo(final int playerId);
    
    @Query("INSERT INTO community_memos (owner_id, title, text) VALUE (:playerId:, :title:, :text:)")
    void saveMemo(final int playerId, final String title, final String text);
    
    @Query("SELECT * FROM community_memos WHERE id=:id: AND owner_id=:playerId:")
    CommunityMemo findMemo(final int id, final int playerId);
    
    @Query("UPDATE community_memos SET title=:title:, text=:text: WHERE id=:id: AND owner_id=:playerId:")
    void updateMemo(final int playerId, final int id, final String title, final String text);
    
    @Query("DELETE FROM community_memos WHERE owner_id=:playerId: AND id=:id:")
    void deleteMemo(final int playerId, final int id);
    
    @Query("SELECT * FROM `bbs_favorites` WHERE `playerId`=:playerId: ORDER BY `favAddDate` DESC")
    List<CommunityFavorite> findFavorites(final int playerId);
    
    @Query("DELETE FROM `bbs_favorites` WHERE `playerId`=:playerId: AND `favId`=:id:")
    void deleteFavorite(final int playerId, final int id);
}
