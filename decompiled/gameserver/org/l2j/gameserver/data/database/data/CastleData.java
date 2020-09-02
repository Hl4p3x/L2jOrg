// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import java.time.LocalDateTime;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.commons.database.annotation.Table;

@Table("castle")
public class CastleData
{
    private int id;
    private String name;
    private CastleSide side;
    private long treasury;
    @Column("siege_date")
    private LocalDateTime siegeDate;
    @Column("siege_time_registration_end")
    private LocalDateTime siegeTimeRegistrationEnd;
    @Column("show_npc_crest")
    private boolean showNpcCrest;
    @Column("ticket_buy_count")
    private int ticketBuyCount;
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public CastleSide getSide() {
        return this.side;
    }
    
    public void setSide(final CastleSide side) {
        this.side = side;
    }
    
    public long getTreasury() {
        return this.treasury;
    }
    
    public void setTreasury(final long treasury) {
        this.treasury = treasury;
    }
    
    public void updateTreasury(final long amount) {
        this.treasury += amount;
    }
    
    public LocalDateTime getSiegeDate() {
        return this.siegeDate;
    }
    
    public void setSiegeDate(final LocalDateTime siegeDate) {
        this.siegeDate = siegeDate;
    }
    
    public LocalDateTime getSiegeTimeRegistrationEnd() {
        return this.siegeTimeRegistrationEnd;
    }
    
    public void setSiegeTimeRegistrationEnd(final LocalDateTime date) {
        this.siegeTimeRegistrationEnd = date;
    }
    
    public boolean isShowNpcCrest() {
        return this.showNpcCrest;
    }
    
    public void setShowNpcCrest(final boolean showNpcCrest) {
        this.showNpcCrest = showNpcCrest;
    }
    
    public int getTicketBuyCount() {
        return this.ticketBuyCount;
    }
    
    public void setTicketBuyCount(final int count) {
        this.ticketBuyCount = count;
    }
}
