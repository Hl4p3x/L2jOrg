// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.entity;

import org.slf4j.LoggerFactory;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import java.util.HashSet;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import org.l2j.gameserver.model.holders.PlayerEventHolder;
import org.slf4j.Logger;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Set;
import java.util.Map;

public class Event
{
    public static final Map<Integer, String> _teamNames;
    public static final Set<Player> _registeredPlayers;
    public static final Map<Integer, Set<Player>> _teams;
    protected static final Logger LOGGER;
    private static final Map<Player, PlayerEventHolder> _connectionLossData;
    public static EventState eventState;
    public static String _eventName;
    public static String _eventCreator;
    public static String _eventInfo;
    public static int _teamsNumber;
    public static int _npcId;
    
    public static int getPlayerTeamId(final Player player) {
        if (player == null) {
            return -1;
        }
        for (final Map.Entry<Integer, Set<Player>> team : Event._teams.entrySet()) {
            if (team.getValue().contains(player)) {
                return team.getKey();
            }
        }
        return -1;
    }
    
    public static List<Player> getTopNKillers(final int n) {
        final Map<Player, Integer> tmp = new HashMap<Player, Integer>();
        for (final Set<Player> teamList : Event._teams.values()) {
            for (final Player player : teamList) {
                if (player.getEventStatus() == null) {
                    continue;
                }
                tmp.put(player, player.getEventStatus().getKills().size());
            }
        }
        sortByValue(tmp);
        if (tmp.size() <= n) {
            return new ArrayList<Player>(tmp.keySet());
        }
        final List<Player> toReturn = new ArrayList<Player>(tmp.keySet());
        return toReturn.subList(1, n);
    }
    
    public static void showEventHtml(final Player player, final String objectid) {
        if (Event.eventState == EventState.STANDBY) {
            try {
                final NpcHtmlMessage html = new NpcHtmlMessage(Integer.parseInt(objectid));
                String htmContent;
                if (Event._registeredPlayers.contains(player)) {
                    htmContent = HtmCache.getInstance().getHtm(player, "data/html/mods/EventEngine/Participating.htm");
                }
                else {
                    htmContent = HtmCache.getInstance().getHtm(player, "data/html/mods/EventEngine/Participation.htm");
                }
                if (htmContent != null) {
                    html.setHtml(htmContent);
                }
                html.replace("%objectId%", objectid);
                html.replace("%eventName%", Event._eventName);
                html.replace("%eventCreator%", Event._eventCreator);
                html.replace("%eventInfo%", Event._eventInfo);
                player.sendPacket(html);
            }
            catch (Exception e) {
                Event.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
            }
        }
    }
    
    public static void spawnEventNpc(final Player target) {
        try {
            final Spawn spawn = new Spawn(Event._npcId);
            spawn.setXYZ(target.getX() + 50, target.getY() + 50, target.getZ());
            spawn.setAmount(1);
            spawn.setHeading(target.getHeading());
            spawn.stopRespawn();
            SpawnTable.getInstance().addNewSpawn(spawn, false);
            spawn.init();
            spawn.getLastSpawn().setCurrentHp(9.99999999E8);
            spawn.getLastSpawn().setTitle(Event._eventName);
            spawn.getLastSpawn().getVariables().set("eventmob", true);
            spawn.getLastSpawn().setIsInvul(true);
            spawn.getLastSpawn().broadcastPacket(new MagicSkillUse(spawn.getLastSpawn(), spawn.getLastSpawn(), 1034, 1, 1, 1));
        }
        catch (Exception e) {
            Event.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public static void unspawnEventNpcs() {
        final Npc npc;
        SpawnTable.getInstance().forEachSpawn(spawn -> {
            npc = spawn.getLastSpawn();
            if (npc != null && npc.getVariables().getBoolean("eventmob", false)) {
                npc.deleteMe();
                spawn.stopRespawn();
                SpawnTable.getInstance().deleteSpawn(spawn, false);
            }
            return Boolean.valueOf(true);
        });
    }
    
    public static boolean isParticipant(final Player player) {
        if (player == null || player.getEventStatus() == null) {
            return false;
        }
        switch (Event.eventState) {
            case OFF: {
                return false;
            }
            case STANDBY: {
                return Event._registeredPlayers.contains(player);
            }
            case ON: {
                for (final Set<Player> teamList : Event._teams.values()) {
                    if (teamList.contains(player)) {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }
    
    public static void registerPlayer(final Player player) {
        if (Event.eventState != EventState.STANDBY) {
            player.sendMessage("The registration period for this event is over.");
            return;
        }
        if (Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP == 0 || AntiFeedManager.getInstance().tryAddPlayer(3, player, Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP)) {
            Event._registeredPlayers.add(player);
        }
        else {
            player.sendMessage("You have reached the maximum allowed participants per IP.");
        }
    }
    
    public static void removeAndResetPlayer(final Player player) {
        try {
            if (isParticipant(player)) {
                if (player.isDead()) {
                    player.restoreExp(100.0);
                    player.doRevive();
                    player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                    player.setCurrentCp(player.getMaxCp());
                }
                player.decayMe();
                player.spawnMe(player.getX(), player.getY(), player.getZ());
                player.broadcastUserInfo();
                player.stopTransformation(true);
            }
            if (player.getEventStatus() != null) {
                player.getEventStatus().restorePlayerStats();
            }
            player.setEventStatus(null);
            Event._registeredPlayers.remove(player);
            final int teamId = getPlayerTeamId(player);
            if (Event._teams.containsKey(teamId)) {
                Event._teams.get(teamId).remove(player);
            }
        }
        catch (Exception e) {
            Event.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public static void savePlayerEventStatus(final Player player) {
        Event._connectionLossData.put(player, player.getEventStatus());
    }
    
    public static void restorePlayerEventStatus(final Player player) {
        if (Event._connectionLossData.containsKey(player)) {
            player.setEventStatus(Event._connectionLossData.get(player));
            Event._connectionLossData.remove(player);
        }
    }
    
    public static String startEventParticipation() {
        try {
            switch (Event.eventState) {
                case ON: {
                    return "Cannot start event, it is already on.";
                }
                case STANDBY: {
                    return "Cannot start event, it is on standby mode.";
                }
                case OFF: {
                    Event.eventState = EventState.STANDBY;
                    break;
                }
            }
            AntiFeedManager.getInstance().registerEvent(3);
            AntiFeedManager.getInstance().clear(3);
            unspawnEventNpcs();
            Event._registeredPlayers.clear();
            if (NpcData.getInstance().getTemplate(Event._npcId) == null) {
                return "Cannot start event, invalid npc id.";
            }
            final FileReader fr = new FileReader(invokedynamic(makeConcatWithConstants:(Ljava/io/File;Ljava/lang/String;)Ljava/lang/String;, Config.DATAPACK_ROOT, Event._eventName));
            try {
                final BufferedReader br = new BufferedReader(fr);
                try {
                    Event._eventCreator = br.readLine();
                    Event._eventInfo = br.readLine();
                    br.close();
                }
                catch (Throwable t) {
                    try {
                        br.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                fr.close();
            }
            catch (Throwable t2) {
                try {
                    fr.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
            final Set<Player> temp = new HashSet<Player>();
            for (final Player player : World.getInstance().getPlayers()) {
                if (!player.isOnline()) {
                    continue;
                }
                if (!temp.contains(player)) {
                    spawnEventNpc(player);
                    temp.add(player);
                }
                final World instance = World.getInstance();
                final Player reference = player;
                final Class<Player> clazz = Player.class;
                final int range = 1000;
                final Set<Player> obj = temp;
                Objects.requireNonNull((HashSet)obj);
                instance.forEachVisibleObjectInRange(reference, (Class<WorldObject>)clazz, range, (Consumer<WorldObject>)obj::add);
            }
        }
        catch (Exception e) {
            Event.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
            return "Cannot start event participation, an error has occured.";
        }
        return "The event participation has been successfully started.";
    }
    
    public static String startEvent() {
        try {
            switch (Event.eventState) {
                case ON: {
                    return "Cannot start event, it is already on.";
                }
                case STANDBY: {
                    Event.eventState = EventState.ON;
                    break;
                }
                case OFF: {
                    return "Cannot start event, it is off. Participation start is required.";
                }
            }
            unspawnEventNpcs();
            Event._teams.clear();
            Event._connectionLossData.clear();
            for (int i = 0; i < Event._teamsNumber; ++i) {
                Event._teams.put(i + 1, (Set<Player>)ConcurrentHashMap.newKeySet());
            }
            int i = 0;
            while (!Event._registeredPlayers.isEmpty()) {
                int max = 0;
                Player biggestLvlPlayer = null;
                for (final Player player : Event._registeredPlayers) {
                    if (player == null) {
                        continue;
                    }
                    if (max >= player.getLevel()) {
                        continue;
                    }
                    max = player.getLevel();
                    biggestLvlPlayer = player;
                }
                if (biggestLvlPlayer == null) {
                    continue;
                }
                Event._registeredPlayers.remove(biggestLvlPlayer);
                Event._teams.get(i + 1).add(biggestLvlPlayer);
                biggestLvlPlayer.setEventStatus();
                i = (i + 1) % Event._teamsNumber;
            }
        }
        catch (Exception e) {
            Event.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
            return "Cannot start event, an error has occured.";
        }
        return "The event has been successfully started.";
    }
    
    public static String finishEvent() {
        switch (Event.eventState) {
            case OFF: {
                return "Cannot finish event, it is already off.";
            }
            case STANDBY: {
                for (final Player player : Event._registeredPlayers) {
                    removeAndResetPlayer(player);
                }
                unspawnEventNpcs();
                Event._registeredPlayers.clear();
                Event._teams.clear();
                Event._connectionLossData.clear();
                Event._teamsNumber = 0;
                Event._eventName = "";
                Event.eventState = EventState.OFF;
                return "The event has been stopped at STANDBY mode, all players unregistered and all event npcs unspawned.";
            }
            case ON: {
                for (final Set<Player> teamList : Event._teams.values()) {
                    for (final Player player2 : teamList) {
                        removeAndResetPlayer(player2);
                    }
                }
                Event.eventState = EventState.OFF;
                AntiFeedManager.getInstance().clear(2);
                unspawnEventNpcs();
                Event._registeredPlayers.clear();
                Event._teams.clear();
                Event._connectionLossData.clear();
                Event._teamsNumber = 0;
                Event._eventName = "";
                Event._npcId = 0;
                Event._eventCreator = "";
                Event._eventInfo = "";
                return "The event has been stopped, all players unregistered and all event npcs unspawned.";
            }
            default: {
                return "The event has been successfully finished.";
            }
        }
    }
    
    private static Map<Player, Integer> sortByValue(final Map<Player, Integer> unsortMap) {
        final List<Map.Entry<Player, Integer>> list = new LinkedList<Map.Entry<Player, Integer>>(unsortMap.entrySet());
        list.sort(Comparator.comparing((Function<? super Map.Entry<Player, Integer>, ? extends Comparable>)Map.Entry::getValue));
        final Map<Player, Integer> sortedMap = new LinkedHashMap<Player, Integer>();
        for (final Map.Entry<Player, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    
    static {
        _teamNames = new ConcurrentHashMap<Integer, String>();
        _registeredPlayers = ConcurrentHashMap.newKeySet();
        _teams = new ConcurrentHashMap<Integer, Set<Player>>();
        LOGGER = LoggerFactory.getLogger((Class)Event.class);
        _connectionLossData = new ConcurrentHashMap<Player, PlayerEventHolder>();
        Event.eventState = EventState.OFF;
        Event._eventName = "";
        Event._eventCreator = "";
        Event._eventInfo = "";
        Event._teamsNumber = 0;
        Event._npcId = 0;
    }
    
    public enum EventState
    {
        OFF, 
        STANDBY, 
        ON;
    }
}
