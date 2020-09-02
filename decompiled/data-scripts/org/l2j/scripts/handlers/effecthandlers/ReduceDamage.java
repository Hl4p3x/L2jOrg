// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Function;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.returns.DamageReturn;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class ReduceDamage extends AbstractEffect
{
    private final double power;
    
    private ReduceDamage(final StatsSet params) {
        this.power = params.getDouble("power");
    }
    
    private DamageReturn onDamageReceivedEvent(final OnCreatureDamageReceived event) {
        if (event.isDamageOverTime()) {
            return null;
        }
        final double newDamage = event.getDamage() * (this.power / 100.0);
        return new DamageReturn(false, true, false, newDamage);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.removeListenerIf(EventType.ON_CREATURE_DAMAGE_RECEIVED, listener -> listener.getOwner() == this);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.addListener((AbstractEventListener)new FunctionEventListener((ListenersContainer)effected, EventType.ON_CREATURE_DAMAGE_RECEIVED, (Function)this::onDamageReceivedEvent, (Object)this));
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ReduceDamage(data);
        }
        
        public String effectName() {
            return "ReduceDamage";
        }
    }
}
