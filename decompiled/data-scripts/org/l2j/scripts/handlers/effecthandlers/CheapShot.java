// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class CheapShot extends AbstractEffect
{
    private CheapShot() {
    }
    
    public long getEffectFlags() {
        return EffectFlag.CHEAPSHOT.getMask();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final CheapShot INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "CheapShot";
        }
        
        static {
            INSTANCE = new CheapShot();
        }
    }
}
