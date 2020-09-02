// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerFishing;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.fishing.ExUserInfoFishing;
import org.l2j.gameserver.network.serverpackets.fishing.ExFishingStart;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.fishing.ExFishingEnd;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.Config;
import java.util.Collection;
import org.l2j.commons.util.Util;
import java.util.function.Predicate;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.data.xml.impl.FishingData;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.engine.geo.GeoEngine;
import java.util.Objects;
import org.l2j.gameserver.world.zone.type.WaterZone;
import org.l2j.gameserver.world.zone.type.FishingZone;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public class Fishing
{
    protected static final Logger LOGGER;
    private final Player player;
    private volatile ILocational baitLocation;
    private ScheduledFuture<?> reelInTask;
    private ScheduledFuture<?> startFishingTask;
    private boolean isFishing;
    private Item currentBait;
    
    public Fishing(final Player player) {
        this.baitLocation = new Location(0, 0, 0);
        this.isFishing = false;
        this.player = player;
    }
    
    private static int computeBaitZ(final Player player, final int baitX, final int baitY, final FishingZone fishingZone, final WaterZone waterZone) {
        if (Objects.isNull(fishingZone)) {
            return Integer.MIN_VALUE;
        }
        if (Objects.isNull(waterZone)) {
            return Integer.MIN_VALUE;
        }
        final int baitZ = waterZone.getWaterZ();
        if (GeoEngine.getInstance().hasGeo(baitX, baitY)) {
            if (GeoEngine.getInstance().getHeight(baitX, baitY, baitZ) > baitZ) {
                return Integer.MIN_VALUE;
            }
            if (GeoEngine.getInstance().getHeight(baitX, baitY, player.getZ()) > baitZ) {
                return Integer.MIN_VALUE;
            }
        }
        return baitZ;
    }
    
    public boolean isFishing() {
        return this.isFishing;
    }
    
    public boolean isAtValidLocation() {
        return this.player.isInsideZone(ZoneType.FISHING);
    }
    
    public boolean canFish() {
        return !this.player.isDead() && !this.player.isAlikeDead() && !this.player.hasBlockActions() && !this.player.isSitting();
    }
    
    private FishingBaitData getCurrentBaitData() {
        if (Objects.nonNull(this.currentBait) && this.currentBait.getCount() > 0L && Objects.nonNull(this.player.getInventory().getItemByObjectId(this.currentBait.getObjectId()))) {
            return FishingData.getInstance().getBaitData(this.currentBait.getId());
        }
        final Collection<Item> baits = this.player.getInventory().getItems(item -> item.getItemType() == EtcItemType.LURE, (Predicate<Item>[])new Predicate[0]);
        if (Util.isNullOrEmpty((Collection)baits)) {
            this.currentBait = null;
            return null;
        }
        this.currentBait = baits.iterator().next();
        return FishingData.getInstance().getBaitData(this.currentBait.getId());
    }
    
    private void cancelTasks() {
        if (Objects.nonNull(this.reelInTask)) {
            this.reelInTask.cancel(false);
            this.reelInTask = null;
        }
        if (Objects.nonNull(this.startFishingTask)) {
            this.startFishingTask.cancel(false);
            this.startFishingTask = null;
        }
    }
    
    public void startFishing() {
        synchronized (this) {
            if (this.isFishing) {
                return;
            }
            this.isFishing = true;
        }
        this.castLine();
    }
    
    private void castLine() {
        if (!Config.ALLOW_FISHING && !this.player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS)) {
            this.player.sendMessage("Fishing is disabled.");
            this.player.sendPacket(ActionFailed.STATIC_PACKET);
            this.stopFishing(ExFishingEnd.FishingEndType.ERROR);
            return;
        }
        this.cancelTasks();
        if (!this.canFish()) {
            if (this.isFishing) {
                this.player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
            }
            this.stopFishing(ExFishingEnd.FishingEndType.ERROR);
            return;
        }
        final Item rod = this.player.getActiveWeaponInstance();
        if (Objects.isNull(rod) || rod.getItemType() != WeaponType.FISHING_ROD) {
            this.player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
            this.player.sendPacket(ActionFailed.STATIC_PACKET);
            this.stopFishing(ExFishingEnd.FishingEndType.ERROR);
            return;
        }
        final FishingBaitData baitData = this.getCurrentBaitData();
        if (baitData == null) {
            this.player.sendPacket(SystemMessageId.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
            this.player.sendPacket(ActionFailed.STATIC_PACKET);
            this.stopFishing(ExFishingEnd.FishingEndType.ERROR);
            return;
        }
        final int minPlayerLevel = baitData.getMinPlayerLevel();
        if (this.player.getLevel() < minPlayerLevel) {
            if (minPlayerLevel == 20) {
                this.player.sendPacket(SystemMessageId.FISHING_WILL_END_BECAUSE_THE_CONDITIONS_HAVE_NOT_BEEN_MET);
            }
            else {
                this.player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_FISHING_LEVEL_REQUIREMENTS);
            }
            this.player.sendPacket(ActionFailed.STATIC_PACKET);
            this.stopFishing(ExFishingEnd.FishingEndType.ERROR);
            return;
        }
        if (this.player.isTransformed() || this.player.isInBoat()) {
            this.player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT_OR_TRANSFORMED);
            this.player.sendPacket(ActionFailed.STATIC_PACKET);
            this.stopFishing(ExFishingEnd.FishingEndType.ERROR);
            return;
        }
        if (this.player.isCrafting() || this.player.isInStoreMode()) {
            this.player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_WORKSHOP_OR_PRIVATE_STORE);
            this.player.sendPacket(ActionFailed.STATIC_PACKET);
            this.stopFishing(ExFishingEnd.FishingEndType.ERROR);
            return;
        }
        if (this.player.isInsideZone(ZoneType.WATER) || this.player.isInWater()) {
            this.player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_UNDER_WATER);
            this.player.sendPacket(ActionFailed.STATIC_PACKET);
            this.stopFishing(ExFishingEnd.FishingEndType.ERROR);
            return;
        }
        this.baitLocation = this.calculateBaitLocation();
        if (!this.player.isInsideZone(ZoneType.FISHING) || this.baitLocation == null) {
            if (this.isFishing) {
                this.player.sendPacket(ActionFailed.STATIC_PACKET);
            }
            else {
                this.player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE);
                this.player.sendPacket(ActionFailed.STATIC_PACKET);
            }
            this.stopFishing(ExFishingEnd.FishingEndType.ERROR);
            return;
        }
        final FishingBaitData fishingBaitData;
        this.reelInTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> {
            this.player.getFishing().reelInWithReward();
            this.startFishingTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> this.player.getFishing().castLine(), (long)Rnd.get(fishingBaitData.getWaitMin(), fishingBaitData.getWaitMax()));
            return;
        }, (long)Rnd.get(baitData.getTimeMin(), baitData.getTimeMax()));
        this.player.stopMove(null);
        this.player.broadcastPacket(new ExFishingStart(this.player, -1, baitData.getLevel(), this.baitLocation));
        this.player.sendPacket(new ExUserInfoFishing(this.player, true, this.baitLocation));
        this.player.sendPacket(new PlaySound("SF_P_01"));
        this.player.sendPacket(SystemMessageId.YOU_CAST_YOUR_LINE_AND_START_TO_FISH);
    }
    
    public void reelInWithReward() {
        final FishingBaitData baitData = this.getCurrentBaitData();
        if (baitData == null) {
            this.reelIn(ExFishingEnd.FishingEndReason.LOSE, false);
            Fishing.LOGGER.warn("Player {} is fishing with unhandled bait", (Object)this.player);
            return;
        }
        double chance = baitData.getChance();
        if (this.player.isChargedShot(ShotType.SOULSHOTS)) {
            this.player.consumeAndRechargeShotCount(ShotType.SOULSHOTS, 1);
            chance *= 1.5;
        }
        if (Rnd.chance(chance)) {
            this.reelIn(ExFishingEnd.FishingEndReason.WIN, true);
        }
        else {
            this.reelIn(ExFishingEnd.FishingEndReason.LOSE, true);
        }
    }
    
    private void reelIn(ExFishingEnd.FishingEndReason reason, final boolean consumeBait) {
        if (!this.isFishing) {
            return;
        }
        this.cancelTasks();
        try {
            if (consumeBait && (this.currentBait == null || !this.player.getInventory().updateItemCount(null, this.currentBait, -1L, this.player, null))) {
                reason = ExFishingEnd.FishingEndReason.LOSE;
                return;
            }
            if (reason == ExFishingEnd.FishingEndReason.WIN && this.currentBait != null) {
                final FishingBaitData baitData = FishingData.getInstance().getBaitData(this.currentBait.getId());
                final int numRewards = baitData.getRewards().size();
                if (numRewards > 0) {
                    final FishingData fishingData = FishingData.getInstance();
                    final int lvlModifier = this.player.getLevel() * this.player.getLevel();
                    this.player.addExpAndSp(Rnd.get(fishingData.getExpRateMin(), fishingData.getExpRateMax()) * lvlModifier, Rnd.get(fishingData.getSpRateMin(), fishingData.getSpRateMax()) * lvlModifier, true);
                    final int fishId = baitData.getRewards().get(Rnd.get(0, numRewards - 1));
                    this.player.getInventory().addItem("Fishing Reward", fishId, 1L, this.player, null);
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
                    msg.addItemName(fishId);
                    this.player.sendPacket(msg);
                    this.player.consumeAndRechargeShots(ShotType.SOULSHOTS, 1);
                }
                else {
                    Fishing.LOGGER.warn("Could not find fishing rewards for bait {}", (Object)this.currentBait.getId());
                }
            }
            else if (reason == ExFishingEnd.FishingEndReason.LOSE) {
                this.player.sendPacket(SystemMessageId.THE_BAIT_HAS_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
            }
            if (consumeBait) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerFishing(this.player, reason), this.player);
            }
        }
        finally {
            this.player.broadcastPacket(new ExFishingEnd(this.player, reason));
            this.player.sendPacket(new ExUserInfoFishing(this.player, false));
        }
    }
    
    public void stopFishing() {
        this.stopFishing(ExFishingEnd.FishingEndType.PLAYER_STOP);
    }
    
    public synchronized void stopFishing(final ExFishingEnd.FishingEndType endType) {
        if (this.isFishing) {
            this.reelIn(ExFishingEnd.FishingEndReason.STOP, false);
            this.isFishing = false;
            switch (endType) {
                case PLAYER_STOP: {
                    this.player.sendPacket(SystemMessageId.YOU_REEL_YOUR_LINE_IN_AND_STOP_FISHING);
                    break;
                }
                case PLAYER_CANCEL: {
                    this.player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
                    break;
                }
            }
        }
    }
    
    public ILocational getBaitLocation() {
        return this.baitLocation;
    }
    
    private Location calculateBaitLocation() {
        final int distMin = FishingData.getInstance().getBaitDistanceMin();
        final int distMax = FishingData.getInstance().getBaitDistanceMax();
        final int distance = Rnd.get(distMin, distMax);
        final double angle = MathUtil.convertHeadingToDegree(this.player.getHeading());
        final double radian = Math.toRadians(angle);
        final double sin = Math.sin(radian);
        final double cos = Math.cos(radian);
        final int baitX = (int)(this.player.getX() + cos * distance);
        final int baitY = (int)(this.player.getY() + sin * distance);
        FishingZone fishingZone = null;
        for (final Zone zone : ZoneManager.getInstance().getZones(this.player)) {
            if (zone instanceof FishingZone) {
                fishingZone = (FishingZone)zone;
                break;
            }
        }
        WaterZone waterZone = null;
        for (final Zone zone2 : ZoneManager.getInstance().getZones(baitX, baitY)) {
            if (zone2 instanceof WaterZone) {
                waterZone = (WaterZone)zone2;
                break;
            }
        }
        final int baitZ = computeBaitZ(this.player, baitX, baitY, fishingZone, waterZone);
        if (baitZ == Integer.MIN_VALUE) {
            this.player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE);
            return null;
        }
        return new Location(baitX, baitY, baitZ);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Fishing.class);
    }
}
