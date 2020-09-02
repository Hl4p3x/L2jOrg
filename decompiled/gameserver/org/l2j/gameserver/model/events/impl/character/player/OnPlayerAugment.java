// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerAugment implements IBaseEvent
{
    private final Player _activeChar;
    private final Item _item;
    private final VariationInstance _augmentation;
    private final boolean _isAugment;
    
    public OnPlayerAugment(final Player activeChar, final Item item, final VariationInstance augment, final boolean isAugment) {
        this._activeChar = activeChar;
        this._item = item;
        this._augmentation = augment;
        this._isAugment = isAugment;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public Item getItem() {
        return this._item;
    }
    
    public VariationInstance getAugmentation() {
        return this._augmentation;
    }
    
    public boolean isAugment() {
        return this._isAugment;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_AUGMENT;
    }
}
