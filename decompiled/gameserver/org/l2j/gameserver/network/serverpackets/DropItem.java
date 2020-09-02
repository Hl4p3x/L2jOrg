// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;

public class DropItem extends ServerPacket
{
    private final Item _item;
    private final int _charObjId;
    
    public DropItem(final Item item, final int playerObjId) {
        this._item = item;
        this._charObjId = playerObjId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.DROP_ITEM);
        this.writeInt(this._charObjId);
        this.writeInt(this._item.getObjectId());
        this.writeInt(this._item.getDisplayId());
        this.writeInt(this._item.getX());
        this.writeInt(this._item.getY());
        this.writeInt(this._item.getZ());
        this.writeByte((byte)(byte)(this._item.isStackable() ? 1 : 0));
        this.writeLong(this._item.getCount());
        this.writeByte((byte)0);
        this.writeByte((byte)this._item.getEnchantLevel());
        this.writeByte((byte)(byte)((this._item.getAugmentation() != null) ? 1 : 0));
        this.writeByte((byte)this._item.getSpecialAbilities().size());
    }
}
