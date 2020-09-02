// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillUse;
import java.util.stream.Stream;
import org.l2j.commons.util.StreamUtil;
import java.util.function.Function;
import java.util.Arrays;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillType;
import java.util.EnumSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class BlockSkill extends AbstractEffect
{
    private final EnumSet<SkillType> magicTypes;
    
    private BlockSkill(final StatsSet params) {
        this.magicTypes = (EnumSet<SkillType>)StreamUtil.collectToEnumSet((Class)SkillType.class, (Stream)Arrays.stream(params.getString("magic-types").split(" ")).map((Function<? super String, ?>)SkillType::valueOf));
    }
    
    private TerminateReturn onSkillUseEvent(final OnCreatureSkillUse event) {
        if (this.magicTypes.contains(event.getSkill().getSkillType())) {
            return new TerminateReturn(true, true, true);
        }
        return null;
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.addListener((AbstractEventListener)new FunctionEventListener((ListenersContainer)effected, EventType.ON_CREATURE_SKILL_USE, (Function)this::onSkillUseEvent, (Object)this));
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.removeListenerIf(EventType.ON_CREATURE_SKILL_USE, listener -> listener.getOwner() == this);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new BlockSkill(data);
        }
        
        public String effectName() {
            return "block-skill";
        }
    }
}
