// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerHennaRemove implements IBaseEvent
{
    private final Player _activeChar;
    private final Henna _henna;
    
    public OnPlayerHennaRemove(final Player activeChar, final Henna henna) {
        this._activeChar = activeChar;
        this._henna = henna;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public Henna getHenna() {
        return this._henna;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_HENNA_REMOVE;
    }
}
