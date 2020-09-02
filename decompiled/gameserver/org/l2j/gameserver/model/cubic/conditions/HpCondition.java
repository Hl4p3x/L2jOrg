// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.cubic.conditions;

import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.CubicInstance;

public class HpCondition implements ICubicCondition
{
    private final HpConditionType _type;
    private final int _hpPer;
    
    public HpCondition(final HpConditionType type, final int hpPer) {
        this._type = type;
        this._hpPer = hpPer;
    }
    
    @Override
    public boolean test(final CubicInstance cubic, final Creature owner, final WorldObject target) {
        if (GameUtils.isCreature(target) || GameUtils.isDoor(target)) {
            final double hpPer = (GameUtils.isDoor(target) ? ((Door)target) : ((Creature)target)).getCurrentHpPercent();
            switch (this._type) {
                case GREATER: {
                    return hpPer > this._hpPer;
                }
                case LESSER: {
                    return hpPer < this._hpPer;
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), this._hpPer);
    }
    
    public enum HpConditionType
    {
        GREATER, 
        LESSER;
    }
}
