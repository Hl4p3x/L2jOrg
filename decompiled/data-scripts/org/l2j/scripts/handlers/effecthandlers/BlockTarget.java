// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class BlockTarget extends AbstractEffect
{
    private BlockTarget() {
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.setTargetable(false);
        World.getInstance().forEachVisibleObject((WorldObject)effected, (Class)Creature.class, target -> {
            if (target.getTarget() == effected) {
                target.setTarget((WorldObject)null);
            }
        });
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.setTargetable(true);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final BlockTarget INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "BlockTarget";
        }
        
        static {
            INSTANCE = new BlockTarget();
        }
    }
}
