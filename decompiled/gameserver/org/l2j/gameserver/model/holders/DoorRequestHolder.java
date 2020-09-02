// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.actor.instance.Door;

public class DoorRequestHolder
{
    private final Door _target;
    
    public DoorRequestHolder(final Door door) {
        this._target = door;
    }
    
    public Door getDoor() {
        return this._target;
    }
}
