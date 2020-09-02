// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.templates.PlayerTemplate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerProfessionChange implements IBaseEvent
{
    private final Player _activeChar;
    private final PlayerTemplate _template;
    private final boolean _isSubClass;
    
    public OnPlayerProfessionChange(final Player activeChar, final PlayerTemplate template, final boolean isSubClass) {
        this._activeChar = activeChar;
        this._template = template;
        this._isSubClass = isSubClass;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public PlayerTemplate getTemplate() {
        return this._template;
    }
    
    public boolean isSubClass() {
        return this._isSubClass;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_PROFESSION_CHANGE;
    }
}
