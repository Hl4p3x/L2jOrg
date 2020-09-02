// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public class ChangeMoveType extends ServerPacket
{
    public static final int WALK = 0;
    public static final int RUN = 1;
    private final int _charObjId;
    private final boolean _running;
    
    public ChangeMoveType(final Creature character) {
        this._charObjId = character.getObjectId();
        this._running = character.isRunning();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CHANGE_MOVE_TYPE);
        this.writeInt(this._charObjId);
        this.writeInt((int)(this._running ? 1 : 0));
        this.writeInt(0);
    }
}
