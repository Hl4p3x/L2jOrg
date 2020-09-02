// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.model.actor.instance.Player;

public final class WarnUserTakeBreakTask implements Runnable
{
    private final Player _player;
    
    public WarnUserTakeBreakTask(final Player player) {
        this._player = player;
    }
    
    @Override
    public void run() {
        if (this._player != null) {
            if (this._player.isOnline()) {
                final long hours = TimeUnit.MILLISECONDS.toHours(this._player.getUptime());
                this._player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_BEEN_PLAYING_FOR_S1_HOUR_S_HOW_ABOUT_TAKING_A_BREAK)).addLong(hours));
            }
            else {
                this._player.stopWarnUserTakeBreak();
            }
        }
    }
}
