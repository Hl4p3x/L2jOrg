// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.teleporter.TeleportLocation;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public final class OnNpcTeleportRequest implements IBaseEvent
{
    private final Player _player;
    private final Npc _npc;
    private final TeleportLocation _loc;
    
    public OnNpcTeleportRequest(final Player player, final Npc npc, final TeleportLocation loc) {
        this._player = player;
        this._npc = npc;
        this._loc = loc;
    }
    
    public Player getPlayer() {
        return this._player;
    }
    
    public Npc getNpc() {
        return this._npc;
    }
    
    public TeleportLocation getLocation() {
        return this._loc;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_NPC_TELEPORT_REQUEST;
    }
}
