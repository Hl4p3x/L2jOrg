// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import java.io.Serializable;

public class TrainingHolder implements Serializable
{
    private static final long TRAINING_DIVIDER;
    private final int _objectId;
    private final int _classIndex;
    private final int _level;
    private final long _startTime;
    private long _endTime;
    
    public TrainingHolder(final int objectId, final int classIndex, final int level, final long startTime, final long endTime) {
        this._endTime = -1L;
        this._objectId = objectId;
        this._classIndex = classIndex;
        this._level = level;
        this._startTime = startTime;
        this._endTime = endTime;
    }
    
    public static long getTrainingDivider() {
        return TrainingHolder.TRAINING_DIVIDER;
    }
    
    public long getEndTime() {
        return this._endTime;
    }
    
    public void setEndTime(final long endTime) {
        this._endTime = endTime;
    }
    
    public int getObjectId() {
        return this._objectId;
    }
    
    public int getClassIndex() {
        return this._classIndex;
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public long getStartTime() {
        return this._startTime;
    }
    
    public boolean isTraining() {
        return this._endTime == -1L;
    }
    
    public boolean isValid(final Player player) {
        return Config.TRAINING_CAMP_ENABLE && player.getObjectId() == this._objectId && player.getClassIndex() == this._classIndex;
    }
    
    public long getElapsedTime() {
        return TimeUnit.SECONDS.convert(System.currentTimeMillis() - this._startTime, TimeUnit.MILLISECONDS);
    }
    
    public long getRemainingTime() {
        return TimeUnit.SECONDS.toMinutes(Config.TRAINING_CAMP_MAX_DURATION - this.getElapsedTime());
    }
    
    public long getTrainingTime(final TimeUnit unit) {
        return Math.min(unit.convert(Config.TRAINING_CAMP_MAX_DURATION, TimeUnit.SECONDS), unit.convert(this._endTime - this._startTime, TimeUnit.MILLISECONDS));
    }
    
    static {
        TRAINING_DIVIDER = TimeUnit.SECONDS.toMinutes(Config.TRAINING_CAMP_MAX_DURATION);
    }
}
