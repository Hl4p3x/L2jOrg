// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;

public class StaticObjStatus extends CreatureStatus
{
    public StaticObjStatus(final StaticWorldObject activeChar) {
        super(activeChar);
    }
    
    @Override
    public StaticWorldObject getOwner() {
        return (StaticWorldObject)super.getOwner();
    }
}
