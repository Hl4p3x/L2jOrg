// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.cubic;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.Objects;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.model.WorldObject;
import java.util.Iterator;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.network.serverpackets.ExUserInfoCubic;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.actor.templates.CubicTemplate;
import org.l2j.gameserver.model.actor.instance.Player;

public class CubicInstance
{
    private final Player _owner;
    private final Player _caster;
    private final CubicTemplate _template;
    private ScheduledFuture<?> _skillUseTask;
    private ScheduledFuture<?> _expireTask;
    
    public CubicInstance(final Player owner, final Player caster, final CubicTemplate template) {
        this._owner = owner;
        this._caster = ((caster == null) ? owner : caster);
        this._template = template;
        this.activate();
    }
    
    private void activate() {
        this._skillUseTask = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate(this::readyToUseSkill, 0L, (long)(this._template.getDelay() * 1000));
        this._expireTask = (ScheduledFuture<?>)ThreadPool.schedule(this::deactivate, (long)(this._template.getDuration() * 1000));
    }
    
    public void deactivate() {
        if (this._skillUseTask != null && !this._skillUseTask.isDone()) {
            this._skillUseTask.cancel(true);
        }
        this._skillUseTask = null;
        if (this._expireTask != null && !this._expireTask.isDone()) {
            this._expireTask.cancel(true);
        }
        this._expireTask = null;
        this._owner.getCubics().remove(this._template.getId());
        this._owner.sendPacket(new ExUserInfoCubic(this._owner));
        this._owner.broadcastCharInfo();
    }
    
    private void readyToUseSkill() {
        switch (this._template.getTargetType()) {
            case TARGET: {
                this.actionToCurrentTarget();
                break;
            }
            case BY_SKILL: {
                this.actionToTargetBySkill();
                break;
            }
            case HEAL: {
                this.actionHeal();
                break;
            }
            case MASTER: {
                this.actionToMaster();
                break;
            }
        }
    }
    
    private CubicSkill chooseSkill() {
        final double random = Rnd.nextDouble() * 100.0;
        double commulativeChance = 0.0;
        for (final CubicSkill cubicSkill : this._template.getSkills()) {
            if ((commulativeChance += cubicSkill.getTriggerRate()) > random) {
                return cubicSkill;
            }
        }
        return null;
    }
    
    private void actionToCurrentTarget() {
        final CubicSkill skill = this.chooseSkill();
        final WorldObject target = this._owner.getTarget();
        if (skill != null && target != null) {
            this.tryToUseSkill(target, skill);
        }
    }
    
    private void actionToTargetBySkill() {
        final CubicSkill skill = this.chooseSkill();
        if (skill != null) {
            switch (skill.getTargetType()) {
                case TARGET: {
                    final WorldObject target = this._owner.getTarget();
                    if (target != null) {
                        this.tryToUseSkill(target, skill);
                        break;
                    }
                    break;
                }
                case HEAL: {
                    this.actionHeal();
                    break;
                }
                case MASTER: {
                    this.tryToUseSkill(this._owner, skill);
                    break;
                }
            }
        }
    }
    
    private void actionHeal() {
        final double random = Rnd.nextDouble() * 100.0;
        double commulativeChance = 0.0;
        for (final CubicSkill cubicSkill : this._template.getSkills()) {
            if ((commulativeChance += cubicSkill.getTriggerRate()) > random) {
                final Skill skill = cubicSkill.getSkill();
                if (skill == null || Rnd.get(100) >= cubicSkill.getSuccessRate()) {
                    continue;
                }
                final Party party = this._owner.getParty();
                Stream<Creature> stream;
                if (party != null) {
                    final CubicSkill cubicSkill2;
                    stream = party.getMembers().stream().filter(c -> MathUtil.isInsideRadius3D(this._owner, c, ((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).partyRange()) && this._template.validateConditions(this, this._owner, c) && cubicSkill2.validateConditions(this, this._owner, c)).map(c -> c);
                }
                else {
                    final CubicSkill cubicSkill3;
                    final Stream<Object> filter = this._owner.getServitorsAndPets().stream().filter(summon -> this._template.validateConditions(this, this._owner, summon) && cubicSkill3.validateConditions(this, this._owner, summon));
                    final Class<Creature> obj = Creature.class;
                    Objects.requireNonNull(obj);
                    stream = (Stream<Creature>)filter.map((Function<? super Object, ?>)obj::cast);
                }
                if (this._template.validateConditions(this, this._owner, this._owner) && cubicSkill.validateConditions(this, this._owner, this._owner)) {
                    stream = Stream.concat((Stream<? extends Creature>)stream, (Stream<? extends Creature>)Stream.of((T)this._owner));
                }
                final Creature target = stream.min(Comparator.comparingInt(Creature::getCurrentHpPercent)).orElse(null);
                if (target != null) {
                    this.activateCubicSkill(skill, target);
                    break;
                }
                continue;
            }
        }
    }
    
    private void actionToMaster() {
        final CubicSkill skill = this.chooseSkill();
        if (skill != null) {
            this.tryToUseSkill(this._owner, skill);
        }
    }
    
    private void tryToUseSkill(WorldObject target, final CubicSkill cubicSkill) {
        final Skill skill = cubicSkill.getSkill();
        if (this._template.getTargetType() != CubicTargetType.MASTER && (this._template.getTargetType() != CubicTargetType.BY_SKILL || cubicSkill.getTargetType() != CubicTargetType.MASTER)) {
            target = skill.getTarget(this._owner, target, false, false, false);
        }
        if (target != null) {
            if (GameUtils.isDoor(target) && !cubicSkill.canUseOnStaticObjects()) {
                return;
            }
            if (this._template.validateConditions(this, this._owner, target) && cubicSkill.validateConditions(this, this._owner, target) && Rnd.get(100) < cubicSkill.getSuccessRate()) {
                this.activateCubicSkill(skill, target);
            }
        }
    }
    
    private void activateCubicSkill(final Skill skill, final WorldObject target) {
        if (!this._owner.hasSkillReuse(skill.getReuseHashCode())) {
            this._caster.broadcastPacket(new MagicSkillUse(this._owner, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), skill.getReuseDelay()));
            skill.activateSkill(this, target);
            this._owner.addTimeStamp(skill, skill.getReuseDelay());
        }
    }
    
    public Creature getOwner() {
        return this._owner;
    }
    
    public Creature getCaster() {
        return this._caster;
    }
    
    public boolean isGivenByOther() {
        return this._caster != this._owner;
    }
    
    public CubicTemplate getTemplate() {
        return this._template;
    }
}
