// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.entity.Siegable;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.function.Function;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.AbstractZoneSettings;
import java.util.Objects;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.Zone;

public class SiegeZone extends Zone
{
    private static final int DISMOUNT_DELAY = 5;
    
    public SiegeZone(final int id) {
        super(id);
        AbstractZoneSettings settings = ZoneManager.getSettings(this.getName());
        if (Objects.isNull(settings)) {
            settings = new Settings();
        }
        this.setSettings(settings);
    }
    
    @Override
    public Settings getSettings() {
        return (Settings)super.getSettings();
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        if (name.equals("castleId")) {
            if (this.getSettings().getSiegeableId() != -1) {
                throw new IllegalArgumentException("Siege object already defined!");
            }
            this.getSettings().setSiegeableId(Integer.parseInt(value));
        }
        else if (name.equals("fortId")) {
            if (this.getSettings().getSiegeableId() != -1) {
                throw new IllegalArgumentException("Siege object already defined!");
            }
            this.getSettings().setSiegeableId(Integer.parseInt(value));
        }
        else {
            super.setParameter(name, value);
        }
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        if (this.getSettings().isActiveSiege()) {
            creature.setInsideZone(ZoneType.PVP, true);
            creature.setInsideZone(ZoneType.SIEGE, true);
            creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, true);
            if (GameUtils.isPlayer(creature)) {
                final Player player = creature.getActingPlayer();
                if (player.isRegisteredOnThisSiegeField(this.getSettings().getSiegeableId())) {
                    player.setIsInSiege(true);
                    final Siegable siegable;
                    if ((siegable = this.getSettings().getSiege()).giveFame() && siegable.getFameFrequency() > 0) {
                        player.startFameTask(siegable.getFameFrequency() * 1000, siegable.getFameAmount());
                    }
                }
                creature.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
                if (!Config.ALLOW_WYVERN_DURING_SIEGE && player.getMountType() == MountType.WYVERN) {
                    player.sendPacket(SystemMessageId.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN_YOU_WILL_BE_DISMOUNTED_FROM_YOUR_WYVERN_IF_YOU_DO_NOT_LEAVE);
                    player.enteredNoLanding(5);
                }
                if (!Config.ALLOW_MOUNTS_DURING_SIEGE && player.isMounted()) {
                    player.dismount();
                }
                if (!Config.ALLOW_MOUNTS_DURING_SIEGE && player.getTransformation().map((Function<? super Transform, ? extends Boolean>)Transform::isRiding).orElse(false)) {
                    player.untransform();
                }
            }
        }
    }
    
    @Override
    protected void onExit(final Creature creature) {
        creature.setInsideZone(ZoneType.PVP, false);
        creature.setInsideZone(ZoneType.SIEGE, false);
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, false);
        if (this.getSettings().isActiveSiege() && GameUtils.isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
            if (player.getMountType() == MountType.WYVERN) {
                player.exitedNoLanding();
            }
            if (player.getPvpFlag() == 0) {
                player.startPvPFlag();
            }
        }
        if (GameUtils.isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            player.stopFameTask();
            player.setIsInSiege(false);
        }
    }
    
    public void onDieInside(final Creature creature) {
        if (this.getSettings().isActiveSiege() && GameUtils.isPlayer(creature) && creature.getActingPlayer().isRegisteredOnThisSiegeField(this.getSettings().getSiegeableId())) {
            int lvl = 1;
            final BuffInfo info = creature.getEffectList().getBuffInfoBySkillId(5660);
            if (info != null) {
                lvl = Math.min(lvl + info.getSkill().getLevel(), 5);
            }
            final Skill skill = SkillEngine.getInstance().getSkill(5660, lvl);
            if (skill != null) {
                skill.applyEffects(creature, creature);
            }
        }
    }
    
    @Override
    public void onPlayerLogoutInside(final Player player) {
        if (player.getClanId() != this.getSettings().getSiegeableId()) {
            player.teleToLocation(TeleportWhereType.TOWN);
        }
    }
    
    public void updateZoneStatusForCharactersInside() {
        if (this.getSettings().isActiveSiege()) {
            this.forEachCreature(this::onEnter);
        }
        else {
            Player player;
            this.forEachCreature(creature -> {
                creature.setInsideZone(ZoneType.PVP, false);
                creature.setInsideZone(ZoneType.SIEGE, false);
                creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, false);
                if (GameUtils.isPlayer(creature)) {
                    player = creature.getActingPlayer();
                    creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
                    player.stopFameTask();
                    if (player.getMountType() == MountType.WYVERN) {
                        player.exitedNoLanding();
                    }
                }
            });
        }
    }
    
    public int getSiegeObjectId() {
        return this.getSettings().getSiegeableId();
    }
    
    public boolean isActive() {
        return this.getSettings().isActiveSiege();
    }
    
    public void setIsActive(final boolean val) {
        this.getSettings().setActiveSiege(val);
    }
    
    public void setSiegeInstance(final Siegable siege) {
        this.getSettings().setSiege(siege);
    }
    
    public void banishForeigners(final int owningClanId) {
        this.forEachPlayer(p -> p.teleToLocation(TeleportWhereType.TOWN), p -> p.getClanId() != owningClanId);
    }
    
    public static final class Settings extends AbstractZoneSettings
    {
        private int siegableId;
        private Siegable siege;
        private boolean isActiveSiege;
        
        protected Settings() {
            this.siegableId = -1;
            this.siege = null;
            this.isActiveSiege = false;
        }
        
        public int getSiegeableId() {
            return this.siegableId;
        }
        
        protected void setSiegeableId(final int id) {
            this.siegableId = id;
        }
        
        public Siegable getSiege() {
            return this.siege;
        }
        
        public void setSiege(final Siegable s) {
            this.siege = s;
        }
        
        public boolean isActiveSiege() {
            return this.isActiveSiege;
        }
        
        public void setActiveSiege(final boolean val) {
            this.isActiveSiege = val;
        }
        
        @Override
        public void clear() {
            this.siegableId = -1;
            this.siege = null;
            this.isActiveSiege = false;
        }
    }
}
