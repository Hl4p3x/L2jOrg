// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;

public class ExCubeGameTeamList extends ServerPacket
{
    private final List<Player> _bluePlayers;
    private final List<Player> _redPlayers;
    private final int _roomNumber;
    
    public ExCubeGameTeamList(final List<Player> redPlayers, final List<Player> bluePlayers, final int roomNumber) {
        this._redPlayers = redPlayers;
        this._bluePlayers = bluePlayers;
        this._roomNumber = roomNumber - 1;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BLOCK_UPSET_LIST);
        this.writeInt(0);
        this.writeInt(this._roomNumber);
        this.writeInt(-1);
        this.writeInt(this._bluePlayers.size());
        for (final Player player : this._bluePlayers) {
            this.writeInt(player.getObjectId());
            this.writeString((CharSequence)player.getName());
        }
        this.writeInt(this._redPlayers.size());
        for (final Player player : this._redPlayers) {
            this.writeInt(player.getObjectId());
            this.writeString((CharSequence)player.getName());
        }
    }
}
