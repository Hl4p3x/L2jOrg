// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.functions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.StatFunction;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.conditions.Condition;

public final class FuncTemplate
{
    private final Class<?> _functionClass;
    private final Condition _attachCond;
    private final Condition _applayCond;
    private final Stat _stat;
    private final int _order;
    private final double _value;
    
    public FuncTemplate(final Condition attachCond, final Condition applayCond, final String functionName, final int order, final Stat stat, final double value) {
        final StatFunction function = StatFunction.valueOf(functionName.toUpperCase());
        if (order >= 0) {
            this._order = order;
        }
        else {
            this._order = function.getOrder();
        }
        this._attachCond = attachCond;
        this._applayCond = applayCond;
        this._stat = stat;
        this._value = value;
        try {
            this._functionClass = Class.forName(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, function.getName()));
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Class<?> getFunctionClass() {
        return this._functionClass;
    }
    
    public Stat getStat() {
        return this._stat;
    }
    
    public int getOrder() {
        return this._order;
    }
    
    public double getValue() {
        return this._value;
    }
    
    public boolean meetCondition(final Creature effected, final Skill skill) {
        return (this._attachCond == null || this._attachCond.test(effected, effected, skill)) && (this._applayCond == null || this._applayCond.test(effected, effected, skill));
    }
}
