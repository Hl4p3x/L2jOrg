// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;

public class FameTask implements Runnable
{
    private final Player _player;
    private final int _value;
    
    public FameTask(final Player player, final int value) {
        this._player = player;
        this._value = value;
    }
    
    @Override
    public void run() {
        if (this._player == null || (this._player.isDead() && !Config.FAME_FOR_DEAD_PLAYERS)) {
            return;
        }
        this._player.setFame(this._player.getFame() + this._value);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_FAME);
        sm.addInt(this._value);
        this._player.sendPacket(sm);
        this._player.sendPacket(new UserInfo(this._player));
    }
}
