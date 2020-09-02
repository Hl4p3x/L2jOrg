// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class Dice extends ServerPacket
{
    private final int _charObjId;
    private final int _itemId;
    private final int _number;
    private final int _x;
    private final int _y;
    private final int _z;
    
    public Dice(final int charObjId, final int itemId, final int number, final int x, final int y, final int z) {
        this._charObjId = charObjId;
        this._itemId = itemId;
        this._number = number;
        this._x = x;
        this._y = y;
        this._z = z;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.DICE);
        this.writeInt(this._charObjId);
        this.writeInt(this._itemId);
        this.writeInt(this._number);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
    }
}
