// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.stream.IntStream;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.commons.util.StreamUtil;
import java.util.function.ToIntFunction;
import java.util.function.Predicate;
import org.l2j.commons.util.Util;
import java.util.Arrays;
import org.l2j.gameserver.model.StatsSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class BlockActions extends AbstractEffect
{
    private final IntSet allowedSkills;
    
    private BlockActions(final StatsSet params) {
        final String[] allowedSkills = params.getString("allowed-skills", "").split(" ");
        this.allowedSkills = StreamUtil.collectToSet(Arrays.stream(allowedSkills).filter(Util::isInteger).mapToInt(Integer::parseInt));
    }
    
    public long getEffectFlags() {
        return this.allowedSkills.isEmpty() ? EffectFlag.BLOCK_ACTIONS.getMask() : EffectFlag.CONDITIONAL_BLOCK_ACTIONS.getMask();
    }
    
    public EffectType getEffectType() {
        return EffectType.BLOCK_ACTIONS;
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final IntStream stream = this.allowedSkills.stream();
        Objects.requireNonNull(effected);
        stream.forEach(effected::addBlockActionsAllowedSkill);
        effected.startParalyze();
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        final IntSet allowedSkills = this.allowedSkills;
        Objects.requireNonNull(effected);
        allowedSkills.forEach(effected::removeBlockActionsAllowedSkill);
        if (GameUtils.isPlayable((WorldObject)effected)) {
            if (GameUtils.isSummon((WorldObject)effected)) {
                if (Objects.nonNull(effector) && !effector.isDead()) {
                    ((Summon)effected).doAttack((WorldObject)effector);
                }
                else {
                    effected.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { effected.getActingPlayer() });
                }
            }
            else {
                effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            }
        }
        else {
            effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new BlockActions(data);
        }
        
        public String effectName() {
            return "block-all-actions";
        }
    }
}
