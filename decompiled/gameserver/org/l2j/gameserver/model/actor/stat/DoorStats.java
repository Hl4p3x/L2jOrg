// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Door;

public class DoorStats extends CreatureStats
{
    private int _upgradeHpRatio;
    
    public DoorStats(final Door activeChar) {
        super(activeChar);
        this._upgradeHpRatio = 1;
    }
    
    @Override
    public Door getCreature() {
        return (Door)super.getCreature();
    }
    
    @Override
    public int getMaxHp() {
        return super.getMaxHp() * this._upgradeHpRatio;
    }
    
    public int getUpgradeHpRatio() {
        return this._upgradeHpRatio;
    }
    
    public void setUpgradeHpRatio(final int ratio) {
        this._upgradeHpRatio = ratio;
    }
}
