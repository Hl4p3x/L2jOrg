// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public class ExTacticalSign extends ServerPacket
{
    private final Creature _target;
    private final int _tokenId;
    
    public ExTacticalSign(final Creature target, final int tokenId) {
        this._target = target;
        this._tokenId = tokenId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_TACTICAL_SIGN);
        this.writeInt(this._target.getObjectId());
        this.writeInt(this._tokenId);
    }
}
