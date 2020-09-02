// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.item.WarehouseDepositList;
import org.l2j.gameserver.network.serverpackets.WareHouseWithdrawalList;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Warehouse;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class ClanWarehouse implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        final Npc npc = (Npc)target;
        if (!(npc instanceof Warehouse) && npc.getClan() != null) {
            return false;
        }
        if (player.hasItemRequest()) {
            return false;
        }
        if (player.getClan() == null) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
            return false;
        }
        if (player.getClan().getLevel() == 0) {
            player.sendPacket(SystemMessageId.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_ABOVE_CAN_USE_A_CLAN_WAREHOUSE);
            return false;
        }
        try {
            if (command.toLowerCase().startsWith(ClanWarehouse.COMMANDS[0])) {
                player.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
                if (!player.hasClanPrivilege(ClanPrivilege.CL_VIEW_WAREHOUSE)) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
                    return true;
                }
                player.setActiveWarehouse(player.getClan().getWarehouse());
                if (player.getActiveWarehouse().getSize() == 0) {
                    player.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
                    return true;
                }
                for (final Item i : player.getActiveWarehouse().getItems()) {
                    if (i.isTimeLimitedItem() && i.getRemainingTime() <= 0L) {
                        player.getActiveWarehouse().destroyItem("Item", i, player, (Object)null);
                    }
                }
                player.sendPacket(new ServerPacket[] { (ServerPacket)new WareHouseWithdrawalList(1, player, 2) });
                player.sendPacket(new ServerPacket[] { (ServerPacket)new WareHouseWithdrawalList(2, player, 2) });
                return true;
            }
            else {
                if (command.toLowerCase().startsWith(ClanWarehouse.COMMANDS[1])) {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
                    player.setActiveWarehouse(player.getClan().getWarehouse());
                    player.setInventoryBlockingStatus(true);
                    WarehouseDepositList.openOfClan(player);
                    return true;
                }
                return false;
            }
        }
        catch (Exception e) {
            ClanWarehouse.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
            return false;
        }
    }
    
    public String[] getBypassList() {
        return ClanWarehouse.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "withdrawc", "depositc" };
    }
}
