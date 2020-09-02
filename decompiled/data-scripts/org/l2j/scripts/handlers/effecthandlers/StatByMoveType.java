// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.MoveType;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class StatByMoveType extends AbstractEffect
{
    private final Stat stat;
    private final MoveType type;
    private final double power;
    
    private StatByMoveType(final StatsSet params) {
        this.stat = (Stat)params.getEnum("stat", (Class)Stat.class);
        this.type = (MoveType)params.getEnum("type", (Class)MoveType.class);
        this.power = params.getDouble("power");
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.getStats().mergeMoveTypeValue(this.stat, this.type, this.power);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.getStats().mergeMoveTypeValue(this.stat, this.type, -this.power);
    }
    
    public boolean onActionTime(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        return skill.isPassive() || skill.isToggle();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new StatByMoveType(data);
        }
        
        public String effectName() {
            return "stat-by-move-type";
        }
    }
}
