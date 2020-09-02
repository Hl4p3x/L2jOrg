// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.model.actor.instance.Player;

public class PvPFlagTask implements Runnable
{
    private final Player _player;
    
    public PvPFlagTask(final Player player) {
        this._player = player;
    }
    
    @Override
    public void run() {
        if (this._player == null) {
            return;
        }
        if (System.currentTimeMillis() > this._player.getPvpFlagLasts()) {
            this._player.stopPvPFlag();
        }
        else if (System.currentTimeMillis() > this._player.getPvpFlagLasts() - 20000L) {
            this._player.updatePvPFlag(2);
        }
        else {
            this._player.updatePvPFlag(1);
        }
    }
}
