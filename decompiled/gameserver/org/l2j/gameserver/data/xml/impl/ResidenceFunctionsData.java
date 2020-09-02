// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.util.ArrayList;
import java.util.function.Supplier;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.residences.ResidenceFunctionTemplate;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class ResidenceFunctionsData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, List<ResidenceFunctionTemplate>> _functions;
    
    private ResidenceFunctionsData() {
        this._functions = new HashMap<Integer, List<ResidenceFunctionTemplate>>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/ResidenceFunctions.xsd");
    }
    
    public synchronized void load() {
        this._functions.clear();
        this.parseDatapackFile("data/ResidenceFunctions.xml");
        ResidenceFunctionsData.LOGGER.info("Loaded: {} functions.", (Object)this._functions.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final NamedNodeMap attrs;
        final StatsSet set;
        int i;
        Node node;
        final NamedNodeMap levelAttrs;
        final StatsSet levelSet;
        final StatsSet newSet;
        int j;
        Node node2;
        final ResidenceFunctionTemplate template;
        this.forEach((Node)doc, "list", list -> this.forEach(list, "function", func -> {
            attrs = func.getAttributes();
            set = new StatsSet(HashMap::new);
            for (i = 0; i < attrs.getLength(); ++i) {
                node = attrs.item(i);
                set.set(node.getNodeName(), node.getNodeValue());
            }
            this.forEach(func, "function", levelNode -> {
                levelAttrs = levelNode.getAttributes();
                levelSet = new StatsSet(HashMap::new);
                levelSet.merge(newSet);
                for (j = 0; j < levelAttrs.getLength(); ++j) {
                    node2 = levelAttrs.item(j);
                    levelSet.set(node2.getNodeName(), node2.getNodeValue());
                }
                template = new ResidenceFunctionTemplate(levelSet);
                this._functions.computeIfAbsent(template.getId(), key -> new ArrayList()).add(template);
            });
        }));
    }
    
    public ResidenceFunctionTemplate getFunction(final int id, final int level) {
        return this._functions.getOrDefault(id, Collections.emptyList()).stream().filter(template -> template.getLevel() == level).findAny().orElse(null);
    }
    
    public List<ResidenceFunctionTemplate> getFunctions(final int id) {
        return this._functions.get(id);
    }
    
    public static ResidenceFunctionsData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ResidenceFunctionsData.class);
    }
    
    private static class Singleton
    {
        private static final ResidenceFunctionsData INSTANCE;
        
        static {
            INSTANCE = new ResidenceFunctionsData();
        }
    }
}
