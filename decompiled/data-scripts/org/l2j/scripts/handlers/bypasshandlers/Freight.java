// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import java.util.Iterator;
import org.l2j.gameserver.model.item.container.PlayerFreight;
import org.l2j.gameserver.network.serverpackets.PackageToList;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.WareHouseWithdrawalList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.container.Warehouse;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class Freight implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        if (command.equalsIgnoreCase(Freight.COMMANDS[0])) {
            final PlayerFreight freight = player.getFreight();
            if (freight != null) {
                if (freight.getSize() > 0) {
                    player.setActiveWarehouse((Warehouse)freight);
                    for (final Item i : player.getActiveWarehouse().getItems()) {
                        if (i.isTimeLimitedItem() && i.getRemainingTime() <= 0L) {
                            player.getActiveWarehouse().destroyItem("Item", i, player, (Object)null);
                        }
                    }
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new WareHouseWithdrawalList(1, player, 1) });
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new WareHouseWithdrawalList(2, player, 1) });
                }
                else {
                    player.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
                }
            }
        }
        else if (command.equalsIgnoreCase(Freight.COMMANDS[1])) {
            if (player.getAccountChars().size() < 1) {
                player.sendPacket(SystemMessageId.THAT_CHARACTER_DOES_NOT_EXIST);
            }
            else {
                player.sendPacket(new ServerPacket[] { (ServerPacket)new PackageToList(player.getAccountChars()) });
            }
        }
        return false;
    }
    
    public String[] getBypassList() {
        return Freight.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "package_withdraw", "package_deposit" };
    }
}
