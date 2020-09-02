// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.voicedcommandhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IVoicedCommandHandler;

public class Banking implements IVoicedCommandHandler
{
    private static final String[] _voicedCommands;
    
    public boolean useVoicedCommand(final String command, final Player activeChar, final String params) {
        if (command.equals("bank")) {
            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, Config.BANKING_SYSTEM_ADENA, Config.BANKING_SYSTEM_GOLDBARS, Config.BANKING_SYSTEM_GOLDBARS, Config.BANKING_SYSTEM_ADENA));
        }
        else if (command.equals("deposit")) {
            if (activeChar.getInventory().getInventoryItemCount(57, 0) >= Config.BANKING_SYSTEM_ADENA) {
                if (!activeChar.reduceAdena("Goldbar", (long)Config.BANKING_SYSTEM_ADENA, (WorldObject)activeChar, false)) {
                    return false;
                }
                activeChar.getInventory().addItem("Goldbar", 3470, (long)Config.BANKING_SYSTEM_GOLDBARS, activeChar, (Object)null);
                activeChar.getInventory().updateDatabase();
                activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, Config.BANKING_SYSTEM_GOLDBARS, Config.BANKING_SYSTEM_ADENA));
            }
            else {
                activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Config.BANKING_SYSTEM_ADENA));
            }
        }
        else if (command.equals("withdraw")) {
            if (activeChar.getInventory().getInventoryItemCount(3470, 0) >= Config.BANKING_SYSTEM_GOLDBARS) {
                if (!activeChar.destroyItemByItemId("Adena", 3470, (long)Config.BANKING_SYSTEM_GOLDBARS, (WorldObject)activeChar, false)) {
                    return false;
                }
                activeChar.getInventory().addAdena("Adena", (long)Config.BANKING_SYSTEM_ADENA, activeChar, (Object)null);
                activeChar.getInventory().updateDatabase();
                activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, Config.BANKING_SYSTEM_ADENA, Config.BANKING_SYSTEM_GOLDBARS));
            }
            else {
                activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Config.BANKING_SYSTEM_ADENA));
            }
        }
        return true;
    }
    
    public String[] getVoicedCommandList() {
        return Banking._voicedCommands;
    }
    
    static {
        _voicedCommands = new String[] { "bank", "withdraw", "deposit" };
    }
}
