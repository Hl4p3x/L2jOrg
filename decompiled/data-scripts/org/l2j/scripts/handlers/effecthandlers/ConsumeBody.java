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

public final class ConsumeBody extends AbstractEffect
{
    private ConsumeBody() {
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!effected.isDead() || effector.getTarget() != effected || (!GameUtils.isNpc((WorldObject)effected) && !GameUtils.isSummon((WorldObject)effected)) || (GameUtils.isSummon((WorldObject)effected) && effector != effected.getActingPlayer())) {
            return;
        }
        if (GameUtils.isNpc((WorldObject)effected)) {
            ((Npc)effected).endDecayTask();
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final ConsumeBody INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "ConsumeBody";
        }
        
        static {
            INSTANCE = new ConsumeBody();
        }
    }
}
