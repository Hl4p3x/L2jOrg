// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.DamageByAttackType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class DamageByAttack extends AbstractEffect
{
    private final double power;
    private final DamageByAttackType type;
    
    private DamageByAttack(final StatsSet params) {
        this.power = params.getDouble("power");
        this.type = (DamageByAttackType)params.getEnum("type", (Class)DamageByAttackType.class, (Enum)DamageByAttackType.NONE);
    }
    
    public void pump(final Creature target, final Skill skill) {
        switch (this.type) {
            case PK: {
                target.getStats().mergeAdd(Stat.PVP_DAMAGE_TAKEN, this.power);
                break;
            }
            case MOB: {
                target.getStats().mergeAdd(Stat.PVE_DAMAGE_TAKEN_MONSTER, this.power);
                break;
            }
            case BOSS: {
                target.getStats().mergeAdd(Stat.PVE_DAMAGE_TAKEN_RAID, this.power);
                break;
            }
            case MONSTER: {
                target.getStats().mergeAdd(Stat.PVE_DAMAGE_TAKEN, this.power);
                break;
            }
            case ANY: {
                target.getStats().mergeAdd(Stat.DAMAGE_TAKEN, this.power);
                break;
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new DamageByAttack(data);
        }
        
        public String effectName() {
            return "damage-by-attack";
        }
    }
}
