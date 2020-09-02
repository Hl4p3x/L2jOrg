// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.model.actor.transform.TransformLevelData;
import org.l2j.gameserver.model.holders.AdditionalItemHolder;
import org.l2j.gameserver.model.holders.AdditionalSkillHolder;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.actor.transform.TransformTemplate;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.actor.transform.Transform;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class TransformData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, Transform> _transformData;
    
    private TransformData() {
        this._transformData = new HashMap<Integer, Transform>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/transformations.xsd");
    }
    
    public synchronized void load() {
        this._transformData.clear();
        this.parseDatapackDirectory("data/stats/transformations", false);
        TransformData.LOGGER.info("Loaded: {} transform templates.", (Object)this._transformData.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("transform".equalsIgnoreCase(d.getNodeName())) {
                        NamedNodeMap attrs = d.getAttributes();
                        final StatsSet set = new StatsSet();
                        for (int i = 0; i < attrs.getLength(); ++i) {
                            final Node att = attrs.item(i);
                            set.set(att.getNodeName(), att.getNodeValue());
                        }
                        final Transform transform = new Transform(set);
                        for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                            final boolean isMale = "Male".equalsIgnoreCase(cd.getNodeName());
                            if ("Male".equalsIgnoreCase(cd.getNodeName()) || "Female".equalsIgnoreCase(cd.getNodeName())) {
                                TransformTemplate templateData = null;
                                for (Node z = cd.getFirstChild(); z != null; z = z.getNextSibling()) {
                                    final String nodeName = z.getNodeName();
                                    switch (nodeName) {
                                        case "common": {
                                            for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
                                                final String nodeName2 = s.getNodeName();
                                                switch (nodeName2) {
                                                    case "base":
                                                    case "stats":
                                                    case "defense":
                                                    case "magicDefense":
                                                    case "collision":
                                                    case "moving": {
                                                        attrs = s.getAttributes();
                                                        for (int j = 0; j < attrs.getLength(); ++j) {
                                                            final Node att2 = attrs.item(j);
                                                            set.set(att2.getNodeName(), att2.getNodeValue());
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                            templateData = new TransformTemplate(set);
                                            transform.setTemplate(isMale, templateData);
                                            break;
                                        }
                                        case "skills": {
                                            if (templateData == null) {
                                                templateData = new TransformTemplate(set);
                                                transform.setTemplate(isMale, templateData);
                                            }
                                            for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
                                                if ("skill".equals(s.getNodeName())) {
                                                    attrs = s.getAttributes();
                                                    final int skillId = this.parseInt(attrs, "id");
                                                    final int skillLevel = this.parseInt(attrs, "level");
                                                    templateData.addSkill(new SkillHolder(skillId, skillLevel));
                                                }
                                            }
                                            break;
                                        }
                                        case "actions": {
                                            if (templateData == null) {
                                                templateData = new TransformTemplate(set);
                                                transform.setTemplate(isMale, templateData);
                                            }
                                            set.set("actions", z.getTextContent());
                                            final int[] actions = set.getIntArray("actions", " ");
                                            templateData.setBasicActionList(new ExBasicActionList(actions));
                                            break;
                                        }
                                        case "additionalSkills": {
                                            if (templateData == null) {
                                                templateData = new TransformTemplate(set);
                                                transform.setTemplate(isMale, templateData);
                                            }
                                            for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
                                                if ("skill".equals(s.getNodeName())) {
                                                    attrs = s.getAttributes();
                                                    final int skillId = this.parseInt(attrs, "id");
                                                    final int skillLevel = this.parseInt(attrs, "level");
                                                    final int minLevel = this.parseInt(attrs, "minLevel");
                                                    templateData.addAdditionalSkill(new AdditionalSkillHolder(skillId, skillLevel, minLevel));
                                                }
                                            }
                                            break;
                                        }
                                        case "items": {
                                            if (templateData == null) {
                                                templateData = new TransformTemplate(set);
                                                transform.setTemplate(isMale, templateData);
                                            }
                                            for (Node s = z.getFirstChild(); s != null; s = s.getNextSibling()) {
                                                if ("item".equals(s.getNodeName())) {
                                                    attrs = s.getAttributes();
                                                    final int itemId = this.parseInt(attrs, "id");
                                                    final boolean allowed = this.parseBoolean(attrs, "allowed");
                                                    templateData.addAdditionalItem(new AdditionalItemHolder(itemId, allowed));
                                                }
                                            }
                                            break;
                                        }
                                        case "levels": {
                                            if (templateData == null) {
                                                templateData = new TransformTemplate(set);
                                                transform.setTemplate(isMale, templateData);
                                            }
                                            final StatsSet levelsSet = new StatsSet();
                                            for (Node s2 = z.getFirstChild(); s2 != null; s2 = s2.getNextSibling()) {
                                                if ("level".equals(s2.getNodeName())) {
                                                    attrs = s2.getAttributes();
                                                    for (int k = 0; k < attrs.getLength(); ++k) {
                                                        final Node att3 = attrs.item(k);
                                                        levelsSet.set(att3.getNodeName(), att3.getNodeValue());
                                                    }
                                                }
                                            }
                                            templateData.addLevelData(new TransformLevelData(levelsSet));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        this._transformData.put(transform.getId(), transform);
                    }
                }
            }
        }
    }
    
    public Transform getTransform(final int id) {
        return this._transformData.get(id);
    }
    
    public static TransformData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)TransformData.class);
    }
    
    private static class Singleton
    {
        private static final TransformData INSTANCE;
        
        static {
            INSTANCE = new TransformData();
        }
    }
}
