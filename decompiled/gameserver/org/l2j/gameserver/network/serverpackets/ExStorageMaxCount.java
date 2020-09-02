// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExStorageMaxCount extends ServerPacket
{
    private final int _inventory;
    private final int _warehouse;
    private final int _clan;
    private final int _privateSell;
    private final int _privateBuy;
    private final int _receipeD;
    private final int _recipe;
    private final int _inventoryExtraSlots;
    private final int _inventoryQuestItems;
    
    public ExStorageMaxCount(final Player activeChar) {
        this._inventory = activeChar.getInventoryLimit();
        this._warehouse = activeChar.getWareHouseLimit();
        this._privateSell = activeChar.getPrivateSellStoreLimit();
        this._privateBuy = activeChar.getPrivateBuyStoreLimit();
        this._clan = Config.WAREHOUSE_SLOTS_CLAN;
        this._receipeD = activeChar.getDwarfRecipeLimit();
        this._recipe = activeChar.getCommonRecipeLimit();
        this._inventoryExtraSlots = (int)activeChar.getStats().getValue(Stat.INVENTORY_NORMAL, 0.0);
        this._inventoryQuestItems = Config.INVENTORY_MAXIMUM_QUEST_ITEMS;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_STORAGE_MAX_COUNT);
        this.writeInt(this._inventory);
        this.writeInt(this._warehouse);
        this.writeInt(this._clan);
        this.writeInt(this._privateSell);
        this.writeInt(this._privateBuy);
        this.writeInt(this._receipeD);
        this.writeInt(this._recipe);
        this.writeInt(this._inventoryExtraSlots);
        this.writeInt(this._inventoryQuestItems);
        this.writeInt(40);
        this.writeInt(40);
        this.writeInt(100);
    }
}
