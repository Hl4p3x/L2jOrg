// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerTransform implements IBaseEvent
{
    private final Player _activeChar;
    private final int _transformId;
    
    public OnPlayerTransform(final Player activeChar, final int transformId) {
        this._activeChar = activeChar;
        this._transformId = transformId;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public int getTransformId() {
        return this._transformId;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_TRANSFORM;
    }
}
