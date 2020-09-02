// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExReplyPostItemList extends AbstractItemPacket
{
    private final int _sendType;
    private final Player _activeChar;
    private final Collection<Item> _itemList;
    
    public ExReplyPostItemList(final int sendType, final Player activeChar) {
        this._sendType = sendType;
        this._activeChar = activeChar;
        this._itemList = this._activeChar.getInventory().getAvailableItems(true, false, false);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_REPLY_POST_ITEM_LIST);
        this.writeByte((byte)this._sendType);
        this.writeInt(this._itemList.size());
        if (this._sendType == 2) {
            this.writeInt(this._itemList.size());
            for (final Item item : this._itemList) {
                this.writeItem(item);
            }
        }
    }
}
