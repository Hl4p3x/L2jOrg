// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchEnd;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Objects;
import org.l2j.gameserver.world.zone.AbstractZoneSettings;
import org.l2j.gameserver.world.zone.ZoneManager;
import java.util.ArrayList;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.instance.Door;
import java.util.List;

public class OlympiadStadiumZone extends SpawnZone
{
    private final List<Door> doors;
    private final List<Spawn> buffers;
    private final List<Location> spectatorLocations;
    
    public OlympiadStadiumZone(final int id) {
        super(id);
        this.doors = new ArrayList<Door>(2);
        this.buffers = new ArrayList<Spawn>(2);
        this.spectatorLocations = new ArrayList<Location>(1);
        AbstractZoneSettings settings = ZoneManager.getSettings(this.getName());
        if (settings == null) {
            settings = new Settings();
        }
        this.setSettings(settings);
    }
    
    @Override
    public Settings getSettings() {
        return (Settings)super.getSettings();
    }
    
    @Override
    public void parseLoc(final int x, final int y, final int z, final String type) {
        if (Objects.nonNull(type) && type.equals("spectatorSpawn")) {
            this.spectatorLocations.add(new Location(x, y, z));
        }
        else {
            super.parseLoc(x, y, z, type);
        }
    }
    
    @Override
    protected final void onEnter(final Creature creature) {
        final OlympiadGameTask task;
        if (Objects.nonNull(task = this.getSettings().getOlympiadTask()) && task.isBattleStarted()) {
            creature.setInsideZone(ZoneType.PVP, true);
            if (GameUtils.isPlayer(creature)) {
                creature.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
                task.getGame().sendOlympiadInfo(creature);
            }
        }
        if (GameUtils.isPlayable(creature)) {
            final Player player = creature.getActingPlayer();
            if (Objects.nonNull(player)) {
                if (!player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS) && !player.isInOlympiadMode() && !player.inObserverMode()) {
                    ThreadPool.execute((Runnable)new KickPlayer(player));
                }
                else {
                    final Summon pet = player.getPet();
                    if (Objects.nonNull(pet)) {
                        pet.unSummon(player);
                    }
                }
            }
        }
    }
    
    @Override
    protected final void onExit(final Creature creature) {
        final OlympiadGameTask task;
        if (Objects.nonNull(task = this.getSettings().getOlympiadTask()) && task.isBattleStarted()) {
            creature.setInsideZone(ZoneType.PVP, false);
            if (GameUtils.isPlayer(creature)) {
                creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
                creature.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
            }
        }
    }
    
    public List<Door> getDoors() {
        return this.doors;
    }
    
    public List<Spawn> getBuffers() {
        return this.buffers;
    }
    
    public List<Location> getSpectatorSpawns() {
        return this.spectatorLocations;
    }
    
    private static final class KickPlayer implements Runnable
    {
        private final Player player;
        
        KickPlayer(final Player player) {
            this.player = player;
        }
        
        @Override
        public void run() {
            this.player.getServitors().values().forEach(s -> s.unSummon(this.player));
            this.player.teleToLocation(TeleportWhereType.TOWN, null);
        }
    }
    
    public static final class Settings extends AbstractZoneSettings
    {
        private OlympiadGameTask _task;
        
        protected Settings() {
            this._task = null;
        }
        
        public OlympiadGameTask getOlympiadTask() {
            return this._task;
        }
        
        @Override
        public void clear() {
            this._task = null;
        }
    }
}
