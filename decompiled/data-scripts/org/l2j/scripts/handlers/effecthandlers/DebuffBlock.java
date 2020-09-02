// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class DebuffBlock extends AbstractEffect
{
    private DebuffBlock() {
    }
    
    public long getEffectFlags() {
        return EffectFlag.DEBUFF_BLOCK.getMask();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final DebuffBlock INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "DebuffBlock";
        }
        
        static {
            INSTANCE = new DebuffBlock();
        }
    }
}
