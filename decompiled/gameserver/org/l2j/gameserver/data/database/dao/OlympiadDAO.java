// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.gameserver.data.database.data.OlympiadData;
import org.l2j.commons.database.DAO;

public interface OlympiadDAO extends DAO<OlympiadData>
{
    OlympiadData findData();
}
