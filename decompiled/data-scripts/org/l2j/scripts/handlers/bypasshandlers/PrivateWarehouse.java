// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.network.serverpackets.WareHouseWithdrawalList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.item.WarehouseDepositList;
import org.l2j.gameserver.model.item.container.Warehouse;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class PrivateWarehouse implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        if (player.hasItemRequest()) {
            return false;
        }
        if (command.toLowerCase().startsWith(PrivateWarehouse.COMMANDS[0])) {
            showWithdrawWindow(player);
            return true;
        }
        if (command.toLowerCase().startsWith(PrivateWarehouse.COMMANDS[1])) {
            player.setActiveWarehouse((Warehouse)player.getWarehouse());
            player.setInventoryBlockingStatus(true);
            WarehouseDepositList.openOfPlayer(player);
            return true;
        }
        return false;
    }
    
    private static void showWithdrawWindow(final Player player) {
        player.setActiveWarehouse((Warehouse)player.getWarehouse());
        if (player.getActiveWarehouse().getSize() == 0) {
            player.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
            return;
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)new WareHouseWithdrawalList(1, player, 1) });
        player.sendPacket(new ServerPacket[] { (ServerPacket)new WareHouseWithdrawalList(2, player, 1) });
    }
    
    public String[] getBypassList() {
        return PrivateWarehouse.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "withdrawp", "depositp" };
    }
}
