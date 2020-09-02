// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExVoteSystemInfo extends ServerPacket
{
    private final int _recomLeft;
    private final int _recomHave;
    private final int _bonusTime;
    private final int _bonusVal;
    private final int _bonusType;
    
    public ExVoteSystemInfo(final Player player) {
        this._recomLeft = player.getRecomLeft();
        this._recomHave = player.getRecomHave();
        this._bonusTime = 0;
        this._bonusVal = 0;
        this._bonusType = 0;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_VOTE_SYSTEM_INFO);
        this.writeInt(this._recomLeft);
        this.writeInt(this._recomHave);
        this.writeInt(this._bonusTime);
        this.writeInt(this._bonusVal);
        this.writeInt(this._bonusType);
    }
}
