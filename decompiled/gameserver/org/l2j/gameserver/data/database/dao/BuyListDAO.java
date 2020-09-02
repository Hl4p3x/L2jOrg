// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import java.util.List;
import org.l2j.gameserver.data.database.data.BuyListInfo;
import org.l2j.commons.database.DAO;

public interface BuyListDAO extends DAO<BuyListInfo>
{
    @Query("SELECT * FROM buylists")
    List<BuyListInfo> findAll();
}
