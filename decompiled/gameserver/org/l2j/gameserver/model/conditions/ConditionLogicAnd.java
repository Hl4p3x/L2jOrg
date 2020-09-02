// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionLogicAnd extends Condition
{
    private static Condition[] _emptyConditions;
    public Condition[] conditions;
    
    public ConditionLogicAnd(final Condition... conditions) {
        this.conditions = ConditionLogicAnd._emptyConditions;
        this.conditions = conditions;
    }
    
    public void add(final Condition condition) {
        if (condition == null) {
            return;
        }
        final int len = this.conditions.length;
        final Condition[] tmp = new Condition[len + 1];
        System.arraycopy(this.conditions, 0, tmp, 0, len);
        tmp[len] = condition;
        this.conditions = tmp;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        for (final Condition c : this.conditions) {
            if (!c.test(effector, effected, skill, item)) {
                return false;
            }
        }
        return true;
    }
    
    static {
        ConditionLogicAnd._emptyConditions = new Condition[0];
    }
}
