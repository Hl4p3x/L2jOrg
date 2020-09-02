// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.model.Clan;
import java.util.Collection;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.util.List;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.enums.ClanHallType;
import org.l2j.gameserver.enums.ClanHallGrade;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.ClanHallTeleportHolder;
import org.l2j.gameserver.model.actor.instance.Door;
import java.util.ArrayList;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.entity.ClanHall;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class ClanHallManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private final IntMap<ClanHall> clanHalls;
    
    private ClanHallManager() {
        this.clanHalls = (IntMap<ClanHall>)new HashIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/residences/clanHalls/clanHall.xsd");
    }
    
    public void load() {
        this.parseDatapackDirectory("data/residences/clanHalls", true);
        ClanHallManager.LOGGER.info("Loaded {} Clan Halls.", (Object)this.clanHalls.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final List<Door> doors = new ArrayList<Door>();
        final List<Integer> npcs = new ArrayList<Integer>();
        final List<ClanHallTeleportHolder> teleports = new ArrayList<ClanHallTeleportHolder>();
        final StatsSet params = new StatsSet();
        for (Node listNode = doc.getFirstChild(); listNode != null; listNode = listNode.getNextSibling()) {
            if ("list".equals(listNode.getNodeName())) {
                for (Node clanHallNode = listNode.getFirstChild(); clanHallNode != null; clanHallNode = clanHallNode.getNextSibling()) {
                    if ("clanHall".equals(clanHallNode.getNodeName())) {
                        params.set("id", this.parseInteger(clanHallNode.getAttributes(), "id"));
                        params.set("name", this.parseString(clanHallNode.getAttributes(), "name", "None"));
                        params.set("grade", this.parseEnum(clanHallNode.getAttributes(), (Class)ClanHallGrade.class, "grade", (Enum)ClanHallGrade.NONE));
                        params.set("type", this.parseEnum(clanHallNode.getAttributes(), (Class)ClanHallType.class, "type", (Enum)ClanHallType.OTHER));
                        for (Node tpNode = clanHallNode.getFirstChild(); tpNode != null; tpNode = tpNode.getNextSibling()) {
                            final String nodeName = tpNode.getNodeName();
                            switch (nodeName) {
                                case "auction": {
                                    final NamedNodeMap at = tpNode.getAttributes();
                                    params.set("minBid", this.parseInteger(at, "min-bid"));
                                    params.set("lease", this.parseInteger(at, "lease"));
                                    params.set("deposit", this.parseInteger(at, "deposit"));
                                    break;
                                }
                                case "npcs": {
                                    for (Node npcNode = tpNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling()) {
                                        if ("npc".equals(npcNode.getNodeName())) {
                                            final NamedNodeMap np = npcNode.getAttributes();
                                            final int npcId = this.parseInteger(np, "id");
                                            npcs.add(npcId);
                                        }
                                    }
                                    params.set("npcList", npcs);
                                    break;
                                }
                                case "doors": {
                                    for (Node npcNode = tpNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling()) {
                                        if ("door".equals(npcNode.getNodeName())) {
                                            final NamedNodeMap np = npcNode.getAttributes();
                                            final int doorId = this.parseInteger(np, "id");
                                            final Door door = DoorDataManager.getInstance().getDoor(doorId);
                                            if (door != null) {
                                                doors.add(door);
                                            }
                                        }
                                    }
                                    params.set("doorList", doors);
                                    break;
                                }
                                case "teleportList": {
                                    for (Node npcNode = tpNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling()) {
                                        if ("teleport".equals(npcNode.getNodeName())) {
                                            final NamedNodeMap np = npcNode.getAttributes();
                                            final int npcStringId = this.parseInteger(np, "npcStringId");
                                            final int x = this.parseInteger(np, "x");
                                            final int y = this.parseInteger(np, "y");
                                            final int z = this.parseInteger(np, "z");
                                            final int minFunctionLevel = this.parseInteger(np, "minFunctionLevel");
                                            final int cost = this.parseInteger(np, "cost");
                                            teleports.add(new ClanHallTeleportHolder(npcStringId, x, y, z, minFunctionLevel, cost));
                                        }
                                    }
                                    params.set("teleportList", teleports);
                                    break;
                                }
                                case "ownerRestartPoint": {
                                    final NamedNodeMap ol = tpNode.getAttributes();
                                    params.set("owner_loc", new Location(this.parseInteger(ol, "x"), this.parseInteger(ol, "y"), this.parseInteger(ol, "z")));
                                    break;
                                }
                                case "banishPoint": {
                                    final NamedNodeMap bl = tpNode.getAttributes();
                                    params.set("banish_loc", new Location(this.parseInteger(bl, "x"), this.parseInteger(bl, "y"), this.parseInteger(bl, "z")));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        this.clanHalls.put(params.getInt("id"), (Object)new ClanHall(params));
    }
    
    public ClanHall getClanHallById(final int clanHallId) {
        return (ClanHall)this.clanHalls.get(clanHallId);
    }
    
    public Collection<ClanHall> getClanHalls() {
        return (Collection<ClanHall>)this.clanHalls.values();
    }
    
    public ClanHall getClanHallByNpcId(final int npcId) {
        return this.clanHalls.values().stream().filter(ch -> ch.getNpcs().contains(npcId)).findFirst().orElse(null);
    }
    
    public ClanHall getClanHallByClan(final Clan clan) {
        return this.clanHalls.values().stream().filter(ch -> ch.getOwner() == clan).findFirst().orElse(null);
    }
    
    public ClanHall getClanHallByDoorId(final int doorId) {
        final Door door = DoorDataManager.getInstance().getDoor(doorId);
        return this.clanHalls.values().stream().filter(ch -> ch.getDoors().contains(door)).findFirst().orElse(null);
    }
    
    public List<ClanHall> getFreeAuctionableHall() {
        return this.clanHalls.values().stream().filter(ch -> ch.getType() == ClanHallType.AUCTIONABLE && ch.getOwner() == null).sorted(Comparator.comparingInt(AbstractResidence::getId)).collect((Collector<? super Object, ?, List<ClanHall>>)Collectors.toList());
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static ClanHallManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClanHallManager.class);
    }
    
    private static class Singleton
    {
        private static final ClanHallManager INSTANCE;
        
        static {
            INSTANCE = new ClanHallManager();
        }
    }
}
