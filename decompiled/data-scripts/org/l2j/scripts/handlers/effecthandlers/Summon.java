// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Summon extends AbstractEffect
{
    private final int npcId;
    private final float expMultiplier;
    private final ItemHolder consumeItem;
    private final int lifeTime;
    
    private Summon(final StatsSet params) {
        if (params.isEmpty()) {
            throw new IllegalArgumentException("Summon effect without parameters!");
        }
        this.npcId = params.getInt("npc");
        this.expMultiplier = params.getFloat("experience-multiplier", 1.0f);
        this.consumeItem = new ItemHolder(params.getInt("consume-item", 0), (long)params.getInt("consume-count", 1));
        this.lifeTime = ((params.getInt("life-time", 0) > 0) ? (params.getInt("life-time") * 1000) : -1);
    }
    
    public EffectType getEffectType() {
        return EffectType.SUMMON;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effected)) {
            return;
        }
        final Player player = effected.getActingPlayer();
        if (player.hasServitors()) {
            player.getServitors().values().forEach(s -> s.unSummon(player));
        }
        final NpcTemplate template = NpcData.getInstance().getTemplate(this.npcId);
        final Servitor summon = new Servitor(template, player);
        final int consumeItemInterval = ((template.getRace() != Race.SIEGE_WEAPON) ? 240 : 60) * 1000;
        summon.setName(template.getName());
        summon.setTitle(effected.getName());
        summon.setReferenceSkill(skill.getId());
        summon.setExpMultiplier(this.expMultiplier);
        summon.setLifeTime((this.lifeTime <= 0) ? Integer.MAX_VALUE : this.lifeTime);
        summon.setItemConsume(this.consumeItem);
        summon.setItemConsumeInterval(consumeItemInterval);
        final byte maxLevel = LevelData.getInstance().getMaxLevel();
        if (summon.getLevel() >= maxLevel) {
            summon.getStats().setExp(LevelData.getInstance().getExpForLevel((int)maxLevel));
            Summon.LOGGER.warn("({}) NpcID: {} has a level above {}. Please rectify.", new Object[] { summon.getName(), summon.getId(), maxLevel });
        }
        else {
            summon.getStats().setExp(LevelData.getInstance().getExpForLevel(summon.getLevel() % (LevelData.getInstance().getMaxLevel() + 1)));
        }
        for (final BuffInfo effect : player.getEffectList().getEffects()) {
            final Skill sk = effect.getSkill();
            if (!sk.isBad()) {
                sk.applyEffects((Creature)player, (Creature)summon, false, effect.getTime());
            }
        }
        summon.setCurrentHp((double)summon.getMaxHp(), false);
        summon.setCurrentMp((double)summon.getMaxMp(), false);
        summon.setHeading(player.getHeading());
        player.addServitor((org.l2j.gameserver.model.actor.Summon)summon);
        summon.setShowSummonAnimation(true);
        summon.spawnMe();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Summon(data);
        }
        
        public String effectName() {
            return "summon";
        }
    }
}
