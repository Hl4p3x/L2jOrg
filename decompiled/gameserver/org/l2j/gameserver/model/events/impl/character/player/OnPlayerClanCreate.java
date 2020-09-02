// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerClanCreate implements IBaseEvent
{
    private final Player _activeChar;
    private final Clan _clan;
    
    public OnPlayerClanCreate(final Player activeChar, final Clan clan) {
        this._activeChar = activeChar;
        this._clan = clan;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public Clan getClan() {
        return this._clan;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_CLAN_CREATE;
    }
}
