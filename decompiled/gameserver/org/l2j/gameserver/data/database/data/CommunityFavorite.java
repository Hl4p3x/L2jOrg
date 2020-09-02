// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import java.time.LocalDateTime;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;

@Table("bbs_favorites")
public class CommunityFavorite
{
    @NonUpdatable
    @Column("favId")
    private int id;
    private int playerId;
    @Column("favTitle")
    private String title;
    @Column("favBypass")
    private String bypass;
    @NonUpdatable
    @Column("favAddDate")
    private LocalDateTime date;
    
    public int getId() {
        return this.id;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getBypass() {
        return this.bypass;
    }
    
    public void setBypass(final String bypass) {
        this.bypass = bypass;
    }
    
    public LocalDateTime getDate() {
        return this.date;
    }
}
