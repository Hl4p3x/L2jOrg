// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collections;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.enums.SiegeGuardType;
import org.l2j.gameserver.enums.CastleSide;
import java.util.ArrayList;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.holders.CastleSpawnHolder;
import org.l2j.gameserver.model.holders.SiegeGuardHolder;
import java.util.List;
import java.util.Map;
import org.l2j.gameserver.util.GameXmlReader;

public final class CastleDataManager extends GameXmlReader
{
    private static final Map<Integer, List<SiegeGuardHolder>> _siegeGuards;
    private final Map<Integer, List<CastleSpawnHolder>> _spawns;
    
    private CastleDataManager() {
        this._spawns = new HashMap<Integer, List<CastleSpawnHolder>>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/castleData.xsd");
    }
    
    public void load() {
        this._spawns.clear();
        CastleDataManager._siegeGuards.clear();
        this.parseDatapackDirectory("data/residences/castles", true);
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node listNode = doc.getFirstChild(); listNode != null; listNode = listNode.getNextSibling()) {
            if ("list".equals(listNode.getNodeName())) {
                for (Node castleNode = listNode.getFirstChild(); castleNode != null; castleNode = castleNode.getNextSibling()) {
                    if ("castle".equals(castleNode.getNodeName())) {
                        final int castleId = this.parseInteger(castleNode.getAttributes(), "id");
                        for (Node tpNode = castleNode.getFirstChild(); tpNode != null; tpNode = tpNode.getNextSibling()) {
                            final List<CastleSpawnHolder> spawns = new ArrayList<CastleSpawnHolder>();
                            if ("spawns".equals(tpNode.getNodeName())) {
                                for (Node npcNode = tpNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling()) {
                                    if ("npc".equals(npcNode.getNodeName())) {
                                        final NamedNodeMap np = npcNode.getAttributes();
                                        final int npcId = this.parseInteger(np, "id");
                                        final CastleSide side = (CastleSide)this.parseEnum(np, (Class)CastleSide.class, "castleSide", (Enum)CastleSide.NEUTRAL);
                                        final int x = this.parseInteger(np, "x");
                                        final int y = this.parseInteger(np, "y");
                                        final int z = this.parseInteger(np, "z");
                                        final int heading = this.parseInteger(np, "heading");
                                        spawns.add(new CastleSpawnHolder(npcId, side, x, y, z, heading));
                                    }
                                }
                                this._spawns.put(castleId, spawns);
                            }
                            else if ("siegeGuards".equals(tpNode.getNodeName())) {
                                final List<SiegeGuardHolder> guards = new ArrayList<SiegeGuardHolder>();
                                for (Node npcNode2 = tpNode.getFirstChild(); npcNode2 != null; npcNode2 = npcNode2.getNextSibling()) {
                                    if ("guard".equals(npcNode2.getNodeName())) {
                                        final NamedNodeMap np2 = npcNode2.getAttributes();
                                        final int itemId = this.parseInteger(np2, "itemId");
                                        final SiegeGuardType type = (SiegeGuardType)this.parseEnum(tpNode.getAttributes(), (Class)SiegeGuardType.class, "type");
                                        final boolean stationary = this.parseBoolean(np2, "stationary", Boolean.valueOf(false));
                                        final int npcId2 = this.parseInteger(np2, "npcId");
                                        final int npcMaxAmount = this.parseInteger(np2, "npcMaxAmount");
                                        guards.add(new SiegeGuardHolder(castleId, itemId, type, stationary, npcId2, npcMaxAmount));
                                    }
                                }
                                CastleDataManager._siegeGuards.put(castleId, guards);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public final List<CastleSpawnHolder> getSpawnsForSide(final int castleId, final CastleSide side) {
        return this._spawns.getOrDefault(castleId, Collections.emptyList()).stream().filter(s -> s.getSide() == side).collect((Collector<? super Object, ?, List<CastleSpawnHolder>>)Collectors.toList());
    }
    
    public final List<SiegeGuardHolder> getSiegeGuardsForCastle(final int castleId) {
        return CastleDataManager._siegeGuards.getOrDefault(castleId, Collections.emptyList());
    }
    
    public static CastleDataManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        _siegeGuards = new HashMap<Integer, List<SiegeGuardHolder>>();
    }
    
    private static class Singleton
    {
        protected static final CastleDataManager INSTANCE;
        
        static {
            INSTANCE = new CastleDataManager();
        }
    }
}
