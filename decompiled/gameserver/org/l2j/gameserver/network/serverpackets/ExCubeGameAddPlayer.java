// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExCubeGameAddPlayer extends ServerPacket
{
    Player _player;
    boolean _isRedTeam;
    
    public ExCubeGameAddPlayer(final Player player, final boolean isRedTeam) {
        this._player = player;
        this._isRedTeam = isRedTeam;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BLOCK_UPSET_LIST);
        this.writeInt(1);
        this.writeInt(-1);
        this.writeInt((int)(this._isRedTeam ? 1 : 0));
        this.writeInt(this._player.getObjectId());
        this.writeString((CharSequence)this._player.getName());
    }
}
