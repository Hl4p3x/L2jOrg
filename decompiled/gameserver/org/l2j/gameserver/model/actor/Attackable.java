// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.holders.ExtendDropDataHolder;
import org.l2j.gameserver.model.actor.status.NpcStatus;
import java.util.function.Consumer;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.taskmanager.AttackableThinkTaskManager;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import java.util.LinkedList;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.datatables.drop.EventDropList;
import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.stream.Stream;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.DropType;
import java.util.function.Predicate;
import org.l2j.gameserver.data.xml.impl.ExtendDropData;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAggroRangeEnter;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAttack;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.instancemanager.WalkingManager;
import java.lang.ref.WeakReference;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.model.Clan;
import java.util.Optional;
import org.l2j.gameserver.model.Party;
import java.util.Iterator;
import java.util.ArrayList;
import org.l2j.gameserver.instancemanager.PcCafePointsManager;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.serverpackets.ExMagicAttackInfo;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.model.entity.Hero;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.function.Function;
import org.l2j.gameserver.model.DamageDoneInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.EventDispatcher;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.ai.AttackableAI;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.model.actor.status.CreatureStatus;
import org.l2j.gameserver.model.actor.status.AttackableStatus;
import org.l2j.gameserver.enums.InstanceType;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.tasks.attackable.CommandChannelTimer;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Seed;
import org.l2j.gameserver.model.AggroInfo;
import java.util.Map;
import java.util.Collection;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.concurrent.atomic.AtomicReference;

public class Attackable extends Npc
{
    private final AtomicReference<ItemHolder> _harvestItem;
    private final AtomicReference<Collection<ItemHolder>> _sweepItems;
    private boolean _isRaid;
    private boolean _isRaidMinion;
    private boolean _champion;
    private final Map<Creature, AggroInfo> _aggroList;
    private boolean _isReturningToSpawnPoint;
    private boolean _canReturnToSpawnPoint;
    private boolean _seeThroughSilentMove;
    private boolean _seeded;
    private Seed _seed;
    private int _seederObjId;
    private int _spoilerObjectId;
    private boolean _plundered;
    private boolean _overhit;
    private double _overhitDamage;
    private Creature _overhitAttacker;
    private volatile CommandChannel _firstCommandChannelAttacked;
    private CommandChannelTimer _commandChannelTimer;
    private long _commandChannelLastAttack;
    private boolean _mustGiveExpSp;
    
    public Attackable(final NpcTemplate template) {
        super(template);
        this._harvestItem = new AtomicReference<ItemHolder>();
        this._sweepItems = new AtomicReference<Collection<ItemHolder>>();
        this._isRaid = false;
        this._isRaidMinion = false;
        this._champion = false;
        this._aggroList = new ConcurrentHashMap<Creature, AggroInfo>();
        this._isReturningToSpawnPoint = false;
        this._canReturnToSpawnPoint = true;
        this._seeThroughSilentMove = false;
        this._seeded = false;
        this._seed = null;
        this._seederObjId = 0;
        this._plundered = false;
        this._firstCommandChannelAttacked = null;
        this._commandChannelTimer = null;
        this._commandChannelLastAttack = 0L;
        this.setInstanceType(InstanceType.Attackable);
        this.setIsInvul(false);
        this._mustGiveExpSp = true;
    }
    
    @Override
    public AttackableStatus getStatus() {
        return (AttackableStatus)super.getStatus();
    }
    
    @Override
    public void initCharStatus() {
        this.setStatus(new AttackableStatus(this));
    }
    
    @Override
    protected CreatureAI initAI() {
        return new AttackableAI(this);
    }
    
    public final Map<Creature, AggroInfo> getAggroList() {
        return this._aggroList;
    }
    
    public final boolean isReturningToSpawnPoint() {
        return this._isReturningToSpawnPoint;
    }
    
    public final void setisReturningToSpawnPoint(final boolean value) {
        this._isReturningToSpawnPoint = value;
    }
    
    public final boolean canReturnToSpawnPoint() {
        return this._canReturnToSpawnPoint;
    }
    
    public final void setCanReturnToSpawnPoint(final boolean value) {
        this._canReturnToSpawnPoint = value;
    }
    
    public boolean canSeeThroughSilentMove() {
        return this._seeThroughSilentMove;
    }
    
    public void setSeeThroughSilentMove(final boolean val) {
        this._seeThroughSilentMove = val;
    }
    
    public void useMagic(final Skill skill) {
        if (!SkillCaster.checkUseConditions(this, skill)) {
            return;
        }
        final WorldObject target = skill.getTarget(this, false, false, false);
        if (target != null) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
        }
    }
    
    @Override
    public void reduceCurrentHp(final double value, final Creature attacker, final Skill skill, final boolean isDOT, final boolean directlyToHp, final boolean critical, final boolean reflect, final DamageInfo.DamageType drown) {
        if (this._isRaid && !this.isMinion() && attacker != null && attacker.getParty() != null && attacker.getParty().isInCommandChannel() && attacker.getParty().getCommandChannel().meetRaidWarCondition(this)) {
            if (this._firstCommandChannelAttacked == null) {
                synchronized (this) {
                    if (this._firstCommandChannelAttacked == null) {
                        this._firstCommandChannelAttacked = attacker.getParty().getCommandChannel();
                        if (this._firstCommandChannelAttacked != null) {
                            this._commandChannelTimer = new CommandChannelTimer(this);
                            this._commandChannelLastAttack = System.currentTimeMillis();
                            ThreadPool.schedule((Runnable)this._commandChannelTimer, 10000L);
                            this._firstCommandChannelAttacked.broadcastPacket(new CreatureSay(0, ChatType.PARTYROOM_ALL, "", "You have looting rights!"));
                        }
                    }
                }
            }
            else if (attacker.getParty().getCommandChannel().equals(this._firstCommandChannelAttacked)) {
                this._commandChannelLastAttack = System.currentTimeMillis();
            }
        }
        if (attacker != null) {
            this.addDamage(attacker, (int)value, skill);
            if (this._isRaid && this.giveRaidCurse() && !Config.RAID_DISABLE_CURSE && attacker.getLevel() > this.getLevel() + 8) {
                final Skill raidCurse = CommonSkill.RAID_CURSE2.getSkill();
                if (raidCurse != null) {
                    raidCurse.applyEffects(this, attacker);
                }
            }
        }
        if (GameUtils.isMonster(this)) {
            Monster master = (Monster)this;
            if (master.hasMinions()) {
                master.getMinionList().onAssist(this, attacker);
            }
            master = master.getLeader();
            if (master != null && master.hasMinions()) {
                master.getMinionList().onAssist(this, attacker);
            }
        }
        super.reduceCurrentHp(value, attacker, skill, isDOT, directlyToHp, critical, reflect, drown);
    }
    
    public synchronized void setMustRewardExpSp(final boolean value) {
        this._mustGiveExpSp = value;
    }
    
    public synchronized boolean getMustRewardExpSP() {
        return this._mustGiveExpSp;
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        Object payload = null;
        if (GameUtils.isMonster(this)) {
            final Monster mob = (Monster)this;
            if (mob.getLeader() != null && mob.getLeader().hasMinions()) {
                payload = mob.getLeader();
            }
        }
        if (Objects.nonNull(killer.getActingPlayer())) {
            EventDispatcher.getInstance().notifyEventAsync(new OnAttackableKill(killer.getActingPlayer(), this, GameUtils.isSummon(killer), payload), this);
        }
        if (GameUtils.isMonster(this)) {
            final Monster mob = (Monster)this;
            if (mob.getLeader() != null && mob.getLeader().hasMinions()) {
                final int respawnTime = Config.MINIONS_RESPAWN_TIME.containsKey(this.getId()) ? (Config.MINIONS_RESPAWN_TIME.get(this.getId()) * 1000) : -1;
                mob.getLeader().getMinionList().onMinionDie(mob, respawnTime);
            }
            if (mob.hasMinions()) {
                mob.getMinionList().onMasterDie(false);
            }
        }
        return true;
    }
    
    @Override
    protected void calculateRewards(final Creature lastAttacker) {
        try {
            if (this._aggroList.isEmpty()) {
                return;
            }
            final Map<Player, DamageDoneInfo> rewards = new ConcurrentHashMap<Player, DamageDoneInfo>();
            Player maxDealer = null;
            long maxDamage = 0L;
            long totalDamage = 0L;
            for (final AggroInfo info : this._aggroList.values()) {
                final Player attacker = info.getAttacker().getActingPlayer();
                if (attacker != null) {
                    final long damage = info.getDamage();
                    if (damage <= 1L) {
                        continue;
                    }
                    if (!GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, this, attacker, true)) {
                        continue;
                    }
                    totalDamage += damage;
                    final DamageDoneInfo reward = rewards.computeIfAbsent(attacker, DamageDoneInfo::new);
                    reward.addDamage(damage);
                    if (reward.getDamage() <= maxDamage) {
                        continue;
                    }
                    maxDealer = attacker;
                    maxDamage = reward.getDamage();
                }
            }
            if (this._isRaid && !this._isRaidMinion) {
                final Player player = (maxDealer != null && maxDealer.isOnline()) ? maxDealer : lastAttacker.getActingPlayer();
                this.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL));
                final int raidbossPoints = (int)(this.getTemplate().getRaidPoints() * Config.RATE_RAIDBOSS_POINTS);
                final Party party = player.getParty();
                if (party != null) {
                    final CommandChannel command = party.getCommandChannel();
                    final List<Player> members = (List<Player>)((command != null) ? command.getMembers().stream().filter(p -> MathUtil.isInsideRadius3D(p, this, ((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).partyRange())).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()) : player.getParty().getMembers().stream().filter(p -> MathUtil.isInsideRadius3D(p, this, ((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).partyRange())).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
                    final int points;
                    members.forEach(p -> {
                        points = Math.max(raidbossPoints / members.size(), 1);
                        p.increaseRaidbossPoints(points);
                        p.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_RAID_POINT_S)).addInt(points));
                        if (p.isNoble()) {
                            Hero.getInstance().setRBkilled(p.getObjectId(), this.getId());
                        }
                        return;
                    });
                }
                else {
                    final int points2 = Math.max(raidbossPoints, 1);
                    player.increaseRaidbossPoints(points2);
                    player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_RAID_POINT_S)).addInt(points2));
                    if (player.isNoble()) {
                        Hero.getInstance().setRBkilled(player.getObjectId(), this.getId());
                    }
                }
            }
            this.doItemDrop((maxDealer != null && maxDealer.isOnline()) ? maxDealer : lastAttacker);
            this.doEventDrop(lastAttacker);
            if (!this.getMustRewardExpSP()) {
                return;
            }
            if (!rewards.isEmpty()) {
                for (final DamageDoneInfo reward2 : rewards.values()) {
                    if (reward2 == null) {
                        continue;
                    }
                    final Player attacker = reward2.getAttacker();
                    final long damage = reward2.getDamage();
                    final Party attackerParty = attacker.getParty();
                    float penalty = 1.0f;
                    final Optional<Summon> summon = attacker.getServitors().values().stream().filter(s -> s.getExpMultiplier() > 1.0f).findFirst();
                    if (summon.isPresent()) {
                        penalty = summon.get().getExpMultiplier();
                    }
                    if (attackerParty == null) {
                        if (!this.isInSurroundingRegion(attacker)) {
                            continue;
                        }
                        final double[] expSp = this.calculateExpAndSp(attacker.getLevel(), damage, totalDamage);
                        double exp = expSp[0];
                        double sp = expSp[1];
                        if (Config.CHAMPION_ENABLE && this._champion) {
                            exp *= Config.CHAMPION_REWARDS_EXP_SP;
                            sp *= Config.CHAMPION_REWARDS_EXP_SP;
                        }
                        exp *= penalty;
                        final Creature overhitAttacker = this._overhitAttacker;
                        if (this._overhit && overhitAttacker != null && overhitAttacker.getActingPlayer() != null && attacker == overhitAttacker.getActingPlayer()) {
                            attacker.sendPacket(SystemMessageId.OVER_HIT);
                            attacker.sendPacket(new ExMagicAttackInfo(overhitAttacker.getObjectId(), this.getObjectId(), 3));
                            exp += this.calculateOverhitExp(exp);
                        }
                        if (attacker.isDead()) {
                            continue;
                        }
                        exp = attacker.getStats().getValue(Stat.EXPSP_RATE, exp);
                        sp = attacker.getStats().getValue(Stat.EXPSP_RATE, sp);
                        attacker.addExpAndSp(exp, sp, this.useVitalityRate());
                        if (exp > 0.0) {
                            final Clan clan = attacker.getClan();
                            if (clan != null) {
                                double finalExp = exp;
                                if (this.useVitalityRate()) {
                                    finalExp *= attacker.getStats().getExpBonusMultiplier();
                                }
                                clan.addHuntingPoints(attacker, this, finalExp);
                            }
                            attacker.updateVitalityPoints(this.getVitalityPoints(attacker.getLevel(), exp, this._isRaid), true, false);
                            PcCafePointsManager.getInstance().givePcCafePoint(attacker, exp);
                        }
                        this.rewardAttributeExp(attacker, damage, totalDamage);
                    }
                    else {
                        long partyDmg = 0L;
                        double partyMul = 1.0;
                        int partyLvl = 0;
                        final List<Player> rewardedMembers = new ArrayList<Player>();
                        final List<Player> groupMembers = attackerParty.isInCommandChannel() ? attackerParty.getCommandChannel().getMembers() : attackerParty.getMembers();
                        for (final Player partyPlayer : groupMembers) {
                            if (partyPlayer != null) {
                                if (partyPlayer.isDead()) {
                                    continue;
                                }
                                final DamageDoneInfo reward3 = rewards.get(partyPlayer);
                                if (reward3 != null) {
                                    if (GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, this, partyPlayer, true)) {
                                        partyDmg += reward3.getDamage();
                                        rewardedMembers.add(partyPlayer);
                                        if (partyPlayer.getLevel() > partyLvl) {
                                            if (attackerParty.isInCommandChannel()) {
                                                partyLvl = attackerParty.getCommandChannel().getLevel();
                                            }
                                            else {
                                                partyLvl = partyPlayer.getLevel();
                                            }
                                        }
                                    }
                                    rewards.remove(partyPlayer);
                                }
                                else {
                                    if (!GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, this, partyPlayer, true)) {
                                        continue;
                                    }
                                    rewardedMembers.add(partyPlayer);
                                    if (partyPlayer.getLevel() <= partyLvl) {
                                        continue;
                                    }
                                    if (attackerParty.isInCommandChannel()) {
                                        partyLvl = attackerParty.getCommandChannel().getLevel();
                                    }
                                    else {
                                        partyLvl = partyPlayer.getLevel();
                                    }
                                }
                            }
                        }
                        if (partyDmg < totalDamage) {
                            partyMul = partyDmg / (double)totalDamage;
                        }
                        final double[] expSp2 = this.calculateExpAndSp(partyLvl, partyDmg, totalDamage);
                        double exp2 = expSp2[0];
                        double sp2 = expSp2[1];
                        if (Config.CHAMPION_ENABLE && this._champion) {
                            exp2 *= Config.CHAMPION_REWARDS_EXP_SP;
                            sp2 *= Config.CHAMPION_REWARDS_EXP_SP;
                        }
                        exp2 *= partyMul;
                        sp2 *= partyMul;
                        final Creature overhitAttacker2 = this._overhitAttacker;
                        if (this._overhit && overhitAttacker2 != null && overhitAttacker2.getActingPlayer() != null && attacker == overhitAttacker2.getActingPlayer()) {
                            attacker.sendPacket(SystemMessageId.OVER_HIT);
                            attacker.sendPacket(new ExMagicAttackInfo(overhitAttacker2.getObjectId(), this.getObjectId(), 3));
                            exp2 += this.calculateOverhitExp(exp2);
                        }
                        if (partyDmg <= 0L) {
                            continue;
                        }
                        attackerParty.distributeXpAndSp(exp2, sp2, rewardedMembers, partyLvl, partyDmg, this);
                        for (final Player rewardedMember : rewardedMembers) {
                            this.rewardAttributeExp(rewardedMember, damage, totalDamage);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            Attackable.LOGGER.error("", (Throwable)e);
        }
    }
    
    private void rewardAttributeExp(final Player player, final long damage, final long totalDamage) {
        if (player.getActiveElementalSpiritType() > 0 && this.getAttributeExp() > 0L && this.getElementalSpiritType() != ElementalType.NONE) {
            final long attributeExp = (long)(this.getAttributeExp() * damage / totalDamage * player.getElementalSpiritXpBonus());
            final ElementalSpirit spirit = player.getElementalSpirit(this.getElementalSpiritType().getSuperior());
            if (Objects.nonNull(spirit)) {
                spirit.addExperience(attributeExp);
            }
        }
    }
    
    @Override
    public void addAttackerToAttackByList(final Creature player) {
        if (player == null || player == this || this.getAttackByList().stream().anyMatch(o -> o.get() == player)) {
            return;
        }
        this.getAttackByList().add(new WeakReference<Creature>(player));
    }
    
    public void addDamage(final Creature attacker, final int damage, final Skill skill) {
        if (attacker == null) {
            return;
        }
        if (!this.isDead()) {
            try {
                if (GameUtils.isWalker(this) && !this.isCoreAIDisabled() && WalkingManager.getInstance().isOnWalk(this)) {
                    WalkingManager.getInstance().stopMoving(this, false, true);
                }
                this.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, attacker);
                double hateValue = damage * 100.0 / (this.getLevel() + 7);
                if (skill == null) {
                    hateValue *= attacker.getStats().getValue(Stat.HATE_ATTACK, 1.0);
                }
                this.addDamageHate(attacker, damage, (int)hateValue);
                final Player player = attacker.getActingPlayer();
                if (player != null) {
                    EventDispatcher.getInstance().notifyEventAsync(new OnAttackableAttack(player, this, damage, skill, GameUtils.isSummon(attacker)), this);
                }
            }
            catch (Exception e) {
                Attackable.LOGGER.error("", (Throwable)e);
            }
        }
    }
    
    public void addDamageHate(Creature attacker, final int damage, int aggro) {
        if (attacker == null || attacker == this) {
            return;
        }
        Player targetPlayer = attacker.getActingPlayer();
        final Creature summoner = attacker.getSummoner();
        if (GameUtils.isNpc(attacker) && GameUtils.isPlayer(summoner) && !attacker.isTargetable()) {
            targetPlayer = summoner.getActingPlayer();
            attacker = summoner;
        }
        final AggroInfo ai = this._aggroList.computeIfAbsent(attacker, AggroInfo::new);
        ai.addDamage(damage);
        if (targetPlayer != null && ai.getHate() == 0 && !targetPlayer.isInvisible()) {
            EventDispatcher.getInstance().notifyEventAsync(new OnAttackableAggroRangeEnter(this, targetPlayer, GameUtils.isSummon(attacker)), this);
        }
        if (targetPlayer == null || targetPlayer.getTrap() == null || !targetPlayer.getTrap().isTriggered()) {
            ai.addHate(aggro);
        }
        if (targetPlayer != null && aggro == 0) {
            this.addDamageHate(attacker, 0, 1);
            if (this.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE) {
                this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            }
        }
        else if (targetPlayer == null && aggro == 0) {
            aggro = 1;
            ai.addHate(1);
        }
        if (aggro != 0 && this.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
    
    public void reduceHate(final Creature target, int amount) {
        if (target == null) {
            final Creature mostHated = this.getMostHated();
            if (mostHated == null) {
                ((AttackableAI)this.getAI()).setGlobalAggro(-25);
                return;
            }
            for (final AggroInfo ai : this._aggroList.values()) {
                ai.addHate(amount);
            }
            amount = this.getHating(mostHated);
            if (amount >= 0) {
                ((AttackableAI)this.getAI()).setGlobalAggro(-25);
                this.clearAggroList();
                this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                this.setWalking();
            }
        }
        else {
            final AggroInfo ai2 = this._aggroList.get(target);
            if (ai2 == null) {
                Attackable.LOGGER.info(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/Creature;Lorg/l2j/gameserver/model/actor/Attackable;)Ljava/lang/String;, target, this));
                return;
            }
            ai2.addHate(amount);
            if (ai2.getHate() >= 0 && this.getMostHated() == null) {
                ((AttackableAI)this.getAI()).setGlobalAggro(-25);
                this.clearAggroList();
                this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                this.setWalking();
            }
        }
    }
    
    public void stopHating(final Creature target) {
        if (target == null) {
            return;
        }
        final AggroInfo ai = this._aggroList.get(target);
        if (ai != null) {
            ai.stopHate();
        }
    }
    
    public Creature getMostHated() {
        if (this._aggroList.isEmpty() || this.isAlikeDead()) {
            return null;
        }
        Creature mostHated = null;
        int maxHate = 0;
        for (final AggroInfo ai : this._aggroList.values()) {
            if (ai == null) {
                continue;
            }
            if (ai.checkHate(this) <= maxHate) {
                continue;
            }
            mostHated = ai.getAttacker();
            maxHate = ai.getHate();
        }
        return mostHated;
    }
    
    public List<Creature> get2MostHated() {
        if (this._aggroList.isEmpty() || this.isAlikeDead()) {
            return null;
        }
        Creature mostHated = null;
        Creature secondMostHated = null;
        int maxHate = 0;
        final List<Creature> result = new ArrayList<Creature>();
        for (final AggroInfo ai : this._aggroList.values()) {
            if (ai.checkHate(this) > maxHate) {
                secondMostHated = mostHated;
                mostHated = ai.getAttacker();
                maxHate = ai.getHate();
            }
        }
        result.add(mostHated);
        final Creature secondMostHatedFinal = secondMostHated;
        if (this.getAttackByList().stream().anyMatch(o -> o.get() == secondMostHatedFinal)) {
            result.add(secondMostHated);
        }
        else {
            result.add(null);
        }
        return result;
    }
    
    public List<Creature> getHateList() {
        if (this._aggroList.isEmpty() || this.isAlikeDead()) {
            return null;
        }
        final List<Creature> result = new ArrayList<Creature>();
        for (final AggroInfo ai : this._aggroList.values()) {
            ai.checkHate(this);
            result.add(ai.getAttacker());
        }
        return result;
    }
    
    public int getHating(final Creature target) {
        if (this._aggroList.isEmpty() || target == null) {
            return 0;
        }
        final AggroInfo ai = this._aggroList.get(target);
        if (ai == null) {
            return 0;
        }
        if (GameUtils.isPlayer(ai.getAttacker())) {
            final Player act = (Player)ai.getAttacker();
            if (act.isInvisible() || act.isInvul() || act.isSpawnProtected()) {
                this._aggroList.remove(target);
                return 0;
            }
        }
        if (!ai.getAttacker().isSpawned() || ai.getAttacker().isInvisible()) {
            this._aggroList.remove(target);
            return 0;
        }
        if (ai.getAttacker().isAlikeDead()) {
            ai.stopHate();
            return 0;
        }
        return ai.getHate();
    }
    
    public void doItemDrop(final Creature mainDamageDealer) {
        this.doItemDrop(this.getTemplate(), mainDamageDealer);
    }
    
    public void doItemDrop(final NpcTemplate npcTemplate, final Creature mainDamageDealer) {
        if (mainDamageDealer == null) {
            return;
        }
        final Player player = mainDamageDealer.getActingPlayer();
        if (player == null || !player.isOnline() || player.getClient() == null) {
            return;
        }
        final Stream<Object> stream = npcTemplate.getExtendDrop().stream();
        final ExtendDropData instance = ExtendDropData.getInstance();
        Objects.requireNonNull(instance);
        stream.map((Function<? super Object, ?>)instance::getExtendDropById).filter(Objects::nonNull).forEach(e -> e.reward(player, this));
        if (this.isSpoiled() && !this._plundered) {
            this._sweepItems.set(npcTemplate.calculateDrops(DropType.SPOIL, this, player));
        }
        final Collection<ItemHolder> deathItems = npcTemplate.calculateDrops(DropType.DROP, this, player);
        for (final ItemHolder drop : deathItems) {
            final ItemTemplate item = ItemEngine.getInstance().getTemplate(drop.getId());
            final CharacterSettings characterSettings = (CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class);
            if (characterSettings.isAutoLoot(item.getId()) || this.isFlying() || (!item.hasExImmediateEffect() && ((!this._isRaid && characterSettings.autoLoot()) || (this._isRaid && characterSettings.autoLootRaid()))) || (item.hasExImmediateEffect() && Config.AUTO_LOOT_HERBS)) {
                player.doAutoLoot(this, drop);
            }
            else {
                this.dropItem(player, drop);
            }
            if (this._isRaid && !this._isRaidMinion) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_DIED_AND_DROPPED_S3_S2_S);
                sm.addString(this.getName());
                sm.addItemName(item);
                sm.addLong(drop.getCount());
                this.broadcastPacket(sm);
            }
        }
    }
    
    public void doEventDrop(final Creature lastAttacker) {
        if (lastAttacker == null) {
            return;
        }
        final Player player = lastAttacker.getActingPlayer();
        if (player == null) {
            return;
        }
        if (player.getLevel() - this.getLevel() > 9) {
            return;
        }
        for (final EventDropList.DateDrop drop : EventDropList.getInstance().getAllDrops()) {
            if (drop.monsterCanDrop(this.getId(), this.getLevel()) && Rnd.chance(drop.getChance())) {
                final int itemId = drop.getItemId();
                final long itemCount = Rnd.get(drop.getMin(), drop.getMax());
                final CharacterSettings characterSettings = (CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class);
                if (characterSettings.autoLoot() || this.isFlying() || characterSettings.isAutoLoot(itemId)) {
                    player.doAutoLoot(this, itemId, itemCount);
                }
                else {
                    this.dropItem(player, itemId, itemCount);
                }
            }
        }
    }
    
    public Item getActiveWeapon() {
        return null;
    }
    
    public boolean containsTarget(final Creature player) {
        return this._aggroList.containsKey(player);
    }
    
    public void clearAggroList() {
        this._aggroList.clear();
        this._overhit = false;
        this._overhitDamage = 0.0;
        this._overhitAttacker = null;
    }
    
    @Override
    public boolean isSweepActive() {
        return !Util.isNullOrEmpty((Collection)this._sweepItems.get());
    }
    
    public List<ItemTemplate> getSpoilLootItems() {
        final Collection<ItemHolder> sweepItems = this._sweepItems.get();
        final List<ItemTemplate> lootItems = new LinkedList<ItemTemplate>();
        if (sweepItems != null) {
            for (final ItemHolder item : sweepItems) {
                lootItems.add(ItemEngine.getInstance().getTemplate(item.getId()));
            }
        }
        return lootItems;
    }
    
    public Collection<ItemHolder> takeSweep() {
        return this._sweepItems.getAndSet(null);
    }
    
    public ItemHolder takeHarvest() {
        return this._harvestItem.getAndSet(null);
    }
    
    public boolean isOldCorpse(final Player attacker, final int remainingTime, final boolean sendMessage) {
        if (this.isDead() && DecayTaskManager.getInstance().getRemainingTime(this) < remainingTime) {
            if (sendMessage && attacker != null) {
                attacker.sendPacket(SystemMessageId.THE_CORPSE_IS_TOO_OLD_THE_SKILL_CANNOT_BE_USED);
            }
            return true;
        }
        return false;
    }
    
    public boolean checkSpoilOwner(final Player sweeper, final boolean sendMessage) {
        if (sweeper.getObjectId() != this._spoilerObjectId && !sweeper.isInLooterParty(this._spoilerObjectId)) {
            if (sendMessage) {
                sweeper.sendPacket(SystemMessageId.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
            }
            return false;
        }
        return true;
    }
    
    public void overhitEnabled(final boolean status) {
        this._overhit = status;
    }
    
    public void setOverhitValues(final Creature attacker, final double damage) {
        final double overhitDmg = -(this.getCurrentHp() - damage);
        if (overhitDmg < 0.0) {
            this.overhitEnabled(false);
            this._overhitDamage = 0.0;
            this._overhitAttacker = null;
            return;
        }
        this.overhitEnabled(true);
        this._overhitDamage = overhitDmg;
        this._overhitAttacker = attacker;
    }
    
    public Creature getOverhitAttacker() {
        return this._overhitAttacker;
    }
    
    public double getOverhitDamage() {
        return this._overhitDamage;
    }
    
    public boolean isOverhit() {
        return this._overhit;
    }
    
    private double[] calculateExpAndSp(final int charLevel, final long damage, final long totalDamage) {
        final int levelDiff = charLevel - this.getLevel();
        double xp = Math.max(0.0, this.getExpReward() * damage / totalDamage);
        double sp = Math.max(0.0, this.getSpReward() * damage / totalDamage);
        if (levelDiff > 2) {
            double mul = 0.0;
            switch (levelDiff) {
                case 3: {
                    mul = 0.97;
                    break;
                }
                case 4: {
                    mul = 0.8;
                    break;
                }
                case 5: {
                    mul = 0.61;
                    break;
                }
                case 6: {
                    mul = 0.37;
                    break;
                }
                case 7: {
                    mul = 0.22;
                    break;
                }
                case 8: {
                    mul = 0.13;
                    break;
                }
                case 9: {
                    mul = 0.08;
                    break;
                }
                default: {
                    mul = 0.05;
                    break;
                }
            }
            xp *= mul;
            sp *= mul;
        }
        return new double[] { xp, sp };
    }
    
    public double calculateOverhitExp(final double exp) {
        double overhitPercentage = this._overhitDamage * 100.0 / this.getMaxHp();
        if (overhitPercentage > 25.0) {
            overhitPercentage = 25.0;
        }
        return overhitPercentage / 100.0 * exp;
    }
    
    @Override
    public boolean canBeAttacked() {
        return true;
    }
    
    @Override
    public void onSpawn() {
        super.onSpawn();
        this.setSpoilerObjectId(0);
        this.clearAggroList();
        this._harvestItem.set(null);
        this._sweepItems.set(null);
        this._plundered = false;
        this.setWalking();
        this._seeded = false;
        this._seed = null;
        this._seederObjId = 0;
        if (this.hasAI() && !this.isInActiveRegion()) {
            this.getAI().stopAITask();
        }
    }
    
    @Override
    public void onRespawn() {
        this._champion = false;
        if (Config.CHAMPION_ENABLE && GameUtils.isMonster(this) && !this.isQuestMonster() && !this.getTemplate().isUndying() && !this._isRaid && !this._isRaidMinion && Config.CHAMPION_FREQUENCY > 0 && this.getLevel() >= Config.CHAMP_MIN_LVL && this.getLevel() <= Config.CHAMP_MAX_LVL && (Config.CHAMPION_ENABLE_IN_INSTANCES || this.getInstanceId() == 0) && Rnd.get(100) < Config.CHAMPION_FREQUENCY) {
            this._champion = true;
            if (Config.SHOW_CHAMPION_AURA) {
                this.setTeam(Team.RED);
            }
        }
        AttackableThinkTaskManager.getInstance().add(this);
        super.onRespawn();
    }
    
    public boolean isSpoiled() {
        return this._spoilerObjectId != 0;
    }
    
    public final int getSpoilerObjectId() {
        return this._spoilerObjectId;
    }
    
    public final void setSpoilerObjectId(final int spoilerObjectId) {
        this._spoilerObjectId = spoilerObjectId;
    }
    
    public void setPlundered(final Player player) {
        this._plundered = true;
        this._spoilerObjectId = player.getObjectId();
        this._sweepItems.set(this.getTemplate().calculateDrops(DropType.SPOIL, this, player));
    }
    
    public final void setSeeded(final Seed seed, final Player seeder) {
        if (!this._seeded) {
            this._seed = seed;
            this._seederObjId = seeder.getObjectId();
        }
    }
    
    public final int getSeederId() {
        return this._seederObjId;
    }
    
    public final Seed getSeed() {
        return this._seed;
    }
    
    public final boolean isSeeded() {
        return this._seeded;
    }
    
    public final void setSeeded(final Player seeder) {
        if (this._seed != null && this._seederObjId == seeder.getObjectId()) {
            this._seeded = true;
            int count = 1;
            for (final int skillId : this.getTemplate().getSkills().keySet()) {
                switch (skillId) {
                    case 4303: {
                        count *= 2;
                        continue;
                    }
                    case 4304: {
                        count *= 3;
                        continue;
                    }
                    case 4305: {
                        count *= 4;
                        continue;
                    }
                    case 4306: {
                        count *= 5;
                        continue;
                    }
                    case 4307: {
                        count *= 6;
                        continue;
                    }
                    case 4308: {
                        count *= 7;
                        continue;
                    }
                    case 4309: {
                        count *= 8;
                        continue;
                    }
                    case 4310: {
                        count *= 9;
                        continue;
                    }
                }
            }
            final int diff = this.getLevel() - this._seed.getLevel() - 5;
            if (diff > 0) {
                count += diff;
            }
            this._harvestItem.set(new ItemHolder(this._seed.getCropId(), count * Config.RATE_DROP_MANOR));
        }
    }
    
    @Override
    public boolean hasRandomAnimation() {
        return Config.MAX_MONSTER_ANIMATION > 0 && this.isRandomAnimationEnabled() && !(this instanceof GrandBoss);
    }
    
    public CommandChannelTimer getCommandChannelTimer() {
        return this._commandChannelTimer;
    }
    
    public void setCommandChannelTimer(final CommandChannelTimer commandChannelTimer) {
        this._commandChannelTimer = commandChannelTimer;
    }
    
    public CommandChannel getFirstCommandChannelAttacked() {
        return this._firstCommandChannelAttacked;
    }
    
    public void setFirstCommandChannelAttacked(final CommandChannel firstCommandChannelAttacked) {
        this._firstCommandChannelAttacked = firstCommandChannelAttacked;
    }
    
    public long getCommandChannelLastAttack() {
        return this._commandChannelLastAttack;
    }
    
    public void setCommandChannelLastAttack(final long channelLastAttack) {
        this._commandChannelLastAttack = channelLastAttack;
    }
    
    public void returnHome() {
        this.clearAggroList();
        if (this.hasAI() && this.getSpawn() != null) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, this.getSpawn().getLocation());
        }
    }
    
    public int getVitalityPoints(final int level, final double exp, final boolean isBoss) {
        if (this.getLevel() <= 0 || this.getExpReward() <= 0.0) {
            return 0;
        }
        int points;
        if (level < 85) {
            points = Math.max((int)(exp / 1000.0 * Math.max(level - this.getLevel(), 1)), 1);
        }
        else {
            points = Math.max((int)(exp / (isBoss ? Config.VITALITY_CONSUME_BY_BOSS : Config.VITALITY_CONSUME_BY_MOB) * Math.max(level - this.getLevel(), 1)), 1);
        }
        return -points;
    }
    
    public boolean useVitalityRate() {
        return !this._champion || Config.CHAMPION_ENABLE_VITALITY;
    }
    
    @Override
    public boolean isRaid() {
        return this._isRaid;
    }
    
    public void setIsRaid(final boolean isRaid) {
        this._isRaid = isRaid;
    }
    
    public void setIsRaidMinion(final boolean val) {
        this._isRaid = val;
        this._isRaidMinion = val;
    }
    
    @Override
    public boolean isRaidMinion() {
        return this._isRaidMinion;
    }
    
    @Override
    public boolean isMinion() {
        return this.getLeader() != null;
    }
    
    public Attackable getLeader() {
        return null;
    }
    
    @Override
    public boolean isChampion() {
        return this._champion;
    }
    
    @Override
    public void setTarget(final WorldObject object) {
        if (Objects.nonNull(object) && this.isDead()) {
            return;
        }
        if (Objects.isNull(object)) {
            final WorldObject target = this.getTarget();
            final Map<Creature, AggroInfo> aggroList = this._aggroList;
            Objects.requireNonNull(aggroList);
            GameUtils.doIfIsCreature(target, (Consumer<Creature>)aggroList::remove);
            if (this._aggroList.isEmpty()) {
                final CreatureAI ai2 = this.getAI();
                final AttackableAI ai;
                if (ai2 instanceof AttackableAI && (ai = (AttackableAI)ai2) == ai2) {
                    ai.setGlobalAggro(-25);
                }
                this.setWalking();
                this.clearAggroList();
            }
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
        super.setTarget(object);
    }
}
