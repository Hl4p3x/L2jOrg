// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Transformation extends AbstractEffect
{
    private final int[] id;
    
    private Transformation(final StatsSet params) {
        this.id = params.getIntArray("id", " ");
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return !GameUtils.isDoor((WorldObject)effected);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (this.id.length > 0) {
            effected.transform(Rnd.get(this.id), true);
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.stopTransformation(false);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Transformation(data);
        }
        
        public String effectName() {
            return "transformation";
        }
    }
}
