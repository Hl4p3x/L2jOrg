// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public final class GrandBoss extends Monster
{
    private static final int BOSS_MAINTENANCE_INTERVAL = 10000;
    private boolean _useRaidCurse;
    
    public GrandBoss(final NpcTemplate template) {
        super(template);
        this._useRaidCurse = true;
        this.setInstanceType(InstanceType.L2GrandBossInstance);
        this.setIsRaid(true);
        this.setLethalable(false);
    }
    
    @Override
    protected int getMaintenanceInterval() {
        return 10000;
    }
    
    @Override
    public void onSpawn() {
        this.setRandomWalking(false);
        super.onSpawn();
    }
    
    @Override
    public int getVitalityPoints(final int level, final double exp, final boolean isBoss) {
        return -super.getVitalityPoints(level, exp, isBoss);
    }
    
    @Override
    public boolean useVitalityRate() {
        return false;
    }
    
    public void setUseRaidCurse(final boolean val) {
        this._useRaidCurse = val;
    }
    
    @Override
    public boolean giveRaidCurse() {
        return this._useRaidCurse;
    }
}
