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
import org.l2j.gameserver.model.base.ClassInfo;
import org.l2j.gameserver.model.base.ClassId;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class ClassListData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<ClassId, ClassInfo> _classData;
    
    private ClassListData() {
        this._classData = new HashMap<ClassId, ClassInfo>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/classList.xsd");
    }
    
    public void load() {
        this._classData.clear();
        this.parseDatapackFile("data/stats/chars/classList.xml");
        ClassListData.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), this._classData.size()));
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equals(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    final NamedNodeMap attrs = d.getAttributes();
                    if ("class".equals(d.getNodeName())) {
                        Node attr = attrs.getNamedItem("classId");
                        final ClassId classId = ClassId.getClassId(this.parseInt(attr));
                        attr = attrs.getNamedItem("name");
                        final String className = attr.getNodeValue();
                        attr = attrs.getNamedItem("parentClassId");
                        final ClassId parentClassId = (attr != null) ? ClassId.getClassId(this.parseInt(attr)) : null;
                        this._classData.put(classId, new ClassInfo(classId, className, parentClassId));
                    }
                }
            }
        }
    }
    
    public Map<ClassId, ClassInfo> getClassList() {
        return this._classData;
    }
    
    public ClassInfo getClass(final ClassId classId) {
        return this._classData.get(classId);
    }
    
    public ClassInfo getClass(final int classId) {
        final ClassId id = ClassId.getClassId(classId);
        return (id != null) ? this._classData.get(id) : null;
    }
    
    public static ClassListData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClassListData.class);
    }
    
    private static class Singleton
    {
        private static final ClassListData INSTANCE;
        
        static {
            INSTANCE = new ClassListData();
        }
    }
}
