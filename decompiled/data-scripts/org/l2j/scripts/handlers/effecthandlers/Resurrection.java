// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Resurrection extends AbstractEffect
{
    private final int power;
    
    private Resurrection(final StatsSet params) {
        this.power = params.getInt("power", 0);
    }
    
    public EffectType getEffectType() {
        return EffectType.RESURRECTION;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayer((WorldObject)effector)) {
            final Player player = effected.getActingPlayer();
            if (!player.isResurrectionBlocked() && !player.isReviveRequested()) {
                effected.getActingPlayer().reviveRequest(effector.getActingPlayer(), skill, GameUtils.isPet((WorldObject)effected), this.power);
            }
        }
        else {
            DecayTaskManager.getInstance().cancel(effected);
            effected.doRevive(Formulas.calculateSkillResurrectRestorePercent((double)this.power, effector));
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Resurrection(data);
        }
        
        public String effectName() {
            return "Resurrection";
        }
    }
}
