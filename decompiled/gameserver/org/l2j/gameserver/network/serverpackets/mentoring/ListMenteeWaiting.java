// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.mentoring;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ListMenteeWaiting extends ServerPacket
{
    private final int PLAYERS_PER_PAGE = 64;
    private final List<Player> _possibleCandiates;
    private final int _page;
    
    public ListMenteeWaiting(final int page, final int minLevel, final int maxLevel) {
        this._possibleCandiates = new ArrayList<Player>();
        this._page = page;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MENTEE_WAITING_LIST);
        this.writeInt(1);
        if (this._possibleCandiates.isEmpty()) {
            this.writeInt(0);
            this.writeInt(0);
            return;
        }
        this.writeInt(this._possibleCandiates.size());
        this.writeInt(this._possibleCandiates.size() % 64);
        for (final Player player : this._possibleCandiates) {
            if (1 <= 64 * this._page && 1 > 64 * (this._page - 1)) {
                this.writeString((CharSequence)player.getName());
                this.writeInt(player.getActiveClass());
                this.writeInt(player.getLevel());
            }
        }
    }
}
