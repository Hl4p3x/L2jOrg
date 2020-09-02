// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.Objects;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class ProtectionBlessing extends AbstractEffect
{
    private ProtectionBlessing() {
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return Objects.nonNull(effector) && GameUtils.isPlayer((WorldObject)effected);
    }
    
    public long getEffectFlags() {
        return EffectFlag.PROTECTION_BLESSING.getMask();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final ProtectionBlessing INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "ProtectionBlessing";
        }
        
        static {
            INSTANCE = new ProtectionBlessing();
        }
    }
}
