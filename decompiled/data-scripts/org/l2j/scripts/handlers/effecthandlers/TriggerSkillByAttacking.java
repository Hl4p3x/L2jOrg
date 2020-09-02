// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;
import java.util.Objects;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttack;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class TriggerSkillByAttacking extends AbstractEffect
{
    private final int chance;
    private final SkillHolder skill;
    
    private TriggerSkillByAttacking(final StatsSet data) {
        this.chance = data.getInt("chance", 100);
        this.skill = new SkillHolder(data.getInt("skill"), data.getInt("power", 1));
    }
    
    private void onAttackEvent(final OnCreatureAttack event) {
        if (this.chance == 0 || this.skill.getSkillId() == 0 || this.skill.getLevel() == 0) {
            return;
        }
        if (event.getAttacker() == event.getTarget()) {
            return;
        }
        if (!Rnd.chance(this.chance)) {
            return;
        }
        final Skill triggerSkill = this.skill.getSkill();
        final WorldObject target = triggerSkill.getTarget(event.getAttacker(), (WorldObject)event.getTarget(), false, false, false);
        if (GameUtils.isCreature(target)) {
            final BuffInfo info = ((Creature)target).getEffectList().getBuffInfoBySkillId(triggerSkill.getId());
            if (Objects.isNull(info) || info.getSkill().getLevel() < triggerSkill.getLevel()) {
                SkillCaster.triggerCast(event.getAttacker(), (Creature)target, triggerSkill);
            }
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.removeListenerIf(EventType.ON_CREATURE_ATTACK, listener -> listener.getOwner() == this);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)effected, EventType.ON_CREATURE_ATTACK, (Consumer)this::onAttackEvent, (Object)this));
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new TriggerSkillByAttacking(data);
        }
        
        public String effectName() {
            return "trigger-skill-by-attacking";
        }
    }
}
