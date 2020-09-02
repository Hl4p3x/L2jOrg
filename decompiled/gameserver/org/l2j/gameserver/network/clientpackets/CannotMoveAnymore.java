// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.network.GameClient;

public final class CannotMoveAnymore extends ClientPacket
{
    private int _x;
    private int _y;
    private int _z;
    private int _heading;
    
    public void readImpl() {
        this._x = this.readInt();
        this._y = this.readInt();
        this._z = this.readInt();
        this._heading = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (player.getAI() != null) {
            player.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, new Location(this._x, this._y, this._z, this._heading));
        }
    }
}
