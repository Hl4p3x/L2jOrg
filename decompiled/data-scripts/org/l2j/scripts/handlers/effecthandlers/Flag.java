// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Flag extends AbstractEffect
{
    private Flag() {
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return GameUtils.isPlayer((WorldObject)effected);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.updatePvPFlag(1);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.getActingPlayer().updatePvPFlag(0);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final Flag INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Flag";
        }
        
        static {
            INSTANCE = new Flag();
        }
    }
}
