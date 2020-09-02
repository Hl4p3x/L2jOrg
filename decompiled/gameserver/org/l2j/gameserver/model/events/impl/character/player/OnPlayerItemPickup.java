// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerItemPickup implements IBaseEvent
{
    private final Player _activeChar;
    private final Item _item;
    
    public OnPlayerItemPickup(final Player activeChar, final Item item) {
        this._activeChar = activeChar;
        this._item = item;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public Item getItem() {
        return this._item;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_ITEM_PICKUP;
    }
}
