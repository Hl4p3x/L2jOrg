// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers.stat;

import org.l2j.gameserver.model.events.impl.character.OnCreatureHpChange;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.StatsSet;
import java.util.concurrent.atomic.AtomicBoolean;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Map;

abstract class AbstractConditionalHpEffect extends AbstractStatEffect
{
    public final int hpPercent;
    private final Map<Creature, AtomicBoolean> updates;
    
    protected AbstractConditionalHpEffect(final StatsSet params, final Stat stat) {
        super(params, stat);
        this.updates = new ConcurrentHashMap<Creature, AtomicBoolean>();
        this.hpPercent = params.getInt("hp-percent", 0);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (Objects.isNull(skill)) {
            return;
        }
        if (this.hpPercent > 0 && !this.updates.containsKey(effected)) {
            this.updates.put(effected, new AtomicBoolean(this.canPump(effector, effected, skill)));
            effected.addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)effected, EventType.ON_CREATURE_HP_CHANGE, (Consumer)this::onHpChange, (Object)this));
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (Objects.isNull(skill)) {
            return;
        }
        effected.removeListenerIf(listener -> listener.getOwner() == this);
        this.updates.remove(effected);
    }
    
    public boolean canPump(final Creature effector, final Creature effected, final Skill skill) {
        return this.hpPercent <= 0 || effected.getCurrentHpPercent() <= this.hpPercent;
    }
    
    private void onHpChange(final OnCreatureHpChange event) {
        final Creature creature = event.getCreature();
        final AtomicBoolean update = this.updates.get(creature);
        if (Objects.isNull(update)) {
            return;
        }
        if (this.canPump(null, creature, null)) {
            if (update.get()) {
                update.set(false);
                creature.getStats().recalculateStats(true);
            }
        }
        else if (!update.get()) {
            update.set(true);
            creature.getStats().recalculateStats(true);
        }
    }
}
