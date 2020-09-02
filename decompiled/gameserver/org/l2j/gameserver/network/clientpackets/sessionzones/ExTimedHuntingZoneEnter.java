// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.sessionzones;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExTimedHuntingZoneEnter extends ClientPacket
{
    private int _zoneId;
    
    public void readImpl() {
        this._zoneId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (player.isMounted()) {
            player.sendMessage("Cannot use time-limited hunting zones while mounted.");
            return;
        }
        if (player.isInDuel()) {
            player.sendMessage("Cannot use time-limited hunting zones during a duel.");
            return;
        }
        if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player)) {
            player.sendMessage("Cannot use time-limited hunting zones while waiting for the Olympiad.");
            return;
        }
        if (player.isOnEvent() || player.getBlockCheckerArena() > -1) {
            player.sendMessage("Cannot use time-limited hunting zones while registered on an event.");
            return;
        }
        if (player.isInInstance()) {
            player.sendMessage("Cannot use time-limited hunting zones while in an instance.");
            return;
        }
        if (this._zoneId == 2 && player.getLevel() < 78) {
            player.sendMessage("Your level does not correspond the zone equivalent.");
        }
        final long currentTime = System.currentTimeMillis();
        long endTime = player.getHuntingZoneResetTime(this._zoneId);
        if (endTime + Config.TIME_LIMITED_ZONE_RESET_DELAY < currentTime) {
            endTime = currentTime + 3600000L;
        }
        if (endTime > currentTime) {
            if (player.getAdena() <= Config.TIME_LIMITED_ZONE_TELEPORT_FEE) {
                player.sendMessage("Not enough adena.");
                return;
            }
            player.reduceAdena("TimedHuntingZone", Config.TIME_LIMITED_ZONE_TELEPORT_FEE, player, true);
            switch (this._zoneId) {
                case 2: {
                    player.teleToLocation(17613, -76862, -6265);
                    break;
                }
            }
            player.setHuntingZoneResetTime(this._zoneId, endTime);
            player.startTimedHuntingZone(this._zoneId, endTime - currentTime);
        }
        else {
            player.sendMessage("You don't have enough time available to enter the hunting zone.");
        }
    }
}
