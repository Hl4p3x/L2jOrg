// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import java.util.Objects;
import org.w3c.dom.Node;
import java.util.function.Consumer;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.Optional;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.data.xml.model.TeleportData;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class TeleportEngine extends GameXmlReader
{
    private static final Logger LOGGER;
    private final IntMap<TeleportData> teleports;
    
    private TeleportEngine() {
        this.teleports = (IntMap<TeleportData>)new HashIntMap();
    }
    
    public Optional<TeleportData> getInfo(final int id) {
        final Optional<TeleportData> data = Optional.ofNullable(this.teleports.get(id));
        if (data.isEmpty()) {
            TeleportEngine.LOGGER.warn("Can't find teleport list for id: {}", (Object)id);
        }
        return data;
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/teleports.xsd");
    }
    
    public void load() {
        this.parseDatapackFile("data/teleports.xml");
        TeleportEngine.LOGGER.info("Loaded {} Teleports", (Object)this.teleports.size());
        this.releaseResources();
    }
    
    protected void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", list -> this.forEach(list, "teleport", (Consumer)this::parseTeleport));
    }
    
    private void parseTeleport(final Node teleportNode) {
        final NamedNodeMap attributes = teleportNode.getAttributes();
        final int id = this.parseInt(attributes, "id");
        final int price = this.parseInt(attributes, "price");
        final byte castle = this.parseByte(attributes, "castle");
        final Node locationNode = teleportNode.getFirstChild();
        if (Objects.nonNull(locationNode)) {
            this.teleports.put(id, (Object)new TeleportData(price, this.parseLocation(locationNode), castle));
        }
        else {
            TeleportEngine.LOGGER.warn("Can't find location node in teleports.xml id {}", (Object)id);
        }
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static TeleportEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)TeleportEngine.class);
    }
    
    private static class Singleton
    {
        private static final TeleportEngine INSTANCE;
        
        static {
            INSTANCE = new TeleportEngine();
        }
    }
}
