// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone;

import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.instancemanager.InstanceManager;
import java.util.function.BiConsumer;
import org.l2j.gameserver.model.actor.Npc;
import java.util.stream.Stream;
import org.l2j.gameserver.model.AbstractPlayerGroup;
import org.l2j.gameserver.model.PcCondOverride;
import java.util.Calendar;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Summon;
import java.util.function.Consumer;
import java.util.Objects;
import org.l2j.gameserver.model.actor.Playable;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.util.Rnd;
import java.util.Iterator;
import org.l2j.gameserver.model.instancezone.conditions.ConditionGroupMax;
import org.l2j.gameserver.model.instancezone.conditions.ConditionGroupMin;
import org.l2j.gameserver.model.instancezone.conditions.ConditionCommandChannel;
import java.util.Collection;
import org.l2j.gameserver.enums.GroupType;
import java.util.Collections;
import org.l2j.gameserver.Config;
import java.util.ArrayList;
import java.util.HashMap;
import org.l2j.gameserver.model.instancezone.conditions.Condition;
import org.l2j.gameserver.enums.InstanceRemoveBuffType;
import org.l2j.gameserver.model.holders.InstanceReenterTimeHolder;
import org.l2j.gameserver.enums.InstanceReenterType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.enums.InstanceTeleportType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import java.util.List;
import org.l2j.gameserver.model.actor.templates.DoorTemplate;
import java.util.Map;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.events.ListenersContainer;

public class InstanceTemplate extends ListenersContainer implements IIdentifiable, INamable
{
    private final Map<Integer, DoorTemplate> _doors;
    private final List<SpawnTemplate> _spawns;
    private int _templateId;
    private String _name;
    private int _duration;
    private long _emptyDestroyTime;
    private int _ejectTime;
    private int _maxWorldCount;
    private boolean _isPvP;
    private boolean _allowPlayerSummon;
    private float _expRate;
    private float _spRate;
    private float _expPartyRate;
    private float _spPartyRate;
    private StatsSet _parameters;
    private InstanceTeleportType _enterLocationType;
    private List<Location> _enterLocations;
    private InstanceTeleportType _exitLocationType;
    private List<Location> _exitLocations;
    private InstanceReenterType _reenterType;
    private List<InstanceReenterTimeHolder> _reenterData;
    private InstanceRemoveBuffType _removeBuffType;
    private List<Integer> _removeBuffExceptions;
    private List<Condition> _conditions;
    private int _groupMask;
    
    public InstanceTemplate(final StatsSet set) {
        this._doors = new HashMap<Integer, DoorTemplate>();
        this._spawns = new ArrayList<SpawnTemplate>();
        this._templateId = -1;
        this._name = "UnknownInstance";
        this._duration = -1;
        this._emptyDestroyTime = -1L;
        this._ejectTime = Config.EJECT_DEAD_PLAYER_TIME;
        this._maxWorldCount = -1;
        this._isPvP = false;
        this._allowPlayerSummon = false;
        this._expRate = Config.RATE_INSTANCE_XP;
        this._spRate = Config.RATE_INSTANCE_SP;
        this._expPartyRate = Config.RATE_INSTANCE_PARTY_XP;
        this._spPartyRate = Config.RATE_INSTANCE_PARTY_SP;
        this._parameters = StatsSet.EMPTY_STATSET;
        this._enterLocationType = InstanceTeleportType.NONE;
        this._enterLocations = null;
        this._exitLocationType = InstanceTeleportType.NONE;
        this._exitLocations = null;
        this._reenterType = InstanceReenterType.NONE;
        this._reenterData = Collections.emptyList();
        this._removeBuffType = InstanceRemoveBuffType.NONE;
        this._removeBuffExceptions = Collections.emptyList();
        this._conditions = Collections.emptyList();
        this._groupMask = GroupType.NONE.getMask();
        this._templateId = set.getInt("id", 0);
        this._name = set.getString("name", null);
        this._maxWorldCount = set.getInt("maxWorlds", -1);
    }
    
    public void allowPlayerSummon(final boolean val) {
        this._allowPlayerSummon = val;
    }
    
    public void setIsPvP(final boolean val) {
        this._isPvP = val;
    }
    
    public void addDoor(final int templateId, final DoorTemplate template) {
        this._doors.put(templateId, template);
    }
    
    public void addSpawns(final List<SpawnTemplate> spawns) {
        this._spawns.addAll(spawns);
    }
    
    public void setEnterLocation(final InstanceTeleportType type, final List<Location> locations) {
        this._enterLocationType = type;
        this._enterLocations = locations;
    }
    
    public void setExitLocation(final InstanceTeleportType type, final List<Location> locations) {
        this._exitLocationType = type;
        this._exitLocations = locations;
    }
    
    public void setReenterData(final InstanceReenterType type, final List<InstanceReenterTimeHolder> holder) {
        this._reenterType = type;
        this._reenterData = holder;
    }
    
    public void setRemoveBuff(final InstanceRemoveBuffType type, final List<Integer> exceptionList) {
        this._removeBuffType = type;
        this._removeBuffExceptions = exceptionList;
    }
    
    public void setConditions(final List<Condition> conditions) {
        this._conditions = conditions;
        boolean onlyCC = false;
        int min = 1;
        int max = 1;
        for (final Condition cond : this._conditions) {
            if (cond instanceof ConditionCommandChannel) {
                onlyCC = true;
            }
            else if (cond instanceof ConditionGroupMin) {
                min = ((ConditionGroupMin)cond).getLimit();
            }
            else {
                if (!(cond instanceof ConditionGroupMax)) {
                    continue;
                }
                max = ((ConditionGroupMax)cond).getLimit();
            }
        }
        this._groupMask = 0;
        if (!onlyCC) {
            if (min == 1) {
                this._groupMask |= GroupType.NONE.getMask();
            }
            final int partySize = Config.ALT_PARTY_MAX_MEMBERS;
            if ((max > 1 && max <= partySize) || (min <= partySize && max > partySize)) {
                this._groupMask |= GroupType.PARTY.getMask();
            }
        }
        if (onlyCC || max > 7) {
            this._groupMask |= GroupType.COMMAND_CHANNEL.getMask();
        }
    }
    
    @Override
    public int getId() {
        return this._templateId;
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    public void setName(final String name) {
        if (name != null && !name.isEmpty()) {
            this._name = name;
        }
    }
    
    public List<Location> getEnterLocations() {
        return this._enterLocations;
    }
    
    public Location getEnterLocation() {
        Location loc = null;
        switch (this._enterLocationType) {
            case RANDOM: {
                loc = this._enterLocations.get(Rnd.get(this._enterLocations.size()));
                break;
            }
            case FIXED: {
                loc = this._enterLocations.get(0);
                break;
            }
        }
        return loc;
    }
    
    public InstanceTeleportType getExitLocationType() {
        return this._exitLocationType;
    }
    
    public Location getExitLocation(final Player player) {
        Location location = null;
        switch (this._exitLocationType) {
            case RANDOM: {
                location = this._exitLocations.get(Rnd.get(this._exitLocations.size()));
                break;
            }
            case FIXED: {
                location = this._exitLocations.get(0);
                break;
            }
            case ORIGIN: {
                final int[] loc = player.getInstanceOrigin();
                if (loc.length == 3) {
                    location = new Location(loc[0], loc[1], loc[2]);
                    player.setInstanceOrigin("");
                    break;
                }
                break;
            }
        }
        return location;
    }
    
    public long getEmptyDestroyTime() {
        return this._emptyDestroyTime;
    }
    
    public void setEmptyDestroyTime(final long emptyDestroyTime) {
        if (emptyDestroyTime >= 0L) {
            this._emptyDestroyTime = TimeUnit.MINUTES.toMillis(emptyDestroyTime);
        }
    }
    
    public int getDuration() {
        return this._duration;
    }
    
    public void setDuration(final int duration) {
        if (duration > 0) {
            this._duration = duration;
        }
    }
    
    public int getEjectTime() {
        return this._ejectTime;
    }
    
    public void setEjectTime(final int ejectTime) {
        if (ejectTime >= 0) {
            this._ejectTime = ejectTime;
        }
    }
    
    public boolean isPlayerSummonAllowed() {
        return this._allowPlayerSummon;
    }
    
    public boolean isPvP() {
        return this._isPvP;
    }
    
    public Map<Integer, DoorTemplate> getDoors() {
        return this._doors;
    }
    
    public List<SpawnTemplate> getSpawns() {
        return this._spawns;
    }
    
    public int getMaxWorlds() {
        return this._maxWorldCount;
    }
    
    public StatsSet getParameters() {
        return this._parameters;
    }
    
    public void setParameters(final Map<String, Object> set) {
        if (!set.isEmpty()) {
            this._parameters = new StatsSet(Collections.unmodifiableMap((Map<? extends String, ?>)set));
        }
    }
    
    public boolean isRemoveBuffEnabled() {
        return this._removeBuffType != InstanceRemoveBuffType.NONE;
    }
    
    public void removePlayerBuff(final Player player) {
        final List<Playable> affected = new ArrayList<Playable>();
        affected.add(player);
        final Collection<Summon> values = player.getServitors().values();
        final List<Playable> obj = affected;
        Objects.requireNonNull((ArrayList)obj);
        values.forEach(obj::add);
        if (player.hasPet()) {
            affected.add(player.getPet());
        }
        if (this._removeBuffType == InstanceRemoveBuffType.ALL) {
            for (final Playable playable : affected) {
                playable.stopAllEffectsExceptThoseThatLastThroughDeath();
            }
        }
        else {
            for (final Playable playable : affected) {
                playable.getEffectList().stopEffects(info -> !info.getSkill().isIrreplacableBuff() && info.getSkill().getBuffType().isBuff() && this.hasRemoveBuffException(info.getSkill()), true, true);
            }
        }
    }
    
    private boolean hasRemoveBuffException(final Skill skill) {
        final boolean containsSkill = this._removeBuffExceptions.contains(skill.getId());
        return (this._removeBuffType == InstanceRemoveBuffType.BLACKLIST) ? containsSkill : (!containsSkill);
    }
    
    public InstanceReenterType getReenterType() {
        return this._reenterType;
    }
    
    public long calculateReenterTime() {
        long time = -1L;
        for (final InstanceReenterTimeHolder data : this._reenterData) {
            if (data.getTime() > 0L) {
                time = System.currentTimeMillis() + data.getTime();
                break;
            }
            final Calendar calendar = Calendar.getInstance();
            calendar.set(10, data.getHour());
            calendar.set(12, data.getMinute());
            calendar.set(13, 0);
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(5, 1);
            }
            if (data.getDay() != null) {
                int day = data.getDay().getValue() + 1;
                if (day > 7) {
                    day = 1;
                }
                calendar.set(7, day);
                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    calendar.add(4, 1);
                }
            }
            if (time != -1L && calendar.getTimeInMillis() >= time) {
                continue;
            }
            time = calendar.getTimeInMillis();
        }
        return time;
    }
    
    private final boolean groupMaskContains(final GroupType type) {
        final int flag = type.getMask();
        return (this._groupMask & flag) == flag;
    }
    
    private final GroupType getEnterGroupType(final Player player) {
        if (this._groupMask == 0) {
            return null;
        }
        if (player.canOverrideCond(PcCondOverride.INSTANCE_CONDITIONS)) {
            return GroupType.NONE;
        }
        final GroupType playerGroup = player.getGroupType();
        if (this.groupMaskContains(playerGroup)) {
            return playerGroup;
        }
        final GroupType type = GroupType.getByMask(this._groupMask);
        if (type != null) {
            return type;
        }
        for (final GroupType t : GroupType.values()) {
            if (t != playerGroup && this.groupMaskContains(t)) {
                return t;
            }
        }
        return null;
    }
    
    public List<Player> getEnterGroup(final Player player) {
        final GroupType type = this.getEnterGroupType(player);
        if (type == null) {
            return null;
        }
        final List<Player> group = new ArrayList<Player>();
        group.add(player);
        AbstractPlayerGroup pGroup = null;
        if (type == GroupType.PARTY) {
            pGroup = player.getParty();
        }
        else if (type == GroupType.COMMAND_CHANNEL) {
            pGroup = player.getCommandChannel();
        }
        if (pGroup != null) {
            final Stream<Object> filter = pGroup.getMembers().stream().filter(p -> !p.equals(player));
            final List<Player> obj = group;
            Objects.requireNonNull((ArrayList)obj);
            filter.forEach(obj::add);
        }
        return group;
    }
    
    public boolean validateConditions(final List<Player> group, final Npc npc, final BiConsumer<Player, String> htmlCallback) {
        for (final Condition cond : this._conditions) {
            if (!cond.validate(npc, group, htmlCallback)) {
                return false;
            }
        }
        return true;
    }
    
    public void applyConditionEffects(final List<Player> group) {
        this._conditions.forEach(c -> c.applyEffect(group));
    }
    
    public float getExpRate() {
        return this._expRate;
    }
    
    public void setExpRate(final float expRate) {
        this._expRate = expRate;
    }
    
    public float getSPRate() {
        return this._spRate;
    }
    
    public void setSPRate(final float spRate) {
        this._spRate = spRate;
    }
    
    public float getExpPartyRate() {
        return this._expPartyRate;
    }
    
    public void setExpPartyRate(final float expRate) {
        this._expPartyRate = expRate;
    }
    
    public float getSPPartyRate() {
        return this._spPartyRate;
    }
    
    public void setSPPartyRate(final float spRate) {
        this._spPartyRate = spRate;
    }
    
    public long getWorldCount() {
        return InstanceManager.getInstance().getWorldCount(this.getId());
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, this._templateId, this._name);
    }
}
