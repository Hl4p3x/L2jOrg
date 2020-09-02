// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class SocialAction extends ServerPacket
{
    public static final int LEVEL_UP = 2122;
    private final int _charObjId;
    private final int _actionId;
    
    public SocialAction(final int objectId, final int actionId) {
        this._charObjId = objectId;
        this._actionId = actionId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SOCIAL_ACTION);
        this.writeInt(this._charObjId);
        this.writeInt(this._actionId);
        this.writeInt(0);
    }
}
