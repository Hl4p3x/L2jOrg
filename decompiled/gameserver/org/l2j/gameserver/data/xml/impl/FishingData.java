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
import org.l2j.gameserver.model.FishingBaitData;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class FishingData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, FishingBaitData> _baitData;
    private int _baitDistanceMin;
    private int _baitDistanceMax;
    private double _expRateMin;
    private double _expRateMax;
    private double _spRateMin;
    private double _spRateMax;
    
    private FishingData() {
        this._baitData = new HashMap<Integer, FishingBaitData>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/Fishing.xsd");
    }
    
    public void load() {
        this._baitData.clear();
        this.parseDatapackFile("data/Fishing.xml");
        FishingData.LOGGER.info("Loaded {} Fishing Data.", (Object)this._baitData.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node listItem = n.getFirstChild(); listItem != null; listItem = listItem.getNextSibling()) {
                    final String nodeName = listItem.getNodeName();
                    switch (nodeName) {
                        case "baitDistance": {
                            this._baitDistanceMin = this.parseInt(listItem.getAttributes(), "min");
                            this._baitDistanceMax = this.parseInt(listItem.getAttributes(), "max");
                            break;
                        }
                        case "experienceRate": {
                            this._expRateMin = this.parseDouble(listItem.getAttributes(), "min");
                            this._expRateMax = this.parseDouble(listItem.getAttributes(), "max");
                            break;
                        }
                        case "skillPointsRate": {
                            this._spRateMin = this.parseDouble(listItem.getAttributes(), "min");
                            this._spRateMax = this.parseDouble(listItem.getAttributes(), "max");
                            break;
                        }
                        case "baits": {
                            for (Node bait = listItem.getFirstChild(); bait != null; bait = bait.getNextSibling()) {
                                if ("bait".equalsIgnoreCase(bait.getNodeName())) {
                                    final NamedNodeMap attrs = bait.getAttributes();
                                    final int itemId = this.parseInt(attrs, "itemId");
                                    final int level = this.parseInt(attrs, "level");
                                    final int minPlayerLevel = this.parseInt(attrs, "minPlayerLevel");
                                    final double chance = this.parseDouble(attrs, "chance");
                                    final int timeMin = this.parseInt(attrs, "timeMin");
                                    final int timeMax = this.parseInt(attrs, "timeMax");
                                    final int waitMin = this.parseInt(attrs, "waitMin");
                                    final int waitMax = this.parseInt(attrs, "waitMax");
                                    final FishingBaitData baitData = new FishingBaitData(itemId, level, minPlayerLevel, chance, timeMin, timeMax, waitMin, waitMax);
                                    for (Node c = bait.getFirstChild(); c != null; c = c.getNextSibling()) {
                                        if ("catch".equalsIgnoreCase(c.getNodeName())) {
                                            baitData.addReward(this.parseInt(c.getAttributes(), "itemId"));
                                        }
                                    }
                                    this._baitData.put(baitData.getItemId(), baitData);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public FishingBaitData getBaitData(final int baitItemId) {
        return this._baitData.get(baitItemId);
    }
    
    public int getBaitDistanceMin() {
        return this._baitDistanceMin;
    }
    
    public int getBaitDistanceMax() {
        return this._baitDistanceMax;
    }
    
    public double getExpRateMin() {
        return this._expRateMin;
    }
    
    public double getExpRateMax() {
        return this._expRateMax;
    }
    
    public double getSpRateMin() {
        return this._spRateMin;
    }
    
    public double getSpRateMax() {
        return this._spRateMax;
    }
    
    public static FishingData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)FishingData.class);
    }
    
    private static class Singleton
    {
        private static final FishingData INSTANCE;
        
        static {
            INSTANCE = new FishingData();
        }
    }
}
