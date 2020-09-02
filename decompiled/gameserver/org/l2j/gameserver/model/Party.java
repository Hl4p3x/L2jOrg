// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.network.serverpackets.ExSetPartyLooting;
import org.l2j.gameserver.network.serverpackets.ExAskModifyPartyLooting;
import java.util.HashSet;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.Collection;
import org.l2j.gameserver.instancemanager.PcCafePointsManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.actor.instance.Servitor;
import java.util.LinkedList;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ExCloseMPCC;
import org.l2j.gameserver.network.serverpackets.ExPartyPetWindowDelete;
import org.l2j.gameserver.network.serverpackets.PartySmallWindowDelete;
import org.l2j.gameserver.instancemanager.DuelManager;
import org.l2j.gameserver.network.serverpackets.ExTacticalSign;
import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.serverpackets.ExOpenMPCC;
import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.network.serverpackets.StatusUpdate;
import org.l2j.gameserver.network.serverpackets.PartySmallWindowAdd;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.serverpackets.ExPartyPetWindowAdd;
import java.util.stream.Stream;
import java.util.function.Consumer;
import java.util.Objects;
import org.l2j.gameserver.network.serverpackets.PartySmallWindowAll;
import org.l2j.gameserver.network.serverpackets.PartySmallWindowDeleteAll;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.commons.util.Rnd;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.world.World;
import java.util.Collections;
import java.util.ArrayList;
import io.github.joealisson.primitive.HashIntMap;
import java.util.Set;
import java.util.concurrent.Future;
import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.network.serverpackets.PartyMemberPosition;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;
import org.l2j.gameserver.model.actor.Creature;
import io.github.joealisson.primitive.IntMap;
import java.time.Duration;

public class Party extends AbstractPlayerGroup
{
    private static final double[] BONUS_EXP_SP;
    private static final Duration PARTY_POSITION_BROADCAST_INTERVAL;
    private static final Duration PARTY_DISTRIBUTION_TYPE_REQUEST_TIMEOUT;
    private static final int[] TACTICAL_SYS_STRINGS;
    private final IntMap<Creature> tacticalSigns;
    private final List<Player> members;
    protected PartyMemberPosition positionPacket;
    private boolean _pendingInvitation;
    private long _pendingInviteTimeout;
    private int partyLvl;
    private volatile PartyDistributionType distributionType;
    private volatile PartyDistributionType changeRequestDistributionType;
    private volatile Future<?> changeDistributionTypeRequestTask;
    private volatile Set<Integer> _changeDistributionTypeAnswers;
    private int itemLastLoot;
    private CommandChannel commandChannel;
    private Future<?> positionBroadcastTask;
    
    public Party(final Player leader, final PartyDistributionType partyDistributionType) {
        this.tacticalSigns = (IntMap<Creature>)new HashIntMap();
        this.members = Collections.synchronizedList(new ArrayList<Player>());
        this._pendingInvitation = false;
        this.changeDistributionTypeRequestTask = null;
        this._changeDistributionTypeAnswers = null;
        this.itemLastLoot = 0;
        this.commandChannel = null;
        this.positionBroadcastTask = null;
        this.members.add(leader);
        this.partyLvl = leader.getLevel();
        this.distributionType = partyDistributionType;
        World.getInstance().incrementParty();
    }
    
    public boolean getPendingInvitation() {
        return this._pendingInvitation;
    }
    
    public void setPendingInvitation(final boolean val) {
        this._pendingInvitation = val;
        this._pendingInviteTimeout = WorldTimeController.getInstance().getGameTicks() + 150;
    }
    
    public boolean isInvitationRequestExpired() {
        return this._pendingInviteTimeout <= WorldTimeController.getInstance().getGameTicks();
    }
    
    private Player getCheckedRandomMember(final int itemId, final Creature target) {
        return (Player)Rnd.get((List)this.members.stream().filter(member -> member.getInventory().validateCapacityByItemId(itemId) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, target, member, true)).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
    }
    
    private Player getCheckedNextLooter(final int ItemId, final Creature target) {
        for (int i = 0; i < this.members.size(); ++i) {
            if (++this.itemLastLoot >= this.members.size()) {
                this.itemLastLoot = 0;
            }
            final Player member = this.members.get(this.itemLastLoot);
            if (member.getInventory().validateCapacityByItemId(ItemId) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, target, member, true)) {
                return member;
            }
        }
        return null;
    }
    
    private Player getActualLooter(final Player player, final int ItemId, final boolean spoil, final Creature target) {
        Player looter = null;
        switch (this.distributionType) {
            case RANDOM: {
                if (!spoil) {
                    looter = this.getCheckedRandomMember(ItemId, target);
                    break;
                }
                break;
            }
            case RANDOM_INCLUDING_SPOIL: {
                looter = this.getCheckedRandomMember(ItemId, target);
                break;
            }
            case BY_TURN: {
                if (!spoil) {
                    looter = this.getCheckedNextLooter(ItemId, target);
                    break;
                }
                break;
            }
            case BY_TURN_INCLUDING_SPOIL: {
                looter = this.getCheckedNextLooter(ItemId, target);
                break;
            }
        }
        return (looter != null) ? looter : player;
    }
    
    public void broadcastToPartyMembersNewLeader() {
        this.members.forEach(member -> {
            member.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET, new PartySmallWindowAll(member, this));
            member.broadcastUserInfo();
        });
    }
    
    public void broadcastToPartyMembers(final Player player, final ServerPacket msg) {
        final Stream<Object> filter = this.members.stream().filter(member -> member.getObjectId() != player.getObjectId());
        Objects.requireNonNull(msg);
        filter.forEach((Consumer<? super Object>)msg::sendTo);
    }
    
    public void addPartyMember(final Player player) {
        if (this.members.contains(player)) {
            return;
        }
        if (Objects.nonNull(this.changeRequestDistributionType)) {
            this.finishLootRequest(false);
        }
        this.members.add(player);
        player.sendPacket(new PartySmallWindowAll(player, this));
        for (final Player member2 : this.members) {
            Util.doIfNonNull((Object)member2.getPet(), pet -> player.sendPacket(new ExPartyPetWindowAdd(pet)));
            member2.getServitors().values().forEach(s -> player.sendPacket(new ExPartyPetWindowAdd(s)));
        }
        player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_JOINED_S1_S_PARTY)).addString(this.getLeader().getName()));
        this.broadcastPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_JOINED_THE_PARTY)).addString(player.getName()));
        this.members.stream().filter(member -> member != player).forEach(member -> member.sendPacket(new PartySmallWindowAdd(player, this)));
        final Summon pet2 = player.getPet();
        if (pet2 != null) {
            this.broadcastPacket(new ExPartyPetWindowAdd(pet2));
        }
        player.getServitors().values().forEach(s -> this.broadcastPacket(new ExPartyPetWindowAdd(s)));
        if (player.getLevel() > this.partyLvl) {
            this.partyLvl = player.getLevel();
        }
        final StatusUpdate su = new StatusUpdate(player).addUpdate(StatusUpdateType.MAX_HP, player.getMaxHp()).addUpdate(StatusUpdateType.CUR_HP, (int)player.getCurrentHp());
        for (final Player member3 : this.members) {
            member3.updateEffectIcons(true);
            member3.broadcastUserInfo();
            Util.doIfNonNull((Object)member3.getPet(), Creature::updateEffectIcons);
            member3.getServitors().values().forEach(Creature::updateEffectIcons);
            member3.sendPacket(su);
        }
        if (this.isInCommandChannel()) {
            player.sendPacket(ExOpenMPCC.STATIC_PACKET);
        }
        if (this.positionBroadcastTask == null) {
            this.positionBroadcastTask = (Future<?>)ThreadPool.scheduleAtFixedRate(() -> {
                if (this.positionPacket == null) {
                    this.positionPacket = new PartyMemberPosition(this);
                }
                else {
                    this.positionPacket.reuse(this);
                }
                this.broadcastPacket(this.positionPacket);
                return;
            }, Party.PARTY_POSITION_BROADCAST_INTERVAL.toMillis() / 2L, Party.PARTY_POSITION_BROADCAST_INTERVAL.toMillis());
        }
        this.applyTacticalSigns(player, false);
        World.getInstance().incrementPartyMember();
    }
    
    private IntMap<Creature> getTacticalSigns() {
        return this.tacticalSigns;
    }
    
    public void applyTacticalSigns(final Player player, final boolean remove) {
        this.tacticalSigns.forEach((key, value) -> player.sendPacket(new ExTacticalSign(value, remove ? 0 : key)));
    }
    
    public void addTacticalSign(final Player activeChar, final int tacticalSignId, final Creature target) {
        final Creature tacticalTarget = (Creature)this.getTacticalSigns().get(tacticalSignId);
        if (tacticalTarget == null) {
            this.tacticalSigns.values().remove(target);
            this.tacticalSigns.put(tacticalSignId, (Object)target);
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_USED_S3_ON_C2);
            sm.addPcName(activeChar);
            sm.addString(target.getName());
            sm.addSystemString(Party.TACTICAL_SYS_STRINGS[tacticalSignId]);
            this.members.forEach(m -> m.sendPacket(new ExTacticalSign(target, tacticalSignId), sm));
        }
        else if (tacticalTarget == target) {
            this.tacticalSigns.remove(tacticalSignId);
            this.members.forEach(m -> m.sendPacket(new ExTacticalSign(tacticalTarget, 0)));
        }
        else {
            this.tacticalSigns.replace(tacticalSignId, (Object)target);
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_USED_S3_ON_C2).addPcName(activeChar).addString(target.getName()).addSystemString(Party.TACTICAL_SYS_STRINGS[tacticalSignId]);
            final SystemMessage sm2;
            this.members.forEach(m -> m.sendPacket(new ExTacticalSign(tacticalTarget, 0), new ExTacticalSign(target, tacticalSignId), sm2));
        }
    }
    
    public void setTargetBasedOnTacticalSignId(final Player player, final int tacticalSignId) {
        final Creature tacticalTarget = (Creature)this.tacticalSigns.get(tacticalSignId);
        if (tacticalTarget != null && !tacticalTarget.isInvisible() && tacticalTarget.isTargetable() && !player.isTargetingDisabled()) {
            player.setTarget(tacticalTarget);
        }
    }
    
    public void removePartyMember(final String name, final MessageType type) {
        this.removePartyMember(this.getPlayerByName(name), type);
    }
    
    public void removePartyMember(final Player player, final MessageType type) {
        if (this.members.contains(player)) {
            final boolean isLeader = this.isLeader(player);
            if (this.members.size() == 2 || (isLeader && !Config.ALT_LEAVE_PARTY_LEADER && type != MessageType.DISCONNECTED)) {
                this.disbandParty();
                return;
            }
            this.members.remove(player);
            this.recalculatePartyLevel();
            this.onPlayerLeave(player, type);
            if (isLeader) {
                this.broadcastPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BECOME_THE_PARTY_LEADER)).addString(this.getLeader().getName()));
                this.broadcastToPartyMembersNewLeader();
            }
        }
    }
    
    private void onPlayerLeave(final Player player, final MessageType type) {
        if (player.isInDuel()) {
            DuelManager.getInstance().onRemoveFromParty(player);
        }
        if (player.isChanneling() && player.getSkillChannelizer().hasChannelized()) {
            player.abortCast();
        }
        else if (player.isChannelized()) {
            player.getSkillChannelized().abortChannelization();
        }
        if (type == MessageType.EXPELLED) {
            player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY);
            this.broadcastPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_WAS_EXPELLED_FROM_THE_PARTY)).addString(player.getName()));
        }
        else if (type == MessageType.LEFT || type == MessageType.DISCONNECTED) {
            player.sendPacket(SystemMessageId.YOU_HAVE_WITHDRAWN_FROM_THE_PARTY);
            this.broadcastPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_LEFT_THE_PARTY)).addString(player.getName()));
        }
        player.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
        player.setParty(null);
        this.broadcastPacket(new PartySmallWindowDelete(player));
        Util.doIfNonNull((Object)player.getPet(), pet -> this.broadcastPacket(new ExPartyPetWindowDelete(pet)));
        player.getServitors().values().forEach(s -> player.sendPacket(new ExPartyPetWindowDelete(s)));
        if (this.isInCommandChannel()) {
            player.sendPacket(ExCloseMPCC.STATIC_PACKET);
        }
        this.applyTacticalSigns(player, true);
        World.getInstance().decrementPartyMember();
    }
    
    public void disbandParty() {
        this.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_PARTY_HAS_DISPERSED));
        this.members.forEach(member -> this.onPlayerLeave(member, MessageType.NONE));
        if (this.isInCommandChannel()) {
            if (this.commandChannel.getLeader().getObjectId() == this.getLeader().getObjectId()) {
                this.commandChannel.disbandChannel();
            }
            else {
                this.commandChannel.removeParty(this);
            }
        }
        this.members.clear();
        this.cancelTasks();
        World.getInstance().decrementParty();
    }
    
    private void cancelTasks() {
        if (Objects.nonNull(this.changeDistributionTypeRequestTask)) {
            this.changeDistributionTypeRequestTask.cancel(true);
            this.changeDistributionTypeRequestTask = null;
        }
        if (Objects.nonNull(this.positionBroadcastTask)) {
            this.positionBroadcastTask.cancel(false);
            this.positionBroadcastTask = null;
        }
    }
    
    public void changePartyLeader(final String name) {
        this.setLeader(this.getPlayerByName(name));
    }
    
    private Player getPlayerByName(final String name) {
        for (final Player member : this.members) {
            if (member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }
    
    public void distributeItem(final Player player, final Item item) {
        if (item.getId() == 57) {
            this.distributeAdena(player, item.getCount(), player);
            ItemEngine.getInstance().destroyItem("Party", item, player, null);
            return;
        }
        final Player target = this.getActualLooter(player, item.getId(), false, player);
        target.addItem("Party", item, player, true);
        if (item.getCount() > 1L) {
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S3_S2);
            msg.addString(target.getName());
            msg.addItemName(item);
            msg.addLong(item.getCount());
            this.broadcastToPartyMembers(target, msg);
        }
        else {
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S2);
            msg.addString(target.getName());
            msg.addItemName(item);
            this.broadcastToPartyMembers(target, msg);
        }
    }
    
    public void distributeItem(final Player player, final int itemId, final long itemCount, final boolean spoil, final Attackable target) {
        if (itemId == 57) {
            this.distributeAdena(player, itemCount, target);
            return;
        }
        final Player looter = this.getActualLooter(player, itemId, spoil, target);
        looter.addItem(spoil ? "Sweeper Party" : "Party", itemId, itemCount, target, true);
        if (itemCount > 1L) {
            final SystemMessage msg = spoil ? SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S3_S2_S_BY_USING_SWEEPER) : SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S3_S2);
            msg.addString(looter.getName());
            msg.addItemName(itemId);
            msg.addLong(itemCount);
            this.broadcastToPartyMembers(looter, msg);
        }
        else {
            final SystemMessage msg = spoil ? SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S2_BY_USING_SWEEPER) : SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_OBTAINED_S2);
            msg.addString(looter.getName());
            msg.addItemName(itemId);
            this.broadcastToPartyMembers(looter, msg);
        }
    }
    
    public void distributeItem(final Player player, final ItemHolder item, final boolean spoil, final Attackable target) {
        this.distributeItem(player, item.getId(), item.getCount(), spoil, target);
    }
    
    public void distributeAdena(final Player player, final long adena, final Creature target) {
        final List<Player> toReward = new LinkedList<Player>();
        for (final Player member : this.members) {
            if (GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, target, member, true)) {
                toReward.add(member);
            }
        }
        if (!toReward.isEmpty()) {
            final long count = adena / toReward.size();
            for (final Player member2 : toReward) {
                member2.addAdena("Party", count, player, true);
            }
        }
    }
    
    public void distributeXpAndSp(double xpReward, double spReward, final List<Player> rewardedMembers, final int topLvl, final long partyDmg, final Attackable target) {
        final List<Player> validMembers = this.getValidMembers(rewardedMembers, topLvl);
        xpReward *= this.getExpBonus(validMembers.size(), target.getInstanceWorld());
        spReward *= this.getSpBonus(validMembers.size(), target.getInstanceWorld());
        int sqLevelSum = 0;
        for (final Player member : validMembers) {
            sqLevelSum += member.getLevel() * member.getLevel();
        }
        for (final Player member : rewardedMembers) {
            if (member.isDead()) {
                continue;
            }
            if (validMembers.contains(member)) {
                float penalty = 1.0f;
                final Summon summon = member.getServitors().values().stream().filter(s -> s.getExpMultiplier() > 1.0f).findFirst().orElse(null);
                if (summon != null) {
                    penalty = ((Servitor)summon).getExpMultiplier();
                }
                final double sqLevel = member.getLevel() * member.getLevel();
                final double preCalculation = sqLevel / sqLevelSum * penalty;
                double exp = member.getStats().getValue(Stat.EXPSP_RATE, xpReward * preCalculation);
                final double sp = member.getStats().getValue(Stat.EXPSP_RATE, spReward * preCalculation);
                exp = this.calculateExpSpPartyCutoff(member.getActingPlayer(), topLvl, exp, sp, target.useVitalityRate());
                if (exp <= 0.0) {
                    continue;
                }
                final Clan clan = member.getClan();
                if (clan != null) {
                    double finalExp = exp;
                    if (target.useVitalityRate()) {
                        finalExp *= member.getStats().getExpBonusMultiplier();
                    }
                    clan.addHuntingPoints(member, target, finalExp);
                }
                member.updateVitalityPoints(target.getVitalityPoints(member.getLevel(), exp, target.isRaid()), true, false);
                PcCafePointsManager.getInstance().givePcCafePoint(member, exp);
            }
            else {
                member.addExpAndSp(0.0, 0.0);
            }
        }
    }
    
    private double calculateExpSpPartyCutoff(final Player player, final int topLvl, final double addExp, final double addSp, final boolean vit) {
        double xp = addExp;
        double sp = addSp;
        if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("highfive")) {
            int i = 0;
            final int lvlDiff = topLvl - player.getLevel();
            for (final int[] gap : Config.PARTY_XP_CUTOFF_GAPS) {
                if (lvlDiff >= gap[0] && lvlDiff <= gap[1]) {
                    xp = addExp * Config.PARTY_XP_CUTOFF_GAP_PERCENTS[i] / 100.0;
                    sp = addSp * Config.PARTY_XP_CUTOFF_GAP_PERCENTS[i] / 100.0;
                    player.addExpAndSp(xp, sp, vit);
                    break;
                }
                ++i;
            }
        }
        else {
            player.addExpAndSp(addExp, addSp, vit);
        }
        return xp;
    }
    
    public void recalculatePartyLevel() {
        int newLevel = 0;
        for (final Player member : this.members) {
            if (member == null) {
                this.members.remove(member);
            }
            else {
                if (member.getLevel() <= newLevel) {
                    continue;
                }
                newLevel = member.getLevel();
            }
        }
        this.partyLvl = newLevel;
    }
    
    private List<Player> getValidMembers(final List<Player> members, final int topLvl) {
        final List<Player> validMembers = new ArrayList<Player>();
        final String party_XP_CUTOFF_METHOD = Config.PARTY_XP_CUTOFF_METHOD;
        switch (party_XP_CUTOFF_METHOD) {
            case "level": {
                for (final Player member : members) {
                    if (topLvl - member.getLevel() <= Config.PARTY_XP_CUTOFF_LEVEL) {
                        validMembers.add(member);
                    }
                }
                break;
            }
            case "percentage": {
                int sqLevelSum = 0;
                for (final Player member2 : members) {
                    sqLevelSum += member2.getLevel() * member2.getLevel();
                }
                for (final Player member2 : members) {
                    final int sqLevel = member2.getLevel() * member2.getLevel();
                    if (sqLevel * 100 >= sqLevelSum * Config.PARTY_XP_CUTOFF_PERCENT) {
                        validMembers.add(member2);
                    }
                }
                break;
            }
            case "auto": {
                int sqLevelSum = 0;
                for (final Player member2 : members) {
                    sqLevelSum += member2.getLevel() * member2.getLevel();
                }
                int i = members.size() - 1;
                if (i < 1) {
                    return members;
                }
                if (i >= Party.BONUS_EXP_SP.length) {
                    i = Party.BONUS_EXP_SP.length - 1;
                }
                for (final Player member3 : members) {
                    final int sqLevel2 = member3.getLevel() * member3.getLevel();
                    if (sqLevel2 >= sqLevelSum / (members.size() * members.size())) {
                        validMembers.add(member3);
                    }
                }
                break;
            }
            case "highfive": {
                validMembers.addAll(members);
                break;
            }
            case "none": {
                validMembers.addAll(members);
                break;
            }
        }
        return validMembers;
    }
    
    private double getBaseExpSpBonus(final int membersCount) {
        int i = membersCount - 1;
        if (i < 1) {
            return 1.0;
        }
        if (i >= Party.BONUS_EXP_SP.length) {
            i = Party.BONUS_EXP_SP.length - 1;
        }
        return Party.BONUS_EXP_SP[i];
    }
    
    private double getExpBonus(final int membersCount, final Instance instance) {
        final float rateMul = (instance != null) ? instance.getExpPartyRate() : Config.RATE_PARTY_XP;
        return (membersCount < 2) ? this.getBaseExpSpBonus(membersCount) : (this.getBaseExpSpBonus(membersCount) * rateMul);
    }
    
    private double getSpBonus(final int membersCount, final Instance instance) {
        final float rateMul = (instance != null) ? instance.getSPPartyRate() : Config.RATE_PARTY_SP;
        return (membersCount < 2) ? this.getBaseExpSpBonus(membersCount) : (this.getBaseExpSpBonus(membersCount) * rateMul);
    }
    
    @Override
    public int getLevel() {
        return this.partyLvl;
    }
    
    public PartyDistributionType getDistributionType() {
        return this.distributionType;
    }
    
    public boolean isInCommandChannel() {
        return this.commandChannel != null;
    }
    
    public CommandChannel getCommandChannel() {
        return this.commandChannel;
    }
    
    public void setCommandChannel(final CommandChannel channel) {
        this.commandChannel = channel;
    }
    
    @Override
    public Player getLeader() {
        return this.members.get(0);
    }
    
    @Override
    public void setLeader(final Player player) {
        if (player != null && !player.isInDuel()) {
            if (this.members.contains(player)) {
                if (this.isLeader(player)) {
                    player.sendPacket(SystemMessageId.SLOW_DOWN_YOU_ARE_ALREADY_THE_PARTY_LEADER);
                }
                else {
                    final Player temp = this.getLeader();
                    final int p1 = this.members.indexOf(player);
                    this.members.set(0, player);
                    this.members.set(p1, temp);
                    SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BECOME_THE_PARTY_LEADER);
                    msg.addString(this.getLeader().getName());
                    this.broadcastPacket(msg);
                    this.broadcastToPartyMembersNewLeader();
                    if (this.isInCommandChannel() && this.commandChannel.isLeader(temp)) {
                        this.commandChannel.setLeader(this.getLeader());
                        msg = SystemMessage.getSystemMessage(SystemMessageId.COMMAND_CHANNEL_AUTHORITY_HAS_BEEN_TRANSFERRED_TO_C1);
                        msg.addString(this.commandChannel.getLeader().getName());
                        this.commandChannel.broadcastPacket(msg);
                    }
                }
            }
            else {
                player.sendPacket(SystemMessageId.YOU_MAY_ONLY_TRANSFER_PARTY_LEADERSHIP_TO_ANOTHER_MEMBER_OF_THE_PARTY);
            }
        }
    }
    
    public synchronized void requestLootChange(final PartyDistributionType partyDistributionType) {
        if (this.changeRequestDistributionType != null) {
            return;
        }
        this.changeRequestDistributionType = partyDistributionType;
        this._changeDistributionTypeAnswers = new HashSet<Integer>();
        this.changeDistributionTypeRequestTask = (Future<?>)ThreadPool.schedule(() -> this.finishLootRequest(false), Party.PARTY_DISTRIBUTION_TYPE_REQUEST_TIMEOUT.toMillis());
        this.broadcastToPartyMembers(this.getLeader(), new ExAskModifyPartyLooting(this.getLeader().getName(), partyDistributionType));
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.REQUESTING_APPROVAL_FOR_CHANGING_PARTY_LOOT_TO_S1);
        sm.addSystemString(partyDistributionType.getSysStringId());
        this.getLeader().sendPacket(sm);
    }
    
    public synchronized void answerLootChangeRequest(final Player member, final boolean answer) {
        if (this.changeRequestDistributionType == null) {
            return;
        }
        if (this._changeDistributionTypeAnswers.contains(member.getObjectId())) {
            return;
        }
        if (!answer) {
            this.finishLootRequest(false);
            return;
        }
        this._changeDistributionTypeAnswers.add(member.getObjectId());
        if (this._changeDistributionTypeAnswers.size() >= this.getMemberCount() - 1) {
            this.finishLootRequest(true);
        }
    }
    
    protected synchronized void finishLootRequest(final boolean success) {
        if (this.changeRequestDistributionType == null) {
            return;
        }
        if (this.changeDistributionTypeRequestTask != null) {
            this.changeDistributionTypeRequestTask.cancel(false);
            this.changeDistributionTypeRequestTask = null;
        }
        if (success) {
            this.broadcastPacket(new ExSetPartyLooting(1, this.changeRequestDistributionType));
            this.distributionType = this.changeRequestDistributionType;
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PARTY_LOOT_WAS_CHANGED_TO_S1);
            sm.addSystemString(this.changeRequestDistributionType.getSysStringId());
            this.broadcastPacket(sm);
        }
        else {
            this.broadcastPacket(new ExSetPartyLooting(0, this.distributionType));
            this.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.PARTY_LOOT_CHANGE_WAS_CANCELLED));
        }
        this.changeRequestDistributionType = null;
        this._changeDistributionTypeAnswers = null;
    }
    
    @Override
    public List<Player> getMembers() {
        return this.members;
    }
    
    public boolean contains(final Player player) {
        return this.members.contains(player);
    }
    
    static {
        BONUS_EXP_SP = new double[] { 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 2.0 };
        PARTY_POSITION_BROADCAST_INTERVAL = Duration.ofSeconds(12L);
        PARTY_DISTRIBUTION_TYPE_REQUEST_TIMEOUT = Duration.ofSeconds(15L);
        TACTICAL_SYS_STRINGS = new int[] { 0, 2664, 2665, 2666, 2667 };
    }
    
    public enum MessageType
    {
        EXPELLED, 
        LEFT, 
        NONE, 
        DISCONNECTED;
    }
}
