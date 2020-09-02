// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import java.util.List;
import org.l2j.gameserver.data.database.data.QuestData;
import org.l2j.commons.database.DAO;

public interface QuestDAO extends DAO<QuestData>
{
    @Query("SELECT DISTINCT name FROM character_quests WHERE charId=:playerId: AND var='<state>' ORDER by name")
    List<String> findQuestNameByPlayerAndState(final int playerId);
    
    @Query("SELECT var, value FROM character_quests WHERE charId=:playerId: AND name=:name: AND var != '<state>'")
    List<QuestData> findByPlayerAndNameExcludeState(final int playerId, final String name);
    
    @Query("SELECT DISTINCT name FROM character_quests WHERE charId=:playerId: AND var='<state>' AND value=:value:")
    List<String> findQuestNameByPlayerAndStateValue(final int playerId, final String value);
}
