// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExFishingEnd extends ServerPacket
{
    private final Player _player;
    private final FishingEndReason _reason;
    
    public ExFishingEnd(final Player player, final FishingEndReason reason) {
        this._player = player;
        this._reason = reason;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_FISHING_END);
        this.writeInt(this._player.getObjectId());
        this.writeByte((byte)this._reason.getReason());
    }
    
    public enum FishingEndReason
    {
        LOSE(0), 
        WIN(1), 
        STOP(2);
        
        private final int _reason;
        
        private FishingEndReason(final int reason) {
            this._reason = reason;
        }
        
        public int getReason() {
            return this._reason;
        }
    }
    
    public enum FishingEndType
    {
        PLAYER_STOP, 
        PLAYER_CANCEL, 
        ERROR;
    }
}
