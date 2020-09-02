// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class IgnoreDeath extends AbstractEffect
{
    private IgnoreDeath() {
    }
    
    public long getEffectFlags() {
        return EffectFlag.IGNORE_DEATH.getMask();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final IgnoreDeath INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "IgnoreDeath";
        }
        
        static {
            INSTANCE = new IgnoreDeath();
        }
    }
}
