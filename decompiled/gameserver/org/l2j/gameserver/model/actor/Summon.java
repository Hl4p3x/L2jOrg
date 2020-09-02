// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor;

import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.actor.stat.PlayableStats;
import org.l2j.gameserver.model.actor.status.PlayableStatus;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.network.serverpackets.TeleportToLocation;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.network.serverpackets.ExPartyPetWindowUpdate;
import org.l2j.gameserver.network.serverpackets.PetStatusUpdate;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.container.PetInventory;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.world.zone.ZoneRegion;
import java.util.Objects;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.sql.impl.PlayerSummonTable;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.network.serverpackets.ExPartyPetWindowDelete;
import org.l2j.gameserver.network.serverpackets.PetDelete;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.model.AggroInfo;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.network.serverpackets.AbstractMaskPacket;
import org.l2j.gameserver.enums.NpcInfoType;
import org.l2j.gameserver.network.serverpackets.SummonInfo;
import org.l2j.gameserver.network.serverpackets.ExPetInfo;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.serverpackets.PetInfo;
import org.l2j.gameserver.ai.SummonAI;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.model.actor.status.CreatureStatus;
import org.l2j.gameserver.model.actor.status.SummonStatus;
import org.l2j.gameserver.model.actor.stat.CreatureStats;
import org.l2j.gameserver.model.actor.stat.SummonStats;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSummonSpawn;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.network.serverpackets.ExPartyPetWindowAdd;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.RelationChanged;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Location;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.instance.Player;

public abstract class Summon extends Playable
{
    private static final int[] PASSIVE_SUMMONS;
    protected boolean _restoreSummon;
    private Player _owner;
    private boolean _follow;
    private boolean _previousFollowStatus;
    private int _summonPoints;
    
    public Summon(final NpcTemplate template, final Player owner) {
        super(template);
        this._restoreSummon = true;
        this._follow = true;
        this._previousFollowStatus = true;
        this._summonPoints = 0;
        this.setInstanceType(InstanceType.L2Summon);
        this.setInstance(owner.getInstanceWorld());
        this.setShowSummonAnimation(true);
        this._owner = owner;
        this.getAI();
        final int x = owner.getX();
        final int y = owner.getY();
        final int z = owner.getZ();
        final Location location = GeoEngine.getInstance().canMoveToTargetLoc(x, y, z, x + Rnd.get(-100, 100), y + Rnd.get(-100, 100), z, owner.getInstanceWorld());
        this.setXYZInvisible(location.getX(), location.getY(), location.getZ());
    }
    
    @Override
    public void onSpawn() {
        super.onSpawn();
        if (Config.SUMMON_STORE_SKILL_COOLTIME && !this.isTeleporting()) {
            this.restoreEffects();
        }
        this.setFollowStatus(true);
        this.sendPacket(new RelationChanged(this, this._owner.getRelation(this._owner), false));
        World.getInstance().forEachVisibleObject(this.getOwner(), Player.class, player -> player.sendPacket(new RelationChanged(this, this._owner.getRelation(player), this.isAutoAttackable(player))));
        final Party party = this._owner.getParty();
        if (party != null) {
            party.broadcastToPartyMembers(this._owner, new ExPartyPetWindowAdd(this));
        }
        this.setShowSummonAnimation(false);
        this._restoreSummon = false;
        this._owner.rechargeShot(ShotType.BEAST_SOULSHOTS);
        this._owner.rechargeShot(ShotType.BEAST_SPIRITSHOTS);
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSummonSpawn(this), this);
    }
    
    @Override
    public SummonStats getStats() {
        return (SummonStats)super.getStats();
    }
    
    @Override
    public void initCharStat() {
        this.setStat(new SummonStats(this));
    }
    
    @Override
    public SummonStatus getStatus() {
        return (SummonStatus)super.getStatus();
    }
    
    @Override
    public void initCharStatus() {
        this.setStatus(new SummonStatus(this));
    }
    
    @Override
    protected CreatureAI initAI() {
        return new SummonAI(this);
    }
    
    @Override
    public NpcTemplate getTemplate() {
        return (NpcTemplate)super.getTemplate();
    }
    
    public abstract int getSummonType();
    
    @Override
    public final void stopAllEffects() {
        super.stopAllEffects();
        this.updateAndBroadcastStatus(1);
    }
    
    @Override
    public final void stopAllEffectsExceptThoseThatLastThroughDeath() {
        super.stopAllEffectsExceptThoseThatLastThroughDeath();
        this.updateAndBroadcastStatus(1);
    }
    
    @Override
    public void updateAbnormalVisualEffects() {
        AbstractMaskPacket<NpcInfoType> packet;
        World.getInstance().forEachVisibleObject(this, Player.class, player -> {
            if (player == this._owner) {
                player.sendPacket(new PetInfo(this, 1));
            }
            else {
                if (GameUtils.isPet(this)) {
                    packet = new ExPetInfo(this, player, 1);
                }
                else {
                    packet = new SummonInfo(this, player, 1);
                }
                packet.addComponentType(NpcInfoType.ABNORMALS);
                player.sendPacket(packet);
            }
        });
    }
    
    public boolean isMountable() {
        return false;
    }
    
    public long getExpForThisLevel() {
        if (this.getLevel() > LevelData.getInstance().getMaxLevel()) {
            return 0L;
        }
        return LevelData.getInstance().getExpForLevel(this.getLevel());
    }
    
    public long getExpForNextLevel() {
        if (this.getLevel() >= LevelData.getInstance().getMaxLevel()) {
            return 0L;
        }
        return LevelData.getInstance().getExpForLevel(this.getLevel() + 1);
    }
    
    @Override
    public final int getReputation() {
        return (this._owner != null) ? this._owner.getReputation() : 0;
    }
    
    @Override
    public final byte getPvpFlag() {
        return (byte)((this._owner != null) ? this._owner.getPvpFlag() : 0);
    }
    
    @Override
    public final Team getTeam() {
        return (this._owner != null) ? this._owner.getTeam() : Team.NONE;
    }
    
    public final Player getOwner() {
        return this._owner;
    }
    
    public void setOwner(final Player newOwner) {
        this._owner = newOwner;
    }
    
    @Override
    public final int getId() {
        return this.getTemplate().getId();
    }
    
    public short getSoulShotsPerHit() {
        if (this.getTemplate().getSoulShot() > 0) {
            return (short)this.getTemplate().getSoulShot();
        }
        return 1;
    }
    
    public short getSpiritShotsPerHit() {
        if (this.getTemplate().getSpiritShot() > 0) {
            return (short)this.getTemplate().getSpiritShot();
        }
        return 1;
    }
    
    public void followOwner() {
        this.setFollowStatus(true);
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        if (this._owner != null) {
            AggroInfo info;
            World.getInstance().forEachVisibleObject(this, Attackable.class, TgMob -> {
                if (TgMob.isDead()) {
                    return;
                }
                else {
                    info = TgMob.getAggroList().get(this);
                    if (info != null) {
                        TgMob.addDamageHate(this._owner, info.getDamage(), info.getHate());
                    }
                    return;
                }
            });
        }
        DecayTaskManager.getInstance().add(this);
        return true;
    }
    
    public boolean doDie(final Creature killer, final boolean decayed) {
        if (!super.doDie(killer)) {
            return false;
        }
        if (!decayed) {
            DecayTaskManager.getInstance().add(this);
        }
        return true;
    }
    
    @Override
    public void onDecay() {
        if (!GameUtils.isPet(this)) {
            super.onDecay();
        }
        this.deleteMe(this._owner);
    }
    
    @Override
    public void broadcastStatusUpdate(final Creature caster) {
        super.broadcastStatusUpdate(caster);
        this.updateAndBroadcastStatus(1);
    }
    
    public void deleteMe(final Player owner) {
        super.deleteMe();
        if (owner != null) {
            owner.sendPacket(new PetDelete(this.getSummonType(), this.getObjectId()));
            final Party party = owner.getParty();
            if (party != null) {
                party.broadcastToPartyMembers(owner, new ExPartyPetWindowDelete(this));
            }
            if (GameUtils.isPet(this)) {
                owner.setPet(null);
            }
            else {
                owner.removeServitor(this.getObjectId());
            }
        }
        if (this.getInventory() != null) {
            this.getInventory().destroyAllItems("pet deleted", this._owner, this);
        }
        this.decayMe();
        PlayerSummonTable.getInstance().removeServitor(this._owner, this.getObjectId());
    }
    
    public void unSummon(final Player owner) {
        if (this.isSpawned() && !this.isDead()) {
            this.setIsInvul(true);
            this.abortAttack();
            this.abortCast();
            this.storeMe();
            this.storeEffect(true);
            if (this.hasAI()) {
                this.getAI().stopAITask();
            }
            this.abortAllSkillCasters();
            this.stopAllEffects();
            this.stopHpMpRegeneration();
            if (owner != null) {
                if (GameUtils.isPet(this)) {
                    owner.setPet(null);
                }
                else {
                    owner.removeServitor(this.getObjectId());
                }
                owner.sendPacket(new PetDelete(this.getSummonType(), this.getObjectId()));
                final Party party = owner.getParty();
                if (party != null) {
                    party.broadcastToPartyMembers(owner, new ExPartyPetWindowDelete(this));
                }
                if (this.getInventory() != null && this.getInventory().getSize() > 0) {
                    this._owner.setPetInvItems(true);
                    this.sendPacket(SystemMessageId.THERE_ARE_ITEMS_IN_YOUR_PET_INVENTORY_RENDERING_YOU_UNABLE_TO_SELL_TRADE_DROP_PET_SUMMONING_ITEMS_PLEASE_EMPTY_YOUR_PET_INVENTORY);
                }
                else {
                    this._owner.setPetInvItems(false);
                }
            }
            final ZoneRegion oldRegion = ZoneManager.getInstance().getRegion(this);
            this.decayMe();
            oldRegion.removeFromZones(this);
            this.setTarget(null);
            if (Objects.nonNull(owner)) {
                owner.disableSummonAutoShot();
            }
        }
    }
    
    public boolean getFollowStatus() {
        return this._follow;
    }
    
    public void setFollowStatus(final boolean state) {
        this._follow = state;
        if (this._follow) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this._owner);
        }
        else {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        }
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return this._owner != null && this._owner.isAutoAttackable(attacker);
    }
    
    public int getControlObjectId() {
        return 0;
    }
    
    @Override
    public PetInventory getInventory() {
        return null;
    }
    
    public void setRestoreSummon(final boolean val) {
    }
    
    @Override
    public Item getActiveWeaponInstance() {
        return null;
    }
    
    @Override
    public Weapon getActiveWeaponItem() {
        return null;
    }
    
    @Override
    public Item getSecondaryWeaponInstance() {
        return null;
    }
    
    @Override
    public Weapon getSecondaryWeaponItem() {
        return null;
    }
    
    @Override
    public boolean isInvul() {
        return super.isInvul() || this._owner.isSpawnProtected();
    }
    
    @Override
    public Party getParty() {
        if (this._owner == null) {
            return null;
        }
        return this._owner.getParty();
    }
    
    @Override
    public boolean isInParty() {
        return this._owner != null && this._owner.isInParty();
    }
    
    @Override
    public boolean useMagic(final Skill skill, final Item item, final boolean forceUse, final boolean dontMove) {
        if (skill == null || this.isDead() || this._owner == null) {
            return false;
        }
        if (skill.isPassive()) {
            return false;
        }
        if (this.isCastingNow(SkillCaster::isAnyNormalType)) {
            return false;
        }
        WorldObject target;
        if (skill.getTargetType() == TargetType.OWNER_PET) {
            target = this._owner;
        }
        else {
            final WorldObject currentTarget = this._owner.getTarget();
            if (currentTarget != null) {
                target = skill.getTarget(this, forceUse && (!GameUtils.isPlayable(currentTarget) || !currentTarget.isInsideZone(ZoneType.PEACE)), dontMove, false);
                final Player currentTargetPlayer = currentTarget.getActingPlayer();
                if (!forceUse && currentTargetPlayer != null && !currentTargetPlayer.isAutoAttackable(this._owner)) {
                    this.sendPacket(SystemMessageId.INVALID_TARGET);
                    return false;
                }
            }
            else {
                target = skill.getTarget(this, forceUse, dontMove, false);
            }
        }
        if (target == null) {
            this.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
            return false;
        }
        if (this.isSkillDisabled(skill)) {
            this.sendPacket(SystemMessageId.THAT_SERVITOR_SKILL_CANNOT_BE_USED_BECAUSE_IT_IS_RECHARGING);
            return false;
        }
        if (this.getCurrentMp() < this.getStats().getMpConsume(skill) + this.getStats().getMpInitialConsume(skill)) {
            this.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
            return false;
        }
        if (this.getCurrentHp() <= skill.getHpConsume()) {
            this.sendPacket(SystemMessageId.NOT_ENOUGH_HP);
            return false;
        }
        if (!skill.checkCondition(this, target)) {
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (skill.isBad() && this._owner.isInOlympiadMode() && !this._owner.isOlympiadStart()) {
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
        return true;
    }
    
    @Override
    public void setIsImmobilized(final boolean value) {
        super.setIsImmobilized(value);
        if (value) {
            this._previousFollowStatus = this._follow;
            if (this._previousFollowStatus) {
                this.setFollowStatus(false);
            }
        }
        else {
            this.setFollowStatus(this._previousFollowStatus);
        }
    }
    
    @Override
    public void sendDamageMessage(final Creature target, final Skill skill, final int damage, final double elementalDamage, final boolean crit, final boolean miss) {
        if (miss || this._owner == null) {
            return;
        }
        if (target.getObjectId() != this._owner.getObjectId()) {
            if (crit) {
                if (this.isServitor()) {
                    this.sendPacket(SystemMessageId.SUMMONED_MONSTER_S_CRITICAL_HIT);
                }
                else {
                    this.sendPacket(SystemMessageId.PET_S_CRITICAL_HIT);
                }
            }
            if (this._owner.isInOlympiadMode() && GameUtils.isPlayer(target) && ((Player)target).isInOlympiadMode() && ((Player)target).getOlympiadGameId() == this._owner.getOlympiadGameId()) {
                OlympiadGameManager.getInstance().notifyCompetitorDamage(this.getOwner(), damage);
            }
            SystemMessage sm;
            if ((target.isHpBlocked() && !GameUtils.isNpc(target)) || (GameUtils.isPlayer(target) && target.isAffected(EffectFlag.DUELIST_FURY) && !this._owner.isAffected(EffectFlag.FACEOFF))) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_ATTACK_HAS_BEEN_BLOCKED);
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_INFLICTED_S3_DAMAGE_ON_C2);
                sm.addNpcName(this);
                sm.addString(target.getName());
                sm.addInt(damage);
                sm.addPopup(target.getObjectId(), this.getObjectId(), damage * -1);
            }
            this.sendPacket(sm);
        }
    }
    
    @Override
    public void reduceCurrentHp(final double damage, final Creature attacker, final Skill skill, final DamageInfo.DamageType damageType) {
        super.reduceCurrentHp(damage, attacker, skill, damageType);
        if (!this.isDead() && !this.isHpBlocked() && this._owner != null && attacker != null && (!this._owner.isAffected(EffectFlag.DUELIST_FURY) || attacker.isAffected(EffectFlag.FACEOFF))) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2);
            sm.addNpcName(this);
            sm.addString(attacker.getName());
            sm.addInt((int)damage);
            sm.addPopup(this.getObjectId(), attacker.getObjectId(), (int)(-damage));
            this.sendPacket(sm);
        }
    }
    
    @Override
    public void doCast(final Skill skill) {
        if (skill.getTarget(this, false, false, false) == null && !this._owner.getAccessLevel().allowPeaceAttack()) {
            this._owner.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
            this._owner.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        super.doCast(skill);
    }
    
    @Override
    public boolean isInCombat() {
        return this._owner != null && this._owner.isInCombat();
    }
    
    @Override
    public Player getActingPlayer() {
        return this._owner;
    }
    
    public void updateAndBroadcastStatus(final int val) {
        if (this._owner == null) {
            return;
        }
        this.sendPacket(new PetStatusUpdate(this));
        if (this.isSpawned()) {
            this.broadcastNpcInfo(val);
        }
        final Party party = this._owner.getParty();
        if (party != null) {
            party.broadcastToPartyMembers(this._owner, new ExPartyPetWindowUpdate(this));
        }
    }
    
    public void broadcastNpcInfo(final int val) {
        World.getInstance().forEachVisibleObject(this, Player.class, player -> {
            if (player != this._owner) {
                player.sendPacket(new ExPetInfo(this, player, val));
            }
        });
    }
    
    public boolean isHungry() {
        return false;
    }
    
    public int getWeapon() {
        return 0;
    }
    
    public int getArmor() {
        return 0;
    }
    
    @Override
    public void sendInfo(final Player activeChar) {
        if (activeChar == this._owner) {
            activeChar.sendPacket(new PetInfo(this, this.isDead() ? 0 : 1));
            if (GameUtils.isPet(this)) {
                activeChar.sendPacket(new PetItemList(this.getInventory().getItems()));
            }
        }
        else {
            activeChar.sendPacket(new ExPetInfo(this, activeChar, 0));
        }
    }
    
    @Override
    public void onTeleported() {
        super.onTeleported();
        this.sendPacket(new TeleportToLocation(this, this.getX(), this.getY(), this.getZ(), this.getHeading()));
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, super.toString(), this.getId(), this._owner);
    }
    
    @Override
    public boolean isUndead() {
        return this.getTemplate().getRace() == Race.UNDEAD;
    }
    
    public void switchMode() {
    }
    
    public void cancelAction() {
        if (!this.isMovementDisabled()) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
    
    public void doAttack(final WorldObject target) {
        if (this._owner != null && target != null) {
            this.setTarget(target);
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
        }
    }
    
    public final boolean canAttack(final WorldObject target, final boolean ctrlPressed) {
        if (this._owner == null) {
            return false;
        }
        if (target == null || this == target || this._owner == target) {
            return false;
        }
        final int npcId = this.getId();
        if (Util.contains(Summon.PASSIVE_SUMMONS, npcId)) {
            this._owner.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (this.isBetrayed()) {
            this.sendPacket(SystemMessageId.YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (this.isAttackingDisabled()) {
            if (!this.isAttackingNow()) {
                return false;
            }
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
        }
        if (GameUtils.isPet(this) && this.getLevel() - this._owner.getLevel() > 20) {
            this.sendPacket(SystemMessageId.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (this._owner.isInOlympiadMode() && !this._owner.isOlympiadStart()) {
            this._owner.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (this._owner.isSiegeFriend(target)) {
            this.sendPacket(SystemMessageId.FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE);
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (!this._owner.getAccessLevel().allowPeaceAttack() && this._owner.isInsidePeaceZone(this, target)) {
            this.sendPacket(SystemMessageId.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
            return false;
        }
        if (this.isLockedTarget()) {
            this.sendPacket(SystemMessageId.FAILED_TO_CHANGE_ENMITY);
            return false;
        }
        if (!target.isAutoAttackable(this._owner) && !ctrlPressed && !GameUtils.isNpc(target)) {
            this.setFollowStatus(false);
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, target);
            this.sendPacket(SystemMessageId.INVALID_TARGET);
            return false;
        }
        return !GameUtils.isDoor(target) || this.getTemplate().getRace() == Race.SIEGE_WEAPON;
    }
    
    @Override
    public void sendPacket(final ServerPacket... packets) {
        if (this._owner != null) {
            this._owner.sendPacket(packets);
        }
    }
    
    @Override
    public void sendPacket(final SystemMessageId id) {
        if (this._owner != null) {
            this._owner.sendPacket(id);
        }
    }
    
    @Override
    public int getClanId() {
        return (this._owner != null) ? this._owner.getClanId() : 0;
    }
    
    @Override
    public int getAllyId() {
        return (this._owner != null) ? this._owner.getAllyId() : 0;
    }
    
    public int getFormId() {
        int formId = 0;
        final int npcId = this.getId();
        if (npcId == 16041 || npcId == 16042) {
            if (this.getLevel() > 69) {
                formId = 3;
            }
            else if (this.getLevel() > 64) {
                formId = 2;
            }
            else if (this.getLevel() > 59) {
                formId = 1;
            }
        }
        else if (npcId == 16025 || npcId == 16037) {
            if (this.getLevel() > 69) {
                formId = 3;
            }
            else if (this.getLevel() > 64) {
                formId = 2;
            }
            else if (this.getLevel() > 59) {
                formId = 1;
            }
        }
        return formId;
    }
    
    public int getSummonPoints() {
        return this._summonPoints;
    }
    
    public void setSummonPoints(final int summonPoints) {
        this._summonPoints = summonPoints;
    }
    
    public void sendInventoryUpdate(final InventoryUpdate iu) {
        final Player owner = this._owner;
        if (owner != null) {
            owner.sendInventoryUpdate(iu);
        }
    }
    
    @Override
    public boolean isMovementDisabled() {
        return super.isMovementDisabled() || !this.getTemplate().canMove();
    }
    
    @Override
    public boolean isTargetable() {
        return super.isTargetable() && this.getTemplate().isTargetable();
    }
    
    @Override
    public void consumeAndRechargeShots(final ShotType shotType, final int targets) {
        if (Objects.nonNull(this._owner)) {
            final boolean isSoulshot = ShotType.SOULSHOTS == shotType;
            final int count = targets * (isSoulshot ? this.getSoulShotsPerHit() : this.getSpiritShotsPerHit());
            if (!this._owner.consumeAndRechargeShotCount(isSoulshot ? ShotType.BEAST_SOULSHOTS : ShotType.BEAST_SPIRITSHOTS, count)) {
                this.unchargeShot(shotType);
            }
        }
    }
    
    static {
        PASSIVE_SUMMONS = new int[] { 12564, 12621, 14702, 14703, 14704, 14705, 14706, 14707, 14708, 14709, 14710, 14711, 14712, 14713, 14714, 14715, 14716, 14717, 14718, 14719, 14720, 14721, 14722, 14723, 14724, 14725, 14726, 14727, 14728, 14729, 14730, 14731, 14732, 14733, 14734, 14735, 14736 };
    }
}
