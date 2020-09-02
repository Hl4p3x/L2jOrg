// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExCubeGameExtendedChangePoints extends ServerPacket
{
    int _timeLeft;
    int _bluePoints;
    int _redPoints;
    boolean _isRedTeam;
    Player _player;
    int _playerPoints;
    
    public ExCubeGameExtendedChangePoints(final int timeLeft, final int bluePoints, final int redPoints, final boolean isRedTeam, final Player player, final int playerPoints) {
        this._timeLeft = timeLeft;
        this._bluePoints = bluePoints;
        this._redPoints = redPoints;
        this._isRedTeam = isRedTeam;
        this._player = player;
        this._playerPoints = playerPoints;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BLOCK_UPSET_STATE);
        this.writeInt(0);
        this.writeInt(this._timeLeft);
        this.writeInt(this._bluePoints);
        this.writeInt(this._redPoints);
        this.writeInt((int)(this._isRedTeam ? 1 : 0));
        this.writeInt(this._player.getObjectId());
        this.writeInt(this._playerPoints);
    }
}
