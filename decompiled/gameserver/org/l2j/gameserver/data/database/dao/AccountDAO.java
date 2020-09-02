// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.AccountData;
import org.l2j.commons.database.DAO;

public interface AccountDAO extends DAO<AccountData>
{
    @Query("DELETE FROM account_data a WHERE a.account NOT IN (SELECT account_name FROM characters)")
    int deleteWithoutAccount();
    
    @Query("SELECT * FROM account_data WHERE account = :account:")
    AccountData findById(final String account);
}
