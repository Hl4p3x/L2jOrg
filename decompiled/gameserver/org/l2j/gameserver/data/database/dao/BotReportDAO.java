// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import java.time.LocalDateTime;
import org.l2j.commons.database.annotation.Query;
import java.util.List;
import org.l2j.gameserver.data.database.data.BotReportData;
import org.l2j.commons.database.DAO;

public interface BotReportDAO extends DAO<BotReportData>
{
    @Query("SELECT * FROM bot_reported_char_data")
    List<BotReportData> findAll();
    
    @Query("DELETE FROM bot_reported_char_data WHERE report_date < :dateLimit:")
    void removeExpiredReports(final LocalDateTime dateLimit);
}
