// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.shuttle.ExStopMoveInShuttle;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class CannotMoveAnymoreInShuttle extends ClientPacket
{
    private int _x;
    private int _y;
    private int _z;
    private int _heading;
    private int _boatId;
    
    public void readImpl() {
        this._boatId = this.readInt();
        this._x = this.readInt();
        this._y = this.readInt();
        this._z = this.readInt();
        this._heading = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.isInShuttle() && activeChar.getShuttle().getObjectId() == this._boatId) {
            activeChar.setInVehiclePosition(new Location(this._x, this._y, this._z));
            activeChar.setHeading(this._heading);
            activeChar.broadcastPacket(new ExStopMoveInShuttle(activeChar, this._boatId));
        }
    }
}
