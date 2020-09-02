// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class SilentMove extends AbstractEffect
{
    private SilentMove() {
    }
    
    public long getEffectFlags() {
        return EffectFlag.SILENT_MOVE.getMask();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final SilentMove INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "SilentMove";
        }
        
        static {
            INSTANCE = new SilentMove();
        }
    }
}
