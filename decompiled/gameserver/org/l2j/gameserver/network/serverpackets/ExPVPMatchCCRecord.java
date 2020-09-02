// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Map;

public class ExPVPMatchCCRecord extends ServerPacket
{
    public static final int INITIALIZE = 0;
    public static final int UPDATE = 1;
    public static final int FINISH = 2;
    private final int _state;
    private final Map<Player, Integer> _players;
    
    public ExPVPMatchCCRecord(final int state, final Map<Player, Integer> players) {
        this._state = state;
        this._players = players;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PVPMATCH_CC_RECORD);
        this.writeInt(this._state);
        this.writeInt(this._players.size());
        for (final Map.Entry<Player, Integer> entry : this._players.entrySet()) {
            this.writeString((CharSequence)entry.getKey().getName());
            this.writeInt((int)entry.getValue());
        }
    }
}
