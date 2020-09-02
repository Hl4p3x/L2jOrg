// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Set;
import java.util.function.Function;
import java.util.Comparator;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.RespawnZone;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.commons.util.Util;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class MapRegionManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<String, MapRegion> regions;
    private final String defaultRespawn = "giran_castle_town";
    
    private MapRegionManager() {
        this.regions = new HashMap<String, MapRegion>();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/mapregion/map-region.xsd");
    }
    
    public void load() {
        this.regions.clear();
        this.parseDatapackDirectory("data/mapregion", false);
        MapRegionManager.LOGGER.info("Loaded {} map regions.", (Object)this.regions.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final NamedNodeMap attributes;
        final String name;
        final String town;
        final Integer loc;
        final Integer bbs;
        final MapRegion region;
        this.forEach((Node)doc, "list", nodeList -> this.forEach(nodeList, "region", regionNode -> {
            attributes = regionNode.getAttributes();
            name = this.parseString(attributes, "name");
            town = this.parseString(attributes, "town");
            loc = this.parseInteger(attributes, "loc");
            bbs = this.parseInteger(attributes, "bbs");
            region = new MapRegion(town, loc, bbs);
            this.parseRegion(regionNode, region);
            this.regions.put(name, region);
        }));
    }
    
    private void parseRegion(final Node regionNode, final MapRegion region) {
        for (Node node = regionNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            final NamedNodeMap attributes = node.getAttributes();
            if ("respawn-point".equalsIgnoreCase(node.getNodeName())) {
                final int spawnX = this.parseInteger(attributes, "x");
                final int spawnY = this.parseInteger(attributes, "y");
                final int spawnZ = this.parseInteger(attributes, "z");
                if (this.parseBoolean(attributes, "chaotic")) {
                    region.addChaoticSpawn(spawnX, spawnY, spawnZ);
                }
                else {
                    region.addSpawn(spawnX, spawnY, spawnZ);
                }
            }
            else if ("map".equalsIgnoreCase(node.getNodeName())) {
                region.addMapTile(this.parseByte(attributes, "x"), this.parseByte(attributes, "y"));
            }
            else if ("banned".equalsIgnoreCase(node.getNodeName())) {
                region.addBannedRace(attributes.getNamedItem("race").getNodeValue(), attributes.getNamedItem("point").getNodeValue());
            }
        }
    }
    
    public final int getMapRegionLocId(final WorldObject obj) {
        return Objects.isNull(obj) ? 0 : this.getMapRegionLocId(obj.getX(), obj.getY());
    }
    
    public final int getMapRegionLocId(final int locX, final int locY) {
        return Util.zeroIfNullOrElse((Object)this.getMapRegion(locX, locY), MapRegion::getLocId);
    }
    
    private MapRegion getMapRegion(final WorldObject obj) {
        return this.getMapRegion(obj.getX(), obj.getY());
    }
    
    private MapRegion getMapRegion(final int locX, final int locY) {
        final int regionX = this.getMapRegionX(locX);
        final int regionY = this.getMapRegionY(locY);
        return this.regions.values().stream().filter(r -> r.isZoneInRegion(regionX, regionY)).findAny().orElse(null);
    }
    
    public final int getMapRegionX(final int posX) {
        return (posX >> 15) + 20;
    }
    
    public final int getMapRegionY(final int posY) {
        return (posY >> 15) + 18;
    }
    
    public String getClosestTownName(final Creature activeChar) {
        final MapRegion region = this.getMapRegion(activeChar);
        return Objects.isNull(region) ? "Aden Castle Town" : region.getTown();
    }
    
    public Location getTeleToLocation(final Creature creature, final TeleportWhereType teleportWhere) {
        if (GameUtils.isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            if (Objects.nonNull(player.getClan()) && !player.isFlyingMounted() && !player.isFlying()) {
                final Location location = this.getResidenceLocation(teleportWhere, player);
                if (Objects.nonNull(location)) {
                    return location;
                }
            }
            if (player.getReputation() < 0) {
                return this.getChaoticLocation(player);
            }
            final Castle castle = CastleManager.getInstance().getCastle(player);
            if (Objects.nonNull(castle) && castle.getSiege().isInProgress() && (castle.getSiege().checkIsDefender(player.getClan()) || castle.getSiege().checkIsAttacker(player.getClan()))) {
                return castle.getResidenceZone().getOtherSpawnLoc();
            }
            final Instance inst = player.getInstanceWorld();
            if (inst != null) {
                final Location loc = inst.getExitLocation(player);
                if (loc != null) {
                    return loc;
                }
            }
            final RespawnZone zone = ZoneManager.getInstance().getZone(player, RespawnZone.class);
            if (Objects.nonNull(zone)) {
                return this.getRestartRegion(player, zone.getRespawnPoint(player)).getSpawnLoc();
            }
        }
        try {
            return this.getMapRegion(creature).getSpawnLoc();
        }
        catch (Exception e) {
            MapRegionManager.LOGGER.warn(e.getMessage(), (Throwable)e);
            return this.regions.get("giran_castle_town").getSpawnLoc();
        }
    }
    
    private Location getChaoticLocation(final Player player) {
        try {
            final RespawnZone zone = ZoneManager.getInstance().getZone(player, RespawnZone.class);
            if (Objects.nonNull(zone)) {
                return this.getRestartRegion(player, zone.getRespawnPoint(player)).getChaoticSpawnLoc();
            }
            if (this.getMapRegion(player).getBannedRaces().containsKey(player.getRace())) {
                return this.regions.get(this.getMapRegion(player).getBannedRaces().get(player.getRace())).getChaoticSpawnLoc();
            }
            return this.getMapRegion(player).getChaoticSpawnLoc();
        }
        catch (Exception e) {
            MapRegionManager.LOGGER.warn(e.getMessage(), (Throwable)e);
            if (player.isFlyingMounted()) {
                return this.regions.get("union_base_of_kserth").getChaoticSpawnLoc();
            }
            return this.regions.get("giran_castle_town").getChaoticSpawnLoc();
        }
    }
    
    private Location getResidenceLocation(final TeleportWhereType teleportWhere, final Player player) {
        Location location = null;
        if (teleportWhere == TeleportWhereType.CLANHALL) {
            location = this.getClanHallLocation(player);
        }
        else if (teleportWhere == TeleportWhereType.CASTLE) {
            location = this.getCastleLocation(player);
        }
        else if (teleportWhere == TeleportWhereType.SIEGEFLAG) {
            location = this.getSiegeFlagLocation(player);
        }
        return location;
    }
    
    private Location getClanHallLocation(final Player player) {
        if (player.isFlyingMounted()) {
            return null;
        }
        final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByClan(player.getClan());
        if (Objects.nonNull(clanHall)) {
            return clanHall.getOwnerLocation();
        }
        return null;
    }
    
    private Location getCastleLocation(final Player player) {
        final Clan clan = player.getClan();
        Castle castle = CastleManager.getInstance().getCastleByOwner(clan);
        if (Objects.isNull(castle)) {
            castle = CastleManager.getInstance().getCastle(player);
            final Siege siege;
            if (!Objects.nonNull(castle) || !(siege = castle.getSiege()).isInProgress() || !Objects.nonNull(siege.getDefenderClan(clan))) {
                return null;
            }
        }
        if (castle.getId() <= 0) {
            return null;
        }
        if (player.getReputation() < 0) {
            return castle.getResidenceZone().getChaoticSpawnLoc();
        }
        return castle.getResidenceZone().getSpawnLoc();
    }
    
    private Location getSiegeFlagLocation(final Player player) {
        final Castle castle = CastleManager.getInstance().getCastle(player);
        Set<Npc> flags = null;
        if (Objects.nonNull(castle) && castle.getSiege().isInProgress()) {
            flags = castle.getSiege().getFlag(player.getClan());
        }
        if (Objects.isNull(flags) || flags.isEmpty()) {
            return null;
        }
        return flags.stream().min(Comparator.comparingDouble(flag -> MathUtil.calculateDistance3D(flag, player))).map((Function<? super Object, ? extends Location>)WorldObject::getLocation).orElse(null);
    }
    
    public MapRegion getRestartRegion(final Player player, final String point) {
        try {
            final MapRegion region = this.regions.get(point);
            if (region.getBannedRaces().containsKey(player.getRace())) {
                this.getRestartRegion(player, region.getBannedRaces().get(player.getRace()));
            }
            return region;
        }
        catch (Exception e) {
            MapRegionManager.LOGGER.warn(e.getMessage(), (Throwable)e);
            return this.regions.get("giran_castle_town");
        }
    }
    
    public MapRegion getMapRegionByName(final String regionName) {
        return this.regions.get(regionName);
    }
    
    public int getBBs(final ILocational loc) {
        final MapRegion region = this.getMapRegion(loc.getX(), loc.getY());
        return Objects.nonNull(region) ? region.getBbs() : this.regions.get("giran_castle_town").getBbs();
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static MapRegionManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)MapRegionManager.class);
    }
    
    private static class Singleton
    {
        private static final MapRegionManager INSTANCE;
        
        static {
            INSTANCE = new MapRegionManager();
        }
    }
}
