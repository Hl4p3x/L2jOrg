// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public final class OnPlayerSocialAction implements IBaseEvent
{
    private final Player _activeChar;
    private final int _socialActionId;
    
    public OnPlayerSocialAction(final Player activeChar, final int socialActionId) {
        this._activeChar = activeChar;
        this._socialActionId = socialActionId;
    }
    
    public final Player getActiveChar() {
        return this._activeChar;
    }
    
    public final int getSocialActionId() {
        return this._socialActionId;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_SOCIAL_ACTION;
    }
}
