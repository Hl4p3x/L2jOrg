// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExValidateLocationInShuttle extends ServerPacket
{
    private final Player _activeChar;
    private final int _shipId;
    private final int _heading;
    private final Location _loc;
    
    public ExValidateLocationInShuttle(final Player player) {
        this._activeChar = player;
        this._shipId = this._activeChar.getShuttle().getObjectId();
        this._loc = player.getInVehiclePosition();
        this._heading = player.getHeading();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_VALIDATE_LOCATION_IN_SHUTTLE);
        this.writeInt(this._activeChar.getObjectId());
        this.writeInt(this._shipId);
        this.writeInt(this._loc.getX());
        this.writeInt(this._loc.getY());
        this.writeInt(this._loc.getZ());
        this.writeInt(this._heading);
    }
}
