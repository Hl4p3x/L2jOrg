// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;

public final class SpawnItem extends ServerPacket
{
    private final Item _item;
    
    public SpawnItem(final Item item) {
        this._item = item;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SPAWN_ITEM);
        this.writeInt(this._item.getObjectId());
        this.writeInt(this._item.getDisplayId());
        this.writeInt(this._item.getX());
        this.writeInt(this._item.getY());
        this.writeInt(this._item.getZ());
        this.writeInt((int)(this._item.isStackable() ? 1 : 0));
        this.writeLong(this._item.getCount());
        this.writeInt(0);
        this.writeByte((byte)this._item.getEnchantLevel());
        this.writeByte((byte)(byte)((this._item.getAugmentation() != null) ? 1 : 0));
        this.writeByte((byte)this._item.getSpecialAbilities().size());
    }
}
