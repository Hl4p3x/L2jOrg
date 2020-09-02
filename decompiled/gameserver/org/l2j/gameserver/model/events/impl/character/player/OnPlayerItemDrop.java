// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerItemDrop implements IBaseEvent
{
    private final Player _activeChar;
    private final Item _item;
    private final Location _loc;
    
    public OnPlayerItemDrop(final Player activeChar, final Item item, final Location loc) {
        this._activeChar = activeChar;
        this._item = item;
        this._loc = loc;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public Item getItem() {
        return this._item;
    }
    
    public Location getLocation() {
        return this._loc;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_ITEM_DROP;
    }
}
