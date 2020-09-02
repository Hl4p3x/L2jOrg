// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerDelete implements IBaseEvent
{
    private final int _objectId;
    private final String _name;
    private final GameClient _client;
    
    public OnPlayerDelete(final int objectId, final String name, final GameClient client) {
        this._objectId = objectId;
        this._name = name;
        this._client = client;
    }
    
    public int getObjectId() {
        return this._objectId;
    }
    
    public String getName() {
        return this._name;
    }
    
    public GameClient getClient() {
        return this._client;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_DELETE;
    }
}
