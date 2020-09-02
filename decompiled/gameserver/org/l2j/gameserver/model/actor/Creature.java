// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.model.stats.MoveType;
import org.l2j.gameserver.enums.Race;
import java.util.concurrent.LinkedBlockingDeque;
import org.l2j.commons.util.EmptyQueue;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.Queue;
import org.l2j.gameserver.model.events.EventType;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttackAvoid;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageDealt;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import org.l2j.gameserver.model.events.returns.DamageReturn;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.options.OptionsSkillType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttacked;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttack;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.actor.tasks.character.NotifyAITask;
import org.l2j.gameserver.model.actor.instance.FriendlyNpc;
import org.l2j.gameserver.network.serverpackets.StopMove;
import org.l2j.gameserver.network.serverpackets.MoveToLocation;
import org.l2j.gameserver.engine.geo.SyncMode;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.engine.geo.settings.GeoEngineSettings;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import java.util.Collection;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.serverpackets.ChangeWaitType;
import org.l2j.gameserver.model.actor.instance.Trap;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import org.l2j.gameserver.network.serverpackets.ServerObjectInfo;
import org.l2j.gameserver.network.serverpackets.ChangeMoveType;
import java.util.Collections;
import org.l2j.gameserver.ai.AttackableAI;
import org.l2j.gameserver.network.serverpackets.Revive;
import org.l2j.gameserver.instancemanager.TimersManager;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.events.impl.character.OnCreatureKilled;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.Hit;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.serverpackets.Attack;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.util.MathUtil;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.network.serverpackets.ExTeleportToLocationActivate;
import org.l2j.gameserver.network.serverpackets.TeleportToLocation;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.world.WorldRegion;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.model.events.impl.character.OnCreatureTeleport;
import org.l2j.gameserver.model.events.returns.LocationReturn;
import org.l2j.gameserver.network.serverpackets.StatusUpdate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.OnCreatureTeleported;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneRegion;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.AccessLevel;
import java.util.function.Function;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.TransformData;
import java.util.function.Predicate;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.item.container.Inventory;
import java.util.Iterator;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.enums.InstanceType;
import java.util.EnumMap;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.world.zone.ZoneType;
import java.util.concurrent.ConcurrentSkipListMap;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.enums.ShotType;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.stats.BasicPropertyResist;
import org.l2j.gameserver.enums.BasicProperty;
import org.l2j.gameserver.model.CreatureContainer;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.model.actor.transform.Transform;
import java.util.Optional;
import org.l2j.gameserver.model.skills.BuffFinishTask;
import org.l2j.gameserver.model.skills.SkillChannelized;
import org.l2j.gameserver.model.skills.SkillChannelizer;
import org.l2j.gameserver.model.holders.IgnoreSkillHolder;
import org.l2j.gameserver.model.options.OptionsSkillHolder;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.model.TimeStamp;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.actor.status.CreatureStatus;
import org.l2j.gameserver.model.actor.stat.CreatureStats;
import java.lang.ref.WeakReference;
import java.util.Set;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.enums.StatusUpdateType;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.model.EffectList;
import java.util.concurrent.locks.StampedLock;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.IDeletable;
import org.l2j.gameserver.model.interfaces.ISkillsHolder;
import org.l2j.gameserver.model.WorldObject;

public abstract class Creature extends WorldObject implements ISkillsHolder, IDeletable
{
    public static final Logger LOGGER;
    public static final double MAX_STATUS_BAR_PX = 352.0;
    private final Map<Integer, Skill> _skills;
    private final byte[] _zones;
    private final StampedLock _attackLock;
    private final EffectList _effectList;
    private final AtomicInteger abnormalShieldBlocks;
    private final Map<Integer, Integer> _knownRelations;
    private final Map<StatusUpdateType, Integer> _statusUpdates;
    protected boolean _showSummonAnimation;
    protected boolean _isTeleporting;
    protected byte _zoneValidateCounter;
    protected long _exceptions;
    protected MoveData _move;
    protected Map<SkillCastingType, SkillCaster> _skillCasters;
    private volatile Set<WeakReference<Creature>> _attackByList;
    private boolean _isDead;
    private boolean _isImmobilized;
    private boolean _isOverloaded;
    private boolean _isPendingRevive;
    protected boolean running;
    private boolean _isInvul;
    private boolean _isUndying;
    private boolean _isFlying;
    private boolean _blockActions;
    private volatile Map<Integer, AtomicInteger> _blockActionsAllowedSkills;
    private CreatureStats stats;
    private CreatureStatus _status;
    private CreatureTemplate _template;
    private String _title;
    private double _hpUpdateIncCheck;
    private double _hpUpdateDecCheck;
    private double _hpUpdateInterval;
    private int _reputation;
    private final Map<Long, TimeStamp> _reuseTimeStampsSkills;
    private final Map<Integer, TimeStamp> _reuseTimeStampsItems;
    private final Map<Long, Long> _disabledSkills;
    private boolean _allSkillsDisabled;
    private Team _team;
    private boolean _lethalable;
    private volatile Map<Integer, OptionsSkillHolder> _triggerSkills;
    private volatile Map<Integer, IgnoreSkillHolder> _ignoreSkillEffects;
    private Creature _summoner;
    private volatile Map<Integer, Npc> _summonedNpcs;
    private SkillChannelizer _channelizer;
    private SkillChannelized _channelized;
    private BuffFinishTask _buffFinishTask;
    private Optional<Transform> _transform;
    private boolean _cursorKeyMovement;
    private boolean _cursorKeyMovementActive;
    private WorldObject _target;
    private volatile long _attackEndTime;
    private volatile long disableRangedAttackEndTime;
    private volatile CreatureAI _ai;
    private volatile CreatureContainer _seenCreatures;
    private volatile Map<BasicProperty, BasicPropertyResist> _basicPropertyResists;
    private ScheduledFuture<?> _hitTask;
    private Map<ShotType, Double> chargedShots;
    private boolean _AIdisabled;
    
    public Creature(final CreatureTemplate template) {
        this(IdFactory.getInstance().getNextId(), template);
    }
    
    public Creature(final int objectId, final CreatureTemplate template) {
        super(objectId);
        this._skills = new ConcurrentSkipListMap<Integer, Skill>();
        this._zones = new byte[ZoneType.getZoneCount()];
        this._attackLock = new StampedLock();
        this._effectList = new EffectList(this);
        this.abnormalShieldBlocks = new AtomicInteger();
        this._knownRelations = new ConcurrentHashMap<Integer, Integer>();
        this._statusUpdates = new ConcurrentHashMap<StatusUpdateType, Integer>();
        this._showSummonAnimation = false;
        this._isTeleporting = false;
        this._zoneValidateCounter = 4;
        this._exceptions = 0L;
        this._skillCasters = new ConcurrentHashMap<SkillCastingType, SkillCaster>();
        this._isDead = false;
        this._isImmobilized = false;
        this._isOverloaded = false;
        this._isPendingRevive = false;
        this._isInvul = false;
        this._isUndying = false;
        this._isFlying = false;
        this._blockActions = false;
        this._blockActionsAllowedSkills = new ConcurrentHashMap<Integer, AtomicInteger>();
        this._hpUpdateIncCheck = 0.0;
        this._hpUpdateDecCheck = 0.0;
        this._hpUpdateInterval = 0.0;
        this._reputation = 0;
        this._reuseTimeStampsSkills = new ConcurrentHashMap<Long, TimeStamp>();
        this._reuseTimeStampsItems = new ConcurrentHashMap<Integer, TimeStamp>();
        this._disabledSkills = new ConcurrentHashMap<Long, Long>();
        this._team = Team.NONE;
        this._lethalable = true;
        this._summoner = null;
        this._summonedNpcs = null;
        this._channelizer = null;
        this._channelized = null;
        this._buffFinishTask = null;
        this._transform = Optional.empty();
        this._cursorKeyMovement = false;
        this._cursorKeyMovementActive = true;
        this._ai = null;
        this._hitTask = null;
        this.chargedShots = new EnumMap<ShotType, Double>(ShotType.class);
        this._AIdisabled = false;
        if (template == null) {
            throw new NullPointerException("Template is null!");
        }
        this.setInstanceType(InstanceType.Creature);
        this._template = template;
        this.initCharStat();
        this.initCharStatus();
        if (GameUtils.isNpc(this)) {
            for (final Skill skill : template.getSkills().values()) {
                this.addSkill(skill);
            }
        }
        else if (GameUtils.isSummon(this)) {
            for (final Skill skill : template.getSkills().values()) {
                this.addSkill(skill);
            }
        }
        this.setIsInvul(true);
    }
    
    public final EffectList getEffectList() {
        return this._effectList;
    }
    
    public Inventory getInventory() {
        return null;
    }
    
    public boolean destroyItemByItemId(final String process, final int itemId, final long count, final WorldObject reference, final boolean sendMessage) {
        return true;
    }
    
    public boolean destroyItem(final String process, final int objectId, final long count, final WorldObject reference, final boolean sendMessage) {
        return true;
    }
    
    @Override
    public final boolean isInsideZone(final ZoneType zone) {
        final Instance instance = this.getInstanceWorld();
        switch (zone) {
            case PVP: {
                return (instance != null && instance.isPvP()) || (this._zones[ZoneType.PVP.ordinal()] > 0 && this._zones[ZoneType.PEACE.ordinal()] == 0);
            }
            case PEACE: {
                if (instance != null && instance.isPvP()) {
                    return false;
                }
                break;
            }
        }
        return this._zones[zone.ordinal()] > 0;
    }
    
    public final void setInsideZone(final ZoneType zone, final boolean state) {
        synchronized (this._zones) {
            if (state) {
                final byte[] zones = this._zones;
                final int ordinal = zone.ordinal();
                ++zones[ordinal];
            }
            else if (this._zones[zone.ordinal()] > 0) {
                final byte[] zones2 = this._zones;
                final int ordinal2 = zone.ordinal();
                --zones2[ordinal2];
            }
        }
    }
    
    public boolean isTransformed() {
        return this._transform.isPresent();
    }
    
    public boolean checkTransformed(final Predicate<Transform> filter) {
        return this._transform.filter(filter).isPresent();
    }
    
    public boolean transform(final int id, final boolean addSkills) {
        final Transform transform = TransformData.getInstance().getTransform(id);
        if (transform != null) {
            this.transform(transform, addSkills);
            return true;
        }
        return false;
    }
    
    public void transform(final Transform transformation, final boolean addSkills) {
        if (!Config.ALLOW_MOUNTS_DURING_SIEGE && transformation.isRiding() && this.isInsideZone(ZoneType.SIEGE)) {
            return;
        }
        this._transform = Optional.of(transformation);
        transformation.onTransform(this, addSkills);
    }
    
    public void untransform() {
        this._transform.ifPresent(t -> t.onUntransform(this));
        this._transform = Optional.empty();
    }
    
    public Optional<Transform> getTransformation() {
        return this._transform;
    }
    
    public int getTransformationId() {
        return this._transform.map((Function<? super Transform, ? extends Integer>)Transform::getId).orElse(0);
    }
    
    public int getTransformationDisplayId() {
        return this._transform.filter(transform -> !transform.isStance()).map((Function<? super Transform, ? extends Integer>)Transform::getDisplayId).orElse(0);
    }
    
    public double getCollisionRadius() {
        final double defaultCollisionRadius = this._template.getCollisionRadius();
        return this._transform.map(transform -> transform.getCollisionRadius(this, defaultCollisionRadius)).orElse(defaultCollisionRadius);
    }
    
    public double getCollisionHeight() {
        final double defaultCollisionHeight = this._template.getCollisionHeight();
        return this._transform.map(transform -> transform.getCollisionHeight(this, defaultCollisionHeight)).orElse(defaultCollisionHeight);
    }
    
    public boolean isGM() {
        return false;
    }
    
    public AccessLevel getAccessLevel() {
        return null;
    }
    
    protected void initCharStatusUpdateValues() {
        this._hpUpdateIncCheck = this.stats.getMaxHp();
        this._hpUpdateInterval = this._hpUpdateIncCheck / 352.0;
        this._hpUpdateDecCheck = this._hpUpdateIncCheck - this._hpUpdateInterval;
    }
    
    public void onDecay() {
        this.decayMe();
        final ZoneRegion region = ZoneManager.getInstance().getRegion(this);
        if (region != null) {
            region.removeFromZones(this);
        }
        if (this._summoner != null) {
            this._summoner.removeSummonedNpc(this.getObjectId());
        }
        if (this._seenCreatures != null) {
            this._seenCreatures.stop();
            this._seenCreatures.reset();
        }
    }
    
    @Override
    public void onSpawn() {
        super.onSpawn();
        this.revalidateZone(true);
        if (this._seenCreatures != null) {
            this._seenCreatures.start();
        }
    }
    
    public synchronized void onTeleported() {
        if (!this._isTeleporting) {
            return;
        }
        this.setSpawned(true);
        World.getInstance().switchRegionIfNeed(this);
        this.setIsTeleporting(false);
        EventDispatcher.getInstance().notifyEventAsync(new OnCreatureTeleported(this), this);
    }
    
    public void addAttackerToAttackByList(final Creature player) {
    }
    
    public void broadcastPacket(final ServerPacket mov) {
        World.getInstance().forEachVisibleObject(this, Player.class, player -> {
            if (this.isVisibleFor(player)) {
                player.sendPacket(mov);
            }
        });
    }
    
    public void broadcastPacket(final ServerPacket mov, final int radiusInKnownlist) {
        World.getInstance().forEachVisibleObjectInRange(this, Player.class, radiusInKnownlist, player -> {
            if (this.isVisibleFor(player)) {
                player.sendPacket(mov);
            }
        });
    }
    
    protected boolean needHpUpdate() {
        final double currentHp = this._status.getCurrentHp();
        final double maxHp = this.stats.getMaxHp();
        if (currentHp <= 1.0 || maxHp < 352.0) {
            return true;
        }
        if (currentHp < this._hpUpdateDecCheck || Math.abs(currentHp - this._hpUpdateDecCheck) <= 1.0E-6 || currentHp > this._hpUpdateIncCheck || Math.abs(currentHp - this._hpUpdateIncCheck) <= 1.0E-6) {
            if (Math.abs(currentHp - maxHp) <= 1.0E-6) {
                this._hpUpdateIncCheck = currentHp + 1.0;
                this._hpUpdateDecCheck = currentHp - this._hpUpdateInterval;
            }
            else {
                final double doubleMulti = currentHp / this._hpUpdateInterval;
                int intMulti = (int)doubleMulti;
                this._hpUpdateDecCheck = this._hpUpdateInterval * ((doubleMulti < intMulti) ? intMulti-- : intMulti);
                this._hpUpdateIncCheck = this._hpUpdateDecCheck + this._hpUpdateInterval;
            }
            return true;
        }
        return false;
    }
    
    public final void broadcastStatusUpdate() {
        this.broadcastStatusUpdate(null);
    }
    
    public void broadcastStatusUpdate(final Creature caster) {
        final StatusUpdate su = new StatusUpdate(this);
        if (caster != null) {
            su.addCaster(caster);
        }
        su.addUpdate(StatusUpdateType.MAX_HP, this.stats.getMaxHp());
        su.addUpdate(StatusUpdateType.CUR_HP, (int)this._status.getCurrentHp());
        this.computeStatusUpdate(su, StatusUpdateType.MAX_MP);
        this.computeStatusUpdate(su, StatusUpdateType.CUR_MP);
        this.broadcastPacket(su);
    }
    
    public void sendMessage(final String text) {
    }
    
    public void teleToLocation(int x, int y, int z, int heading, Instance instance) {
        final LocationReturn term = EventDispatcher.getInstance().notifyEvent(new OnCreatureTeleport(this, x, y, z, heading, instance), this, LocationReturn.class);
        if (term != null) {
            if (term.terminate()) {
                return;
            }
            if (term.overrideLocation()) {
                x = term.getX();
                y = term.getY();
                z = term.getZ();
                heading = term.getHeading();
                instance = term.getInstance();
            }
        }
        if (this._isPendingRevive) {
            this.doRevive();
        }
        this.sendPacket(ActionFailed.get(SkillCastingType.NORMAL));
        this.sendPacket(ActionFailed.get(SkillCastingType.NORMAL_SECOND));
        if (this.isMoving()) {
            this.stopMove(null);
        }
        this.abortCast();
        this.setTarget(null);
        this.setIsTeleporting(true);
        World.getInstance().removeVisibleObject(this, this.getWorldRegion());
        this.setWorldRegion(null);
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        z += 5;
        this.broadcastPacket(new TeleportToLocation(this, x, y, z, heading));
        if (this.getInstanceWorld() != instance) {
            this.setInstance(instance);
        }
        if (heading != 0) {
            this.setHeading(heading);
        }
        this.setXYZInvisible(x, y, z);
        this.sendPacket(new ExTeleportToLocationActivate(this));
        if (!GameUtils.isPlayer(this) || (this.getActingPlayer().getClient() != null && this.getActingPlayer().getClient().isDetached())) {
            this.onTeleported();
        }
        this.revalidateZone(true);
    }
    
    public void teleToLocation(final int x, final int y, final int z) {
        this.teleToLocation(x, y, z, 0, this.getInstanceWorld());
    }
    
    public void teleToLocation(final int x, final int y, final int z, final Instance instance) {
        this.teleToLocation(x, y, z, 0, instance);
    }
    
    public void teleToLocation(final int x, final int y, final int z, final int heading) {
        this.teleToLocation(x, y, z, heading, this.getInstanceWorld());
    }
    
    public void teleToLocation(final int x, final int y, final int z, final int heading, final boolean randomOffset) {
        this.teleToLocation(x, y, z, heading, randomOffset ? Config.MAX_OFFSET_ON_TELEPORT : 0, this.getInstanceWorld());
    }
    
    public void teleToLocation(final int x, final int y, final int z, final int heading, final boolean randomOffset, final Instance instance) {
        this.teleToLocation(x, y, z, heading, randomOffset ? Config.MAX_OFFSET_ON_TELEPORT : 0, instance);
    }
    
    public void teleToLocation(final int x, final int y, final int z, final int heading, final int randomOffset) {
        this.teleToLocation(x, y, z, heading, randomOffset, this.getInstanceWorld());
    }
    
    public void teleToLocation(int x, int y, final int z, final int heading, final int randomOffset, final Instance instance) {
        if (Config.OFFSET_ON_TELEPORT_ENABLED && randomOffset > 0) {
            x += Rnd.get(-randomOffset, randomOffset);
            y += Rnd.get(-randomOffset, randomOffset);
        }
        this.teleToLocation(x, y, z, heading, instance);
    }
    
    public void teleToLocation(final ILocational loc) {
        this.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading());
    }
    
    public void teleToLocation(final ILocational loc, final Instance instance) {
        this.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), instance);
    }
    
    public void teleToLocation(final ILocational loc, final int randomOffset) {
        this.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffset);
    }
    
    public void teleToLocation(final ILocational loc, final int randomOffset, final Instance instance) {
        this.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffset, instance);
    }
    
    public void teleToLocation(final ILocational loc, final boolean randomOffset) {
        this.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffset ? Config.MAX_OFFSET_ON_TELEPORT : 0);
    }
    
    public void teleToLocation(final ILocational loc, final boolean randomOffset, final Instance instance) {
        this.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffset, instance);
    }
    
    public void teleToLocation(final TeleportWhereType teleportWhere) {
        this.teleToLocation(teleportWhere, this.getInstanceWorld());
    }
    
    public void teleToLocation(final TeleportWhereType teleportWhere, final Instance instance) {
        this.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(this, teleportWhere), true, instance);
    }
    
    public void doAutoAttack(final Creature target) {
        final long stamp = this._attackLock.tryWriteLock();
        if (stamp == 0L) {
            return;
        }
        try {
            if (Objects.isNull(target) || (this.isAttackingDisabled() && !GameUtils.isSummon(this)) || !target.isTargetable()) {
                return;
            }
            if (!this.isAlikeDead()) {
                if ((GameUtils.isNpc(this) && target.isAlikeDead()) || !this.isInSurroundingRegion(target) || target.isDead()) {
                    this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                    this.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
                if (this.checkTransformed(transform -> !transform.canAttack())) {
                    this.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
            final Player player = this.getActingPlayer();
            if (Objects.nonNull(player)) {
                if (player.inObserverMode()) {
                    this.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
                    this.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
                if (player.isSiegeFriend(target)) {
                    this.sendPacket(SystemMessageId.FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE);
                    this.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
                if (target.isInsidePeaceZone(player)) {
                    this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                    this.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
            else if (this.isInsidePeaceZone(this, target)) {
                this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                this.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            this.stopEffectsOnAction();
            if (!GeoEngine.getInstance().canSeeTarget(this, target)) {
                this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                this.sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
                this.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            final Weapon weaponItem = this.getActiveWeaponItem();
            if (Objects.nonNull(weaponItem)) {
                if (!weaponItem.isAttackWeapon()) {
                    if (weaponItem.getItemType() == WeaponType.FISHING_ROD) {
                        this.sendPacket(SystemMessageId.YOU_LOOK_ODDLY_AT_THE_FISHING_POLE_IN_DISBELIEF_AND_REALIZE_THAT_YOU_CAN_T_ATTACK_ANYTHING_WITH_THIS);
                    }
                    else {
                        this.sendPacket(SystemMessageId.THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS);
                    }
                    this.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
                if (weaponItem.getItemType().isRanged() && !this.checkRangedAttackCondition(weaponItem, target)) {
                    this.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
            if (this.isMoving()) {
                this.stopMove(this.getLocation());
            }
            final WeaponType weaponType = this.getAttackType();
            final boolean isTwoHanded = weaponItem != null && weaponItem.getBodyPart() == BodyPart.TWO_HAND;
            final int timeAtk = Formulas.calculateTimeBetweenAttacks(this.stats.getPAtkSpd());
            final int timeToHit = Formulas.calculateTimeToHit(timeAtk, weaponType, isTwoHanded, false);
            this._attackEndTime = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeAtk);
            this.setHeading(MathUtil.calculateHeadingFrom(this, target));
            final Attack attack = this.generateAttackTargetData(target, weaponItem, weaponType);
            boolean crossbow = false;
            switch (weaponType) {
                case CROSSBOW:
                case TWO_HAND_CROSSBOW: {
                    crossbow = true;
                }
                case BOW: {
                    final int reuse = Formulas.calculateReuseTime(this, weaponItem);
                    this.onStartRangedAttack(crossbow, reuse);
                    this._hitTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> this.onHitTimeNotDual(weaponItem, attack, timeToHit, timeAtk), (long)timeToHit);
                    break;
                }
                case DUAL:
                case FIST:
                case DUAL_BLUNT:
                case DUAL_DAGGER: {
                    final int timeToHit2 = Formulas.calculateTimeToHit(timeAtk, weaponType, isTwoHanded, true) - timeToHit;
                    this._hitTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> this.onFirstHitTimeForDual(weaponItem, attack, timeToHit, timeAtk, timeToHit2), (long)timeToHit);
                    break;
                }
                default: {
                    this._hitTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> this.onHitTimeNotDual(weaponItem, attack, timeToHit, timeAtk), (long)timeToHit);
                    break;
                }
            }
            if (attack.hasHits()) {
                this.broadcastPacket(attack);
            }
            if (player != null) {
                AttackStanceTaskManager.getInstance().addAttackStanceTask(player);
                player.updatePvPStatus(target);
            }
        }
        finally {
            this._attackLock.unlockWrite(stamp);
        }
    }
    
    protected void onStartRangedAttack(final boolean crossbow, final int reuse) {
        this.disableRangedAttackEndTime = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(reuse);
    }
    
    protected boolean checkRangedAttackCondition(final Weapon weaponItem, final Creature target) {
        return this.disableRangedAttackEndTime <= System.nanoTime();
    }
    
    private Attack generateAttackTargetData(final Creature target, final Weapon weapon, final WeaponType weaponType) {
        final boolean isDual = WeaponType.DUAL == weaponType || WeaponType.DUAL_BLUNT == weaponType || WeaponType.DUAL_DAGGER == weaponType || WeaponType.FIST == weaponType;
        final Attack attack = new Attack(this, target);
        boolean shotConsumed = false;
        Hit hit = this.generateHit(target, weapon, shotConsumed, isDual);
        attack.addHit(hit);
        shotConsumed = hit.isShotUsed();
        if (isDual) {
            hit = this.generateHit(target, weapon, shotConsumed, isDual);
            attack.addHit(hit);
        }
        final int attackCountMax = (int)this.stats.getValue(Stat.ATTACK_COUNT_MAX, 1.0);
        if (attackCountMax > 1 && this.stats.getValue(Stat.PHYSICAL_POLEARM_TARGET_SINGLE, 0.0) <= 0.0) {
            final double headingAngle = MathUtil.convertHeadingToDegree(this.getHeading());
            final int maxRadius = this.stats.getPhysicalAttackRadius();
            final int physicalAttackAngle = this.stats.getPhysicalAttackAngle();
            final Attack attack2;
            World.getInstance().forVisibleObjectsInRange(this, Creature.class, maxRadius, attackCountMax, creature -> this.canBeAttacked(target, headingAngle, physicalAttackAngle, creature), creature -> attack2.addHit(this.generateHit(creature, weapon, attack2.isShotUsed(), false)));
        }
        return attack;
    }
    
    private boolean canBeAttacked(final Creature target, final double headingAngle, final int physicalAttackAngle, final Creature creature) {
        return !creature.equals(target) && !creature.isAlikeDead() && creature.isAutoAttackable(this) && Math.abs(this.calculateDirectionTo(creature) - headingAngle) <= physicalAttackAngle;
    }
    
    private Hit generateHit(final Creature target, final Weapon weapon, boolean shotConsumed, final boolean halfDamage) {
        int damage = 0;
        byte shld = 0;
        boolean crit = false;
        final boolean miss = Formulas.calcHitMiss(this, target);
        if (!shotConsumed) {
            shotConsumed = (!miss && this.isChargedShot(ShotType.SOULSHOTS));
        }
        if (!miss) {
            shld = Formulas.calcShldUse(this, target);
            crit = Formulas.calcCrit(this.stats.getCriticalHit(), this, target, null);
            damage = (int)Formulas.calcAutoAttackDamage(this, target, shld, crit, shotConsumed);
            if (halfDamage) {
                damage /= 2;
            }
        }
        return new Hit(target, damage, miss, crit, shld, shotConsumed ? (Objects.nonNull(weapon) ? weapon.getItemGrade().ordinal() : 0) : -1);
    }
    
    public void doCast(final Skill skill) {
        this.doCast(skill, null, false, false, SkillCastingType.NORMAL);
    }
    
    public void doCast(final Skill skill, final SkillCastingType castingType) {
        this.doCast(skill, null, false, false, castingType);
    }
    
    public void doCast(final Skill skill, final Item item, final boolean ctrlPressed, final boolean shiftPressed) {
        this.doCast(skill, item, ctrlPressed, shiftPressed, SkillCastingType.NORMAL);
    }
    
    public synchronized void doCast(final Skill skill, final Item item, final boolean ctrlPressed, final boolean shiftPressed, final SkillCastingType castingType) {
        final SkillCaster skillCaster = SkillCaster.castSkill(this, this._target, skill, item, castingType, ctrlPressed, shiftPressed);
        if (skillCaster == null && GameUtils.isPlayer(this)) {
            this.sendPacket(ActionFailed.get(castingType));
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
    
    public final Map<Integer, TimeStamp> getItemReuseTimeStamps() {
        return this._reuseTimeStampsItems;
    }
    
    public final void addTimeStampItem(final Item item, final long reuse) {
        this.addTimeStampItem(item, reuse, -1L);
    }
    
    public final void addTimeStampItem(final Item item, final long reuse, final long systime) {
        this._reuseTimeStampsItems.put(item.getObjectId(), new TimeStamp(item, reuse, systime));
    }
    
    public final synchronized long getItemRemainingReuseTime(final int itemObjId) {
        final TimeStamp reuseStamp = this._reuseTimeStampsItems.get(itemObjId);
        return (reuseStamp != null) ? reuseStamp.getRemaining() : -1L;
    }
    
    public final long getReuseDelayOnGroup(final int group) {
        if (group > 0 && !this._reuseTimeStampsItems.isEmpty()) {
            final long currentTime = System.currentTimeMillis();
            for (final TimeStamp ts : this._reuseTimeStampsItems.values()) {
                if (ts.getSharedReuseGroup() == group) {
                    final long stamp = ts.getStamp();
                    if (currentTime < stamp) {
                        return Math.max(stamp - currentTime, 0L);
                    }
                    continue;
                }
            }
        }
        return -1L;
    }
    
    public final Map<Long, TimeStamp> getSkillReuseTimeStamps() {
        return this._reuseTimeStampsSkills;
    }
    
    public final void addTimeStamp(final Skill skill, final long reuse) {
        this.addTimeStamp(skill, reuse, -1L);
    }
    
    public final void addTimeStamp(final Skill skill, final long reuse, final long systime) {
        this._reuseTimeStampsSkills.put(skill.getReuseHashCode(), new TimeStamp(skill, reuse, systime));
    }
    
    public final synchronized void removeTimeStamp(final Skill skill) {
        this._reuseTimeStampsSkills.remove(skill.getReuseHashCode());
    }
    
    public final synchronized void resetTimeStamps() {
        this._reuseTimeStampsSkills.clear();
    }
    
    public final synchronized long getSkillRemainingReuseTime(final long hashCode) {
        final TimeStamp reuseStamp = this._reuseTimeStampsSkills.get(hashCode);
        return (reuseStamp != null) ? reuseStamp.getRemaining() : -1L;
    }
    
    public final synchronized boolean hasSkillReuse(final long hashCode) {
        final TimeStamp reuseStamp = this._reuseTimeStampsSkills.get(hashCode);
        return reuseStamp != null && reuseStamp.hasNotPassed();
    }
    
    public final synchronized TimeStamp getSkillReuseTimeStamp(final long hashCode) {
        return this._reuseTimeStampsSkills.get(hashCode);
    }
    
    public Map<Long, Long> getDisabledSkills() {
        return this._disabledSkills;
    }
    
    public void enableSkill(final Skill skill) {
        if (skill == null) {
            return;
        }
        this._disabledSkills.remove(skill.getReuseHashCode());
    }
    
    public void disableSkill(final Skill skill, final long delay) {
        if (skill == null) {
            return;
        }
        this._disabledSkills.put(skill.getReuseHashCode(), (delay > 0L) ? (System.currentTimeMillis() + delay) : Long.MAX_VALUE);
    }
    
    public void resetDisabledSkills() {
        this._disabledSkills.clear();
    }
    
    public boolean isSkillDisabled(final Skill skill) {
        if (skill == null) {
            return false;
        }
        if (this._allSkillsDisabled || (!skill.canCastWhileDisabled() && this.isAllSkillsDisabled())) {
            return true;
        }
        if (this.isAffected(EffectFlag.CONDITIONAL_BLOCK_ACTIONS) && !this.isBlockedActionsAllowedSkill(skill)) {
            return true;
        }
        final long hashCode = skill.getReuseHashCode();
        if (this.hasSkillReuse(hashCode)) {
            return true;
        }
        if (this._disabledSkills.isEmpty()) {
            return false;
        }
        final Long stamp = this._disabledSkills.get(hashCode);
        if (stamp == null) {
            return false;
        }
        if (stamp < System.currentTimeMillis()) {
            this._disabledSkills.remove(hashCode);
            return false;
        }
        return true;
    }
    
    public void disableAllSkills() {
        this._allSkillsDisabled = true;
    }
    
    public void enableAllSkills() {
        this._allSkillsDisabled = false;
    }
    
    public boolean doDie(final Creature killer) {
        synchronized (this) {
            if (this._isDead) {
                return false;
            }
            final TerminateReturn returnBack = EventDispatcher.getInstance().notifyEvent(new OnCreatureDeath(killer, this), this, TerminateReturn.class);
            if (Objects.nonNull(returnBack) && returnBack.terminate()) {
                return false;
            }
            this.setCurrentHp(0.0);
            this._isDead = true;
        }
        this.stopMove(null);
        this.calculateRewards(killer);
        if (this.hasAI()) {
            this.getAI().notifyEvent(CtrlEvent.EVT_DEAD);
        }
        EventDispatcher.getInstance().notifyEvent(new OnCreatureKilled(killer, this), killer);
        this._status.stopHpMpRegeneration();
        if (GameUtils.isMonster(this)) {
            final Spawn spawn = ((Npc)this).getSpawn();
            if (spawn != null && spawn.isRespawnEnabled()) {
                this.stopAllEffects();
            }
            else {
                this._effectList.stopAllEffectsWithoutExclusions(true, true);
            }
        }
        else {
            this.stopAllEffectsExceptThoseThatLastThroughDeath();
        }
        this.broadcastStatusUpdate();
        ZoneManager.getInstance().getRegion(this).onDeath(this);
        this.getAttackByList().clear();
        this.forgetTarget();
        if (this.isChannelized()) {
            this.getSkillChannelized().abortChannelization();
        }
        return true;
    }
    
    public void forgetTarget() {
        this.abortAttack();
        this.abortCast();
        this.setTarget(null);
    }
    
    @Override
    public boolean decayMe() {
        if (this.hasAI()) {
            this.getAI().stopAITask();
        }
        return super.decayMe();
    }
    
    @Override
    public boolean deleteMe() {
        if (this.hasAI()) {
            this.getAI().stopAITask();
        }
        if (this._summoner != null) {
            this._summoner.removeSummonedNpc(this.getObjectId());
        }
        this._effectList.stopAllEffectsWithoutExclusions(false, false);
        TimersManager.getInstance().cancelTimers(this.getObjectId());
        this.cancelBuffFinishTask();
        this.setWorldRegion(null);
        return true;
    }
    
    protected void calculateRewards(final Creature killer) {
    }
    
    public void doRevive() {
        if (!this._isDead) {
            return;
        }
        if (!this._isTeleporting) {
            this.setIsPendingRevive(false);
            this.setIsDead(false);
            if (Config.RESPAWN_RESTORE_CP > 0.0 && this._status.getCurrentCp() < this.stats.getMaxCp() * Config.RESPAWN_RESTORE_CP) {
                this._status.setCurrentCp(this.stats.getMaxCp() * Config.RESPAWN_RESTORE_CP);
            }
            if (Config.RESPAWN_RESTORE_HP > 0.0 && this._status.getCurrentHp() < this.stats.getMaxHp() * Config.RESPAWN_RESTORE_HP) {
                this._status.setCurrentHp(this.stats.getMaxHp() * Config.RESPAWN_RESTORE_HP);
            }
            if (Config.RESPAWN_RESTORE_MP > 0.0 && this._status.getCurrentMp() < this.stats.getMaxMp() * Config.RESPAWN_RESTORE_MP) {
                this._status.setCurrentMp(this.stats.getMaxMp() * Config.RESPAWN_RESTORE_MP);
            }
            this.broadcastPacket(new Revive(this));
            ZoneManager.getInstance().getRegion(this).onRevive(this);
        }
        else {
            this.setIsPendingRevive(true);
        }
    }
    
    public void doRevive(final double revivePower) {
        this.doRevive();
    }
    
    public CreatureAI getAI() {
        CreatureAI ai = this._ai;
        if (ai == null) {
            synchronized (this) {
                ai = this._ai;
                if (ai == null) {
                    ai = (this._ai = this.initAI());
                }
            }
        }
        return ai;
    }
    
    public void setAI(final CreatureAI newAI) {
        final CreatureAI oldAI = this._ai;
        if (oldAI != null && oldAI != newAI && oldAI instanceof AttackableAI) {
            oldAI.stopAITask();
        }
        this._ai = newAI;
    }
    
    protected CreatureAI initAI() {
        return new CreatureAI(this);
    }
    
    public void detachAI() {
        if (GameUtils.isWalker(this)) {
            return;
        }
        this.setAI(null);
    }
    
    public boolean hasAI() {
        return this._ai != null;
    }
    
    public boolean isRaid() {
        return false;
    }
    
    public boolean isMinion() {
        return false;
    }
    
    public boolean isRaidMinion() {
        return false;
    }
    
    public final Set<WeakReference<Creature>> getAttackByList() {
        if (this._attackByList == null) {
            synchronized (this) {
                if (this._attackByList == null) {
                    this._attackByList = (Set<WeakReference<Creature>>)ConcurrentHashMap.newKeySet();
                }
            }
        }
        return this._attackByList;
    }
    
    public final boolean isControlBlocked() {
        return this.isAffected(EffectFlag.BLOCK_CONTROL);
    }
    
    public final boolean isAllSkillsDisabled() {
        return this._allSkillsDisabled || this.hasBlockActions();
    }
    
    public boolean isAttackingDisabled() {
        return this.hasBlockActions() || this.isAttackingNow() || this.isAlikeDead() || this.isPhysicalAttackMuted() || this._AIdisabled;
    }
    
    public final boolean isConfused() {
        return this.isAffected(EffectFlag.CONFUSED);
    }
    
    public boolean isAlikeDead() {
        return this._isDead;
    }
    
    public final boolean isDead() {
        return this._isDead;
    }
    
    public final void setIsDead(final boolean value) {
        this._isDead = value;
    }
    
    public boolean isImmobilized() {
        return this._isImmobilized;
    }
    
    public void setIsImmobilized(final boolean value) {
        this._isImmobilized = value;
    }
    
    public final boolean isMuted() {
        return this.isAffected(EffectFlag.MUTED);
    }
    
    public final boolean isPhysicalMuted() {
        return this.isAffected(EffectFlag.PSYCHICAL_MUTED);
    }
    
    public final boolean isPhysicalAttackMuted() {
        return this.isAffected(EffectFlag.PSYCHICAL_ATTACK_MUTED);
    }
    
    public boolean isMovementDisabled() {
        return this.hasBlockActions() || this.isRooted() || this._isOverloaded || this._isImmobilized || this.isAlikeDead() || this._isTeleporting;
    }
    
    public final boolean isOverloaded() {
        return this._isOverloaded;
    }
    
    public final void setIsOverloaded(final boolean value) {
        this._isOverloaded = value;
    }
    
    public final boolean isPendingRevive() {
        return this._isDead && this._isPendingRevive;
    }
    
    public final void setIsPendingRevive(final boolean value) {
        this._isPendingRevive = value;
    }
    
    public final boolean isDisarmed() {
        return this.isAffected(EffectFlag.DISARMED);
    }
    
    public Summon getPet() {
        return null;
    }
    
    public Map<Integer, Summon> getServitors() {
        return Collections.emptyMap();
    }
    
    public Summon getServitor(final int objectId) {
        return null;
    }
    
    public final boolean hasSummon() {
        return this.hasPet() || !this.getServitors().isEmpty();
    }
    
    public final boolean hasPet() {
        return this.getPet() != null;
    }
    
    public final boolean hasServitor(final int objectId) {
        return this.getServitors().containsKey(objectId);
    }
    
    public final boolean hasServitors() {
        return !this.getServitors().isEmpty();
    }
    
    public void removeServitor(final int objectId) {
        this.getServitors().remove(objectId);
    }
    
    public final boolean isRooted() {
        return this.isAffected(EffectFlag.ROOTED);
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    private final void setIsRunning(final boolean value) {
        if (this.running == value) {
            return;
        }
        this.running = value;
        if (this.stats.getRunSpeed() != 0.0) {
            this.broadcastPacket(new ChangeMoveType(this));
        }
        if (GameUtils.isPlayer(this)) {
            this.getActingPlayer().broadcastUserInfo();
        }
        else if (GameUtils.isSummon(this)) {
            this.broadcastStatusUpdate();
        }
        else if (GameUtils.isNpc(this)) {
            World.getInstance().forEachVisibleObject(this, Player.class, player -> {
                if (!(!this.isVisibleFor(player))) {
                    if (this.stats.getRunSpeed() == 0.0) {
                        player.sendPacket(new ServerObjectInfo((Npc)this, player));
                    }
                    else {
                        player.sendPacket(new NpcInfo((Npc)this));
                    }
                }
            });
        }
    }
    
    public final void setRunning() {
        this.setIsRunning(true);
    }
    
    public final boolean hasBlockActions() {
        return this._blockActions || this.isAffected(EffectFlag.BLOCK_ACTIONS) || this.isAffected(EffectFlag.CONDITIONAL_BLOCK_ACTIONS);
    }
    
    public final void setBlockActions(final boolean blockActions) {
        this._blockActions = blockActions;
    }
    
    public final boolean isBetrayed() {
        return this.isAffected(EffectFlag.BETRAYED);
    }
    
    public final boolean isTeleporting() {
        return this._isTeleporting;
    }
    
    public void setIsTeleporting(final boolean value) {
        this._isTeleporting = value;
    }
    
    public void setIsInvul(final boolean b) {
        this._isInvul = b;
    }
    
    @Override
    public boolean isInvul() {
        return this._isInvul || this._isTeleporting;
    }
    
    public boolean isUndying() {
        return this._isUndying || this.isInvul() || this.isAffected(EffectFlag.IGNORE_DEATH) || this.isInsideZone(ZoneType.UNDYING);
    }
    
    public void setUndying(final boolean undying) {
        this._isUndying = undying;
    }
    
    public boolean isHpBlocked() {
        return this.isInvul() || this.isAffected(EffectFlag.HP_BLOCK);
    }
    
    public boolean isMpBlocked() {
        return this.isInvul() || this.isAffected(EffectFlag.MP_BLOCK);
    }
    
    public boolean isBuffBlocked() {
        return this.isAffected(EffectFlag.BUFF_BLOCK);
    }
    
    public boolean isDebuffBlocked() {
        return this.isInvul() || this.isAffected(EffectFlag.DEBUFF_BLOCK);
    }
    
    public boolean isUndead() {
        return false;
    }
    
    public boolean isResurrectionBlocked() {
        return this.isAffected(EffectFlag.BLOCK_RESURRECTION);
    }
    
    public final boolean isFlying() {
        return this._isFlying;
    }
    
    public final void setIsFlying(final boolean mode) {
        this._isFlying = mode;
    }
    
    public CreatureStats getStats() {
        return this.stats;
    }
    
    public final void setStat(final CreatureStats value) {
        this.stats = value;
    }
    
    public void initCharStat() {
        this.stats = new CreatureStats(this);
    }
    
    public CreatureStatus getStatus() {
        return this._status;
    }
    
    public final void setStatus(final CreatureStatus value) {
        this._status = value;
    }
    
    public void initCharStatus() {
        this._status = new CreatureStatus(this);
    }
    
    public CreatureTemplate getTemplate() {
        return this._template;
    }
    
    protected final void setTemplate(final CreatureTemplate template) {
        this._template = template;
    }
    
    public final String getTitle() {
        if (this.isChampion()) {
            return Config.CHAMP_TITLE;
        }
        if (Config.SHOW_NPC_LVL && GameUtils.isMonster(this)) {
            String t = invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, this.getLevel(), ((Monster)this).isAggressive() ? "*" : "");
            if (this._title != null) {
                t = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, t, this._title);
            }
            return t;
        }
        if (GameUtils.isTrap(this) && ((Trap)this).getOwner() != null) {
            this._title = ((Trap)this).getOwner().getName();
        }
        return (this._title != null) ? this._title : "";
    }
    
    public final void setTitle(final String value) {
        if (value == null) {
            this._title = "";
        }
        else {
            this._title = ((value.length() > 21) ? value.substring(0, 20) : value);
        }
    }
    
    public final void setWalking() {
        this.setIsRunning(false);
    }
    
    public final void startFakeDeath() {
        if (!GameUtils.isPlayer(this)) {
            return;
        }
        this.abortAttack();
        this.abortCast();
        this.stopMove(null);
        this.getAI().notifyEvent(CtrlEvent.EVT_FAKE_DEATH);
        this.broadcastPacket(new ChangeWaitType(this, 2));
    }
    
    public final void startParalyze() {
        this.abortAttack();
        this.abortCast();
        this.stopMove(null);
        this.getAI().notifyEvent(CtrlEvent.EVT_ACTION_BLOCKED);
    }
    
    public void stopAllEffects() {
        this._effectList.stopAllEffects(true);
    }
    
    public void stopAllEffectsExceptThoseThatLastThroughDeath() {
        this._effectList.stopAllEffectsExceptThoseThatLastThroughDeath();
    }
    
    public void stopSkillEffects(final boolean removed, final int skillId) {
        this._effectList.stopSkillEffects(removed, skillId);
    }
    
    public void stopSkillEffects(final Skill skill) {
        this._effectList.stopSkillEffects(true, skill.getId());
    }
    
    public final void stopEffects(final EffectFlag effectFlag) {
        this._effectList.stopEffects(effectFlag);
    }
    
    public final void stopEffectsOnAction() {
        this._effectList.stopEffectsOnAction();
    }
    
    public final void stopEffectsOnDamage() {
        this._effectList.stopEffectsOnDamage();
    }
    
    public final void stopFakeDeath(final boolean removeEffects) {
        if (removeEffects) {
            this.stopEffects(EffectFlag.FAKE_DEATH);
        }
        if (GameUtils.isPlayer(this)) {
            this.getActingPlayer().setRecentFakeDeath(true);
        }
        this.broadcastPacket(new ChangeWaitType(this, 3));
        this.broadcastPacket(new Revive(this));
    }
    
    public final void stopStunning(final boolean removeEffects) {
        if (removeEffects) {
            this._effectList.stopEffects(AbnormalType.STUN);
        }
        if (!GameUtils.isPlayer(this)) {
            this.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }
    
    public final void startStunning() {
        this.abortAttack();
        this.abortCast();
        this.stopMove(null);
        this.getAI().notifyEvent(CtrlEvent.EVT_ACTION_BLOCKED);
        if (!GameUtils.isSummon(this)) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        }
        this.updateAbnormalVisualEffects();
    }
    
    public final void stopTransformation(final boolean removeEffects) {
        if (removeEffects) {
            this._effectList.stopEffects(AbnormalType.TRANSFORM);
            this._effectList.stopEffects(AbnormalType.CHANGEBODY);
        }
        if (this._transform.isPresent()) {
            this.untransform();
        }
        if (!GameUtils.isPlayer(this)) {
            this.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
        this.updateAbnormalVisualEffects();
    }
    
    public void updateAbnormalVisualEffects() {
    }
    
    public final void updateEffectIcons() {
        this.updateEffectIcons(false);
    }
    
    public void updateEffectIcons(final boolean partyOnly) {
    }
    
    public boolean isAffectedBySkill(final SkillHolder skill) {
        return this.isAffectedBySkill(skill.getSkillId());
    }
    
    public boolean isAffectedBySkill(final int skillId) {
        return this._effectList.isAffectedBySkill(skillId);
    }
    
    public int getAffectedSkillLevel(final int skillId) {
        final BuffInfo info = this._effectList.getBuffInfoBySkillId(skillId);
        return (info == null) ? 0 : info.getSkill().getLevel();
    }
    
    public void broadcastModifiedStats(final Set<Stat> changed) {
        if (!this.isSpawned()) {
            return;
        }
        if (Util.isNullOrEmpty((Collection)changed)) {
            return;
        }
        if (GameUtils.isPlayer(this) && !this.getActingPlayer().isOnline()) {
            return;
        }
        if (this.isMoving() && this.stats.getMoveSpeed() <= 0.0) {
            this.stopMove(null);
        }
        if (GameUtils.isSummon(this)) {
            final Summon summon = (Summon)this;
            if (summon.getOwner() != null) {
                summon.updateAndBroadcastStatus(1);
            }
        }
        else {
            final boolean broadcastFull = true;
            final StatusUpdate su = new StatusUpdate(this);
            UserInfo info = null;
            if (GameUtils.isPlayer(this)) {
                info = new UserInfo(this.getActingPlayer(), false);
                info.addComponentType(UserInfoType.SLOTS, UserInfoType.ENCHANTLEVEL);
            }
            for (final Stat stat : changed) {
                if (info != null) {
                    switch (stat) {
                        case SPEED:
                        case RUN_SPEED:
                        case WALK_SPEED:
                        case SWIM_RUN_SPEED:
                        case SWIM_WALK_SPEED:
                        case FLY_RUN_SPEED:
                        case FLY_WALK_SPEED: {
                            info.addComponentType(UserInfoType.MULTIPLIER);
                            continue;
                        }
                        case PHYSICAL_ATTACK_SPEED: {
                            info.addComponentType(UserInfoType.MULTIPLIER, UserInfoType.STATS);
                            continue;
                        }
                        case PHYSICAL_ATTACK:
                        case PHYSICAL_DEFENCE:
                        case EVASION_RATE:
                        case ACCURACY:
                        case CRITICAL_RATE:
                        case MAGIC_CRITICAL_RATE:
                        case MAGIC_EVASION_RATE:
                        case ACCURACY_MAGIC:
                        case MAGIC_ATTACK:
                        case MAGIC_ATTACK_SPEED:
                        case MAGICAL_DEFENCE:
                        case HIT_AT_NIGHT: {
                            info.addComponentType(UserInfoType.STATS);
                            continue;
                        }
                        case MAX_CP: {
                            if (GameUtils.isPlayer(this)) {
                                info.addComponentType(UserInfoType.MAX_HPCPMP);
                                continue;
                            }
                            su.addUpdate(StatusUpdateType.MAX_CP, this.stats.getMaxCp());
                            continue;
                        }
                        case MAX_HP: {
                            if (GameUtils.isPlayer(this)) {
                                info.addComponentType(UserInfoType.MAX_HPCPMP);
                                continue;
                            }
                            su.addUpdate(StatusUpdateType.MAX_HP, this.stats.getMaxHp());
                            continue;
                        }
                        case MAX_MP: {
                            if (GameUtils.isPlayer(this)) {
                                info.addComponentType(UserInfoType.MAX_HPCPMP);
                                continue;
                            }
                            su.addUpdate(StatusUpdateType.MAX_CP, this.stats.getMaxMp());
                            continue;
                        }
                        case STAT_STR:
                        case STAT_CON:
                        case STAT_DEX:
                        case STAT_INT:
                        case STAT_WIT:
                        case STAT_MEN: {
                            info.addComponentType(UserInfoType.BASE_STATS);
                            info.addComponentType(UserInfoType.STATS_ABILITIES);
                            info.addComponentType(UserInfoType.STATS_POINTS);
                            continue;
                        }
                        case FIRE_RES:
                        case WATER_RES:
                        case WIND_RES:
                        case EARTH_RES:
                        case HOLY_RES:
                        case DARK_RES: {
                            info.addComponentType(UserInfoType.ELEMENTALS);
                            continue;
                        }
                        case FIRE_POWER:
                        case WATER_POWER:
                        case WIND_POWER:
                        case EARTH_POWER:
                        case HOLY_POWER:
                        case DARK_POWER: {
                            info.addComponentType(UserInfoType.ATK_ELEMENTAL);
                            continue;
                        }
                        case ELEMENTAL_SPIRIT_EARTH_ATTACK:
                        case ELEMENTAL_SPIRIT_EARTH_DEFENSE:
                        case ELEMENTAL_SPIRIT_FIRE_ATTACK:
                        case ELEMENTAL_SPIRIT_FIRE_DEFENSE:
                        case ELEMENTAL_SPIRIT_WATER_ATTACK:
                        case ELEMENTAL_SPIRIT_WATER_DEFENSE:
                        case ELEMENTAL_SPIRIT_WIND_ATTACK:
                        case ELEMENTAL_SPIRIT_WIND_DEFENSE: {
                            info.addComponentType(UserInfoType.SPIRITS);
                            continue;
                        }
                    }
                }
            }
            if (GameUtils.isPlayer(this)) {
                final Player player2 = this.getActingPlayer();
                player2.refreshOverloaded(true);
                this.sendPacket(info);
                player2.broadcastCharInfo();
                if (this.hasServitors() && this.hasAbnormalType(AbnormalType.ABILITY_CHANGE)) {
                    this.getServitors().values().forEach(Creature::broadcastStatusUpdate);
                }
            }
            else if (GameUtils.isNpc(this)) {
                World.getInstance().forEachVisibleObject(this, Player.class, player -> {
                    if (!(!this.isVisibleFor(player))) {
                        if (this.stats.getRunSpeed() == 0.0) {
                            player.sendPacket(new ServerObjectInfo((Npc)this, player));
                        }
                        else {
                            player.sendPacket(new NpcInfo((Npc)this));
                        }
                    }
                });
            }
            else if (su.hasUpdates()) {
                this.broadcastPacket(su);
            }
        }
    }
    
    public final int getXdestination() {
        final MoveData m = this._move;
        if (m != null) {
            return m._xDestination;
        }
        return this.getX();
    }
    
    public final int getYdestination() {
        final MoveData m = this._move;
        if (m != null) {
            return m._yDestination;
        }
        return this.getY();
    }
    
    public final int getZdestination() {
        final MoveData m = this._move;
        if (m != null) {
            return m._zDestination;
        }
        return this.getZ();
    }
    
    public boolean isInCombat() {
        return this.hasAI() && this.getAI().isAutoAttacking();
    }
    
    public final boolean isMoving() {
        return this._move != null;
    }
    
    public final boolean isOnGeodataPath() {
        final MoveData m = this._move;
        return m != null && m.onGeodataPathIndex != -1 && m.onGeodataPathIndex != m.geoPath.size() - 1;
    }
    
    public final boolean isCastingNow() {
        return !this._skillCasters.isEmpty();
    }
    
    public final boolean isCastingNow(final SkillCastingType skillCastingType) {
        return this._skillCasters.containsKey(skillCastingType);
    }
    
    public final boolean isCastingNow(final Predicate<SkillCaster> filter) {
        return this._skillCasters.values().stream().anyMatch(filter);
    }
    
    public final boolean isAttackingNow() {
        return this._attackEndTime > System.nanoTime();
    }
    
    public final void abortAttack() {
        if (this.isAttackingNow()) {
            final ScheduledFuture<?> hitTask = this._hitTask;
            if (hitTask != null) {
                hitTask.cancel(false);
                this._hitTask = null;
            }
            this.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
    
    public final void abortAllSkillCasters() {
        for (final SkillCaster skillCaster : this.getSkillCasters()) {
            skillCaster.stopCasting(true);
            if (GameUtils.isPlayer(this)) {
                this.getActingPlayer().setQueuedSkill(null, null, false, false);
            }
        }
    }
    
    public final boolean abortCast() {
        return this.abortCast(SkillCaster::isAnyNormalType);
    }
    
    public final boolean abortCast(final Predicate<SkillCaster> filter) {
        final SkillCaster skillCaster = this.getSkillCaster(SkillCaster::canAbortCast, filter);
        if (skillCaster != null) {
            skillCaster.stopCasting(true);
            if (GameUtils.isPlayer(this)) {
                this.getActingPlayer().setQueuedSkill(null, null, false, false);
            }
            return true;
        }
        return false;
    }
    
    public boolean updatePosition() {
        final MoveData m = this._move;
        if (m == null) {
            return true;
        }
        if (!this.isSpawned()) {
            this._move = null;
            return true;
        }
        if (m._moveTimestamp == 0) {
            m._moveTimestamp = m._moveStartTime;
            m._xAccurate = this.getX();
            m._yAccurate = this.getY();
        }
        final int gameTicks = WorldTimeController.getInstance().getGameTicks();
        if (m._moveTimestamp == gameTicks) {
            return false;
        }
        final int xPrev = this.getX();
        final int yPrev = this.getY();
        final int zPrev = this.getZ();
        double dx;
        double dy;
        if (((GeoEngineSettings)Configurator.getSettings((Class)GeoEngineSettings.class)).isSyncMode(SyncMode.CLIENT)) {
            dx = m._xDestination - xPrev;
            dy = m._yDestination - yPrev;
        }
        else {
            dx = m._xDestination - m._xAccurate;
            dy = m._yDestination - m._yAccurate;
        }
        final double dz = m._zDestination - zPrev;
        if (GameUtils.isPlayer(this) && !this._isFlying) {
            final double distance = Math.hypot(dx, dy);
            if (this._cursorKeyMovement || distance > 3000.0) {
                final double angle = MathUtil.convertHeadingToDegree(this.getHeading());
                final double radian = Math.toRadians(angle);
                final double course = Math.toRadians(180.0);
                final double frontDistance = 10.0 * (this.stats.getMoveSpeed() / 100.0);
                final int x1 = (int)(Math.cos(3.141592653589793 + radian + course) * frontDistance);
                final int y1 = (int)(Math.sin(3.141592653589793 + radian + course) * frontDistance);
                final int x2 = xPrev + x1;
                final int y2 = yPrev + y1;
                if (!GeoEngine.getInstance().canMoveToTarget(xPrev, yPrev, zPrev, x2, y2, zPrev, this.getInstanceWorld())) {
                    this._move.onGeodataPathIndex = -1;
                    this.stopMove(this.getActingPlayer().getLastServerPosition());
                    return this._cursorKeyMovementActive = false;
                }
            }
            if (dz > 180.0 && distance < 300.0) {
                this._move.onGeodataPathIndex = -1;
                this.stopMove(this.getActingPlayer().getLastServerPosition());
                return false;
            }
        }
        final boolean isFloating = this._isFlying || this.isInsideZone(ZoneType.WATER);
        double delta = dx * dx + dy * dy;
        if (delta < 10000.0 && dz * dz > 2500.0 && !isFloating) {
            delta = Math.sqrt(delta);
        }
        else {
            delta = Math.sqrt(delta + dz * dz);
        }
        double distFraction = Double.MAX_VALUE;
        if (delta > 1.0) {
            final double distPassed = this.stats.getMoveSpeed() * (gameTicks - m._moveTimestamp) / 10.0;
            distFraction = distPassed / delta;
        }
        if (distFraction > 1.0) {
            super.setXYZ(m._xDestination, m._yDestination, m._zDestination);
        }
        else {
            final MoveData moveData = m;
            moveData._xAccurate += dx * distFraction;
            final MoveData moveData2 = m;
            moveData2._yAccurate += dy * distFraction;
            super.setXYZ((int)m._xAccurate, (int)m._yAccurate, zPrev + (int)(dz * distFraction + 0.5));
        }
        this.revalidateZone(false);
        m._moveTimestamp = gameTicks;
        this.broadcastPacket(new MoveToLocation(this));
        if (distFraction > 1.0) {
            ThreadPool.execute(() -> this.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED));
            return true;
        }
        return false;
    }
    
    public void revalidateZone(final boolean force) {
        if (force) {
            this._zoneValidateCounter = 4;
        }
        else {
            --this._zoneValidateCounter;
            if (this._zoneValidateCounter >= 0) {
                return;
            }
            this._zoneValidateCounter = 4;
        }
        final ZoneRegion region = ZoneManager.getInstance().getRegion(this);
        if (region != null) {
            region.revalidateZones(this);
        }
        else {
            World.getInstance().disposeOutOfBoundsObject(this);
        }
    }
    
    public void stopMove(final Location loc) {
        this._move = null;
        this._cursorKeyMovement = false;
        if (loc != null) {
            this.setXYZ(loc.getX(), loc.getY(), loc.getZ());
            this.setHeading(loc.getHeading());
            this.revalidateZone(true);
        }
        this.broadcastPacket(new StopMove(this));
    }
    
    public boolean isShowSummonAnimation() {
        return this._showSummonAnimation;
    }
    
    public void setShowSummonAnimation(final boolean showSummonAnimation) {
        this._showSummonAnimation = showSummonAnimation;
    }
    
    public final int getTargetId() {
        if (this._target != null) {
            return this._target.getObjectId();
        }
        return 0;
    }
    
    public final WorldObject getTarget() {
        return this._target;
    }
    
    public void setTarget(WorldObject object) {
        if (object != null && !object.isSpawned()) {
            object = null;
        }
        this._target = object;
    }
    
    public void moveToLocation(int x, int y, int z, int offset) {
        final double speed = this.stats.getMoveSpeed();
        if (speed <= 0.0 || this.isMovementDisabled()) {
            return;
        }
        final int curX = this.getX();
        final int curY = this.getY();
        final int curZ = this.getZ();
        double dx = x - curX;
        double dy = y - curY;
        double dz = z - curZ;
        double distance = Math.hypot(dx, dy);
        if (!this._cursorKeyMovementActive && distance > 200.0) {
            return;
        }
        final boolean verticalMovementOnly = this._isFlying && distance == 0.0 && dz != 0.0;
        if (verticalMovementOnly) {
            distance = Math.abs(dz);
        }
        final boolean isInWater = this.isInsideZone(ZoneType.WATER);
        if (isInWater && distance > 700.0) {
            final double divider = 700.0 / distance;
            x = curX + (int)(divider * dx);
            y = curY + (int)(divider * dy);
            z = curZ + (int)(divider * dz);
            dx = x - curX;
            dy = y - curY;
            dz = z - curZ;
            distance = Math.hypot(dx, dy);
        }
        double sin;
        double cos;
        if (offset > 0 || distance < 1.0) {
            offset -= (int)Math.abs(dz);
            if (offset < 5) {
                offset = 5;
            }
            if (distance < 1.0 || distance - offset <= 0.0) {
                this.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED);
                return;
            }
            sin = dy / distance;
            cos = dx / distance;
            distance -= offset - 5;
            x = curX + (int)(distance * cos);
            y = curY + (int)(distance * sin);
        }
        else {
            sin = dy / distance;
            cos = dx / distance;
        }
        final MoveData m = new MoveData();
        m.onGeodataPathIndex = -1;
        m.disregardingGeodata = false;
        if (!this._isFlying && !isInWater && !this.isVehicle() && !this._cursorKeyMovement) {
            final boolean isInVehicle = GameUtils.isPlayer(this) && this.getActingPlayer().getVehicle() != null;
            if (isInVehicle) {
                m.disregardingGeodata = true;
            }
            final GeoEngineSettings geoSettings = (GeoEngineSettings)Configurator.getSettings((Class)GeoEngineSettings.class);
            if (geoSettings.isEnabledPathFinding() && !(this instanceof FriendlyNpc)) {
                final double originalDistance = distance;
                final int originalX = x;
                final int originalY = y;
                final int originalZ = z;
                final int gtx = originalX + 294912 >> 4;
                final int gty = originalY + 262144 >> 4;
                if (this.isOnGeodataPath()) {
                    try {
                        if (gtx == this._move.geoPathGtx && gty == this._move.geoPathGty) {
                            return;
                        }
                        this._move.onGeodataPathIndex = -1;
                    }
                    catch (NullPointerException ex) {}
                }
                if (!isInVehicle && (!GameUtils.isPlayer(this) || distance <= 3000.0) && (!GameUtils.isMonster(this) || Math.abs(dz) <= 100.0) && (curZ - z <= 300 || distance >= 300.0)) {
                    final Location destiny = GeoEngine.getInstance().canMoveToTargetLoc(curX, curY, curZ, x, y, z, this.getInstanceWorld());
                    x = destiny.getX();
                    y = destiny.getY();
                    dx = x - curX;
                    dy = y - curY;
                    dz = z - curZ;
                    distance = (verticalMovementOnly ? Math.pow(dz, 2.0) : Math.hypot(dx, dy));
                }
                if (originalDistance - distance > 30.0 && !this.isControlBlocked() && !isInVehicle) {
                    m.geoPath = GeoEngine.getInstance().findPath(curX, curY, curZ, originalX, originalY, originalZ, this.getInstanceWorld());
                    if (m.geoPath == null || m.geoPath.size() < 2) {
                        m.disregardingGeodata = true;
                        final Location newDestination = GeoEngine.getInstance().canMoveToTargetLoc(curX, curY, curZ, originalX, originalY, originalZ, this.getInstanceWorld());
                        x = newDestination.getX();
                        y = newDestination.getY();
                        z = newDestination.getZ();
                    }
                    else {
                        m.onGeodataPathIndex = 0;
                        m.geoPathGtx = gtx;
                        m.geoPathGty = gty;
                        m.geoPathAccurateTx = originalX;
                        m.geoPathAccurateTy = originalY;
                        x = m.geoPath.get(m.onGeodataPathIndex).getX();
                        y = m.geoPath.get(m.onGeodataPathIndex).getY();
                        z = m.geoPath.get(m.onGeodataPathIndex).getZ();
                        dx = x - curX;
                        dy = y - curY;
                        dz = z - curZ;
                        distance = (verticalMovementOnly ? Math.pow(dz, 2.0) : Math.hypot(dx, dy));
                        sin = dy / distance;
                        cos = dx / distance;
                    }
                }
            }
            if (distance < 1.0 && (geoSettings.isEnabledPathFinding() || GameUtils.isPlayable(this))) {
                if (GameUtils.isSummon(this)) {
                    if (this.getAI().getTarget() != this.getActingPlayer()) {
                        ((Summon)this).setFollowStatus(false);
                        this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                    }
                }
                else {
                    this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                }
                return;
            }
        }
        if ((this._isFlying || isInWater) && !verticalMovementOnly) {
            distance = Math.hypot(distance, dz);
        }
        final int ticksToMove = 1 + (int)(10.0 * distance / speed);
        m._xDestination = x;
        m._yDestination = y;
        m._zDestination = z;
        m._heading = 0;
        if (!verticalMovementOnly) {
            this.setHeading(MathUtil.calculateHeadingFrom(cos, sin));
        }
        m._moveStartTime = WorldTimeController.getInstance().getGameTicks();
        this._move = m;
        WorldTimeController.getInstance().registerMovingObject(this);
        if (ticksToMove * 100 > 3000) {
            ThreadPool.schedule((Runnable)new NotifyAITask(this, CtrlEvent.EVT_ARRIVED_REVALIDATE), 2000L);
        }
    }
    
    public boolean moveToNextRoutePoint() {
        if (!this.isOnGeodataPath()) {
            this._move = null;
            return false;
        }
        final double speed = this.stats.getMoveSpeed();
        if (speed <= 0.0 || this.isMovementDisabled()) {
            this._move = null;
            return false;
        }
        final MoveData md = this._move;
        if (md == null) {
            return false;
        }
        final int curX = this.getX();
        final int curY = this.getY();
        final MoveData m = new MoveData();
        m.onGeodataPathIndex = md.onGeodataPathIndex + 1;
        m.geoPath = md.geoPath;
        m.geoPathGtx = md.geoPathGtx;
        m.geoPathGty = md.geoPathGty;
        m.geoPathAccurateTx = md.geoPathAccurateTx;
        m.geoPathAccurateTy = md.geoPathAccurateTy;
        if (md.onGeodataPathIndex == md.geoPath.size() - 2) {
            m._xDestination = md.geoPathAccurateTx;
            m._yDestination = md.geoPathAccurateTy;
            m._zDestination = md.geoPath.get(m.onGeodataPathIndex).getZ();
        }
        else {
            m._xDestination = md.geoPath.get(m.onGeodataPathIndex).getX();
            m._yDestination = md.geoPath.get(m.onGeodataPathIndex).getY();
            m._zDestination = md.geoPath.get(m.onGeodataPathIndex).getZ();
        }
        final double distance = Math.hypot(m._xDestination - curX, m._yDestination - curY);
        if (distance != 0.0) {
            this.setHeading(MathUtil.calculateHeadingFrom(curX, curY, m._xDestination, m._yDestination));
        }
        final int ticksToMove = 1 + (int)(10.0 * distance / speed);
        m._heading = 0;
        m._moveStartTime = WorldTimeController.getInstance().getGameTicks();
        this._move = m;
        WorldTimeController.getInstance().registerMovingObject(this);
        if (ticksToMove * 100 > 3000) {
            ThreadPool.schedule((Runnable)new NotifyAITask(this, CtrlEvent.EVT_ARRIVED_REVALIDATE), 2000L);
        }
        this.broadcastPacket(new MoveToLocation(this));
        return true;
    }
    
    public boolean validateMovementHeading(final int heading) {
        final MoveData m = this._move;
        if (m == null) {
            return true;
        }
        boolean result = true;
        if (m._heading != heading) {
            result = (m._heading == 0);
            m._heading = heading;
        }
        return result;
    }
    
    public void addExpAndSp(final double addToExp, final double addToSp) {
    }
    
    public abstract Item getActiveWeaponInstance();
    
    public abstract Weapon getActiveWeaponItem();
    
    public abstract Item getSecondaryWeaponInstance();
    
    public abstract ItemTemplate getSecondaryWeaponItem();
    
    public void onHitTimeNotDual(final Weapon weapon, final Attack attack, final int hitTime, final int attackTime) {
        if (this._isDead) {
            this.getAI().notifyEvent(CtrlEvent.EVT_CANCEL);
            return;
        }
        for (final Hit hit : attack.getHits()) {
            final Creature target = (Creature)hit.getTarget();
            if (target != null && !target.isDead()) {
                if (!this.isInSurroundingRegion(target)) {
                    continue;
                }
                if (hit.isMiss()) {
                    this.notifyAttackAvoid(target, false);
                }
                else {
                    this.onHitTarget(target, weapon, hit);
                }
            }
        }
        this._hitTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> this.onAttackFinish(attack), (long)(attackTime - hitTime));
    }
    
    public void onFirstHitTimeForDual(final Weapon weapon, final Attack attack, final int hitTime, final int attackTime, final int delayForSecondAttack) {
        if (this._isDead) {
            this.getAI().notifyEvent(CtrlEvent.EVT_CANCEL);
            return;
        }
        this._hitTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> this.onSecondHitTimeForDual(weapon, attack, hitTime, delayForSecondAttack, attackTime), (long)delayForSecondAttack);
        final Hit hit = attack.getHits().get(0);
        final Creature target = (Creature)hit.getTarget();
        if (target == null || target.isDead() || !this.isInSurroundingRegion(target)) {
            this.getAI().notifyEvent(CtrlEvent.EVT_CANCEL);
            return;
        }
        if (hit.isMiss()) {
            this.notifyAttackAvoid(target, false);
        }
        else {
            this.onHitTarget(target, weapon, hit);
        }
    }
    
    public void onSecondHitTimeForDual(final Weapon weapon, final Attack attack, final int hitTime1, final int hitTime2, final int attackTime) {
        if (this._isDead) {
            this.getAI().notifyEvent(CtrlEvent.EVT_CANCEL);
            return;
        }
        for (int i = 1; i < attack.getHits().size(); ++i) {
            final Hit hit = attack.getHits().get(i);
            final Creature target = (Creature)hit.getTarget();
            if (target != null && !target.isDead()) {
                if (this.isInSurroundingRegion(target)) {
                    if (hit.isMiss()) {
                        this.notifyAttackAvoid(target, false);
                    }
                    else {
                        this.onHitTarget(target, weapon, hit);
                    }
                }
            }
        }
        this._hitTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> this.onAttackFinish(attack), (long)(attackTime - (hitTime1 + hitTime2)));
    }
    
    public void onHitTarget(final Creature target, final Weapon weapon, final Hit hit) {
        this.doAttack(hit.getDamage(), target, null, false, false, hit.isCritical(), false);
        EventDispatcher.getInstance().notifyEvent(new OnCreatureAttack(this, target, null), this);
        EventDispatcher.getInstance().notifyEvent(new OnCreatureAttacked(this, target, null), target);
        if (this._triggerSkills != null) {
            for (final OptionsSkillHolder holder : this._triggerSkills.values()) {
                if (((!hit.isCritical() && holder.getSkillType() == OptionsSkillType.ATTACK) || (holder.getSkillType() == OptionsSkillType.CRITICAL && hit.isCritical())) && Rnd.get(100) < holder.getChance()) {
                    SkillCaster.triggerCast(this, target, holder.getSkill(), null, false);
                }
            }
        }
        if (hit.isCritical() && weapon != null) {
            weapon.applyConditionalSkills(this, target, null, ItemSkillType.ON_CRITICAL_SKILL);
        }
    }
    
    private void onAttackFinish(final Attack attack) {
        if (attack.getHits().stream().anyMatch(h -> !h.isMiss())) {
            this.consumeAndRechargeShots(ShotType.SOULSHOTS, attack.getHitsWithSoulshotCount());
        }
        this.getAI().notifyEvent(CtrlEvent.EVT_READY_TO_ACT);
    }
    
    public void breakAttack() {
        if (this.isAttackingNow()) {
            this.abortAttack();
            if (GameUtils.isPlayer(this)) {
                this.sendPacket(SystemMessageId.YOUR_ATTACK_HAS_FAILED);
            }
        }
    }
    
    public void breakCast() {
        final SkillCaster skillCaster = this.getSkillCaster(SkillCaster::isAnyNormalType, (Predicate<SkillCaster>[])new Predicate[0]);
        if (skillCaster != null && skillCaster.getSkill().isMagic()) {
            skillCaster.stopCasting(true);
            if (GameUtils.isPlayer(this)) {
                this.sendPacket(SystemMessageId.YOUR_CASTING_HAS_BEEN_INTERRUPTED);
            }
        }
    }
    
    @Override
    public void onForcedAttack(final Player player) {
        if (this.isInsidePeaceZone(player)) {
            player.sendPacket(SystemMessageId.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (player.isInOlympiadMode() && GameUtils.isPlayable(player.getTarget())) {
            final Player target = player.getTarget().getActingPlayer();
            if (target.isInOlympiadMode() && (!player.isOlympiadStart() || player.getOlympiadGameId() != target.getOlympiadGameId())) {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        }
        if (player.getTarget() != null && !player.getTarget().canBeAttacked() && !player.getAccessLevel().allowPeaceAttack()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (player.isConfused()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (!GeoEngine.getInstance().canSeeTarget(player, this)) {
            player.sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (player.getBlockCheckerArena() != -1) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
    }
    
    public boolean isInsidePeaceZone(final WorldObject attacker) {
        return this.isInsidePeaceZone(attacker, this);
    }
    
    public boolean isInsidePeaceZone(final WorldObject attacker, final WorldObject target) {
        final Instance instanceWorld = this.getInstanceWorld();
        if (target == null || !GameUtils.isPlayable(target) || !GameUtils.isPlayable(attacker) || (instanceWorld != null && instanceWorld.isPvP())) {
            return false;
        }
        if (Config.ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE) {
            if (target.getActingPlayer() != null && target.getActingPlayer().getReputation() < 0) {
                return false;
            }
            if (attacker.getActingPlayer() != null && attacker.getActingPlayer().getReputation() < 0 && target.getActingPlayer() != null && target.getActingPlayer().getPvpFlag() > 0) {
                return false;
            }
        }
        return (attacker.getActingPlayer() == null || !attacker.getActingPlayer().getAccessLevel().allowPeaceAttack()) && (target.isInsideZone(ZoneType.PEACE) || attacker.isInsideZone(ZoneType.PEACE));
    }
    
    public boolean isInActiveRegion() {
        final WorldRegion region = this.getWorldRegion();
        return region != null && region.isActive();
    }
    
    public boolean isInParty() {
        return false;
    }
    
    public Party getParty() {
        return null;
    }
    
    @Override
    public Skill addSkill(final Skill newSkill) {
        Skill oldSkill = null;
        if (Objects.nonNull(newSkill)) {
            oldSkill = this._skills.get(newSkill.getId());
            if (Objects.nonNull(oldSkill) && newSkill.getLevel() < oldSkill.getLevel()) {
                return oldSkill;
            }
            this._skills.put(newSkill.getId(), newSkill);
            if (Objects.nonNull(oldSkill) && oldSkill.isPassive()) {
                this._effectList.stopSkillEffects(true, oldSkill);
            }
            if (newSkill.isPassive()) {
                newSkill.applyEffects(this, this, false, true, false, 0, null);
                this.stats.recalculateStats(true);
            }
        }
        return oldSkill;
    }
    
    public Skill removeSkill(final Skill skill, final boolean cancelEffect) {
        return (skill != null) ? this.removeSkill(skill.getId(), cancelEffect) : null;
    }
    
    public Skill removeSkill(final int skillId) {
        return this.removeSkill(skillId, true);
    }
    
    public Skill removeSkill(final int skillId, final boolean cancelEffect) {
        final Skill oldSkill = this._skills.remove(skillId);
        if (oldSkill != null) {
            this.abortCast(s -> s.getSkill().getId() == skillId);
            if (cancelEffect || oldSkill.isToggle() || oldSkill.isPassive()) {
                this.stopSkillEffects(true, oldSkill.getId());
                this.stats.recalculateStats(true);
            }
        }
        return oldSkill;
    }
    
    public final Collection<Skill> getAllSkills() {
        return this._skills.values();
    }
    
    @Override
    public Map<Integer, Skill> getSkills() {
        return this._skills;
    }
    
    @Override
    public int getSkillLevel(final int skillId) {
        return Util.zeroIfNullOrElse((Object)this.getKnownSkill(skillId), Skill::getLevel);
    }
    
    @Override
    public Skill getKnownSkill(final int skillId) {
        return this._skills.get(skillId);
    }
    
    public int getBuffCount() {
        return this._effectList.getBuffCount();
    }
    
    public int getDanceCount() {
        return this._effectList.getDanceCount();
    }
    
    public void notifyQuestEventSkillFinished(final Skill skill, final WorldObject target) {
    }
    
    public double getLevelMod() {
        final double defaultLevelMod = (this.getLevel() + 89) / 100.0;
        return this._transform.filter(transform -> !transform.isStance()).map(transform -> transform.getLevelMod(this)).orElse(defaultLevelMod);
    }
    
    public byte getPvpFlag() {
        return 0;
    }
    
    public void updatePvPFlag(final int value) {
    }
    
    public final double getRandomDamageMultiplier() {
        final int random = (int)this.stats.getValue(Stat.RANDOM_DAMAGE);
        return 1.0 + Rnd.get(-random, random) / 100.0;
    }
    
    public final long getAttackEndTime() {
        return this._attackEndTime;
    }
    
    protected long getRangedAttackEndTime() {
        return this.disableRangedAttackEndTime;
    }
    
    public abstract int getLevel();
    
    public int getAccuracy() {
        return this.stats.getAccuracy();
    }
    
    public int getMagicAccuracy() {
        return this.stats.getMagicAccuracy();
    }
    
    public int getMagicEvasionRate() {
        return this.stats.getMagicEvasionRate();
    }
    
    public final double getAttackSpeedMultiplier() {
        return this.stats.getAttackSpeedMultiplier();
    }
    
    public final double getCriticalDmg(final int init) {
        return this.stats.getCriticalDmg(init);
    }
    
    public int getCriticalHit() {
        return this.stats.getCriticalHit();
    }
    
    public int getEvasionRate() {
        return this.stats.getEvasionRate();
    }
    
    public final int getMagicalAttackRange(final Skill skill) {
        return this.stats.getMagicalAttackRange(skill);
    }
    
    public final int getMaxCp() {
        return this.stats.getMaxCp();
    }
    
    public final int getMaxRecoverableCp() {
        return this.stats.getMaxRecoverableCp();
    }
    
    public int getMAtk() {
        return this.stats.getMAtk();
    }
    
    public int getMAtkSpd() {
        return this.stats.getMAtkSpd();
    }
    
    public int getMaxMp() {
        return this.stats.getMaxMp();
    }
    
    public int getMaxRecoverableMp() {
        return this.stats.getMaxRecoverableMp();
    }
    
    public int getMaxHp() {
        return this.stats.getMaxHp();
    }
    
    public int getMaxRecoverableHp() {
        return this.stats.getMaxRecoverableHp();
    }
    
    public final int getMCriticalHit() {
        return this.stats.getMCriticalHit();
    }
    
    public int getMDef() {
        return this.stats.getMDef();
    }
    
    public int getPAtk() {
        return this.stats.getPAtk();
    }
    
    public int getPAtkSpd() {
        return this.stats.getPAtkSpd();
    }
    
    public int getPDef() {
        return this.stats.getPDef();
    }
    
    public final int getPhysicalAttackRange() {
        return this.stats.getPhysicalAttackRange();
    }
    
    public double getMovementSpeedMultiplier() {
        return this.stats.getMovementSpeedMultiplier();
    }
    
    public double getRunSpeed() {
        return this.stats.getRunSpeed();
    }
    
    public double getWalkSpeed() {
        return this.stats.getWalkSpeed();
    }
    
    public final double getSwimRunSpeed() {
        return this.stats.getSwimRunSpeed();
    }
    
    public final double getSwimWalkSpeed() {
        return this.stats.getSwimWalkSpeed();
    }
    
    public double getMoveSpeed() {
        return this.stats.getMoveSpeed();
    }
    
    public final int getShldDef() {
        return this.stats.getShldDef();
    }
    
    public int getSTR() {
        return this.stats.getSTR();
    }
    
    public int getDEX() {
        return this.stats.getDEX();
    }
    
    public int getCON() {
        return this.stats.getCON();
    }
    
    public int getINT() {
        return this.stats.getINT();
    }
    
    public int getWIT() {
        return this.stats.getWIT();
    }
    
    public int getMEN() {
        return this.stats.getMEN();
    }
    
    public void addStatusListener(final Creature object) {
        this._status.addStatusListener(object);
    }
    
    public void doAttack(double damage, final Creature target, final Skill skill, final boolean isDOT, final boolean directlyToHp, final boolean critical, final boolean reflect) {
        if (target.hasAI()) {
            target.getAI().clientStartAutoAttack();
            target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, this);
        }
        this.getAI().clientStartAutoAttack();
        if (!reflect && !isDOT) {
            if (this.isBehind(target)) {
                damage *= this.stats.getValue(Stat.REAR_DAMAGE_RATE, 1.0);
            }
            if (!target.isDead() && skill != null) {
                Formulas.calcCounterAttack(this, target, skill, true);
                if (skill.isMagic() && target.getStats().getValue(Stat.VENGEANCE_SKILL_MAGIC_DAMAGE, 0.0) > Rnd.get(100)) {
                    this.reduceCurrentHp(damage, target, skill, isDOT, directlyToHp, critical, true, DamageInfo.DamageType.REFLECT);
                    return;
                }
            }
        }
        if (target.isImmobilized()) {
            damage *= this.stats.getValue(Stat.DAMAGE_IMMOBILIZED, 1.0);
            damage *= target.stats.getValue(Stat.DAMAGE_TAKEN_IMMOBILIZED, 1.0);
        }
        target.reduceCurrentHp(damage, this, skill, isDOT, directlyToHp, critical, reflect, DamageInfo.DamageType.ATTACK);
        if (!reflect && !isDOT && !target.isDead()) {
            int reflectedDamage = 0;
            final double reflectPercent = target.getStats().getValue(Stat.REFLECT_DAMAGE_PERCENT, 0.0) - this.getStats().getValue(Stat.REFLECT_DAMAGE_PERCENT_DEFENSE, 0.0);
            if (reflectPercent > 0.0) {
                reflectedDamage = (int)(reflectPercent / 100.0 * damage);
                reflectedDamage = Math.min(reflectedDamage, target.getMaxHp());
                if (skill != null && skill.isMagic()) {
                    reflectedDamage = (int)Math.min(reflectedDamage, target.getStats().getMDef() * 1.5);
                }
                else {
                    reflectedDamage = Math.min(reflectedDamage, target.getStats().getPDef());
                }
            }
            if (skill == null) {
                final double absorbPercent = this.getStats().getValue(Stat.ABSORB_DAMAGE_PERCENT, 0.0) * target.getStats().getValue(Stat.ABSORB_DAMAGE_DEFENCE, 1.0);
                if (absorbPercent > 0.0 && Rnd.nextDouble() < this.stats.getValue(Stat.ABSORB_DAMAGE_CHANCE)) {
                    int absorbDamage = (int)Math.min(absorbPercent * damage, this.stats.getMaxRecoverableHp() - this._status.getCurrentHp());
                    absorbDamage = Math.min(absorbDamage, (int)target.getCurrentHp());
                    if (absorbDamage > 0) {
                        this.setCurrentHp(this._status.getCurrentHp() + absorbDamage);
                    }
                }
            }
            if (skill != null && Rnd.get(10) < 3) {
                final double absorbPercent = this.stats.getValue(Stat.ABSORB_MANA_DAMAGE_PERCENT, 0.0);
                if (absorbPercent > 0.0) {
                    int absorbDamage = (int)Math.min(absorbPercent / 100.0 * damage, this.stats.getMaxRecoverableMp() - this._status.getCurrentMp());
                    absorbDamage = Math.min(absorbDamage, (int)target.getCurrentMp());
                    if (absorbDamage > 0) {
                        this.setCurrentMp(this._status.getCurrentMp() + absorbDamage);
                    }
                }
            }
            if (reflectedDamage > 0) {
                target.doAttack(reflectedDamage, this, skill, isDOT, directlyToHp, critical, true);
            }
        }
        if (!target.isRaid() && Formulas.calcAtkBreak(target, damage)) {
            target.breakAttack();
            target.breakCast();
        }
    }
    
    public void reduceCurrentHp(final double value, final Creature attacker, final Skill skill, final DamageInfo.DamageType damageType) {
        this.reduceCurrentHp(value, attacker, skill, false, false, false, false, damageType);
    }
    
    public void reduceCurrentHp(double value, final Creature attacker, final Skill skill, final boolean isDOT, final boolean directlyToHp, final boolean critical, final boolean reflect, final DamageInfo.DamageType damageType) {
        final DamageReturn damageReturn = EventDispatcher.getInstance().notifyEvent(new OnCreatureDamageReceived(attacker, this, value, skill, critical, isDOT, reflect), this, DamageReturn.class);
        if (damageReturn != null) {
            if (damageReturn.terminate()) {
                return;
            }
            if (damageReturn.override()) {
                value = damageReturn.getDamage();
            }
        }
        double elementalDamage = 0.0;
        if (Objects.nonNull(attacker)) {
            if (GameUtils.isPlayable(attacker) && GameUtils.isPlayable(this)) {
                value *= (100.0 + Math.max(this.stats.getValue(Stat.PVP_DAMAGE_TAKEN), -80.0)) / 100.0;
            }
            else if (attacker.isRaid() || attacker.isRaidMinion()) {
                value *= (100.0 + Math.max(this.stats.getValue(Stat.PVE_DAMAGE_TAKEN_RAID), -80.0)) / 100.0;
                value *= (100.0 + Math.max(this.stats.getValue(Stat.PVE_DAMAGE_TAKEN), -80.0)) / 100.0;
            }
            else if (GameUtils.isMonster(attacker)) {
                value *= (100.0 + Math.max(this.stats.getValue(Stat.PVE_DAMAGE_TAKEN_MONSTER), -80.0)) / 100.0;
                value *= (100.0 + Math.max(this.stats.getValue(Stat.PVE_DAMAGE_TAKEN), -80.0)) / 100.0;
            }
            value *= (100.0 + Math.max(this.stats.getValue(Stat.DAMAGE_TAKEN), -80.0)) / 100.0;
            if (!reflect && !isDOT) {
                elementalDamage = Formulas.calcSpiritElementalDamage(attacker, this, value);
            }
            value += elementalDamage;
        }
        if (Config.CHAMPION_ENABLE && this.isChampion() && Config.CHAMPION_HP > 0) {
            value /= Config.CHAMPION_HP;
        }
        final double damageCap = this.stats.getValue(Stat.DAMAGE_LIMIT);
        if (damageCap > 0.0) {
            value = Math.min(value, damageCap);
        }
        value = Math.max(0.0, value);
        this.onReceiveDamage(attacker, skill, value, damageType);
        if (GameUtils.isPlayer(this)) {
            this.getActingPlayer().getStatus().reduceHp(value, attacker, Objects.isNull(skill) || !skill.isToggle(), isDOT, false, directlyToHp);
        }
        else {
            this._status.reduceHp(value, attacker, Objects.isNull(skill) || !skill.isToggle(), isDOT, false);
        }
        if (Objects.nonNull(attacker)) {
            attacker.sendDamageMessage(this, skill, (int)value, elementalDamage, critical, false);
            EventDispatcher.getInstance().notifyEventAsync(new OnCreatureDamageDealt(attacker, this, value, skill, critical, isDOT, reflect), attacker);
        }
    }
    
    protected void onReceiveDamage(final Creature attacker, final Skill skill, final double value, final DamageInfo.DamageType damageType) {
    }
    
    public void reduceCurrentMp(final double i) {
        this._status.reduceMp(i);
    }
    
    @Override
    public void removeStatusListener(final Creature object) {
        this._status.removeStatusListener(object);
    }
    
    public void stopHpMpRegeneration() {
        this._status.stopHpMpRegeneration();
    }
    
    public final double getCurrentCp() {
        return this._status.getCurrentCp();
    }
    
    public final void setCurrentCp(final double newCp) {
        this._status.setCurrentCp(newCp);
    }
    
    public final int getCurrentCpPercent() {
        return (int)(this._status.getCurrentCp() * 100.0 / this.stats.getMaxCp());
    }
    
    public final void setCurrentCp(final double newCp, final boolean broadcast) {
        this._status.setCurrentCp(newCp, broadcast);
    }
    
    public final double getCurrentHp() {
        return this._status.getCurrentHp();
    }
    
    public final void setCurrentHp(final double newHp) {
        this._status.setCurrentHp(newHp);
    }
    
    public final int getCurrentHpPercent() {
        return (int)(this._status.getCurrentHp() * 100.0 / this.stats.getMaxHp());
    }
    
    public final void setCurrentHp(final double newHp, final boolean broadcast) {
        this._status.setCurrentHp(newHp, broadcast);
    }
    
    public final void setCurrentHpMp(final double newHp, final double newMp) {
        this._status.setCurrentHpMp(newHp, newMp);
    }
    
    public final double getCurrentMp() {
        return this._status.getCurrentMp();
    }
    
    public final void setCurrentMp(final double newMp) {
        this._status.setCurrentMp(newMp);
    }
    
    public final int getCurrentMpPercent() {
        return (int)(this._status.getCurrentMp() * 100.0 / this.stats.getMaxMp());
    }
    
    public final void setCurrentMp(final double newMp, final boolean broadcast) {
        this._status.setCurrentMp(newMp, false);
    }
    
    public int getMaxLoad() {
        if (GameUtils.isPlayer(this) || GameUtils.isPet(this)) {
            final double baseLoad = Math.floor(BaseStats.CON.calcBonus(this) * 69000.0 * ((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).weightLimitMultiplier());
            return (int)this.stats.getValue(Stat.WEIGHT_LIMIT, baseLoad);
        }
        return 0;
    }
    
    public int getBonusWeightPenalty() {
        if (GameUtils.isPlayer(this) || GameUtils.isPet(this)) {
            return (int)this.stats.getValue(Stat.WEIGHT_PENALTY, 1.0);
        }
        return 0;
    }
    
    public int getCurrentLoad() {
        if (GameUtils.isPlayer(this) || GameUtils.isPet(this)) {
            return this.getInventory().getTotalWeight();
        }
        return 0;
    }
    
    public boolean isChampion() {
        return false;
    }
    
    public void sendDamageMessage(final Creature target, final Skill skill, final int damage, final double elementalDamage, final boolean crit, final boolean miss) {
    }
    
    public AttributeType getAttackElement() {
        return this.stats.getAttackElement();
    }
    
    public int getAttackElementValue(final AttributeType attackAttribute) {
        return this.stats.getAttackElementValue(attackAttribute);
    }
    
    public int getDefenseElementValue(final AttributeType defenseAttribute) {
        return this.stats.getDefenseElementValue(defenseAttribute);
    }
    
    public final void startPhysicalAttackMuted() {
        this.abortAttack();
    }
    
    public void disableCoreAI(final boolean val) {
        this._AIdisabled = val;
    }
    
    public boolean isCoreAIDisabled() {
        return this._AIdisabled;
    }
    
    public boolean giveRaidCurse() {
        return true;
    }
    
    public boolean isAffected(final EffectFlag flag) {
        return this._effectList.isAffected(flag);
    }
    
    public void broadcastSocialAction(final int id) {
        this.broadcastPacket(new SocialAction(this.getObjectId(), id));
    }
    
    public Team getTeam() {
        return this._team;
    }
    
    public void setTeam(final Team team) {
        this._team = team;
    }
    
    public void addOverrideCond(final PcCondOverride... excs) {
        for (final PcCondOverride exc : excs) {
            this._exceptions |= exc.getMask();
        }
    }
    
    public void removeOverridedCond(final PcCondOverride... excs) {
        for (final PcCondOverride exc : excs) {
            this._exceptions &= ~exc.getMask();
        }
    }
    
    public boolean canOverrideCond(final PcCondOverride excs) {
        return (this._exceptions & (long)excs.getMask()) == excs.getMask();
    }
    
    public long getOverrideCond() {
        return this._exceptions;
    }
    
    public void setOverrideCond(final long masks) {
        this._exceptions = masks;
    }
    
    public boolean isLethalable() {
        return this._lethalable;
    }
    
    public void setLethalable(final boolean val) {
        this._lethalable = val;
    }
    
    public boolean hasTriggerSkills() {
        return this._triggerSkills != null && !this._triggerSkills.isEmpty();
    }
    
    public Map<Integer, OptionsSkillHolder> getTriggerSkills() {
        if (this._triggerSkills == null) {
            synchronized (this) {
                if (this._triggerSkills == null) {
                    this._triggerSkills = new ConcurrentHashMap<Integer, OptionsSkillHolder>();
                }
            }
        }
        return this._triggerSkills;
    }
    
    public void addTriggerSkill(final OptionsSkillHolder holder) {
        this.getTriggerSkills().put(holder.getSkillId(), holder);
    }
    
    public void removeTriggerSkill(final OptionsSkillHolder holder) {
        this.getTriggerSkills().remove(holder.getSkillId());
    }
    
    public boolean canRevive() {
        return true;
    }
    
    public void setCanRevive(final boolean val) {
    }
    
    public boolean isSweepActive() {
        return false;
    }
    
    public boolean isOnEvent() {
        return false;
    }
    
    public int getClanId() {
        return 0;
    }
    
    public Clan getClan() {
        return null;
    }
    
    public boolean isAcademyMember() {
        return false;
    }
    
    public int getPledgeType() {
        return 0;
    }
    
    public int getAllyId() {
        return 0;
    }
    
    public void notifyAttackAvoid(final Creature target, final boolean isDot) {
        EventDispatcher.getInstance().notifyEventAsync(new OnCreatureAttackAvoid(this, target, isDot), target);
    }
    
    public final WeaponType getAttackType() {
        final Weapon weapon = this.getActiveWeaponItem();
        if (weapon != null) {
            return weapon.getItemType();
        }
        final WeaponType defaultWeaponType = this._template.getBaseAttackType();
        return this._transform.map(transform -> transform.getBaseAttackType(this, defaultWeaponType)).orElse(defaultWeaponType);
    }
    
    public final boolean isInCategory(final CategoryType type) {
        return CategoryManager.getInstance().isInCategory(type, this);
    }
    
    public final boolean isInOneOfCategory(final CategoryType... types) {
        for (final CategoryType type : types) {
            if (CategoryManager.getInstance().isInCategory(type, this.getId())) {
                return true;
            }
        }
        return false;
    }
    
    public Creature getSummoner() {
        return this._summoner;
    }
    
    public void setSummoner(final Creature summoner) {
        this._summoner = summoner;
    }
    
    public final void addSummonedNpc(final Npc npc) {
        if (this._summonedNpcs == null) {
            synchronized (this) {
                if (this._summonedNpcs == null) {
                    this._summonedNpcs = new ConcurrentHashMap<Integer, Npc>();
                }
            }
        }
        this._summonedNpcs.put(npc.getObjectId(), npc);
        npc.setSummoner(this);
    }
    
    public final void removeSummonedNpc(final int objectId) {
        if (this._summonedNpcs != null) {
            this._summonedNpcs.remove(objectId);
        }
    }
    
    public final Collection<Npc> getSummonedNpcs() {
        return (Collection<Npc>)((this._summonedNpcs != null) ? this._summonedNpcs.values() : Collections.emptyList());
    }
    
    public final Npc getSummonedNpc(final int objectId) {
        if (this._summonedNpcs != null) {
            return this._summonedNpcs.get(objectId);
        }
        return null;
    }
    
    public final int getSummonedNpcCount() {
        return (this._summonedNpcs != null) ? this._summonedNpcs.size() : 0;
    }
    
    public final void resetSummonedNpcs() {
        if (this._summonedNpcs != null) {
            this._summonedNpcs.clear();
        }
    }
    
    public Collection<SkillCaster> getSkillCasters() {
        return this._skillCasters.values();
    }
    
    public SkillCaster addSkillCaster(final SkillCastingType castingType, final SkillCaster skillCaster) {
        return this._skillCasters.put(castingType, skillCaster);
    }
    
    public SkillCaster removeSkillCaster(final SkillCastingType castingType) {
        return this._skillCasters.remove(castingType);
    }
    
    @SafeVarargs
    public final List<SkillCaster> getSkillCasters(Predicate<SkillCaster> filter, final Predicate<SkillCaster>... filters) {
        for (final Predicate<SkillCaster> additionalFilter : filters) {
            filter = filter.and(additionalFilter);
        }
        return this._skillCasters.values().stream().filter(filter).collect((Collector<? super SkillCaster, ?, List<SkillCaster>>)Collectors.toList());
    }
    
    @SafeVarargs
    public final SkillCaster getSkillCaster(Predicate<SkillCaster> filter, final Predicate<SkillCaster>... filters) {
        for (final Predicate<SkillCaster> additionalFilter : filters) {
            filter = filter.and(additionalFilter);
        }
        return this._skillCasters.values().stream().filter(filter).findAny().orElse(null);
    }
    
    public final boolean isChanneling() {
        return this._channelizer != null && this._channelizer.isChanneling();
    }
    
    public final SkillChannelizer getSkillChannelizer() {
        if (this._channelizer == null) {
            this._channelizer = new SkillChannelizer(this);
        }
        return this._channelizer;
    }
    
    public final boolean isChannelized() {
        return this._channelized != null && !this._channelized.isChannelized();
    }
    
    public final SkillChannelized getSkillChannelized() {
        if (this._channelized == null) {
            this._channelized = new SkillChannelized();
        }
        return this._channelized;
    }
    
    public void addIgnoreSkillEffects(final SkillHolder holder) {
        final IgnoreSkillHolder ignoreSkillHolder = this.getIgnoreSkillEffects().get(holder.getSkillId());
        if (ignoreSkillHolder != null) {
            ignoreSkillHolder.increaseInstances();
            return;
        }
        this.getIgnoreSkillEffects().put(holder.getSkillId(), new IgnoreSkillHolder(holder));
    }
    
    public void removeIgnoreSkillEffects(final SkillHolder holder) {
        final IgnoreSkillHolder ignoreSkillHolder = this.getIgnoreSkillEffects().get(holder.getSkillId());
        if (ignoreSkillHolder != null && ignoreSkillHolder.decreaseInstances() < 1) {
            this.getIgnoreSkillEffects().remove(holder.getSkillId());
        }
    }
    
    public boolean isIgnoringSkillEffects(final int skillId, final int skillLvl) {
        if (this._ignoreSkillEffects != null) {
            final SkillHolder holder = this.getIgnoreSkillEffects().get(skillId);
            return holder != null && (holder.getLevel() < 1 || holder.getLevel() == skillLvl);
        }
        return false;
    }
    
    private Map<Integer, IgnoreSkillHolder> getIgnoreSkillEffects() {
        if (this._ignoreSkillEffects == null) {
            synchronized (this) {
                if (this._ignoreSkillEffects == null) {
                    this._ignoreSkillEffects = new ConcurrentHashMap<Integer, IgnoreSkillHolder>();
                }
            }
        }
        return this._ignoreSkillEffects;
    }
    
    @Override
    public Queue<AbstractEventListener> getListeners(final EventType type) {
        final Queue<AbstractEventListener> objectListenres = super.getListeners(type);
        final Queue<AbstractEventListener> templateListeners = this._template.getListeners(type);
        final Queue<AbstractEventListener> globalListeners = (GameUtils.isNpc(this) && !GameUtils.isMonster(this)) ? Listeners.Npcs().getListeners(type) : (GameUtils.isMonster(this) ? Listeners.Monsters().getListeners(type) : (GameUtils.isPlayer(this) ? Listeners.players().getListeners(type) : EmptyQueue.emptyQueue()));
        if (objectListenres.isEmpty() && templateListeners.isEmpty() && globalListeners.isEmpty()) {
            return (Queue<AbstractEventListener>)EmptyQueue.emptyQueue();
        }
        if (!objectListenres.isEmpty() && templateListeners.isEmpty() && globalListeners.isEmpty()) {
            return objectListenres;
        }
        if (!templateListeners.isEmpty() && objectListenres.isEmpty() && globalListeners.isEmpty()) {
            return templateListeners;
        }
        if (!globalListeners.isEmpty() && objectListenres.isEmpty() && templateListeners.isEmpty()) {
            return globalListeners;
        }
        final Queue<AbstractEventListener> both = new LinkedBlockingDeque<AbstractEventListener>(objectListenres.size() + templateListeners.size() + globalListeners.size());
        both.addAll((Collection<?>)objectListenres);
        both.addAll((Collection<?>)templateListeners);
        both.addAll((Collection<?>)globalListeners);
        return both;
    }
    
    public Race getRace() {
        return this._template.getRace();
    }
    
    @Override
    public final void setXYZ(final int newX, final int newY, final int newZ) {
        final ZoneRegion oldZoneRegion = ZoneManager.getInstance().getRegion(this);
        final ZoneRegion newZoneRegion = ZoneManager.getInstance().getRegion(newX, newY);
        if (newZoneRegion == null) {
            return;
        }
        if (oldZoneRegion != newZoneRegion) {
            oldZoneRegion.removeFromZones(this);
            newZoneRegion.revalidateZones(this);
        }
        super.setXYZ(newX, newY, newZ);
    }
    
    public final Map<Integer, Integer> getKnownRelations() {
        return this._knownRelations;
    }
    
    @Override
    public boolean isTargetable() {
        return super.isTargetable() && !this.isAffected(EffectFlag.UNTARGETABLE);
    }
    
    public boolean isTargetingDisabled() {
        return this.isAffected(EffectFlag.TARGETING_DISABLED);
    }
    
    public boolean cannotEscape() {
        return this.isAffected(EffectFlag.CANNOT_ESCAPE);
    }
    
    public int getAbnormalShieldBlocks() {
        return this.abnormalShieldBlocks.get();
    }
    
    public void setAbnormalShieldBlocks(final int times) {
        this.abnormalShieldBlocks.set(times);
    }
    
    public int decrementAbnormalShieldBlocks() {
        return this.abnormalShieldBlocks.decrementAndGet();
    }
    
    public boolean hasAbnormalType(final AbnormalType abnormalType) {
        return this._effectList.hasAbnormalType(abnormalType);
    }
    
    public void addBlockActionsAllowedSkill(final int skillId) {
        this._blockActionsAllowedSkills.computeIfAbsent(skillId, k -> new AtomicInteger()).incrementAndGet();
    }
    
    public void removeBlockActionsAllowedSkill(final int skillId) {
        this._blockActionsAllowedSkills.computeIfPresent(skillId, (k, v) -> (v.decrementAndGet() != 0) ? v : null);
    }
    
    public boolean isBlockedActionsAllowedSkill(final Skill skill) {
        return this._blockActionsAllowedSkills.containsKey(skill.getId());
    }
    
    public void initSeenCreatures(final int range) {
        this.initSeenCreatures(range, null);
    }
    
    public void initSeenCreatures(final int range, final Predicate<Creature> condition) {
        if (this._seenCreatures == null) {
            synchronized (this) {
                if (this._seenCreatures == null) {
                    this._seenCreatures = new CreatureContainer(this, range, condition);
                }
            }
        }
    }
    
    public CreatureContainer getSeenCreatures() {
        return this._seenCreatures;
    }
    
    public MoveType getMoveType() {
        if (this.isMoving() && this.running) {
            return MoveType.RUNNING;
        }
        if (this.isMoving() && !this.running) {
            return MoveType.WALKING;
        }
        return MoveType.STANDING;
    }
    
    protected final void computeStatusUpdate(final StatusUpdate su, final StatusUpdateType type) {
        final int newValue = type.getValue(this);
        final int n;
        this._statusUpdates.compute(type, (key, oldValue) -> {
            if (oldValue == null || oldValue != n) {
                su.addUpdate(type, n);
                return Integer.valueOf(n);
            }
            else {
                return oldValue;
            }
        });
    }
    
    protected final void addStatusUpdateValue(final StatusUpdateType type) {
        this._statusUpdates.put(type, type.getValue(this));
    }
    
    protected void initStatusUpdateCache() {
        this.addStatusUpdateValue(StatusUpdateType.MAX_HP);
        this.addStatusUpdateValue(StatusUpdateType.MAX_MP);
        this.addStatusUpdateValue(StatusUpdateType.CUR_HP);
        this.addStatusUpdateValue(StatusUpdateType.CUR_MP);
    }
    
    public boolean hasBasicPropertyResist() {
        return true;
    }
    
    public BasicPropertyResist getBasicPropertyResist(final BasicProperty basicProperty) {
        if (this._basicPropertyResists == null) {
            synchronized (this) {
                if (this._basicPropertyResists == null) {
                    this._basicPropertyResists = new ConcurrentHashMap<BasicProperty, BasicPropertyResist>();
                }
            }
        }
        return this._basicPropertyResists.computeIfAbsent(basicProperty, k -> new BasicPropertyResist());
    }
    
    public int getReputation() {
        return this._reputation;
    }
    
    public void setReputation(final int reputation) {
        this._reputation = reputation;
    }
    
    public boolean isChargedShot(final ShotType type) {
        return this.chargedShots.containsKey(type);
    }
    
    public void chargeShot(final ShotType type, final double bonus) {
        this.chargedShots.put(type, bonus);
    }
    
    public double chargedShotBonus(final ShotType type) {
        return this.chargedShots.getOrDefault(type, 1.0);
    }
    
    public void unchargeShot(final ShotType type) {
        this.chargedShots.remove(type);
    }
    
    public void unchargeAllShots() {
        this.chargedShots.clear();
    }
    
    public void consumeAndRechargeShots(final ShotType shotType, final int targets) {
    }
    
    public void setCursorKeyMovement(final boolean value) {
        this._cursorKeyMovement = value;
    }
    
    public boolean isCursorKeyMovementActive() {
        return this._cursorKeyMovementActive;
    }
    
    public void setCursorKeyMovementActive(final boolean value) {
        this._cursorKeyMovementActive = value;
    }
    
    public double getElementalSpiritDefenseOf(final ElementalType type) {
        return (this.getElementalSpiritType() == type) ? 100.0 : 0.0;
    }
    
    public ElementalType getElementalSpiritType() {
        return ElementalType.NONE;
    }
    
    public void addBuffInfoTime(final BuffInfo info) {
        if (this._buffFinishTask == null) {
            this._buffFinishTask = new BuffFinishTask();
        }
        this._buffFinishTask.addBuffInfo(info);
    }
    
    public void removeBuffInfoTime(final BuffInfo info) {
        if (this._buffFinishTask != null) {
            this._buffFinishTask.removeBuffInfo(info);
        }
    }
    
    public void cancelBuffFinishTask() {
        if (this._buffFinishTask != null) {
            final ScheduledFuture<?> task = this._buffFinishTask.getTask();
            if (task != null && !task.isCancelled() && !task.isDone()) {
                task.cancel(true);
            }
            this._buffFinishTask = null;
        }
    }
    
    public int getBuffRemainTimeBySkillOrAbormalType(final Skill skill) {
        return this._effectList.remainTimeBySkillIdOrAbnormalType(skill.getId(), skill.getAbnormalType());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(Creature.class.getName());
    }
    
    public static class MoveData
    {
        public int _moveStartTime;
        public int _moveTimestamp;
        public int _xDestination;
        public int _yDestination;
        public int _zDestination;
        public double _xAccurate;
        public double _yAccurate;
        public double _zAccurate;
        public int _heading;
        public boolean disregardingGeodata;
        public int onGeodataPathIndex;
        public List<Location> geoPath;
        public int geoPathAccurateTx;
        public int geoPathAccurateTy;
        public int geoPathGtx;
        public int geoPathGty;
    }
}
