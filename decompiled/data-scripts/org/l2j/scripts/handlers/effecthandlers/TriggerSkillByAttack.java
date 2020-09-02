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
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.commons.util.Rnd;
import java.util.Objects;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageDealt;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class TriggerSkillByAttack extends AbstractEffect
{
    private final int minDamage;
    private final int chance;
    private final SkillHolder skill;
    private final TargetType targetType;
    private final InstanceType instanceType;
    private int allowWeapons;
    private final Boolean isCritical;
    private final boolean allowNormalAttack;
    private final boolean allowSkillAttack;
    private final boolean allowReflect;
    
    private TriggerSkillByAttack(final StatsSet params) {
        this.minDamage = params.getInt("min-damage", 1);
        this.chance = params.getInt("chance", 100);
        this.skill = new SkillHolder(params.getInt("skill"), params.getInt("power", 1));
        this.targetType = (TargetType)params.getEnum("target", (Class)TargetType.class, (Enum)TargetType.SELF);
        this.instanceType = (InstanceType)params.getEnum("attackerType", (Class)InstanceType.class, (Enum)InstanceType.Creature);
        this.isCritical = params.getBoolean("critical");
        this.allowNormalAttack = params.getBoolean("allowNormalAttack", true);
        this.allowSkillAttack = params.getBoolean("allowSkillAttack", false);
        this.allowReflect = params.getBoolean("allowReflect", false);
        if (params.contains("weapons") && Util.isNotEmpty(params.getString("weapons"))) {
            for (final String s : params.getString("weapons").split(" ")) {
                this.allowWeapons |= WeaponType.valueOf(s).mask();
            }
        }
    }
    
    private void onAttackEvent(final OnCreatureDamageDealt event) {
        if (event.isDamageOverTime() || this.chance == 0 || this.skill.getSkillId() == 0 || this.skill.getLevel() == 0 || (!this.allowNormalAttack && !this.allowSkillAttack)) {
            return;
        }
        if (Objects.nonNull(this.isCritical) && this.isCritical != event.isCritical()) {
            return;
        }
        if (!this.allowSkillAttack && Objects.nonNull(event.getSkill())) {
            return;
        }
        if (!this.allowNormalAttack && Objects.nonNull(event.getSkill())) {
            return;
        }
        if (!this.allowReflect && event.isReflect()) {
            return;
        }
        if (event.getAttacker() == event.getTarget()) {
            return;
        }
        if (event.getDamage() < this.minDamage || !Rnd.chance(this.chance) || !event.getAttacker().getInstanceType().isType(this.instanceType)) {
            return;
        }
        if (this.allowWeapons > 0 && (event.getAttacker().getActiveWeaponItem() == null || (event.getAttacker().getActiveWeaponItem().getItemType().mask() & this.allowWeapons) == 0x0)) {
            return;
        }
        final Skill triggerSkill = this.skill.getSkill();
        WorldObject target = null;
        try {
            target = TargetHandler.getInstance().getHandler((Enum)this.targetType).getTarget(event.getAttacker(), (WorldObject)event.getTarget(), triggerSkill, false, false, false);
        }
        catch (Exception e) {
            TriggerSkillByAttack.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
        if (GameUtils.isCreature(target)) {
            final BuffInfo info = ((Creature)target).getEffectList().getBuffInfoBySkillId(triggerSkill.getId());
            if (info == null || info.getSkill().getLevel() < triggerSkill.getLevel()) {
                SkillCaster.triggerCast(event.getAttacker(), (Creature)target, triggerSkill);
            }
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.removeListenerIf(EventType.ON_CREATURE_DAMAGE_DEALT, listener -> listener.getOwner() == this);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)effected, EventType.ON_CREATURE_DAMAGE_DEALT, (Consumer)this::onAttackEvent, (Object)this));
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new TriggerSkillByAttack(data);
        }
        
        public String effectName() {
            return "trigger-skill-by-attack";
        }
    }
}
