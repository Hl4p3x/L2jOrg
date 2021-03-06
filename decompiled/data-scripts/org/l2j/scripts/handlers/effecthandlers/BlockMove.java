// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class BlockMove extends AbstractEffect
{
    private BlockMove() {
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.setIsImmobilized(true);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.setIsImmobilized(false);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final BlockMove INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "BlockMove";
        }
        
        static {
            INSTANCE = new BlockMove();
        }
    }
}
