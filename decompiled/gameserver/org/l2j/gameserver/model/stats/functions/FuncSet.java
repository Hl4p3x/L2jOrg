// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.functions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.stats.Stat;

public class FuncSet extends AbstractFunction
{
    public FuncSet(final Stat stat, final int order, final Object owner, final double value, final Condition applayCond) {
        super(stat, order, owner, value, applayCond);
    }
    
    @Override
    public double calc(final Creature effector, final Creature effected, final Skill skill, final double initVal) {
        if (this.getApplayCond() == null || this.getApplayCond().test(effector, effected, skill)) {
            return this.getValue();
        }
        return initVal;
    }
}
