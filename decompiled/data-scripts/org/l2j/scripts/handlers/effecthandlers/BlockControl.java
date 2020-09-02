// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class BlockControl extends AbstractEffect
{
    private BlockControl() {
    }
    
    public long getEffectFlags() {
        return EffectFlag.BLOCK_CONTROL.getMask();
    }
    
    public EffectType getEffectType() {
        return EffectType.BLOCK_CONTROL;
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final BlockControl INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "BlockControl";
        }
        
        static {
            INSTANCE = new BlockControl();
        }
    }
}
