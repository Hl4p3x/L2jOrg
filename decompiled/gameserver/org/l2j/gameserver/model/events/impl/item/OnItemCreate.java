// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.item;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnItemCreate implements IBaseEvent
{
    private final String _process;
    private final Item _item;
    private final Creature _activeChar;
    private final Object _reference;
    
    public OnItemCreate(final String process, final Item item, final Creature actor, final Object reference) {
        this._process = process;
        this._item = item;
        this._activeChar = actor;
        this._reference = reference;
    }
    
    public String getProcess() {
        return this._process;
    }
    
    public Item getItem() {
        return this._item;
    }
    
    public Creature getActiveChar() {
        return this._activeChar;
    }
    
    public Object getReference() {
        return this._reference;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_ITEM_CREATE;
    }
}
