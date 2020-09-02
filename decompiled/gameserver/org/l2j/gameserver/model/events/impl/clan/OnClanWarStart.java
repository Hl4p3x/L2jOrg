// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.clan;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnClanWarStart implements IBaseEvent
{
    private final Clan _clan1;
    private final Clan _clan2;
    
    public OnClanWarStart(final Clan clan1, final Clan clan2) {
        this._clan1 = clan1;
        this._clan2 = clan2;
    }
    
    public Clan getClan1() {
        return this._clan1;
    }
    
    public Clan getClan2() {
        return this._clan2;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CLAN_WAR_START;
    }
}
