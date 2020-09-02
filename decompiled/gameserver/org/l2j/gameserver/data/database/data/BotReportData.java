// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import java.time.LocalDateTime;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("bot_reported_char_data")
public class BotReportData
{
    @Column("bot_id")
    private int botId;
    @Column("reporter_id")
    private int reporterId;
    private String type;
    @Column("report_date")
    private LocalDateTime reportDate;
    
    public BotReportData() {
    }
    
    public BotReportData(final int botId, final int reporterId, final String type) {
        this.botId = botId;
        this.reporterId = reporterId;
        this.reportDate = LocalDateTime.now();
        this.type = type;
    }
    
    public int getBotId() {
        return this.botId;
    }
    
    public int getReporterId() {
        return this.reporterId;
    }
    
    public String getType() {
        return this.type;
    }
    
    public LocalDateTime getReportDate() {
        return this.reportDate;
    }
}
