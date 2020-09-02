// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.functions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.stats.Stat;

public class FuncDiv extends AbstractFunction
{
    public FuncDiv(final Stat stat, final int order, final Object owner, final double value, final Condition applayCond) {
        super(stat, order, owner, value, applayCond);
    }
    
    @Override
    public double calc(final Creature effector, final Creature effected, final Skill skill, final double initVal) {
        if (this.getApplayCond() != null) {
            if (!this.getApplayCond().test(effector, effected, skill)) {
                return initVal;
            }
        }
        try {
            return initVal / this.getValue();
        }
        catch (Exception e) {
            FuncDiv.LOG.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;D)Ljava/lang/String;, FuncDiv.class.getSimpleName(), this.getValue()));
        }
        return initVal;
    }
}
