// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Grow extends AbstractEffect
{
    private Grow() {
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isNpc((WorldObject)effected)) {
            final Npc npc = (Npc)effected;
            npc.setCollisionHeight(npc.getTemplate().getCollisionHeightGrown());
            npc.setCollisionRadius(npc.getTemplate().getCollisionRadiusGrown());
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (GameUtils.isNpc((WorldObject)effected)) {
            final Npc npc = (Npc)effected;
            npc.setCollisionHeight((double)npc.getTemplate().getCollisionHeight());
            npc.setCollisionRadius(npc.getTemplate().getfCollisionRadius());
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final Grow INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Grow";
        }
        
        static {
            INSTANCE = new Grow();
        }
    }
}
