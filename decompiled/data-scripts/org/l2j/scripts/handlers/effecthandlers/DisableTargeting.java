// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class DisableTargeting extends AbstractEffect
{
    private DisableTargeting() {
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.setTarget((WorldObject)null);
        effected.abortAttack();
        effected.abortCast();
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }
    
    public long getEffectFlags() {
        return EffectFlag.TARGETING_DISABLED.getMask();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final DisableTargeting INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "DisableTargeting";
        }
        
        static {
            INSTANCE = new DisableTargeting();
        }
    }
}
