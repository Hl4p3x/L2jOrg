// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.Collection;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class StaticObjectData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, StaticWorldObject> _staticObjects;
    
    private StaticObjectData() {
        this._staticObjects = new HashMap<Integer, StaticWorldObject>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/StaticObjects.xsd");
    }
    
    public void load() {
        this._staticObjects.clear();
        this.parseDatapackFile("data/StaticObjects.xml");
        StaticObjectData.LOGGER.info("Loaded {} static object templates.", (Object)this._staticObjects.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("object".equalsIgnoreCase(d.getNodeName())) {
                        final NamedNodeMap attrs = d.getAttributes();
                        final StatsSet set = new StatsSet();
                        for (int i = 0; i < attrs.getLength(); ++i) {
                            final Node att = attrs.item(i);
                            set.set(att.getNodeName(), att.getNodeValue());
                        }
                        this.addObject(set);
                    }
                }
            }
        }
    }
    
    private void addObject(final StatsSet set) {
        final StaticWorldObject obj = new StaticWorldObject(new CreatureTemplate(new StatsSet()), set.getInt("id"));
        obj.setType(set.getInt("type", 0));
        obj.setName(set.getString("name"));
        obj.setMap(set.getString("texture", "none"), set.getInt("map_x", 0), set.getInt("map_y", 0));
        obj.spawnMe(set.getInt("x"), set.getInt("y"), set.getInt("z"));
        this._staticObjects.put(obj.getObjectId(), obj);
    }
    
    public Collection<StaticWorldObject> getStaticObjects() {
        return this._staticObjects.values();
    }
    
    public static StaticObjectData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)StaticObjectData.class);
    }
    
    private static class Singleton
    {
        private static final StaticObjectData INSTANCE;
        
        static {
            INSTANCE = new StaticObjectData();
        }
    }
}
