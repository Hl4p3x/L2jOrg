// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public class NicknameChanged extends ServerPacket
{
    private final String _title;
    private final int _objectId;
    
    public NicknameChanged(final Creature cha) {
        this._objectId = cha.getObjectId();
        this._title = cha.getTitle();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.NICKNAME_CHANGED);
        this.writeInt(this._objectId);
        this.writeString((CharSequence)this._title);
    }
}
