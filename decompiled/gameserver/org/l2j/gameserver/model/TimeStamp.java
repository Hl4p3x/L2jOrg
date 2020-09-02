// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

public class TimeStamp
{
    private final int _id1;
    private final int _id2;
    private final int _id3;
    private final long _reuse;
    private volatile long _stamp;
    private final int _group;
    
    public TimeStamp(final Skill skill, final long reuse, final long systime) {
        this._id1 = skill.getId();
        this._id2 = skill.getLevel();
        this._id3 = skill.getSubLevel();
        this._reuse = reuse;
        this._stamp = ((systime > 0L) ? systime : ((reuse != 0L) ? (System.currentTimeMillis() + reuse) : 0L));
        this._group = -1;
    }
    
    public TimeStamp(final Item item, final long reuse, final long systime) {
        this._id1 = item.getId();
        this._id2 = item.getObjectId();
        this._id3 = 0;
        this._reuse = reuse;
        this._stamp = ((systime > 0L) ? systime : ((reuse != 0L) ? (System.currentTimeMillis() + reuse) : 0L));
        this._group = item.getSharedReuseGroup();
    }
    
    public long getStamp() {
        return this._stamp;
    }
    
    public int getItemId() {
        return this._id1;
    }
    
    public int getItemObjectId() {
        return this._id2;
    }
    
    public int getSkillId() {
        return this._id1;
    }
    
    public int getSkillLvl() {
        return this._id2;
    }
    
    public int getSkillSubLvl() {
        return this._id3;
    }
    
    public long getReuse() {
        return this._reuse;
    }
    
    public int getSharedReuseGroup() {
        return this._group;
    }
    
    public long getRemaining() {
        if (this._stamp == 0L) {
            return 0L;
        }
        final long remainingTime = Math.max(this._stamp - System.currentTimeMillis(), 0L);
        if (remainingTime == 0L) {
            this._stamp = 0L;
        }
        return remainingTime;
    }
    
    public boolean hasNotPassed() {
        if (this._stamp == 0L) {
            return false;
        }
        final boolean hasNotPassed = System.currentTimeMillis() < this._stamp;
        if (!hasNotPassed) {
            this._stamp = 0L;
        }
        return hasNotPassed;
    }
}
