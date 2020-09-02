// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
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

public class KarmaData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, Double> _karmaTable;
    
    private KarmaData() {
        this._karmaTable = new HashMap<Integer, Double>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/pcKarmaIncrease.xsd");
    }
    
    public synchronized void load() {
        this._karmaTable.clear();
        this.parseDatapackFile("data/stats/chars/pcKarmaIncrease.xml");
        KarmaData.LOGGER.info("Loaded {} karma modifiers.", (Object)this._karmaTable.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("pcKarmaIncrease".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("increase".equalsIgnoreCase(d.getNodeName())) {
                        final NamedNodeMap attrs = d.getAttributes();
                        final int level = this.parseInteger(attrs, "lvl");
                        this._karmaTable.put(level, this.parseDouble(attrs, "val"));
                    }
                }
            }
        }
    }
    
    public double getMultiplier(final int level) {
        return this._karmaTable.get(level);
    }
    
    public static KarmaData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)KarmaData.class);
    }
    
    private static class Singleton
    {
        private static final KarmaData INSTANCE;
        
        static {
            INSTANCE = new KarmaData();
        }
    }
}
