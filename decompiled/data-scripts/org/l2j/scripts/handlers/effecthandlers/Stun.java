// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.instance.SiegeFlag;
import org.l2j.gameserver.model.actor.instance.Defender;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.Objects;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class Stun extends AbstractEffect
{
    public long getEffectFlags() {
        return EffectFlag.STUNNED.getMask();
    }
    
    public EffectType getEffectType() {
        return EffectType.STUN;
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return !Objects.isNull(effected) && !effected.isRaid() && (GameUtils.isPlayer((WorldObject)effected) || GameUtils.isSummon((WorldObject)effected) || (GameUtils.isAttackable((WorldObject)effected) && !(effected instanceof Defender) && !(effected instanceof SiegeFlag) && effected.getTemplate().getRace() != Race.SIEGE_WEAPON));
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.getAI().notifyEvent(CtrlEvent.EVT_ACTION_BLOCKED);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (!GameUtils.isPlayer((WorldObject)effected)) {
            effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final Stun INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Stun";
        }
        
        static {
            INSTANCE = new Stun();
        }
    }
}
