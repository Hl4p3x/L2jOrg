// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExCubeGameChangeTeam extends ServerPacket
{
    Player _player;
    boolean _fromRedTeam;
    
    public ExCubeGameChangeTeam(final Player player, final boolean fromRedTeam) {
        this._player = player;
        this._fromRedTeam = fromRedTeam;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BLOCK_UPSET_LIST);
        this.writeInt(5);
        this.writeInt(this._player.getObjectId());
        this.writeInt((int)(this._fromRedTeam ? 1 : 0));
        this.writeInt((int)(this._fromRedTeam ? 0 : 1));
    }
}
