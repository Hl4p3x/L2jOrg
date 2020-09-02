// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class EnlargeAbnormalSlot extends AbstractEffect
{
    private final int slots;
    
    private EnlargeAbnormalSlot(final StatsSet params) {
        this.slots = params.getInt("power", 0);
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return effector != null && GameUtils.isPlayer((WorldObject)effected);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.getStats().setMaxBuffCount(effected.getStats().getMaxBuffCount() + this.slots);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.getStats().setMaxBuffCount(Math.max(0, effected.getStats().getMaxBuffCount() - this.slots));
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new EnlargeAbnormalSlot(data);
        }
        
        public String effectName() {
            return "EnlargeAbnormalSlot";
        }
    }
}
