// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Door;

public final class DoorInfo extends ServerPacket
{
    private final Door _door;
    
    public DoorInfo(final Door door) {
        this._door = door;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.DOOR_INFO);
        this.writeInt(this._door.getObjectId());
        this.writeInt(this._door.getId());
    }
}
