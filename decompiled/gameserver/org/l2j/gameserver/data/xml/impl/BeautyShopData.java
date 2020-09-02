// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.model.beautyshop.BeautyItem;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.beautyshop.BeautyData;
import org.l2j.gameserver.enums.Sex;
import org.l2j.gameserver.enums.Race;
import java.util.Map;
import org.l2j.gameserver.util.GameXmlReader;

public final class BeautyShopData extends GameXmlReader
{
    private final Map<Race, Map<Sex, BeautyData>> _beautyList;
    private final Map<Sex, BeautyData> _beautyData;
    
    private BeautyShopData() {
        this._beautyList = new HashMap<Race, Map<Sex, BeautyData>>();
        this._beautyData = new HashMap<Sex, BeautyData>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/beautyShop.xsd");
    }
    
    public synchronized void load() {
        this._beautyList.clear();
        this._beautyData.clear();
        this.parseDatapackFile("data/beautyShop.xml");
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        Race race = null;
        Sex sex = null;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("race".equalsIgnoreCase(d.getNodeName())) {
                        Node att = d.getAttributes().getNamedItem("type");
                        if (att != null) {
                            race = (Race)this.parseEnum(att, (Class)Race.class);
                        }
                        for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling()) {
                            if ("sex".equalsIgnoreCase(b.getNodeName())) {
                                att = b.getAttributes().getNamedItem("type");
                                if (att != null) {
                                    sex = (Sex)this.parseEnum(att, (Class)Sex.class);
                                }
                                final BeautyData beautyData = new BeautyData();
                                for (Node a = b.getFirstChild(); a != null; a = a.getNextSibling()) {
                                    if ("hair".equalsIgnoreCase(a.getNodeName())) {
                                        NamedNodeMap attrs = a.getAttributes();
                                        StatsSet set = new StatsSet();
                                        for (int i = 0; i < attrs.getLength(); ++i) {
                                            att = attrs.item(i);
                                            set.set(att.getNodeName(), att.getNodeValue());
                                        }
                                        final BeautyItem hair = new BeautyItem(set);
                                        for (Node g = a.getFirstChild(); g != null; g = g.getNextSibling()) {
                                            if ("color".equalsIgnoreCase(g.getNodeName())) {
                                                attrs = g.getAttributes();
                                                set = new StatsSet();
                                                for (int j = 0; j < attrs.getLength(); ++j) {
                                                    att = attrs.item(j);
                                                    set.set(att.getNodeName(), att.getNodeValue());
                                                }
                                                hair.addColor(set);
                                            }
                                        }
                                        beautyData.addHair(hair);
                                    }
                                    else if ("face".equalsIgnoreCase(a.getNodeName())) {
                                        final NamedNodeMap attrs = a.getAttributes();
                                        final StatsSet set = new StatsSet();
                                        for (int i = 0; i < attrs.getLength(); ++i) {
                                            att = attrs.item(i);
                                            set.set(att.getNodeName(), att.getNodeValue());
                                        }
                                        final BeautyItem face = new BeautyItem(set);
                                        beautyData.addFace(face);
                                    }
                                }
                                this._beautyData.put(sex, beautyData);
                            }
                        }
                        this._beautyList.put(race, this._beautyData);
                    }
                }
            }
        }
    }
    
    public boolean hasBeautyData(final Race race, final Sex sex) {
        return this._beautyList.containsKey(race) && this._beautyList.get(race).containsKey(sex);
    }
    
    public BeautyData getBeautyData(final Race race, final Sex sex) {
        if (this._beautyList.containsKey(race)) {
            return this._beautyList.get(race).get(sex);
        }
        return null;
    }
    
    public static BeautyShopData getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final BeautyShopData INSTANCE;
        
        static {
            INSTANCE = new BeautyShopData();
        }
    }
}
