// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Door;

public final class DoorStatusUpdate extends ServerPacket
{
    private final Door _door;
    
    public DoorStatusUpdate(final Door door) {
        this._door = door;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.DOOR_STATUS_UPDATE);
        this.writeInt(this._door.getObjectId());
        this.writeInt((int)(this._door.isOpen() ? 0 : 1));
        this.writeInt(this._door.getDamage());
        this.writeInt((int)(this._door.isEnemy() ? 1 : 0));
        this.writeInt(this._door.getId());
        this.writeInt((int)this._door.getCurrentHp());
        this.writeInt(this._door.getMaxHp());
    }
}
