// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import java.sql.ResultSet;
import java.util.function.Consumer;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.engine.item.shop.l2store.RestrictionPeriod;
import org.l2j.commons.database.DAO;

public interface LCoinShopDAO extends DAO<Object>
{
    @Query("INSERT INTO lcoin_shop_history (product_id, account, count, restriction_type)\nVALUES (:id:, :account:, :count:, :restrictionPeriod: )\nON DUPLICATE KEY UPDATE count=count + :count:")
    void saveHistory(final String account, final int id, final int count, final RestrictionPeriod restrictionPeriod);
    
    @Query("DELETE FROM lcoin_shop_history\nWHERE (restriction_type = 'DAY' AND sell_date < CURRENT_DATE)\n  OR (restriction_type = 'MONTH' AND sell_date < CURRENT_DATE - INTERVAL 30 DAY)")
    void deleteExpired();
    
    @Query("SELECT product_id, `account`,  SUM(`count`) AS `sum` FROM lcoin_shop_history\nGROUP BY product_id, `account`;\n")
    void loadAllGrouped(final Consumer<ResultSet> result);
}
