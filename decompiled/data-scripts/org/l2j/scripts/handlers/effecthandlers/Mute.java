// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.ai.CtrlEvent;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Mute extends AbstractEffect
{
    private Mute() {
    }
    
    public long getEffectFlags() {
        return EffectFlag.MUTED.getMask();
    }
    
    public EffectType getEffectType() {
        return EffectType.MUTE;
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (Objects.isNull(effected) || effected.isRaid()) {
            return;
        }
        effected.abortCast();
        effected.getAI().notifyEvent(CtrlEvent.EVT_MUTED);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final Mute INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Mute";
        }
        
        static {
            INSTANCE = new Mute();
        }
    }
}
