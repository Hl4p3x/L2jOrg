// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.function.Function;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.concurrent.Future;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.commons.util.Util;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.Objects;
import org.l2j.gameserver.world.zone.TaskZoneSettings;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.AbstractZoneSettings;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.holders.SkillHolder;
import java.util.List;
import org.slf4j.Logger;
import org.l2j.gameserver.world.zone.Zone;

public final class EffectZone extends Zone
{
    private static final Logger LOGGER;
    private final Object taskLock;
    private boolean bypassConditions;
    private List<SkillHolder> skills;
    private int chance;
    private int initialDelay;
    private int reuse;
    private boolean isShowDangerIcon;
    
    public EffectZone(final int id) {
        super(id);
        this.taskLock = new Object();
        this.chance = 100;
        this.reuse = 30000;
        this.setTargetType(InstanceType.Playable);
        this.isShowDangerIcon = true;
        final AbstractZoneSettings settings = Objects.requireNonNullElseGet(ZoneManager.getSettings(this.getName()), TaskZoneSettings::new);
        this.setSettings(settings);
    }
    
    @Override
    public TaskZoneSettings getSettings() {
        return (TaskZoneSettings)super.getSettings();
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        switch (name) {
            case "chance": {
                this.chance = Integer.parseInt(value);
                break;
            }
            case "initialDelay": {
                this.initialDelay = Integer.parseInt(value);
                break;
            }
            case "reuse": {
                this.reuse = Integer.parseInt(value);
                break;
            }
            case "bypassSkillConditions": {
                this.bypassConditions = Boolean.parseBoolean(value);
                break;
            }
            case "maxDynamicSkillCount": {
                this.skills = new ArrayList<SkillHolder>(Integer.parseInt(value));
                break;
            }
            case "showDangerIcon": {
                this.isShowDangerIcon = Boolean.parseBoolean(value);
                break;
            }
            case "skillIdLvl": {
                this.parseSkills(value);
                break;
            }
            default: {
                super.setParameter(name, value);
                break;
            }
        }
    }
    
    private void parseSkills(final String value) {
        final String[] propertySplit = value.split(";");
        this.skills = Arrays.stream(propertySplit).map(s -> s.split("-")).filter((Predicate<? super Object>)this::validSkillProperty).map(s -> new SkillHolder(Integer.parseInt(s[0]), Integer.parseInt(s[1]))).collect((Collector<? super Object, ?, List<SkillHolder>>)Collectors.toList());
    }
    
    private boolean validSkillProperty(final String[] skillIdLvl) {
        if (skillIdLvl.length != 2 || !Util.isInteger(skillIdLvl[0]) || !Util.isInteger(skillIdLvl[1])) {
            EffectZone.LOGGER.warn("invalid config property -> skillsIdLvl '{}'", (Object)skillIdLvl);
            return false;
        }
        return true;
    }
    
    public int getSkillLevel(final int skillId) {
        if (this.skills == null || skillId > this.skills.size() || this.skills.get(skillId) == null) {
            return 0;
        }
        return this.skills.get(skillId).getLevel();
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        if (Objects.nonNull(this.skills) && Objects.isNull(this.getSettings().getTask())) {
            synchronized (this.taskLock) {
                if (this.getSettings().getTask() == null) {
                    this.getSettings().setTask(ThreadPool.scheduleAtFixedRate((Runnable)new ApplySkill(), (long)this.initialDelay, (long)this.reuse));
                }
            }
        }
        if (GameUtils.isPlayer(creature)) {
            creature.setInsideZone(ZoneType.ALTERED, true);
            if (this.isShowDangerIcon) {
                creature.setInsideZone(ZoneType.DANGER_AREA, true);
                creature.sendPacket(new EtcStatusUpdate(creature.getActingPlayer()));
            }
        }
    }
    
    @Override
    protected void onExit(final Creature creature) {
        if (GameUtils.isPlayer(creature)) {
            creature.setInsideZone(ZoneType.ALTERED, false);
            if (this.isShowDangerIcon) {
                creature.setInsideZone(ZoneType.DANGER_AREA, false);
                if (!creature.isInsideZone(ZoneType.DANGER_AREA)) {
                    creature.sendPacket(new EtcStatusUpdate(creature.getActingPlayer()));
                }
            }
        }
        if (this.creatures.isEmpty() && Objects.nonNull(this.getSettings().getTask())) {
            this.getSettings().clear();
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)EffectZone.class);
    }
    
    private final class ApplySkill implements Runnable
    {
        ApplySkill() {
            if (Objects.isNull(EffectZone.this.skills)) {
                throw new IllegalStateException("No skills defined.");
            }
        }
        
        @Override
        public void run() {
            if (EffectZone.this.isEnabled()) {
                EffectZone.this.skills.stream().map((Function<? super Object, ?>)SkillHolder::getSkill).forEach(s -> EffectZone.this.forEachCreature(c -> s.activateSkill(c, c), c -> this.canApplySkill(c) && this.checkSkillCondition(s, c)));
            }
        }
        
        private boolean checkSkillCondition(final Skill skill, final Creature creature) {
            return Objects.nonNull(skill) && (EffectZone.this.bypassConditions || skill.checkCondition(creature, creature)) && creature.getAffectedSkillLevel(skill.getId()) < skill.getLevel();
        }
        
        private boolean canApplySkill(final Creature creature) {
            return Objects.nonNull(creature) && !creature.isDead() && Rnd.chance(EffectZone.this.chance);
        }
    }
}
