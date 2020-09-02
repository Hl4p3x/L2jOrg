// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.model.actor.instance.Player;

public class HennaDurationTask implements Runnable
{
    private final Player _player;
    private final int _slot;
    
    public HennaDurationTask(final Player player, final int slot) {
        this._player = player;
        this._slot = slot;
    }
    
    @Override
    public void run() {
        if (this._player != null) {
            this._player.removeHenna(this._slot);
        }
    }
}
