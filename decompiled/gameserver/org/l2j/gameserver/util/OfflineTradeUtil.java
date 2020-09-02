// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.data.sql.impl.OfflineTradersTable;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public final class OfflineTradeUtil
{
    protected static final Logger LOGGER_ACCOUNTING;
    
    private OfflineTradeUtil() {
    }
    
    private static boolean offlineMode(final Player player) {
        if (player == null || player.isInOlympiadMode() || player.isBlockedFromExit() || player.isJailed() || player.getVehicle() != null) {
            return false;
        }
        boolean canSetShop = false;
        switch (player.getPrivateStoreType()) {
            case SELL:
            case PACKAGE_SELL:
            case BUY: {
                canSetShop = Config.OFFLINE_TRADE_ENABLE;
                break;
            }
            case MANUFACTURE: {
                canSetShop = Config.OFFLINE_TRADE_ENABLE;
                break;
            }
            default: {
                canSetShop = (Config.OFFLINE_CRAFT_ENABLE && player.isCrafting());
                break;
            }
        }
        if (Config.OFFLINE_MODE_IN_PEACE_ZONE && !player.isInsideZone(ZoneType.PEACE)) {
            canSetShop = false;
        }
        final GameClient client = player.getClient();
        return client != null && !client.isDetached() && canSetShop;
    }
    
    public static boolean enteredOfflineMode(final Player player) {
        if (!offlineMode(player)) {
            return false;
        }
        final GameClient client = player.getClient();
        client.close(true);
        client.setDetached(true);
        player.leaveParty();
        OlympiadManager.getInstance().unRegisterNoble(player);
        Summon pet = player.getPet();
        if (pet != null) {
            pet.setRestoreSummon(true);
            pet.unSummon(player);
            pet = player.getPet();
            if (pet != null) {
                pet.broadcastNpcInfo(0);
            }
        }
        player.getServitors().values().forEach(s -> {
            s.setRestoreSummon(true);
            s.unSummon(player);
            return;
        });
        if (Config.OFFLINE_SET_NAME_COLOR) {
            player.getAppearance().setNameColor(Config.OFFLINE_NAME_COLOR);
            player.broadcastUserInfo();
        }
        if (player.getOfflineStartTime() == 0L) {
            player.setOfflineStartTime(System.currentTimeMillis());
        }
        if (Config.STORE_OFFLINE_TRADE_IN_REALTIME) {
            OfflineTradersTable.onTransaction(player, false, true);
        }
        player.storeMe();
        OfflineTradeUtil.LOGGER_ACCOUNTING.info(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/network/GameClient;)Ljava/lang/String;, client));
        return true;
    }
    
    static {
        LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
    }
}
