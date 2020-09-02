// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class DispelAll extends AbstractEffect
{
    private DispelAll() {
    }
    
    public EffectType getEffectType() {
        return EffectType.DISPEL;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.stopAllEffects();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final DispelAll INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "DispelAll";
        }
        
        static {
            INSTANCE = new DispelAll();
        }
    }
}
