// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.skills;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.MagicSkillLaunched;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.engine.geo.GeoEngine;
import java.util.ArrayList;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.List;
import org.l2j.gameserver.model.actor.Creature;
import org.slf4j.Logger;

public class SkillChannelizer implements Runnable
{
    private static final Logger LOGGER;
    private final Creature _channelizer;
    private List<Creature> _channelized;
    private Skill _skill;
    private volatile ScheduledFuture<?> _task;
    
    public SkillChannelizer(final Creature channelizer) {
        this._task = null;
        this._channelizer = channelizer;
    }
    
    public boolean hasChannelized() {
        return this._channelized != null;
    }
    
    public void startChanneling(final Skill skill) {
        if (this.isChanneling()) {
            SkillChannelizer.LOGGER.warn("Character: {} is attempting to channel skill but he already does!", (Object)this._channelizer);
            return;
        }
        this._skill = skill;
        this._task = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate((Runnable)this, skill.getChannelingTickInitialDelay(), skill.getChannelingTickInterval());
    }
    
    public void stopChanneling() {
        if (!this.isChanneling()) {
            SkillChannelizer.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()));
            return;
        }
        this._task.cancel(false);
        this._task = null;
        if (this._channelized != null) {
            for (final Creature chars : this._channelized) {
                chars.getSkillChannelized().removeChannelizer(this._skill.getChannelingSkillId(), this._channelizer);
            }
            this._channelized = null;
        }
        this._skill = null;
    }
    
    public Skill getSkill() {
        return this._skill;
    }
    
    public boolean isChanneling() {
        return this._task != null;
    }
    
    @Override
    public void run() {
        if (!this.isChanneling()) {
            return;
        }
        final Skill skill = this._skill;
        List<Creature> channelized = this._channelized;
        try {
            if (skill.getMpPerChanneling() > 0) {
                if (this._channelizer.getCurrentMp() < skill.getMpPerChanneling()) {
                    if (GameUtils.isPlayer(this._channelizer)) {
                        this._channelizer.sendPacket(SystemMessageId.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
                    }
                    this._channelizer.abortCast();
                    return;
                }
                this._channelizer.reduceCurrentMp(skill.getMpPerChanneling());
            }
            final List<Creature> targetList = new ArrayList<Creature>();
            final WorldObject target = skill.getTarget(this._channelizer, false, false, false);
            if (target != null) {
                final List<Creature> list;
                final Skill skill2;
                skill.forEachTargetAffected(this._channelizer, target, o -> {
                    if (GameUtils.isCreature(o)) {
                        list.add(o);
                        o.getSkillChannelized().addChannelizer(skill2.getChannelingSkillId(), this._channelizer);
                    }
                    return;
                });
            }
            if (targetList.isEmpty()) {
                return;
            }
            channelized = targetList;
            for (final Creature character : channelized) {
                if (!GameUtils.checkIfInRange(skill.getEffectRange(), this._channelizer, character, true)) {
                    continue;
                }
                if (!GeoEngine.getInstance().canSeeTarget(this._channelizer, character)) {
                    continue;
                }
                if (skill.getChannelingSkillId() > 0) {
                    final int maxSkillLevel = SkillEngine.getInstance().getMaxLevel(skill.getChannelingSkillId());
                    final int skillLevel = Math.min(character.getSkillChannelized().getChannerlizersSize(skill.getChannelingSkillId()), maxSkillLevel);
                    if (skillLevel == 0) {
                        continue;
                    }
                    final BuffInfo info = character.getEffectList().getBuffInfoBySkillId(skill.getChannelingSkillId());
                    if (info == null || info.getSkill().getLevel() < skillLevel) {
                        final Skill channeledSkill = SkillEngine.getInstance().getSkill(skill.getChannelingSkillId(), skillLevel);
                        if (channeledSkill == null) {
                            SkillChannelizer.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/engine/skill/api/Skill;)Ljava/lang/String;, skill));
                            this._channelizer.abortCast();
                            return;
                        }
                        if (GameUtils.isPlayer(this._channelizer)) {
                            ((Player)this._channelizer).updatePvPStatus(character);
                        }
                        channeledSkill.applyEffects(this._channelizer, character);
                    }
                    if (skill.isToggle()) {
                        continue;
                    }
                    this._channelizer.broadcastPacket(new MagicSkillLaunched(this._channelizer, skill.getId(), skill.getLevel(), SkillCastingType.NORMAL, new WorldObject[] { character }));
                }
                else {
                    skill.applyChannelingEffects(this._channelizer, character);
                }
            }
            if (skill.useSoulShot()) {
                this._channelizer.consumeAndRechargeShots(ShotType.SOULSHOTS, targetList.size());
            }
            if (skill.useSpiritShot()) {
                this._channelizer.consumeAndRechargeShots(ShotType.SPIRITSHOTS, targetList.size());
            }
        }
        catch (Exception e) {
            SkillChannelizer.LOGGER.warn("Error while channelizing skill: {} channelizer: {} channelized: {}", new Object[] { skill, this._channelizer, channelized, e });
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SkillChannelizer.class);
    }
}
