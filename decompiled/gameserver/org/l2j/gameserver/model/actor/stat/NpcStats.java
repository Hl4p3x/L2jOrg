// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;

public class NpcStats extends CreatureStats
{
    public NpcStats(final Npc activeChar) {
        super(activeChar);
    }
    
    @Override
    public byte getLevel() {
        return this.getCreature().getTemplate().getLevel();
    }
    
    @Override
    public Npc getCreature() {
        return (Npc)super.getCreature();
    }
}
