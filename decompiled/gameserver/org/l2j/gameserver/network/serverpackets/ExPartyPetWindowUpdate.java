// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Summon;

public class ExPartyPetWindowUpdate extends ServerPacket
{
    private final Summon _summon;
    
    public ExPartyPetWindowUpdate(final Summon summon) {
        this._summon = summon;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PARTY_PET_WINDOW_UPDATE);
        this.writeInt(this._summon.getObjectId());
        this.writeInt(this._summon.getTemplate().getDisplayId() + 1000000);
        this.writeByte((byte)this._summon.getSummonType());
        this.writeInt(this._summon.getOwner().getObjectId());
        this.writeInt((int)this._summon.getCurrentHp());
        this.writeInt(this._summon.getMaxHp());
        this.writeInt((int)this._summon.getCurrentMp());
        this.writeInt(this._summon.getMaxMp());
    }
}
