// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import java.time.LocalDate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class MyBirthday implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public boolean useUserCommand(final int id, final Player player) {
        if (id != MyBirthday.COMMAND_IDS[0]) {
            return false;
        }
        final LocalDate date = player.getCreateDate();
        player.sendPacket(new ServerPacket[] { (ServerPacket)((SystemMessage)((SystemMessage)((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.C1_S_BIRTHDAY_IS_S3_S4_S2).addPcName(player)).addInt(date.getYear())).addInt(date.getMonthValue())).addInt(date.getDayOfMonth()) });
        return true;
    }
    
    public int[] getUserCommandList() {
        return MyBirthday.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 126 };
    }
}
