// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.HashIntSet;
import org.l2j.gameserver.model.StatsSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class ResurrectionSpecial extends AbstractEffect
{
    private final int power;
    private final IntSet instanceIds;
    
    private ResurrectionSpecial(final StatsSet params) {
        this.power = params.getInt("power", 0);
        final String instanceIds = params.getString("instanceId", (String)null);
        if (instanceIds != null && !instanceIds.isEmpty()) {
            this.instanceIds = (IntSet)new HashIntSet();
            for (final String id : instanceIds.split(";")) {
                this.instanceIds.add(Integer.parseInt(id));
            }
        }
        else {
            this.instanceIds = Containers.emptyIntSet();
        }
    }
    
    public EffectType getEffectType() {
        return EffectType.RESURRECTION_SPECIAL;
    }
    
    public long getEffectFlags() {
        return EffectFlag.RESURRECTION_SPECIAL.getMask();
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (!GameUtils.isPlayer((WorldObject)effected) && !GameUtils.isPet((WorldObject)effected)) {
            return;
        }
        final Player caster = effector.getActingPlayer();
        final Instance instance = caster.getInstanceWorld();
        if (!this.instanceIds.isEmpty() && (instance == null || !this.instanceIds.contains(instance.getTemplateId()))) {
            return;
        }
        if (GameUtils.isPlayer((WorldObject)effected)) {
            effected.getActingPlayer().reviveRequest(caster, skill, false, this.power);
        }
        else if (GameUtils.isPet((WorldObject)effected)) {
            final Pet pet = (Pet)effected;
            effected.getActingPlayer().reviveRequest(pet.getActingPlayer(), skill, true, this.power);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ResurrectionSpecial(data);
        }
        
        public String effectName() {
            return "ResurrectionSpecial";
        }
    }
}
