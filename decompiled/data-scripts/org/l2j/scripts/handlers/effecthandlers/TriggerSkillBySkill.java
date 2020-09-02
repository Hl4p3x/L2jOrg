// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillFinishCast;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class TriggerSkillBySkill extends AbstractEffect
{
    private final int castSkillId;
    private final int chance;
    private final SkillHolder skill;
    private final TargetType targetType;
    
    private TriggerSkillBySkill(final StatsSet params) {
        this.castSkillId = params.getInt("skill-trigger");
        this.chance = params.getInt("chance", 100);
        this.skill = new SkillHolder(params.getInt("skill"), params.getInt("power"));
        this.targetType = (TargetType)params.getEnum("target", (Class)TargetType.class, (Enum)TargetType.TARGET);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)effected, EventType.ON_CREATURE_SKILL_FINISH_CAST, (Consumer)this::onSkillUseEvent, (Object)this));
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.removeListenerIf(EventType.ON_CREATURE_SKILL_FINISH_CAST, listener -> listener.getOwner() == this);
    }
    
    private void onSkillUseEvent(final OnCreatureSkillFinishCast event) {
        if (this.chance == 0 || this.skill.getSkillId() == 0 || this.skill.getLevel() == 0 || this.castSkillId == 0) {
            return;
        }
        if (this.castSkillId != event.getSkill().getId()) {
            return;
        }
        if (!GameUtils.isCreature(event.getTarget())) {
            return;
        }
        if (this.chance < 100 && Rnd.chance(this.chance)) {
            return;
        }
        final Skill triggerSkill = this.skill.getSkill();
        WorldObject target = null;
        try {
            target = TargetHandler.getInstance().getHandler((Enum)this.targetType).getTarget(event.getCaster(), event.getTarget(), triggerSkill, false, false, false);
        }
        catch (Exception e) {
            TriggerSkillBySkill.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
        if (GameUtils.isCreature(target)) {
            SkillCaster.triggerCast(event.getCaster(), (Creature)target, triggerSkill);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new TriggerSkillBySkill(data);
        }
        
        public String effectName() {
            return "trigger-skill-by-skill";
        }
    }
}
