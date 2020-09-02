// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class BlockResurrection extends AbstractEffect
{
    private BlockResurrection() {
    }
    
    public long getEffectFlags() {
        return EffectFlag.BLOCK_RESURRECTION.getMask();
    }
    
    public static final class Factory implements SkillEffectFactory
    {
        private static final BlockResurrection INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return null;
        }
        
        public String effectName() {
            return "BlockResurrection";
        }
        
        static {
            INSTANCE = new BlockResurrection();
        }
    }
}
