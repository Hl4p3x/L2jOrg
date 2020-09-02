// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExTutorialShowId extends ServerPacket
{
    private final int _id;
    
    public ExTutorialShowId(final int id) {
        this._id = id;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_TUTORIAL_SHOW_ID);
        this.writeInt(this._id);
    }
}
