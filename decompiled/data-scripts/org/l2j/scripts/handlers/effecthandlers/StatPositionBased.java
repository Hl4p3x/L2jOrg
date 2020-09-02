// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import java.util.function.BiFunction;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class StatPositionBased extends AbstractEffect
{
    private final double power;
    private final Position position;
    private final Stat stat;
    
    private StatPositionBased(final StatsSet params) {
        this.stat = (Stat)params.getEnum("stat", (Class)Stat.class);
        this.power = params.getDouble("power", 0.0);
        this.position = (Position)params.getEnum("position", (Class)Position.class, (Enum)Position.FRONT);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.getStats().mergePositionTypeValue(this.stat, this.position, this.power / 100.0 + 1.0, (BiFunction)MathUtil::mul);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.getStats().mergePositionTypeValue(this.stat, this.position, this.power / 100.0 + 1.0, (BiFunction)MathUtil::div);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new StatPositionBased(data);
        }
        
        public String effectName() {
            return "stat-position-based";
        }
    }
}
