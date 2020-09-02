// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class RealDamage extends AbstractEffect
{
    private final double power;
    
    private RealDamage(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final double damage = this.power - this.power * (Math.min(effected.getStats().getValue(Stat.REAL_DAMAGE_RESIST, 1.0), 1.8) - 1.0);
        effected.reduceCurrentHp(damage, effector, skill, false, true, false, false, DamageInfo.DamageType.OTHER);
        if (GameUtils.isPlayer((WorldObject)effector)) {
            effector.sendDamageMessage(effected, skill, (int)damage, 0.0, false, false);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new RealDamage(data);
        }
        
        public String effectName() {
            return "RealDamage";
        }
    }
}
