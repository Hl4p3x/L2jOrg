// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerAbilityPointsChanged implements IBaseEvent
{
    private final Player _activeChar;
    private final int _newAbilityPoints;
    private final int _oldAbilityPoints;
    
    public OnPlayerAbilityPointsChanged(final Player activeChar, final int newAbilityPoints, final int oldAbilityPoints) {
        this._activeChar = activeChar;
        this._newAbilityPoints = newAbilityPoints;
        this._oldAbilityPoints = oldAbilityPoints;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public long getNewAbilityPoints() {
        return this._newAbilityPoints;
    }
    
    public long getOldAbilityPoints() {
        return this._oldAbilityPoints;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_ABILITY_POINTS_CHANGED;
    }
}
