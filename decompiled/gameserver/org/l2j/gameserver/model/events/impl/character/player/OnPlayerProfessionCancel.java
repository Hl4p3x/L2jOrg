// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerProfessionCancel implements IBaseEvent
{
    private final Player _activeChar;
    private final int _classId;
    
    public OnPlayerProfessionCancel(final Player activeChar, final int classId) {
        this._activeChar = activeChar;
        this._classId = classId;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public int getClassId() {
        return this._classId;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_PROFESSION_CANCEL;
    }
}
