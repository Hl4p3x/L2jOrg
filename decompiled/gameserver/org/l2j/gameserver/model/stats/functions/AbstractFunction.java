// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.functions;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.stats.Stat;
import org.slf4j.Logger;

public abstract class AbstractFunction
{
    protected static final Logger LOG;
    private final Stat _stat;
    private final int _order;
    private final Object _funcOwner;
    private final Condition _applayCond;
    private final double _value;
    
    public AbstractFunction(final Stat stat, final int order, final Object owner, final double value, final Condition applayCond) {
        this._stat = stat;
        this._order = order;
        this._funcOwner = owner;
        this._value = value;
        this._applayCond = applayCond;
    }
    
    public Condition getApplayCond() {
        return this._applayCond;
    }
    
    public final Object getFuncOwner() {
        return this._funcOwner;
    }
    
    public final int getOrder() {
        return this._order;
    }
    
    public final Stat getStat() {
        return this._stat;
    }
    
    public final double getValue() {
        return this._value;
    }
    
    public abstract double calc(final Creature effector, final Creature effected, final Skill skill, final double initVal);
    
    static {
        LOG = LoggerFactory.getLogger(AbstractFunction.class.getName());
    }
}
