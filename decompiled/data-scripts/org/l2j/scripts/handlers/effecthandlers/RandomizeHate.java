// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class RandomizeHate extends AbstractEffect
{
    private final double power;
    
    private RandomizeHate(final StatsSet params) {
        this.power = params.getDouble("power", 100.0);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return Formulas.calcProbability(this.power, effector, effected, skill);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected == effector || !GameUtils.isAttackable((WorldObject)effected)) {
            return;
        }
        final Attackable effectedMob = (Attackable)effected;
        final Npc npc;
        final Creature target = (Creature)World.getInstance().findAnyVisibleObject((WorldObject)effected, (Class)Creature.class, ((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).partyRange() / 2, false, creature -> creature != effector && (!GameUtils.isAttackable((WorldObject)creature) || !creature.isInMyClan(npc)));
        final int hate = effectedMob.getHating(effector);
        effectedMob.stopHating(effector);
        effectedMob.addDamageHate(target, 0, hate);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new RandomizeHate(data);
        }
        
        public String effectName() {
            return "RandomizeHate";
        }
    }
}
