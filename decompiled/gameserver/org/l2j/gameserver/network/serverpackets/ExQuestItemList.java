// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.function.Predicate;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExQuestItemList extends AbstractItemPacket
{
    private final int _sendType;
    private final Player _activeChar;
    private final Collection<Item> _items;
    
    public ExQuestItemList(final int sendType, final Player activeChar) {
        this._sendType = sendType;
        this._activeChar = activeChar;
        this._items = activeChar.getInventory().getItems(Item::isQuestItem, (Predicate<Item>[])new Predicate[0]);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_QUEST_ITEMLIST);
        this.writeByte((byte)this._sendType);
        if (this._sendType == 2) {
            this.writeInt(this._items.size());
        }
        else {
            this.writeShort((short)0);
        }
        this.writeInt(this._items.size());
        for (final Item item : this._items) {
            this.writeItem(item);
        }
        this.writeInventoryBlock(this._activeChar.getInventory());
    }
}
