// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class ManaHealOverTime extends AbstractEffect
{
    private final double power;
    
    private ManaHealOverTime(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.setTicks(params.getInt("ticks"));
    }
    
    public boolean onActionTime(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead()) {
            return false;
        }
        double mp = effected.getCurrentMp();
        final double maxmp = effected.getMaxRecoverableMp();
        if (mp >= maxmp) {
            return true;
        }
        double power = this.power;
        if (Objects.nonNull(item) && (item.isPotion() || item.isElixir())) {
            power += effected.getStats().getValue(Stat.ADDITIONAL_POTION_MP, 0.0) / this.getTicks();
        }
        mp += power * this.getTicksMultiplier();
        mp = Math.min(mp, maxmp);
        effected.setCurrentMp(mp, false);
        effected.broadcastStatusUpdate(effector);
        return skill.isToggle();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ManaHealOverTime(data);
        }
        
        public String effectName() {
            return "ManaHealOverTime";
        }
    }
}
