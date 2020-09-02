// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import java.util.ArrayList;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;
import org.slf4j.Logger;

public final class WareHouseWithdrawalList extends AbstractItemPacket
{
    private static final Logger LOGGER;
    public static final int PRIVATE = 1;
    public static final int CLAN = 2;
    public static final int CASTLE = 3;
    public static final int FREIGHT = 1;
    private final int _sendType;
    private final int _invSize;
    private final List<Integer> _itemsStackable;
    private Player _activeChar;
    private long _playerAdena;
    private Collection<Item> _items;
    private int _whType;
    
    public WareHouseWithdrawalList(final int sendType, final Player player, final int type) {
        this._itemsStackable = new ArrayList<Integer>();
        this._sendType = sendType;
        this._activeChar = player;
        this._whType = type;
        this._playerAdena = this._activeChar.getAdena();
        this._invSize = player.getInventory().getSize();
        if (this._activeChar.getActiveWarehouse() == null) {
            WareHouseWithdrawalList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._activeChar.getName()));
            return;
        }
        this._items = this._activeChar.getActiveWarehouse().getItems();
        for (final Item item : this._items) {
            if (item.isStackable()) {
                this._itemsStackable.add(item.getDisplayId());
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.WAREHOUSE_WITHDRAW_LIST);
        this.writeByte((byte)this._sendType);
        if (this._sendType == 2) {
            this.writeShort((short)0);
            this.writeInt(this._invSize);
            this.writeInt(this._items.size());
            for (final Item item : this._items) {
                this.writeItem(item);
                this.writeInt(item.getObjectId());
                this.writeInt(0);
                this.writeInt(0);
            }
        }
        else {
            this.writeShort((short)this._whType);
            this.writeLong(this._playerAdena);
            this.writeInt(this._invSize);
            this.writeInt(this._items.size());
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)WareHouseWithdrawalList.class);
    }
}
