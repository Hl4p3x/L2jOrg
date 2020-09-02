// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.cubic.conditions;

import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.CubicInstance;

public class HealthCondition implements ICubicCondition
{
    private final int _min;
    private final int _max;
    
    public HealthCondition(final int min, final int max) {
        this._min = min;
        this._max = max;
    }
    
    @Override
    public boolean test(final CubicInstance cubic, final Creature owner, final WorldObject target) {
        if (GameUtils.isCreature(target) || GameUtils.isDoor(target)) {
            final double hpPer = (GameUtils.isDoor(target) ? ((Door)target) : ((Creature)target)).getCurrentHpPercent();
            return hpPer > this._min && hpPer < this._max;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), this._min, this._max);
    }
}
