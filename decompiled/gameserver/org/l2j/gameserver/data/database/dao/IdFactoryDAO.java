// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.database.DAO;

public interface IdFactoryDAO extends DAO<Object>
{
    @Query("SELECT charId AS id FROM characters\nUNION SELECT object_id AS id FROM items\nUNION SELECT clan_id AS id FROM clan_data\nUNION SELECT object_id AS id FROM itemsonground\nUNION SELECT id FROM mail")
    IntSet findUsedObjectIds();
}
