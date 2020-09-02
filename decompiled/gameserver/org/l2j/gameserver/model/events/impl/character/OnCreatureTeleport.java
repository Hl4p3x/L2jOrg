// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnCreatureTeleport implements IBaseEvent
{
    private final Creature _creature;
    private final int _destX;
    private final int _destY;
    private final int _destZ;
    private final int _destHeading;
    private final Instance _destInstance;
    
    public OnCreatureTeleport(final Creature creature, final int destX, final int destY, final int destZ, final int destHeading, final Instance destInstance) {
        this._creature = creature;
        this._destX = destX;
        this._destY = destY;
        this._destZ = destZ;
        this._destHeading = destHeading;
        this._destInstance = destInstance;
    }
    
    public Creature getCreature() {
        return this._creature;
    }
    
    public int getDestX() {
        return this._destX;
    }
    
    public int getDestY() {
        return this._destY;
    }
    
    public int getDestZ() {
        return this._destZ;
    }
    
    public int getDestHeading() {
        return this._destHeading;
    }
    
    public Instance getDestInstance() {
        return this._destInstance;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_TELEPORT;
    }
}
