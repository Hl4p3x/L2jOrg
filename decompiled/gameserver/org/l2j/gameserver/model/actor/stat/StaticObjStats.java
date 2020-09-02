// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;

public class StaticObjStats extends CreatureStats
{
    public StaticObjStats(final StaticWorldObject activeChar) {
        super(activeChar);
    }
    
    @Override
    public StaticWorldObject getCreature() {
        return (StaticWorldObject)super.getCreature();
    }
    
    @Override
    public final byte getLevel() {
        return (byte)this.getCreature().getLevel();
    }
}
