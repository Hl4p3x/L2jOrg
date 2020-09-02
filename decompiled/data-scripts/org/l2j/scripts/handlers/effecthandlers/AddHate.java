// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class AddHate extends AbstractEffect
{
    private final double power;
    
    private AddHate(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isAttackable((WorldObject)effected)) {
            return;
        }
        final double val = this.power;
        if (val > 0.0) {
            ((Attackable)effected).addDamageHate(effector, 0, (int)val);
        }
        else if (val < 0.0) {
            ((Attackable)effected).reduceHate(effector, (int)(-val));
        }
    }
    
    public static final class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new AddHate(data);
        }
        
        public String effectName() {
            return "AddHate";
        }
    }
}
