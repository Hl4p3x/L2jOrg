// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Relax extends AbstractEffect
{
    private final double power;
    
    private Relax(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.setTicks(params.getInt("ticks"));
    }
    
    public long getEffectFlags() {
        return EffectFlag.RELAXING.getMask();
    }
    
    public EffectType getEffectType() {
        return EffectType.RELAXING;
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayer((WorldObject)effected)) {
            effected.getActingPlayer().sitDown(false);
        }
        else {
            effected.getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
        }
    }
    
    public boolean onActionTime(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead()) {
            return false;
        }
        if (GameUtils.isPlayer((WorldObject)effected) && !effected.getActingPlayer().isSitting()) {
            return false;
        }
        if (effected.getCurrentHp() + 1.0 > effected.getMaxRecoverableHp() && skill.isToggle()) {
            effected.sendPacket(SystemMessageId.THAT_SKILL_HAS_BEEN_DE_ACTIVATED_AS_HP_WAS_FULLY_RECOVERED);
            return false;
        }
        final double manaDam = this.power * this.getTicksMultiplier();
        if (manaDam > effected.getCurrentMp() && skill.isToggle()) {
            effected.sendPacket(SystemMessageId.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
            return false;
        }
        effected.reduceCurrentMp(manaDam);
        return skill.isToggle();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Relax(data);
        }
        
        public String effectName() {
            return "Relax";
        }
    }
}
