// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Summon;

public class ExPartyPetWindowDelete extends ServerPacket
{
    private final Summon _summon;
    
    public ExPartyPetWindowDelete(final Summon summon) {
        this._summon = summon;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PARTY_PET_WINDOW_DELETE);
        this.writeInt(this._summon.getObjectId());
        this.writeByte((byte)this._summon.getSummonType());
        this.writeInt(this._summon.getOwner().getObjectId());
    }
}
