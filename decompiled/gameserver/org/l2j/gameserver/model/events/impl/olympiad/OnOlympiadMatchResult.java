// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.olympiad;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.olympiad.CompetitionType;
import org.l2j.gameserver.model.olympiad.Participant;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnOlympiadMatchResult implements IBaseEvent
{
    private final Participant _winner;
    private final Participant _loser;
    private final CompetitionType _type;
    
    public OnOlympiadMatchResult(final Participant winner, final Participant looser, final CompetitionType type) {
        this._winner = winner;
        this._loser = looser;
        this._type = type;
    }
    
    public Participant getWinner() {
        return this._winner;
    }
    
    public Participant getLoser() {
        return this._loser;
    }
    
    public CompetitionType getCompetitionType() {
        return this._type;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_OLYMPIAD_MATCH_RESULT;
    }
}
