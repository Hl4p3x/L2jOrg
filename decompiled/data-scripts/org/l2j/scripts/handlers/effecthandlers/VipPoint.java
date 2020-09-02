// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class VipPoint extends AbstractEffect
{
    private final int power;
    
    private VipPoint(final StatsSet params) {
        this.power = params.getInt("power", 0);
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effector) || !GameUtils.isPlayer((WorldObject)effected) || effected.isAlikeDead()) {
            return;
        }
        effector.getActingPlayer().updateVipPoints((long)this.power);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new VipPoint(data);
        }
        
        public String effectName() {
            return "VipPoint";
        }
    }
}
