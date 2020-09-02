// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.serverpackets.StopRotation;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.StartRotation;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Bluff extends AbstractEffect
{
    private final int power;
    
    private Bluff(final StatsSet params) {
        this.power = params.getInt("power", 100);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return Formulas.calcProbability((double)this.power, effector, effected, skill);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.getId() == 35062 || effected.isRaid() || effected.isRaidMinion()) {
            return;
        }
        effected.broadcastPacket((ServerPacket)new StartRotation(effected.getObjectId(), effected.getHeading(), 1, 65535));
        effected.broadcastPacket((ServerPacket)new StopRotation(effected.getObjectId(), effector.getHeading(), 65535));
        effected.setHeading(effector.getHeading());
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Bluff(data);
        }
        
        public String effectName() {
            return "Bluff";
        }
    }
}
