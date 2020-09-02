// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import java.util.List;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.base.ClassId;
import java.util.ArrayList;
import org.l2j.gameserver.model.StatsSet;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.item.Henna;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class HennaData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, Henna> _hennaList;
    
    private HennaData() {
        this._hennaList = new HashMap<Integer, Henna>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/hennaList.xsd");
    }
    
    public void load() {
        this._hennaList.clear();
        this.parseDatapackFile("data/stats/hennaList.xml");
        HennaData.LOGGER.info("Loaded {} Henna data.", (Object)this._hennaList.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equals(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("henna".equals(d.getNodeName())) {
                        this.parseHenna(d);
                    }
                }
            }
        }
    }
    
    private void parseHenna(final Node d) {
        final StatsSet set = new StatsSet();
        final List<ClassId> wearClassIds = new ArrayList<ClassId>();
        final List<Skill> skills = new ArrayList<Skill>();
        NamedNodeMap attrs = d.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Node attr = attrs.item(i);
            set.set(attr.getNodeName(), attr.getNodeValue());
        }
        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
            final String name = c.getNodeName();
            attrs = c.getAttributes();
            final String s = name;
            switch (s) {
                case "stats": {
                    for (int j = 0; j < attrs.getLength(); ++j) {
                        final Node attr = attrs.item(j);
                        set.set(attr.getNodeName(), attr.getNodeValue());
                    }
                    break;
                }
                case "wear": {
                    Node attr = attrs.getNamedItem("count");
                    set.set("wear_count", attr.getNodeValue());
                    attr = attrs.getNamedItem("fee");
                    set.set("wear_fee", attr.getNodeValue());
                    break;
                }
                case "cancel": {
                    Node attr = attrs.getNamedItem("count");
                    set.set("cancel_count", attr.getNodeValue());
                    attr = attrs.getNamedItem("fee");
                    set.set("cancel_fee", attr.getNodeValue());
                    break;
                }
                case "duration": {
                    final Node attr = attrs.getNamedItem("time");
                    set.set("duration", attr.getNodeValue());
                    break;
                }
                case "skill": {
                    skills.add(SkillEngine.getInstance().getSkill(this.parseInteger(attrs, "id"), this.parseInteger(attrs, "level")));
                    break;
                }
                case "classId": {
                    wearClassIds.add(ClassId.getClassId(Integer.parseInt(c.getTextContent())));
                    break;
                }
            }
        }
        final Henna henna = new Henna(set);
        henna.setSkills(skills);
        henna.setWearClassIds(wearClassIds);
        this._hennaList.put(henna.getDyeId(), henna);
    }
    
    public Henna getHenna(final int id) {
        return this._hennaList.get(id);
    }
    
    public List<Henna> getHennaList(final ClassId classId) {
        final List<Henna> list = new ArrayList<Henna>();
        for (final Henna henna : this._hennaList.values()) {
            if (henna.isAllowedClass(classId)) {
                list.add(henna);
            }
        }
        return list;
    }
    
    public static HennaData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)HennaData.class);
    }
    
    private static class Singleton
    {
        private static final HennaData INSTANCE;
        
        static {
            INSTANCE = new HennaData();
        }
    }
}
