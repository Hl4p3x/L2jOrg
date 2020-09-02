// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Lethal extends AbstractEffect
{
    private final double fullLethal;
    private final double halfLethal;
    
    private Lethal(final StatsSet params) {
        this.fullLethal = params.getDouble("power", 0.0);
        this.halfLethal = params.getDouble("half-power", 0.0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public EffectType getEffectType() {
        return EffectType.LETHAL_ATTACK;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayer((WorldObject)effector) && !effector.getAccessLevel().canGiveDamage()) {
            return;
        }
        if (skill.getMagicLevel() < effected.getLevel() - 6) {
            return;
        }
        if (!effected.isLethalable() || effected.isHpBlocked()) {
            return;
        }
        if (GameUtils.isPlayer((WorldObject)effector) && GameUtils.isPlayer((WorldObject)effected) && effected.isAffected(EffectFlag.DUELIST_FURY) && !effector.isAffected(EffectFlag.DUELIST_FURY)) {
            return;
        }
        final double chanceMultiplier = Formulas.calcAttributeBonus(effector, effected, skill) * Formulas.calcGeneralTraitBonus(effector, effected, skill.getTrait(), false);
        if (Rnd.get(100) < effected.getStats().getValue(Stat.INSTANT_KILL_RESIST, 0.0)) {
            effected.sendPacket(new ServerPacket[] { (ServerPacket)((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_EVADED_C2_S_ATTACK).addString(effected.getName())).addString(effector.getName()) });
            effector.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.C1_S_ATTACK_WENT_ASTRAY).addString(effector.getName()) });
        }
        else if (Rnd.get(100) < this.fullLethal * chanceMultiplier) {
            if (GameUtils.isPlayer((WorldObject)effected)) {
                effected.setCurrentCp(1.0);
                effected.setCurrentHp(1.0);
                effected.sendPacket(SystemMessageId.LETHAL_STRIKE);
            }
            else if (GameUtils.isMonster((WorldObject)effected) || GameUtils.isSummon((WorldObject)effected)) {
                effected.setCurrentHp(1.0);
            }
            effector.sendPacket(SystemMessageId.HIT_WITH_LETHAL_STRIKE);
        }
        else if (Rnd.get(100) < this.halfLethal * chanceMultiplier) {
            if (GameUtils.isPlayer((WorldObject)effected)) {
                effected.setCurrentCp(1.0);
                effected.sendPacket(SystemMessageId.HALF_KILL);
                effected.sendPacket(SystemMessageId.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_HALF_KILL_SKILL);
            }
            else if (GameUtils.isMonster((WorldObject)effected) || GameUtils.isSummon((WorldObject)effected)) {
                effected.setCurrentHp(effected.getCurrentHp() * 0.5);
            }
            effector.sendPacket(SystemMessageId.HALF_KILL);
        }
        Formulas.calcCounterAttack(effector, effected, skill, false);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Lethal(data);
        }
        
        public String effectName() {
            return "lethal";
        }
    }
}
