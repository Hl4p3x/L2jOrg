// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.request.AbstractRequest;

public class DoorRequest extends AbstractRequest
{
    private final Door _target;
    
    public DoorRequest(final Player player, final Door door) {
        super(player);
        this._target = door;
    }
    
    public Door getDoor() {
        return this._target;
    }
    
    @Override
    public boolean isUsingItem(final int objectId) {
        return false;
    }
}
