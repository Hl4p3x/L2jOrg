// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadRecord;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class OlympiadStat implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public boolean useUserCommand(final int id, final Player player) {
        if (player.getClassId().level() < 2) {
            player.sendPacket(SystemMessageId.THIS_COMMAND_IS_AVAILABLE_ONLY_WHEN_THE_TARGET_HAS_COMPLETED_THE_2ND_CLASS_TRANSFER);
            return false;
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExOlympiadRecord() });
        return true;
    }
    
    public int[] getUserCommandList() {
        return OlympiadStat.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 109 };
    }
}
