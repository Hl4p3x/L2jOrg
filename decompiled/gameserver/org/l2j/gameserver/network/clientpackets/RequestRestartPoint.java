// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.model.entity.Castle;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.quest.Event;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestRestartPoint extends ClientPacket
{
    private static final Logger LOGGER;
    protected int _requestedPointType;
    
    public void readImpl() {
        this._requestedPointType = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!player.canRevive()) {
            return;
        }
        if (player.isFakeDeath()) {
            player.stopFakeDeath(true);
            return;
        }
        if (!player.isDead()) {
            RequestRestartPoint.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            return;
        }
        if (player.isOnCustomEvent()) {
            for (final AbstractEventListener listener : player.getListeners(EventType.ON_CREATURE_DEATH)) {
                if (listener.getOwner() instanceof Event) {
                    ((Event)listener.getOwner()).notifyEvent("ResurrectPlayer", null, player);
                    return;
                }
            }
        }
        final Castle castle = CastleManager.getInstance().getCastle(player);
        if (castle != null && castle.getSiege().isInProgress() && player.getClan() != null && castle.getSiege().checkIsAttacker(player.getClan())) {
            ThreadPool.schedule((Runnable)new DeathTask(player), (long)castle.getSiege().getAttackerRespawnDelay());
            if (castle.getSiege().getAttackerRespawnDelay() > 0) {
                player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, castle.getSiege().getAttackerRespawnDelay() / 1000));
            }
            return;
        }
        this.portPlayer(player);
    }
    
    protected final void portPlayer(final Player player) {
        Location loc = null;
        Instance instance = null;
        if (player.isJailed()) {
            this._requestedPointType = 27;
        }
        switch (this._requestedPointType) {
            case 1: {
                if (player.getClan() == null || player.getClan().getHideoutId() == 0) {
                    RequestRestartPoint.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                    return;
                }
                loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.CLANHALL);
                final ClanHall residense = ClanHallManager.getInstance().getClanHallByClan(player.getClan());
                if (residense != null && residense.hasFunction(ResidenceFunctionType.EXP_RESTORE)) {
                    player.restoreExp(residense.getFunction(ResidenceFunctionType.EXP_RESTORE).getValue());
                    break;
                }
                break;
            }
            case 2: {
                final Clan clan = player.getClan();
                Castle castle = CastleManager.getInstance().getCastle(player);
                if (castle != null && castle.getSiege().isInProgress()) {
                    if (castle.getSiege().checkIsDefender(clan)) {
                        loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.CASTLE);
                    }
                    else {
                        if (!castle.getSiege().checkIsAttacker(clan)) {
                            RequestRestartPoint.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                            return;
                        }
                        loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN);
                    }
                }
                else {
                    if (clan == null || clan.getCastleId() == 0) {
                        return;
                    }
                    loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.CASTLE);
                }
                if (clan == null) {
                    break;
                }
                castle = CastleManager.getInstance().getCastleByOwner(clan);
                if (castle != null) {
                    final Castle.CastleFunction castleFunction = castle.getCastleFunction(4);
                    if (castleFunction != null) {
                        player.restoreExp(castleFunction.getLevel());
                    }
                    break;
                }
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                SiegeClanData siegeClan = null;
                final Castle castle = CastleManager.getInstance().getCastle(player);
                if (castle != null && castle.getSiege().isInProgress()) {
                    siegeClan = castle.getSiege().getAttackerClan(player.getClan());
                }
                if (siegeClan == null || siegeClan.getFlags().isEmpty()) {
                    RequestRestartPoint.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                    return;
                }
                loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.SIEGEFLAG);
                break;
            }
            case 5: {
                if (!player.isGM() && !player.getInventory().haveItemForSelfResurrection()) {
                    RequestRestartPoint.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                    return;
                }
                if (player.isGM() || player.destroyItemByItemId("Feather", 10649, 1L, player, false) || player.destroyItemByItemId("Feather", 13300, 1L, player, false) || player.destroyItemByItemId("Feather", 13128, 1L, player, false)) {
                    player.doRevive(100.0);
                    break;
                }
                instance = player.getInstanceWorld();
                loc = new Location(player);
                break;
            }
            case 6:
            case 7: {
                break;
            }
            case 27: {
                if (!player.isJailed()) {
                    return;
                }
                loc = new Location(-114356, -249645, -2984);
                break;
            }
            default: {
                loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN);
                break;
            }
        }
        if (loc != null) {
            player.setIsPendingRevive(true);
            player.teleToLocation(loc, true, instance);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestRestartPoint.class);
    }
    
    class DeathTask implements Runnable
    {
        final Player activeChar;
        
        DeathTask(final Player _activeChar) {
            this.activeChar = _activeChar;
        }
        
        @Override
        public void run() {
            RequestRestartPoint.this.portPlayer(this.activeChar);
        }
    }
}
