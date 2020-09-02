// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public final class PartySmallWindowDelete extends ServerPacket
{
    private final Player _member;
    
    public PartySmallWindowDelete(final Player member) {
        this._member = member;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PARTY_SMALL_WINDOW_DELETE);
        this.writeInt(this._member.getObjectId());
        this.writeString((CharSequence)this._member.getName());
    }
}
