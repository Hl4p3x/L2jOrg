// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import java.util.Date;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.model.actor.instance.Player;
import java.text.SimpleDateFormat;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class Time implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    private static final SimpleDateFormat fmt;
    
    public boolean useUserCommand(final int id, final Player player) {
        if (Time.COMMAND_IDS[0] != id) {
            return false;
        }
        final int t = WorldTimeController.getInstance().getGameTime();
        final String h = Integer.toString(t / 60 % 24);
        String m;
        if (t % 60 < 10) {
            m = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, t % 60);
        }
        else {
            m = Integer.toString(t % 60);
        }
        SystemMessage sm;
        if (WorldTimeController.getInstance().isNight()) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THE_CURRENT_TIME_IS_S1_S2);
            sm.addString(h);
            sm.addString(m);
        }
        else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THE_CURRENT_TIME_IS_S1_S2);
            sm.addString(h);
            sm.addString(m);
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
        if (Config.DISPLAY_SERVER_TIME) {
            player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Time.fmt.format(new Date(System.currentTimeMillis()))));
        }
        return true;
    }
    
    public int[] getUserCommandList() {
        return Time.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 77 };
        fmt = new SimpleDateFormat("H:mm.");
    }
}
