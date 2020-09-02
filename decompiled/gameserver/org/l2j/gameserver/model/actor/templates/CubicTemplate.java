// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.templates;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.CubicInstance;
import java.util.ArrayList;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.cubic.conditions.ICubicCondition;
import org.l2j.gameserver.model.cubic.CubicTargetType;
import org.l2j.gameserver.model.cubic.CubicSkill;
import java.util.List;
import org.l2j.gameserver.model.cubic.ICubicConditionHolder;

public class CubicTemplate implements ICubicConditionHolder
{
    public final List<CubicSkill> _skills;
    private final int _id;
    private final int _level;
    private final int _slot;
    private final int _duration;
    private final int _delay;
    private final int _maxCount;
    private final int _useUp;
    private final double _power;
    private final CubicTargetType _targetType;
    private final List<ICubicCondition> _conditions;
    
    public CubicTemplate(final StatsSet set) {
        this._skills = new ArrayList<CubicSkill>();
        this._conditions = new ArrayList<ICubicCondition>();
        this._id = set.getInt("id");
        this._level = set.getInt("level");
        this._slot = set.getInt("slot");
        this._duration = set.getInt("duration");
        this._delay = set.getInt("delay");
        this._maxCount = set.getInt("maxCount");
        this._useUp = set.getInt("useUp");
        this._power = set.getDouble("power");
        this._targetType = set.getEnum("targetType", CubicTargetType.class, CubicTargetType.TARGET);
    }
    
    public int getId() {
        return this._id;
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public int getSlot() {
        return this._slot;
    }
    
    public int getDuration() {
        return this._duration;
    }
    
    public int getDelay() {
        return this._delay;
    }
    
    public int getMaxCount() {
        return this._maxCount;
    }
    
    public int getUseUp() {
        return this._useUp;
    }
    
    public double getPower() {
        return this._power;
    }
    
    public CubicTargetType getTargetType() {
        return this._targetType;
    }
    
    public List<CubicSkill> getSkills() {
        return this._skills;
    }
    
    @Override
    public boolean validateConditions(final CubicInstance cubic, final Creature owner, final WorldObject target) {
        return this._conditions.isEmpty() || this._conditions.stream().allMatch(condition -> condition.test(cubic, owner, target));
    }
    
    @Override
    public void addCondition(final ICubicCondition condition) {
        this._conditions.add(condition);
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(IIIIIIIDLjava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;)Ljava/lang/String;, this._id, this._level, this._slot, this._duration, this._delay, this._maxCount, this._useUp, this._power, System.lineSeparator(), this._skills, System.lineSeparator(), this._conditions, System.lineSeparator());
    }
}
