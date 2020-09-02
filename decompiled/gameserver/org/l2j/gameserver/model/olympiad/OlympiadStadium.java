// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

import org.slf4j.LoggerFactory;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchEnd;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadUserInfo;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.Creature;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.Spawn;
import java.util.List;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.world.zone.type.OlympiadStadiumZone;
import org.slf4j.Logger;

public class OlympiadStadium
{
    private static final Logger LOGGER;
    private final OlympiadStadiumZone _zone;
    private final Instance _instance;
    private final List<Spawn> _buffers;
    private OlympiadGameTask _task;
    
    protected OlympiadStadium(final OlympiadStadiumZone olyzone, final int stadium) {
        this._task = null;
        this._zone = olyzone;
        this._instance = InstanceManager.getInstance().createInstance(olyzone.getInstanceTemplateId(), null);
        this._buffers = this._instance.getNpcs().stream().map((Function<? super Object, ?>)Npc::getSpawn).collect((Collector<? super Object, ?, List<Spawn>>)Collectors.toList());
        this._buffers.stream().map((Function<? super Object, ?>)Spawn::getLastSpawn).forEach(Creature::decayMe);
    }
    
    public OlympiadStadiumZone getZone() {
        return this._zone;
    }
    
    public final void registerTask(final OlympiadGameTask task) {
        this._task = task;
    }
    
    public OlympiadGameTask getTask() {
        return this._task;
    }
    
    public Instance getInstance() {
        return this._instance;
    }
    
    public final void openDoors() {
        this._instance.getDoors().forEach(Door::openMe);
    }
    
    public final void closeDoors() {
        this._instance.getDoors().forEach(Door::closeMe);
    }
    
    public final void spawnBuffers() {
        this._buffers.forEach(Spawn::startRespawn);
        this._buffers.forEach(Spawn::doSpawn);
    }
    
    public final void deleteBuffers() {
        this._buffers.forEach(Spawn::stopRespawn);
        this._buffers.stream().map((Function<? super Object, ?>)Spawn::getLastSpawn).filter(Objects::nonNull).forEach(Npc::deleteMe);
    }
    
    public final void broadcastStatusUpdate(final Player player) {
        final ExOlympiadUserInfo packet = new ExOlympiadUserInfo(player);
        for (final Player target : this._instance.getPlayers()) {
            if (target.inObserverMode() || target.getOlympiadSide() != player.getOlympiadSide()) {
                target.sendPacket(packet);
            }
        }
    }
    
    public final void broadcastPacket(final ServerPacket packet) {
        this._instance.broadcastPacket(packet);
    }
    
    public final void broadcastPacketToObservers(final ServerPacket packet) {
        for (final Player target : this._instance.getPlayers()) {
            if (target.inObserverMode()) {
                target.sendPacket(packet);
            }
        }
    }
    
    public final void updateZoneStatusForCharactersInside() {
        if (this._task == null) {
            return;
        }
        final boolean battleStarted = this._task.isBattleStarted();
        SystemMessage sm;
        if (battleStarted) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
        }
        else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
        }
        for (final Player player : this._instance.getPlayers()) {
            if (player.inObserverMode()) {
                return;
            }
            if (battleStarted) {
                player.setInsideZone(ZoneType.PVP, true);
                player.sendPacket(sm);
            }
            else {
                player.setInsideZone(ZoneType.PVP, false);
                player.sendPacket(sm);
                player.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
            }
        }
    }
    
    public final void updateZoneInfoForObservers() {
        if (this._task == null) {
            return;
        }
        for (final Player player : this._instance.getPlayers()) {
            if (!player.inObserverMode()) {
                return;
            }
            final OlympiadGameTask nextArena = OlympiadGameManager.getInstance().getOlympiadTask(player.getOlympiadGameId());
            final List<Location> spectatorSpawns = nextArena.getStadium().getZone().getSpectatorSpawns();
            if (spectatorSpawns.isEmpty()) {
                OlympiadStadium.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/world/zone/type/OlympiadStadiumZone;)Ljava/lang/String;, nextArena.getStadium().getZone()));
                return;
            }
            final Location loc = spectatorSpawns.get(Rnd.get(spectatorSpawns.size()));
            player.enterOlympiadObserverMode(loc, player.getOlympiadGameId());
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)OlympiadStadium.class);
    }
}
