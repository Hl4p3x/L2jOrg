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
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.util.Rnd;
import java.util.Objects;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttackAvoid;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class TriggerSkillByAvoid extends AbstractEffect
{
    private final int chance;
    private final SkillHolder skill;
    private final TargetType targetType;
    
    private TriggerSkillByAvoid(final StatsSet params) {
        this.chance = params.getInt("chance", 100);
        this.skill = new SkillHolder(params.getInt("skill", 0), params.getInt("power", 0));
        this.targetType = (TargetType)params.getEnum("target", (Class)TargetType.class, (Enum)TargetType.TARGET);
    }
    
    private void onAvoidEvent(final OnCreatureAttackAvoid event) {
        if (event.isDamageOverTime() || this.chance == 0 || this.skill.getSkillId() == 0 || this.skill.getLevel() == 0) {
            return;
        }
        final ITargetTypeHandler targetHandler = TargetHandler.getInstance().getHandler((Enum)this.targetType);
        if (Objects.isNull(targetHandler)) {
            TriggerSkillByAvoid.LOGGER.warn("Handler for target type: {} does not exist.", (Object)this.targetType);
            return;
        }
        if (this.chance < 100 && Rnd.get(100) > this.chance) {
            return;
        }
        final Skill triggerSkill = this.skill.getSkill();
        WorldObject target = null;
        try {
            target = TargetHandler.getInstance().getHandler((Enum)this.targetType).getTarget(event.getTarget(), (WorldObject)event.getAttacker(), triggerSkill, false, false, false);
        }
        catch (Exception e) {
            TriggerSkillByAvoid.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
        if (GameUtils.isCreature(target)) {
            SkillCaster.triggerCast(event.getAttacker(), (Creature)target, triggerSkill);
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.removeListenerIf(EventType.ON_CREATURE_ATTACK_AVOID, listener -> listener.getOwner() == this);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)effected, EventType.ON_CREATURE_ATTACK_AVOID, (Consumer)this::onAvoidEvent, (Object)this));
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new TriggerSkillByAvoid(data);
        }
        
        public String effectName() {
            return "trigger-skill-by-avoid";
        }
    }
}
