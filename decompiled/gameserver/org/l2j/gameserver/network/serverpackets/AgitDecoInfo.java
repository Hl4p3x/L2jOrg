// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.residences.AbstractResidence;

public class AgitDecoInfo extends ServerPacket
{
    private final AbstractResidence _residense;
    
    public AgitDecoInfo(final AbstractResidence residense) {
        this._residense = residense;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.AGIT_DECO_INFO);
        this.writeInt(this._residense.getId());
        for (final ResidenceFunctionType type : ResidenceFunctionType.values()) {
            if (type != ResidenceFunctionType.NONE) {
                this.writeByte((byte)(byte)(this._residense.hasFunction(type) ? 1 : 0));
            }
        }
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
    }
}
