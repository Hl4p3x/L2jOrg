// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import java.util.List;
import org.l2j.gameserver.data.database.data.ElementalSpiritData;
import org.l2j.commons.database.DAO;

public interface ElementalSpiritDAO extends DAO<ElementalSpiritData>
{
    @Query("SELECT * FROM character_spirits WHERE charId = :playerId:")
    List<ElementalSpiritData> findByPlayerId(final int playerId);
    
    @Query("UPDATE character_spirits SET in_use = type = :type: WHERE charId = :playerId:")
    void updateActiveSpirit(final int playerId, final byte type);
}
