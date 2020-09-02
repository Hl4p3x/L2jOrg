// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class PolearmSingleTarget extends AbstractEffect
{
    private PolearmSingleTarget() {
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayer((WorldObject)effected)) {
            effected.getStats().addFixedValue(Stat.PHYSICAL_POLEARM_TARGET_SINGLE, Double.valueOf(1.0));
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (GameUtils.isPlayer((WorldObject)effected)) {
            effected.getStats().removeFixedValue(Stat.PHYSICAL_POLEARM_TARGET_SINGLE);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final PolearmSingleTarget INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "PolearmSingleTarget";
        }
        
        static {
            INSTANCE = new PolearmSingleTarget();
        }
    }
}
