// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.l2j.gameserver.model.StatsSet;
import java.util.ArrayList;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.item.PcItemTemplate;
import java.util.List;
import org.l2j.gameserver.model.base.ClassId;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class InitialEquipmentData extends GameXmlReader
{
    private static final Logger LOGGER;
    private static final String NORMAL = "data/stats/initialEquipment.xml";
    private static final String EVENT = "data/stats/initialEquipmentEvent.xml";
    private final Map<ClassId, List<PcItemTemplate>> _initialEquipmentList;
    
    private InitialEquipmentData() {
        this._initialEquipmentList = new HashMap<ClassId, List<PcItemTemplate>>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/initialEquipment.xsd");
    }
    
    public void load() {
        this._initialEquipmentList.clear();
        this.parseDatapackFile(((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).initialEquipEvent() ? "data/stats/initialEquipmentEvent.xml" : "data/stats/initialEquipment.xml");
        InitialEquipmentData.LOGGER.info("Loaded {} Initial Equipment data.", (Object)this._initialEquipmentList.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("equipment".equalsIgnoreCase(d.getNodeName())) {
                        this.parseEquipment(d);
                    }
                }
            }
        }
    }
    
    private void parseEquipment(final Node d) {
        NamedNodeMap attrs = d.getAttributes();
        final ClassId classId = ClassId.getClassId(Integer.parseInt(attrs.getNamedItem("classId").getNodeValue()));
        final List<PcItemTemplate> equipList = new ArrayList<PcItemTemplate>();
        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
            if ("item".equalsIgnoreCase(c.getNodeName())) {
                final StatsSet set = new StatsSet();
                attrs = c.getAttributes();
                for (int i = 0; i < attrs.getLength(); ++i) {
                    final Node attr = attrs.item(i);
                    set.set(attr.getNodeName(), attr.getNodeValue());
                }
                equipList.add(new PcItemTemplate(set));
            }
        }
        this._initialEquipmentList.put(classId, equipList);
    }
    
    public List<PcItemTemplate> getEquipmentList(final ClassId cId) {
        return this._initialEquipmentList.get(cId);
    }
    
    public List<PcItemTemplate> getEquipmentList(final int cId) {
        return this._initialEquipmentList.get(ClassId.getClassId(cId));
    }
    
    public static InitialEquipmentData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)InitialEquipmentData.class);
    }
    
    private static class Singleton
    {
        protected static final InitialEquipmentData INSTANCE;
        
        static {
            INSTANCE = new InitialEquipmentData();
        }
    }
}
