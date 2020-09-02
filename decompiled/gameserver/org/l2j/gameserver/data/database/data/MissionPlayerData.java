// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("character_missions")
public class MissionPlayerData
{
    @Column("char_id")
    private int playerObjectId;
    @Column("mission_id")
    private int missionId;
    private MissionStatus status;
    private int progress;
    @NonUpdatable
    private boolean recentlyCompleted;
    
    public MissionPlayerData() {
        this.status = MissionStatus.NOT_AVAILABLE;
    }
    
    public MissionPlayerData(final int objectId, final int missionId) {
        this.status = MissionStatus.NOT_AVAILABLE;
        this.playerObjectId = objectId;
        this.missionId = missionId;
    }
    
    public int getObjectId() {
        return this.playerObjectId;
    }
    
    public MissionStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(final MissionStatus status) {
        this.status = status;
    }
    
    public int getProgress() {
        return this.progress;
    }
    
    public void setProgress(final int progress) {
        this.progress = progress;
    }
    
    public int increaseProgress() {
        return ++this.progress;
    }
    
    public boolean isRecentlyCompleted() {
        return this.recentlyCompleted;
    }
    
    public void setRecentlyCompleted(final boolean recentlyCompleted) {
        this.recentlyCompleted = recentlyCompleted;
    }
    
    public int getMissionId() {
        return this.missionId;
    }
    
    public boolean isAvailable() {
        return this.status == MissionStatus.AVAILABLE;
    }
}
