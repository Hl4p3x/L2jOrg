// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class TargetMe extends AbstractEffect
{
    private TargetMe() {
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (GameUtils.isPlayable((WorldObject)effected)) {
            ((Playable)effected).setLockedTarget((Creature)null);
        }
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayable((WorldObject)effected)) {
            if (effected.getTarget() != effector) {
                effected.setTarget((WorldObject)effector);
            }
            ((Playable)effected).setLockedTarget(effector);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final TargetMe INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "TargetMe";
        }
        
        static {
            INSTANCE = new TargetMe();
        }
    }
}
