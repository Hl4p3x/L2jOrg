// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public final class OnPlayerPressTutorialMark implements IBaseEvent
{
    private final Player _activeChar;
    private final int _markId;
    
    public OnPlayerPressTutorialMark(final Player activeChar, final int markId) {
        this._activeChar = activeChar;
        this._markId = markId;
    }
    
    public Player getPlayer() {
        return this._activeChar;
    }
    
    public int getMarkId() {
        return this._markId;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_PRESS_TUTORIAL_MARK;
    }
}
