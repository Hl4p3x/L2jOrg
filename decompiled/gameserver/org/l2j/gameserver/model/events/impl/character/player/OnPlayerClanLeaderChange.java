// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerClanLeaderChange implements IBaseEvent
{
    private final ClanMember _oldLeader;
    private final ClanMember _newLeader;
    private final Clan _clan;
    
    public OnPlayerClanLeaderChange(final ClanMember oldLeader, final ClanMember newLeader, final Clan clan) {
        this._oldLeader = oldLeader;
        this._newLeader = newLeader;
        this._clan = clan;
    }
    
    public ClanMember getOldLeader() {
        return this._oldLeader;
    }
    
    public ClanMember getNewLeader() {
        return this._newLeader;
    }
    
    public Clan getClan() {
        return this._clan;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_CLAN_LEADER_CHANGE;
    }
}
