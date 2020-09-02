// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerFameChanged implements IBaseEvent
{
    private final Player _activeChar;
    private final int _oldFame;
    private final int _newFame;
    
    public OnPlayerFameChanged(final Player activeChar, final int oldFame, final int newFame) {
        this._activeChar = activeChar;
        this._oldFame = oldFame;
        this._newFame = newFame;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public int getOldFame() {
        return this._oldFame;
    }
    
    public int getNewFame() {
        return this._newFame;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_FAME_CHANGED;
    }
}
