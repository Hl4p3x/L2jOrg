// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;

public class TeleportTask implements Runnable
{
    private final Player _activeChar;
    private final Location _loc;
    
    public TeleportTask(final Player player, final Location loc) {
        this._activeChar = player;
        this._loc = loc;
    }
    
    @Override
    public void run() {
        if (this._activeChar != null && this._activeChar.isOnline()) {
            this._activeChar.teleToLocation(this._loc, true);
        }
    }
}
