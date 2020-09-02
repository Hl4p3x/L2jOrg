// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Summon;

public class PetStatusShow extends ServerPacket
{
    private final int _summonType;
    private final int _summonObjectId;
    
    public PetStatusShow(final Summon summon) {
        this._summonType = summon.getSummonType();
        this._summonObjectId = summon.getObjectId();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PET_STATUS_SHOW);
        this.writeInt(this._summonType);
        this.writeInt(this._summonObjectId);
    }
}
