// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class ImmobilePetBuff extends AbstractEffect
{
    private ImmobilePetBuff() {
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.setIsImmobilized(false);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isSummon((WorldObject)effected) && GameUtils.isPlayer((WorldObject)effector) && ((Summon)effected).getOwner() == effector) {
            effected.setIsImmobilized(true);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final ImmobilePetBuff INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "ImmobilePetBuff";
        }
        
        static {
            INSTANCE = new ImmobilePetBuff();
        }
    }
}
