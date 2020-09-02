// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class MpConsumePerLevel extends AbstractEffect
{
    private final double power;
    
    private MpConsumePerLevel(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.setTicks(params.getInt("ticks"));
    }
    
    public boolean onActionTime(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead()) {
            return false;
        }
        final double base = this.power * this.getTicksMultiplier();
        final double consume = (skill.getAbnormalTime() > 0) ? ((effected.getLevel() - 1) / 7.5 * base * skill.getAbnormalTime()) : base;
        if (consume > effected.getCurrentMp()) {
            effected.sendPacket(SystemMessageId.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
            return false;
        }
        effected.reduceCurrentMp(consume);
        return skill.isToggle();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new MpConsumePerLevel(data);
        }
        
        public String effectName() {
            return "MpConsumePerLevel";
        }
    }
}
