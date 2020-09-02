// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.concurrent.Future;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Creature;
import java.util.function.Supplier;
import java.util.Objects;
import org.l2j.gameserver.world.zone.TaskZoneSettings;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.AbstractZoneSettings;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.world.zone.Zone;

public class DamageZone extends Zone
{
    private int damageHPPerSec;
    private int damageMPPerSec;
    private int castleId;
    private Castle castle;
    private int startTask;
    private int reuseTask;
    
    public DamageZone(final int id) {
        super(id);
        this.damageHPPerSec = 200;
        this.startTask = 10;
        this.reuseTask = 5000;
        this.setTargetType(InstanceType.Playable);
        final AbstractZoneSettings settings = Objects.requireNonNullElseGet(ZoneManager.getSettings(this.getName()), TaskZoneSettings::new);
        this.setSettings(settings);
    }
    
    @Override
    public TaskZoneSettings getSettings() {
        return (TaskZoneSettings)super.getSettings();
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        if (name.equals("dmgHPSec")) {
            this.damageHPPerSec = Integer.parseInt(value);
        }
        else if (name.equals("dmgMPSec")) {
            this.damageMPPerSec = Integer.parseInt(value);
        }
        else if (name.equals("castleId")) {
            this.castleId = Integer.parseInt(value);
        }
        else if (name.equalsIgnoreCase("initialDelay")) {
            this.startTask = Integer.parseInt(value);
        }
        else if (name.equalsIgnoreCase("reuse")) {
            this.reuseTask = Integer.parseInt(value);
        }
        else {
            super.setParameter(name, value);
        }
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        if (this.getSettings().getTask() == null && (this.damageHPPerSec != 0 || this.damageMPPerSec != 0)) {
            final Player player = creature.getActingPlayer();
            if (this.getCastle() != null && (!this.getCastle().getSiege().isInProgress() || player == null || player.getSiegeState() == 2)) {
                return;
            }
            synchronized (this) {
                if (this.getSettings().getTask() == null) {
                    this.getSettings().setTask(ThreadPool.scheduleAtFixedRate((Runnable)new ApplyDamage(), (long)this.startTask, (long)this.reuseTask));
                }
            }
        }
    }
    
    @Override
    protected void onExit(final Creature character) {
        if (this.creatures.isEmpty() && this.getSettings().getTask() != null) {
            this.getSettings().clear();
        }
    }
    
    protected Castle getCastle() {
        if (this.castleId > 0 && this.castle == null) {
            this.castle = CastleManager.getInstance().getCastleById(this.castleId);
        }
        return this.castle;
    }
    
    private final class ApplyDamage implements Runnable
    {
        @Override
        public void run() {
            if (!DamageZone.this.isEnabled()) {
                return;
            }
            if (Objects.nonNull(DamageZone.this.castle) && !DamageZone.this.castle.getSiege().isInProgress()) {
                DamageZone.this.getSettings().clear();
                return;
            }
            DamageZone.this.forEachCreature(this::doDamage, this::canReceiveDamage);
        }
        
        private boolean canReceiveDamage(final Creature creature) {
            return !creature.isDead() && (!GameUtils.isPlayer(creature) || !((Player)creature).isInSiege() || ((Player)creature).getSiegeState() != 2);
        }
        
        private void doDamage(final Creature creature) {
            final double multiplier = 1.0 + creature.getStats().getValue(Stat.DAMAGE_ZONE_VULN, 0.0) / 100.0;
            if (DamageZone.this.damageHPPerSec != 0) {
                creature.reduceCurrentHp(DamageZone.this.damageHPPerSec * multiplier, null, null, DamageInfo.DamageType.ZONE);
            }
            if (DamageZone.this.damageMPPerSec != 0) {
                creature.reduceCurrentMp(DamageZone.this.damageMPPerSec * multiplier);
            }
        }
    }
}
