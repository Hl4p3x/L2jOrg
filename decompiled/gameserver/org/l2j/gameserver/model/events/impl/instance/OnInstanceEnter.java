// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.instance;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public final class OnInstanceEnter implements IBaseEvent
{
    private final Player _player;
    private final Instance _instance;
    
    public OnInstanceEnter(final Player player, final Instance instance) {
        this._player = player;
        this._instance = instance;
    }
    
    public Player getPlayer() {
        return this._player;
    }
    
    public Instance getInstanceWorld() {
        return this._instance;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_INSTANCE_ENTER;
    }
}
