// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;

public class PackageSendableList extends AbstractItemPacket
{
    private final Collection<Item> _items;
    private final int _objectId;
    private final long _adena;
    private final int _sendType;
    
    public PackageSendableList(final int sendType, final Player player, final int objectId) {
        this._sendType = sendType;
        this._items = player.getInventory().getAvailableItems(true, true, true);
        this._objectId = objectId;
        this._adena = player.getAdena();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PACKAGE_SENDABLE_LIST);
        this.writeByte((byte)this._sendType);
        if (this._sendType == 2) {
            this.writeInt(this._items.size());
            this.writeInt(this._items.size());
            for (final Item item : this._items) {
                this.writeItem(item);
                this.writeInt(item.getObjectId());
            }
        }
        else {
            this.writeInt(this._objectId);
            this.writeLong(this._adena);
            this.writeInt(this._items.size());
        }
    }
}
