// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class RaidBoss extends Monster
{
    private static final int RAIDBOSS_MAINTENANCE_INTERVAL = 30000;
    private boolean _useRaidCurse;
    
    public RaidBoss(final NpcTemplate template) {
        super(template);
        this._useRaidCurse = true;
        this.setInstanceType(InstanceType.L2RaidBossInstance);
        this.setIsRaid(true);
        this.setLethalable(false);
    }
    
    @Override
    public void onSpawn() {
        super.onSpawn();
        this.setRandomWalking(false);
        this.broadcastPacket(new PlaySound(1, this.getParameters().getString("RaidSpawnMusic", "Rm01_A"), 0, 0, 0, 0, 0));
    }
    
    @Override
    protected int getMaintenanceInterval() {
        return 30000;
    }
    
    @Override
    protected void startMaintenanceTask() {
        this._maintenanceTask = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate(() -> this.checkAndReturnToSpawn(), 60000L, (long)(this.getMaintenanceInterval() + Rnd.get(5000)));
    }
    
    protected void checkAndReturnToSpawn() {
        if (this.isDead() || this.isMovementDisabled() || !this.canReturnToSpawnPoint()) {
            return;
        }
        final Spawn spawn = this.getSpawn();
        if (spawn == null) {
            return;
        }
        final int spawnX = spawn.getX();
        final int spawnY = spawn.getY();
        final int spawnZ = spawn.getZ();
        if (!this.isInCombat() && !this.isMovementDisabled() && !MathUtil.isInsideRadius3D(this, spawnX, spawnY, spawnZ, Math.max(Config.MAX_DRIFT_RANGE, 200))) {
            this.teleToLocation(spawnX, spawnY, spawnZ);
        }
    }
    
    @Override
    public int getVitalityPoints(final int level, final double exp, final boolean isBoss) {
        return -super.getVitalityPoints(level, exp, isBoss);
    }
    
    @Override
    public boolean useVitalityRate() {
        return Config.RAIDBOSS_USE_VITALITY;
    }
    
    public void setUseRaidCurse(final boolean val) {
        this._useRaidCurse = val;
    }
    
    @Override
    public boolean giveRaidCurse() {
        return this._useRaidCurse;
    }
}
