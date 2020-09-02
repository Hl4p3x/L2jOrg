// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;

public class RecoBonusTaskEnd implements Runnable
{
    private final Player _player;
    
    public RecoBonusTaskEnd(final Player player) {
        this._player = player;
    }
    
    @Override
    public void run() {
        if (this._player != null) {
            this._player.sendPacket(new ExVoteSystemInfo(this._player));
        }
    }
}
