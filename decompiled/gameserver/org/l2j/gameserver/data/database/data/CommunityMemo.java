// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import java.time.LocalDateTime;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("community_memos")
public class CommunityMemo
{
    private int id;
    @Column("owner_id")
    private int playerId;
    private LocalDateTime date;
    private String title;
    private String text;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public LocalDateTime getDate() {
        return this.date;
    }
    
    public void setDate(final LocalDateTime date) {
        this.date = date;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
}
