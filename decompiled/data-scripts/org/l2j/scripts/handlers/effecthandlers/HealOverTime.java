// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.serverpackets.ExRegenMax;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class HealOverTime extends AbstractEffect
{
    private final double power;
    
    private HealOverTime(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.setTicks(params.getInt("ticks"));
    }
    
    public boolean onActionTime(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead() || GameUtils.isDoor((WorldObject)effected)) {
            return false;
        }
        double hp = effected.getCurrentHp();
        final double maxhp = effected.getMaxRecoverableHp();
        if (hp >= maxhp) {
            return false;
        }
        double power = this.power;
        if (Objects.nonNull(item) && (item.isPotion() || item.isElixir())) {
            power += effected.getStats().getValue(Stat.ADDITIONAL_POTION_HP, 0.0) / this.getTicks();
        }
        hp += power * this.getTicksMultiplier();
        hp = Math.min(hp, maxhp);
        effected.setCurrentHp(hp, false);
        effected.broadcastStatusUpdate(effector);
        return skill.isToggle();
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayer((WorldObject)effected) && this.getTicks() > 0 && skill.getAbnormalType() == AbnormalType.HP_RECOVER) {
            double power = this.power;
            if (Objects.nonNull(item) && (item.isPotion() || item.isElixir())) {
                final double bonus = effected.getStats().getValue(Stat.ADDITIONAL_POTION_HP, 0.0);
                if (bonus > 0.0) {
                    power += bonus / this.getTicks();
                }
            }
            effected.sendPacket(new ServerPacket[] { (ServerPacket)new ExRegenMax(skill.getAbnormalTime(), this.getTicks(), power) });
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new HealOverTime(data);
        }
        
        public String effectName() {
            return "HealOverTime";
        }
    }
}
