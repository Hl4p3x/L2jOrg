// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.cubic;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import java.util.ArrayList;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.cubic.conditions.ICubicCondition;
import java.util.List;
import org.l2j.gameserver.model.holders.SkillHolder;

public class CubicSkill extends SkillHolder implements ICubicConditionHolder
{
    private final int _triggerRate;
    private final int _successRate;
    private final boolean _canUseOnStaticObjects;
    private final CubicTargetType _targetType;
    private final List<ICubicCondition> _conditions;
    private final boolean _targetDebuff;
    
    public CubicSkill(final StatsSet set) {
        super(set.getInt("id"), set.getInt("level"));
        this._conditions = new ArrayList<ICubicCondition>();
        this._triggerRate = set.getInt("triggerRate", 100);
        this._successRate = set.getInt("successRate", 100);
        this._canUseOnStaticObjects = set.getBoolean("canUseOnStaticObjects", false);
        this._targetType = set.getEnum("target", CubicTargetType.class, CubicTargetType.TARGET);
        this._targetDebuff = set.getBoolean("targetDebuff", false);
    }
    
    public int getTriggerRate() {
        return this._triggerRate;
    }
    
    public int getSuccessRate() {
        return this._successRate;
    }
    
    public boolean canUseOnStaticObjects() {
        return this._canUseOnStaticObjects;
    }
    
    public CubicTargetType getTargetType() {
        return this._targetType;
    }
    
    public boolean isTargetingDebuff() {
        return this._targetDebuff;
    }
    
    @Override
    public boolean validateConditions(final CubicInstance cubic, final Creature owner, final WorldObject target) {
        return (!this._targetDebuff || (GameUtils.isCreature(target) && ((Creature)target).getEffectList().getDebuffCount() > 0)) && (this._conditions.isEmpty() || this._conditions.stream().allMatch(condition -> condition.test(cubic, owner, target)));
    }
    
    @Override
    public void addCondition(final ICubicCondition condition) {
        this._conditions.add(condition);
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(IIIIZLorg/l2j/gameserver/model/cubic/CubicTargetType;ZLjava/lang/String;)Ljava/lang/String;, this.getSkillId(), this.getLevel(), this._triggerRate, this._successRate, this._canUseOnStaticObjects, this._targetType, this._targetDebuff, System.lineSeparator());
    }
}
