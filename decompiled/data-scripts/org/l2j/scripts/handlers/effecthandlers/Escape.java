// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Escape extends AbstractEffect
{
    private final TeleportWhereType location;
    
    private Escape(final StatsSet params) {
        this.location = (TeleportWhereType)params.getEnum("location", (Class)TeleportWhereType.class, (Enum)null);
    }
    
    public EffectType getEffectType() {
        return EffectType.TELEPORT;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return super.canStart(effector, effected, skill) && !effected.cannotEscape();
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (Objects.nonNull(this.location)) {
            effected.teleToLocation(this.location, (Instance)null);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Escape(data);
        }
        
        public String effectName() {
            return "escape";
        }
    }
}
