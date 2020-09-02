// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.DAO;

public interface L2StoreDAO extends DAO<Object>
{
    @Query("SELECT SUM(count) FROM l2store_history\nWHERE product_id = :productId: AND account=:account: AND sell_date = CURRENT_DATE")
    int countBoughtProductToday(final String account, final int productId);
    
    @Query("INSERT INTO l2store_history ( product_id, count, account)\nVALUES ( :productId:, :count:, :account:)\nON DUPLICATE KEY UPDATE count=count + :count:")
    void addHistory(final int productId, final int count, final String account);
    
    @Query("SELECT EXISTS (\nSELECT 1 FROM l2store_history\nWHERE account=:account: AND sell_date = CURRENT_DATE AND product_id BETWEEN :minProductId: AND :maxProductId: )")
    boolean hasBoughtAnyProductInRangeToday(final String account, final int minProductId, final int maxProductId);
    
    @Query("SELECT SUM(count) FROM l2store_history WHERE account = :account: AND product_id = :productId:")
    int countBoughtProduct(final String account, final int productId);
    
    @Query("SELECT SUM(count) FROM l2store_history WHERE account=:account: AND product_id = :productId: AND CURRENT_DATE - INTERVAL :days: DAY <= sell_date")
    int countBoughtProductInDays(final String account, final int productId, final int days);
}
