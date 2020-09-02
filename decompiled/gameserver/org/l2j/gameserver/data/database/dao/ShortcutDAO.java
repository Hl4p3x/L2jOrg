// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import java.util.List;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.commons.database.DAO;

public interface ShortcutDAO extends DAO<Shortcut>
{
    @Query("DELETE FROM character_shortcuts WHERE player_id=:playerId: AND client_id=:clientId: AND class_index=:classIndex:")
    void delete(final int playerId, final int clientId, final int classIndex);
    
    @Query("SELECT * FROM character_shortcuts WHERE player_id=:playerId: AND class_index=:classIndex:")
    List<Shortcut> findByPlayer(final int playerId, final int classIndex);
    
    @Query("DELETE FROM character_shortcuts WHERE player_id=:playerId: AND class_index=:classIndex:")
    void deleteFromSubclass(final int playerId, final int classIndex);
    
    @Query("DELETE FROM character_shortcuts WHERE player_id=:playerId:")
    void deleteAll(final int playerId);
}
