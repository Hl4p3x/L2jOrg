// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerFlyMounted extends Condition
{
    private static final ConditionPlayerFlyMounted FLYING;
    private static final ConditionPlayerFlyMounted NO_FLYING;
    public final boolean _val;
    
    private ConditionPlayerFlyMounted(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector.getActingPlayer() == null || effector.getActingPlayer().isFlyingMounted() == this._val;
    }
    
    public static ConditionPlayerFlyMounted of(final boolean flying) {
        return flying ? ConditionPlayerFlyMounted.FLYING : ConditionPlayerFlyMounted.NO_FLYING;
    }
    
    static {
        FLYING = new ConditionPlayerFlyMounted(true);
        NO_FLYING = new ConditionPlayerFlyMounted(false);
    }
}
