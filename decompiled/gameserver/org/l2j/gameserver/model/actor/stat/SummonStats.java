// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.Summon;

public class SummonStats extends PlayableStats
{
    public SummonStats(final Summon activeChar) {
        super(activeChar);
    }
    
    @Override
    public Summon getCreature() {
        return (Summon)super.getCreature();
    }
    
    @Override
    public double getRunSpeed() {
        return Math.min(super.getRunSpeed(), Config.MAX_RUN_SPEED + 50);
    }
    
    @Override
    public double getWalkSpeed() {
        return Math.min(super.getWalkSpeed(), Config.MAX_RUN_SPEED + 50);
    }
}
