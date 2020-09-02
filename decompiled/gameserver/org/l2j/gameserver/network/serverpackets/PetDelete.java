// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class PetDelete extends ServerPacket
{
    private final int _petType;
    private final int _petObjId;
    
    public PetDelete(final int petType, final int petObjId) {
        this._petType = petType;
        this._petObjId = petObjId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PET_DELETE);
        this.writeInt(this._petType);
        this.writeInt(this._petObjId);
    }
}
