// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ChairSit extends ServerPacket
{
    private final Player _activeChar;
    private final int _staticObjectId;
    
    public ChairSit(final Player player, final int staticObjectId) {
        this._activeChar = player;
        this._staticObjectId = staticObjectId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CHAIR_SIT);
        this.writeInt(this._activeChar.getObjectId());
        this.writeInt(this._staticObjectId);
    }
}
