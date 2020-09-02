// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Unsummon extends AbstractEffect
{
    private final int power;
    
    private Unsummon(final StatsSet params) {
        this.power = params.getInt("power", -1);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        if (this.power < 0) {
            return true;
        }
        final int magicLevel = skill.getMagicLevel();
        if (magicLevel <= 0 || effected.getLevel() - 9 <= magicLevel) {
            final double chance = this.power * Formulas.calcAttributeBonus(effector, effected, skill) * Formulas.calcGeneralTraitBonus(effector, effected, skill.getTrait(), false);
            return Rnd.chance(chance);
        }
        return false;
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return GameUtils.isSummon((WorldObject)effected);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isServitor()) {
            final Summon servitor = (Summon)effected;
            final Player summonOwner = servitor.getOwner();
            servitor.abortAttack();
            servitor.abortCast();
            servitor.stopAllEffects();
            servitor.unSummon(summonOwner);
            summonOwner.sendPacket(SystemMessageId.YOUR_SERVITOR_HAS_VANISHED_YOU_LL_NEED_TO_SUMMON_A_NEW_ONE);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Unsummon(data);
        }
        
        public String effectName() {
            return "Unsummon";
        }
    }
}
