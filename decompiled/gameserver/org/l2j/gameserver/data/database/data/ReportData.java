// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import java.time.LocalDate;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("bbs_reports")
public class ReportData
{
    @Column("report_id")
    private int id;
    @Column("player_id")
    private int playerId;
    String report;
    @Column("report_date")
    LocalDate data;
    boolean pending;
    
    public ReportData() {
        this.data = LocalDate.now();
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public void setReport(final String report) {
        this.report = report;
    }
    
    public void setPending(final boolean pending) {
        this.pending = pending;
    }
}
