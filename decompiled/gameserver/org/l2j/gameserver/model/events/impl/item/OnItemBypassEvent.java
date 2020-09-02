// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.item;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnItemBypassEvent implements IBaseEvent
{
    private final Item _item;
    private final Player _activeChar;
    private final String _event;
    
    public OnItemBypassEvent(final Item item, final Player activeChar, final String event) {
        this._item = item;
        this._activeChar = activeChar;
        this._event = event;
    }
    
    public Item getItem() {
        return this._item;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public String getEvent() {
        return this._event;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_ITEM_BYPASS_EVENT;
    }
}
