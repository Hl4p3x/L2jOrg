// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.options;

import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Iterator;
import java.util.ArrayList;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.effects.AbstractEffect;
import java.util.List;

public class Options
{
    private final int _id;
    private List<AbstractEffect> _effects;
    private List<SkillHolder> _activeSkill;
    private List<SkillHolder> _passiveSkill;
    private List<OptionsSkillHolder> _activationSkills;
    
    public Options(final int id) {
        this._effects = null;
        this._activeSkill = null;
        this._passiveSkill = null;
        this._activationSkills = null;
        this._id = id;
    }
    
    public final int getId() {
        return this._id;
    }
    
    public void addEffect(final AbstractEffect effect) {
        if (this._effects == null) {
            this._effects = new ArrayList<AbstractEffect>();
        }
        this._effects.add(effect);
    }
    
    public List<AbstractEffect> getEffects() {
        return this._effects;
    }
    
    public boolean hasEffects() {
        return this._effects != null;
    }
    
    public boolean hasActiveSkills() {
        return this._activeSkill != null;
    }
    
    public List<SkillHolder> getActiveSkills() {
        return this._activeSkill;
    }
    
    public void addActiveSkill(final SkillHolder holder) {
        if (this._activeSkill == null) {
            this._activeSkill = new ArrayList<SkillHolder>();
        }
        this._activeSkill.add(holder);
    }
    
    public boolean hasPassiveSkills() {
        return this._passiveSkill != null;
    }
    
    public List<SkillHolder> getPassiveSkills() {
        return this._passiveSkill;
    }
    
    public void addPassiveSkill(final SkillHolder holder) {
        if (this._passiveSkill == null) {
            this._passiveSkill = new ArrayList<SkillHolder>();
        }
        this._passiveSkill.add(holder);
    }
    
    public boolean hasActivationSkills() {
        return this._activationSkills != null;
    }
    
    public boolean hasActivationSkills(final OptionsSkillType type) {
        if (this._activationSkills != null) {
            for (final OptionsSkillHolder holder : this._activationSkills) {
                if (holder.getSkillType() == type) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public List<OptionsSkillHolder> getActivationsSkills() {
        return this._activationSkills;
    }
    
    public List<OptionsSkillHolder> getActivationsSkills(final OptionsSkillType type) {
        final List<OptionsSkillHolder> temp = new ArrayList<OptionsSkillHolder>();
        if (this._activationSkills != null) {
            for (final OptionsSkillHolder holder : this._activationSkills) {
                if (holder.getSkillType() == type) {
                    temp.add(holder);
                }
            }
        }
        return temp;
    }
    
    public void addActivationSkill(final OptionsSkillHolder holder) {
        if (this._activationSkills == null) {
            this._activationSkills = new ArrayList<OptionsSkillHolder>();
        }
        this._activationSkills.add(holder);
    }
    
    public void apply(final Player player) {
        if (this.hasEffects()) {
            final BuffInfo info = new BuffInfo(player, player, null, true, null, this);
            for (final AbstractEffect effect : this._effects) {
                if (effect.isInstant()) {
                    if (!effect.calcSuccess(info.getEffector(), info.getEffected(), info.getSkill())) {
                        continue;
                    }
                    effect.instant(info.getEffector(), info.getEffected(), info.getSkill(), info.getItem());
                }
                else {
                    effect.continuousInstant(info.getEffector(), info.getEffected(), info.getSkill(), info.getItem());
                    effect.pump(player, info.getSkill());
                    if (!effect.canStart(info.getEffector(), info.getEffected(), info.getSkill())) {
                        continue;
                    }
                    info.addEffect(effect);
                }
            }
            if (!info.getEffects().isEmpty()) {
                player.getEffectList().add(info);
            }
        }
        if (this.hasActiveSkills()) {
            for (final SkillHolder holder : this._activeSkill) {
                this.addSkill(player, holder.getSkill());
            }
        }
        if (this.hasPassiveSkills()) {
            for (final SkillHolder holder : this._passiveSkill) {
                this.addSkill(player, holder.getSkill());
            }
        }
        if (this.hasActivationSkills()) {
            for (final OptionsSkillHolder holder2 : this._activationSkills) {
                player.addTriggerSkill(holder2);
            }
        }
        player.getStats().recalculateStats(true);
        player.sendSkillList();
    }
    
    public void remove(final Player player) {
        if (this.hasEffects()) {
            for (final BuffInfo info : player.getEffectList().getOptions()) {
                if (info.getOption() == this) {
                    player.getEffectList().remove(info, false, true, true);
                }
            }
        }
        if (this.hasActiveSkills()) {
            for (final SkillHolder holder : this._activeSkill) {
                player.removeSkill(holder.getSkill(), false, false);
            }
        }
        if (this.hasPassiveSkills()) {
            for (final SkillHolder holder : this._passiveSkill) {
                player.removeSkill(holder.getSkill(), false, true);
            }
        }
        if (this.hasActivationSkills()) {
            for (final OptionsSkillHolder holder2 : this._activationSkills) {
                player.removeTriggerSkill(holder2);
            }
        }
        player.getStats().recalculateStats(true);
        player.sendSkillList();
    }
    
    private void addSkill(final Player player, final Skill skill) {
        boolean updateTimeStamp = false;
        player.addSkill(skill, false);
        if (skill.isActive()) {
            final long remainingTime = player.getSkillRemainingReuseTime(skill.getReuseHashCode());
            if (remainingTime > 0L) {
                player.addTimeStamp(skill, remainingTime);
                player.disableSkill(skill, remainingTime);
            }
            updateTimeStamp = true;
        }
        if (updateTimeStamp) {
            player.sendPacket(new SkillCoolTime(player));
        }
    }
}
