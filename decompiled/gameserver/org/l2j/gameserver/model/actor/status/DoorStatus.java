// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Door;

public class DoorStatus extends CreatureStatus
{
    public DoorStatus(final Door activeChar) {
        super(activeChar);
    }
    
    @Override
    public Door getOwner() {
        return (Door)super.getOwner();
    }
}
