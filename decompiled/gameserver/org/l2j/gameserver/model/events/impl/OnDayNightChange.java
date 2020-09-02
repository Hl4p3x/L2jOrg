// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl;

import org.l2j.gameserver.model.events.EventType;

public class OnDayNightChange implements IBaseEvent
{
    private static final OnDayNightChange NIGHT_CHANGE;
    private static final OnDayNightChange DAY_CHANGE;
    private final boolean _isNight;
    
    private OnDayNightChange(final boolean isNight) {
        this._isNight = isNight;
    }
    
    public static OnDayNightChange of(final boolean isNight) {
        return isNight ? OnDayNightChange.NIGHT_CHANGE : OnDayNightChange.DAY_CHANGE;
    }
    
    public boolean isNight() {
        return this._isNight;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_DAY_NIGHT_CHANGE;
    }
    
    static {
        NIGHT_CHANGE = new OnDayNightChange(true);
        DAY_CHANGE = new OnDayNightChange(false);
    }
}
