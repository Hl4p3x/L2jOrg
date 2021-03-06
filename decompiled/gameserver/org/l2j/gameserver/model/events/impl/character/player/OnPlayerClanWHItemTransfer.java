// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerClanWHItemTransfer implements IBaseEvent
{
    private final String _process;
    private final Player _activeChar;
    private final Item _item;
    private final long _count;
    private final ItemContainer _container;
    
    public OnPlayerClanWHItemTransfer(final String process, final Player activeChar, final Item item, final long count, final ItemContainer container) {
        this._process = process;
        this._activeChar = activeChar;
        this._item = item;
        this._count = count;
        this._container = container;
    }
    
    public String getProcess() {
        return this._process;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public Item getItem() {
        return this._item;
    }
    
    public long getCount() {
        return this._count;
    }
    
    public ItemContainer getContainer() {
        return this._container;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_CLAN_WH_ITEM_TRANSFER;
    }
}
