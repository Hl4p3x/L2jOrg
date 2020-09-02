// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.ai.CtrlIntention;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Confuse extends AbstractEffect
{
    private final int chance;
    
    private Confuse(final StatsSet params) {
        this.chance = params.getInt("power", 100);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return Formulas.calcProbability((double)this.chance, effector, effected, skill);
    }
    
    public long getEffectFlags() {
        return EffectFlag.CONFUSED.getMask();
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.getAI().notifyEvent(CtrlEvent.EVT_CONFUSED);
        final Creature creature = (Creature)World.getInstance().findAnyVisibleObject((WorldObject)effected, (Class)Creature.class, ((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).partyRange(), false, c -> GeoEngine.getInstance().canSeeTarget((WorldObject)effected, c));
        if (Objects.nonNull(creature)) {
            effected.setTarget((WorldObject)creature);
            effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { creature });
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Confuse(data);
        }
        
        public String effectName() {
            return "Confuse";
        }
    }
}
