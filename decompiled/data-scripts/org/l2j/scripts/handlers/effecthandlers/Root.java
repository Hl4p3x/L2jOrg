// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Location;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Root extends AbstractEffect
{
    private Root() {
    }
    
    public long getEffectFlags() {
        return EffectFlag.ROOTED.getMask();
    }
    
    public EffectType getEffectType() {
        return EffectType.ROOT;
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (!GameUtils.isPlayer((WorldObject)effected)) {
            effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (Objects.isNull(effected) || effected.isRaid()) {
            return;
        }
        effected.stopMove((Location)null);
        effected.getAI().notifyEvent(CtrlEvent.EVT_ROOTED);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final Root INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Root";
        }
        
        static {
            INSTANCE = new Root();
        }
    }
}
