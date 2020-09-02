// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import java.util.Iterator;
import java.util.Set;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.xml.impl.ArmorSetsData;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.function.Predicate;
import org.l2j.gameserver.model.ArmorSet;
import java.util.HashSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class BaseStatsFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = creature.getTemplate().getBaseValue(stat, 0.0);
        final BaseStats baseStat = BaseStats.valueOf(stat);
        if (GameUtils.isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            final Set<ArmorSet> appliedSets = new HashSet<ArmorSet>(2);
            for (final Item item : player.getInventory().getPaperdollItems((Predicate<Item>[])new Predicate[0])) {
                for (final ArmorSet set : ArmorSetsData.getInstance().getSets(item.getId())) {
                    if (set.getPiecesCount(player, Item::getId) >= set.getMinimumPieces() && appliedSets.add(set)) {
                        baseValue += set.getStatsBonus(baseStat);
                    }
                }
            }
            baseValue += player.getHennaValue(baseStat);
            baseValue += player.getStatsData().getValue(baseStat);
        }
        final double value = this.validateValue(creature, Stat.defaultValue(creature, stat, baseValue), 1.0, 200.0);
        if (GameUtils.isPlayer(creature)) {
            this.checkEnhancementStatBonus(creature, baseStat, value);
        }
        return value;
    }
    
    private void checkEnhancementStatBonus(final Creature creature, final BaseStats baseStat, final double value) {
        final int skillId = baseStat.getEnhancementSkillId();
        final int skillLevel = baseStat.getEnhancementSkillLevel(value);
        if (skillLevel > 0) {
            final int existentLevel = creature.getSkillLevel(skillId);
            if (existentLevel != skillLevel) {
                final Skill skill = SkillEngine.getInstance().getSkill(skillId, skillLevel);
                creature.addSkill(skill);
            }
        }
        else {
            creature.removeSkill(skillId);
        }
    }
}
