// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.NonUpdatable;
import java.time.LocalDate;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("olympiad_data")
public class OlympiadData
{
    private int id;
    @Column("current_cycle")
    private int season;
    private int period;
    @Column("olympiad_end")
    private long olympiadEnd;
    @Column("validation_end")
    private long validationEnd;
    @Column("next_weekly_change")
    private long nextWeeklyChange;
    @NonUpdatable
    private LocalDate nextSeasonDate;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getSeason() {
        return this.season;
    }
    
    public void setSeason(final int season) {
        this.season = season;
    }
    
    public int getPeriod() {
        return this.period;
    }
    
    public void setPeriod(final int period) {
        this.period = period;
    }
    
    public long getOlympiadEnd() {
        return this.olympiadEnd;
    }
    
    public void setOlympiadEnd(final long olympiadEnd) {
        this.olympiadEnd = olympiadEnd;
    }
    
    public long getNextWeeklyChange() {
        return this.nextWeeklyChange;
    }
    
    public void setNextWeeklyChange(final long nextWeeklyChange) {
        this.nextWeeklyChange = nextWeeklyChange;
    }
    
    public void increaseSeason() {
        ++this.season;
    }
    
    public long getValidationEnd() {
        return this.validationEnd;
    }
    
    public void setValidationEnd(final long validationEnd) {
        this.validationEnd = validationEnd;
    }
    
    public void setNextSeasonDate(final LocalDate nextSeasonDate) {
        this.nextSeasonDate = nextSeasonDate;
    }
    
    public LocalDate getNextSeasonDate() {
        return this.nextSeasonDate;
    }
}
