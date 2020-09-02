// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayableExpChanged implements IBaseEvent
{
    private final Playable _activeChar;
    private final long _oldExp;
    private final long _newExp;
    
    public OnPlayableExpChanged(final Playable activeChar, final long oldExp, final long newExp) {
        this._activeChar = activeChar;
        this._oldExp = oldExp;
        this._newExp = newExp;
    }
    
    public Playable getPlayable() {
        return this._activeChar;
    }
    
    public long getOldExp() {
        return this._oldExp;
    }
    
    public long getNewExp() {
        return this._newExp;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYABLE_EXP_CHANGED;
    }
}
