// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class RebalanceHP extends AbstractEffect
{
    private RebalanceHP() {
    }
    
    public EffectType getEffectType() {
        return EffectType.REBALANCE_HP;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effector)) {
            return;
        }
        double fullHP = 0.0;
        double currentHPs = 0.0;
        final Party party = effector.getParty();
        if (Objects.nonNull(party)) {
            for (final Player member : party.getMembers()) {
                if (!member.isDead() && GameUtils.checkIfInRange(skill.getAffectRange(), (WorldObject)effector, (WorldObject)member, true)) {
                    fullHP += member.getMaxHp();
                    currentHPs += member.getCurrentHp();
                }
                final Summon summon = (Summon)member.getPet();
                if (summon != null && !summon.isDead() && GameUtils.checkIfInRange(skill.getAffectRange(), (WorldObject)effector, (WorldObject)summon, true)) {
                    fullHP += summon.getMaxHp();
                    currentHPs += summon.getCurrentHp();
                }
                for (final Summon servitors : member.getServitors().values()) {
                    if (!servitors.isDead() && GameUtils.checkIfInRange(skill.getAffectRange(), (WorldObject)effector, (WorldObject)servitors, true)) {
                        fullHP += servitors.getMaxHp();
                        currentHPs += servitors.getCurrentHp();
                    }
                }
            }
            final double percentHP = currentHPs / fullHP;
            for (final Player member2 : party.getMembers()) {
                if (!member2.isDead() && GameUtils.checkIfInRange(skill.getAffectRange(), (WorldObject)effector, (WorldObject)member2, true)) {
                    double newHP = member2.getMaxHp() * percentHP;
                    if (newHP > member2.getCurrentHp()) {
                        if (member2.getCurrentHp() > member2.getMaxRecoverableHp()) {
                            newHP = member2.getCurrentHp();
                        }
                        else if (newHP > member2.getMaxRecoverableHp()) {
                            newHP = member2.getMaxRecoverableHp();
                        }
                    }
                    member2.setCurrentHp(newHP);
                }
                final Summon summon2 = (Summon)member2.getPet();
                if (summon2 != null && !summon2.isDead() && GameUtils.checkIfInRange(skill.getAffectRange(), (WorldObject)effector, (WorldObject)summon2, true)) {
                    double newHP2 = summon2.getMaxHp() * percentHP;
                    if (newHP2 > summon2.getCurrentHp()) {
                        if (summon2.getCurrentHp() > summon2.getMaxRecoverableHp()) {
                            newHP2 = summon2.getCurrentHp();
                        }
                        else if (newHP2 > summon2.getMaxRecoverableHp()) {
                            newHP2 = summon2.getMaxRecoverableHp();
                        }
                    }
                    summon2.setCurrentHp(newHP2);
                }
                for (final Summon servitors2 : member2.getServitors().values()) {
                    if (!servitors2.isDead() && GameUtils.checkIfInRange(skill.getAffectRange(), (WorldObject)effector, (WorldObject)servitors2, true)) {
                        double newHP3 = servitors2.getMaxHp() * percentHP;
                        if (newHP3 > servitors2.getCurrentHp()) {
                            if (servitors2.getCurrentHp() > servitors2.getMaxRecoverableHp()) {
                                newHP3 = servitors2.getCurrentHp();
                            }
                            else if (newHP3 > servitors2.getMaxRecoverableHp()) {
                                newHP3 = servitors2.getMaxRecoverableHp();
                            }
                        }
                        servitors2.setCurrentHp(newHP3);
                    }
                }
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final RebalanceHP INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "RebalanceHP";
        }
        
        static {
            INSTANCE = new RebalanceHP();
        }
    }
}
