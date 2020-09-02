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
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class LevelData extends GameXmlReader
{
    private static final Logger LOGGER;
    private byte maxLevel;
    private final IntMap<LevelInfo> levelInfos;
    
    private LevelData() {
        this.maxLevel = 90;
        this.levelInfos = (IntMap<LevelInfo>)new HashIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/level-data.xsd");
    }
    
    public void load() {
        this.parseDatapackFile("data/level-data.xml");
        LevelData.LOGGER.info("Max Player Level is: {}", (Object)this.maxLevel);
        LevelData.LOGGER.info("Loaded {} levels info", (Object)this.levelInfos.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final Node list = doc.getFirstChild();
        this.maxLevel = this.parseByte(list.getAttributes(), "max-level");
        final NamedNodeMap attr;
        final int level;
        this.forEach(list, "level-data", node -> {
            attr = node.getAttributes();
            level = this.parseInt(attr, "level");
            this.levelInfos.put(level, (Object)new LevelInfo(this.parseShort(attr, "characteristic-points"), this.parseLong(attr, "experience"), this.parseFloat(attr, "xp-percent-lost")));
            return;
        });
        final int maxInfo = this.levelInfos.keySet().stream().max().orElse(1);
        if (maxInfo < this.maxLevel) {
            this.maxLevel = (byte)maxInfo;
            LevelData.LOGGER.warn("Adjusting maxLevel to max level info {}", (Object)this.maxLevel);
        }
    }
    
    public short getCharacteristicPoints(final int level) {
        return ((LevelInfo)this.levelInfos.get(Math.min(level, this.maxLevel))).characteristicPoints;
    }
    
    public long getExpForLevel(final int level) {
        return ((LevelInfo)this.levelInfos.get(Math.max(1, Math.min(level, this.maxLevel + 1)))).experience;
    }
    
    public float getXpPercentLost(final int level) {
        return ((LevelInfo)this.levelInfos.get(Math.min(level, this.maxLevel))).expPercentLost;
    }
    
    public long getMaxExp() {
        return ((LevelInfo)this.levelInfos.get(91)).experience;
    }
    
    public byte getMaxLevel() {
        return this.maxLevel;
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static LevelData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)LevelData.class);
    }
    
    private static class Singleton
    {
        private static final LevelData INSTANCE;
        
        static {
            INSTANCE = new LevelData();
        }
    }
    
    private static class LevelInfo
    {
        private final short characteristicPoints;
        private final long experience;
        private final float expPercentLost;
        
        public LevelInfo(final short characteristicPoints, final long experience, final float expPercentLost) {
            this.characteristicPoints = characteristicPoints;
            this.experience = experience;
            this.expPercentLost = expPercentLost;
        }
    }
}
