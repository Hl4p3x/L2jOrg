// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class PhysicalMute extends AbstractEffect
{
    private PhysicalMute() {
    }
    
    public long getEffectFlags() {
        return EffectFlag.PSYCHICAL_MUTED.getMask();
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.getAI().notifyEvent(CtrlEvent.EVT_MUTED);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final PhysicalMute INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "PhysicalMute";
        }
        
        static {
            INSTANCE = new PhysicalMute();
        }
    }
}
