// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.RecipeShopManageList;
import org.l2j.gameserver.network.serverpackets.PrivateStoreManageListBuy;
import org.l2j.gameserver.network.serverpackets.PrivateStoreManageListSell;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import java.util.Objects;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public final class PrivateStore implements IPlayerActionHandler
{
    private static final Logger LOGGER;
    
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        final PrivateStoreType type = PrivateStoreType.findById(action.getOptionId());
        if (Objects.isNull(type)) {
            PrivateStore.LOGGER.warn("Incorrect private store type: {}", (Object)action.getOptionId());
            return;
        }
        if (!player.canOpenPrivateStore()) {
            if (player.isInsideZone(ZoneType.NO_STORE)) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
            }
            player.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
            return;
        }
        switch (type) {
            case SELL:
            case SELL_MANAGE:
            case PACKAGE_SELL: {
                if (player.getPrivateStoreType() == PrivateStoreType.SELL || player.getPrivateStoreType() == PrivateStoreType.SELL_MANAGE || player.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL) {
                    player.setPrivateStoreType(PrivateStoreType.NONE);
                    break;
                }
                break;
            }
            case BUY:
            case BUY_MANAGE: {
                if (player.getPrivateStoreType() == PrivateStoreType.BUY || player.getPrivateStoreType() == PrivateStoreType.BUY_MANAGE) {
                    player.setPrivateStoreType(PrivateStoreType.NONE);
                    break;
                }
                break;
            }
            case MANUFACTURE: {
                player.setPrivateStoreType(PrivateStoreType.NONE);
                player.broadcastUserInfo();
                break;
            }
        }
        if (player.getPrivateStoreType() == PrivateStoreType.NONE) {
            if (player.isSitting()) {
                player.standUp();
            }
            switch (type) {
                case SELL:
                case SELL_MANAGE:
                case PACKAGE_SELL: {
                    player.setPrivateStoreType(PrivateStoreType.SELL_MANAGE);
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new PrivateStoreManageListSell(1, player, type == PrivateStoreType.PACKAGE_SELL) });
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new PrivateStoreManageListSell(2, player, type == PrivateStoreType.PACKAGE_SELL) });
                    break;
                }
                case BUY:
                case BUY_MANAGE: {
                    player.setPrivateStoreType(PrivateStoreType.BUY_MANAGE);
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new PrivateStoreManageListBuy(1, player) });
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new PrivateStoreManageListBuy(2, player) });
                    break;
                }
                case MANUFACTURE: {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new RecipeShopManageList(player, true) });
                    break;
                }
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PrivateStore.class);
    }
}
