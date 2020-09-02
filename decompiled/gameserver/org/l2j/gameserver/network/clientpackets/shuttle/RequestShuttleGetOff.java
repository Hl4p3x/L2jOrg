// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestShuttleGetOff extends ClientPacket
{
    private int _x;
    private int _y;
    private int _z;
    
    public void readImpl() {
        this.readInt();
        this._x = this.readInt();
        this._y = this.readInt();
        this._z = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.getShuttle() != null) {
            activeChar.getShuttle().removePassenger(activeChar, this._x, this._y, this._z);
        }
    }
}
