// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats;

import org.l2j.gameserver.model.actor.Creature;
import java.util.function.BiPredicate;

public class StatsHolder
{
    private final Stat _stat;
    private final double _value;
    private final BiPredicate<Creature, StatsHolder> _condition;
    
    public StatsHolder(final Stat stat, final double value, final BiPredicate<Creature, StatsHolder> condition) {
        this._stat = stat;
        this._value = value;
        this._condition = condition;
    }
    
    public StatsHolder(final Stat stat, final double value) {
        this(stat, value, null);
    }
    
    public Stat getStat() {
        return this._stat;
    }
    
    public double getValue() {
        return this._value;
    }
    
    public boolean verifyCondition(final Creature creature) {
        return this._condition == null || this._condition.test(creature, this);
    }
}
