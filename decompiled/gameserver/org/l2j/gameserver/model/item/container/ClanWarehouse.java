// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.container;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemTransfer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemDestroy;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemAdd;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Clan;

public final class ClanWarehouse extends Warehouse
{
    private final Clan _clan;
    
    public ClanWarehouse(final Clan clan) {
        this._clan = clan;
    }
    
    @Override
    public String getName() {
        return "ClanWarehouse";
    }
    
    @Override
    public int getOwnerId() {
        return this._clan.getId();
    }
    
    @Override
    public Player getOwner() {
        return this._clan.getLeader().getPlayerInstance();
    }
    
    public ItemLocation getBaseLocation() {
        return ItemLocation.CLANWH;
    }
    
    @Override
    public boolean validateCapacity(final long slots) {
        return this.items.size() + slots <= Config.WAREHOUSE_SLOTS_CLAN;
    }
    
    @Override
    public Item addItem(final String process, final int itemId, final long count, final Player actor, final Object reference) {
        final Item item = super.addItem(process, itemId, count, actor, reference);
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemAdd(process, actor, item, this), item.getTemplate());
        return item;
    }
    
    @Override
    public Item addItem(final String process, final Item item, final Player actor, final Object reference) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemAdd(process, actor, item, this), item.getTemplate());
        return super.addItem(process, item, actor, reference);
    }
    
    @Override
    public Item destroyItem(final String process, final Item item, final long count, final Player actor, final Object reference) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemDestroy(process, actor, item, count, this), item.getTemplate());
        return super.destroyItem(process, item, count, actor, reference);
    }
    
    @Override
    public Item transferItem(final String process, final int objectId, final long count, final ItemContainer target, final Player actor, final Object reference) {
        final Item item = this.getItemByObjectId(objectId);
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemTransfer(process, actor, item, count, target), item.getTemplate());
        return super.transferItem(process, objectId, count, target, actor, reference);
    }
    
    @Override
    public WarehouseType getType() {
        return WarehouseType.CLAN;
    }
}
