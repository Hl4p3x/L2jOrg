// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.DAO;

public interface PrimeShopDAO extends DAO<Object>
{
    @Query("SELECT SUM(count) FROM shop_history WHERE product_id = :productId: AND bidder=:playerObjectId: AND sell_date = CURRENT_DATE")
    int countBougthItemToday(final int playerObjectId, final int productId);
    
    @Query("INSERT INTO shop_history ( product_id, count, bidder ) VALUES ( :productId:, :count:, :playerObjectId:)")
    void addHistory(final int productId, final int count, final int playerObjectId);
    
    @Query("SELECT EXISTS ( SELECT 1 FROM shop_history WHERE bidder=:playerObjectId: AND sell_date = CURRENT_DATE AND product_id BETWEEN :minProductId: AND :maxProductId: )")
    boolean hasBougthAnyItemInRangeToday(final int playerObjectId, final int minProductId, final int maxProductId);
}
