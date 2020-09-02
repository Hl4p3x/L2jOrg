// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class TransferHate extends AbstractEffect
{
    private final int power;
    
    private TransferHate(final StatsSet params) {
        this.power = params.getInt("power", 100);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return Formulas.calcProbability((double)this.power, effector, effected, skill);
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return GameUtils.checkIfInRange(skill.getEffectRange(), (WorldObject)effector, (WorldObject)effected, true);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        int hate;
        World.getInstance().forEachVisibleObjectInRange((WorldObject)effector, (Class)Attackable.class, skill.getAffectRange(), hater -> {
            if (!hater.isDead()) {
                hate = hater.getHating(effector);
                if (hate > 0) {
                    hater.reduceHate(effector, -hate);
                    hater.addDamageHate(effected, 0, hate);
                }
            }
        });
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new TransferHate(data);
        }
        
        public String effectName() {
            return "TransferHate";
        }
    }
}
