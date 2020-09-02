// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.elemental;

import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import java.util.function.Function;
import org.w3c.dom.Node;
import java.util.function.Consumer;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class ElementalSpiritEngine extends GameXmlReader
{
    private static final Logger LOGGER;
    public static final long EXTRACT_FEE = 1000000L;
    public static final float FRAGMENT_XP_CONSUME = 50000.0f;
    public static final int TALENT_INIT_FEE = 50000;
    public static final int MAX_STAGE = 5;
    private final Map<Byte, Map<Byte, ElementalSpiritTemplate>> spiritData;
    
    private ElementalSpiritEngine() {
        this.spiritData = new HashMap<Byte, Map<Byte, ElementalSpiritTemplate>>(4);
    }
    
    public ElementalSpiritTemplate getSpirit(final byte type, final byte stage) {
        if (this.spiritData.containsKey(type)) {
            return this.spiritData.get(type).get(stage);
        }
        return null;
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/elemental/elemental-spirits.xsd");
    }
    
    public void load() {
        this.parseDatapackFile("data/elemental/elemental-spirits.xml");
        ElementalSpiritEngine.LOGGER.info("Loaded {} Elemental Spirits Templates.", (Object)this.spiritData.size());
        this.releaseResources();
    }
    
    protected void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", list -> this.forEach(list, "spirit", (Consumer)this::parseSpirit));
    }
    
    private void parseSpirit(final Node spiritNode) {
        final NamedNodeMap attributes = spiritNode.getAttributes();
        final byte type = this.parseByte(attributes, "type");
        final byte stage = this.parseByte(attributes, "stage");
        final int npcId = this.parseInt(attributes, "npc");
        final int extractItem = this.parseInt(attributes, "extract-item");
        final int maxCharacteristics = this.parseInt(attributes, "max-characteristics");
        final ElementalSpiritTemplate template = new ElementalSpiritTemplate(type, stage, npcId, extractItem, maxCharacteristics);
        this.spiritData.computeIfAbsent(type, (Function<? super Byte, ? extends Map<Byte, ElementalSpiritTemplate>>)HashMap::new).put(stage, template);
        final NamedNodeMap levelInfo;
        final int level;
        final int attack;
        final int defense;
        final int criticalRate;
        final int criticalDamage;
        final long maxExperience;
        final ElementalSpiritTemplate elementalSpiritTemplate;
        this.forEach(spiritNode, "level", levelNode -> {
            levelInfo = levelNode.getAttributes();
            level = this.parseInt(levelInfo, "id");
            attack = this.parseInt(levelInfo, "atk");
            defense = this.parseInt(levelInfo, "def");
            criticalRate = this.parseInt(levelInfo, "crit-rate");
            criticalDamage = this.parseInt(levelInfo, "crit-dam");
            maxExperience = this.parseLong(levelInfo, "max-exp");
            elementalSpiritTemplate.addLevelInfo(level, attack, defense, criticalRate, criticalDamage, maxExperience);
            return;
        });
        final NamedNodeMap itemInfo;
        final int itemId;
        final int count;
        final ElementalSpiritTemplate elementalSpiritTemplate2;
        this.forEach(spiritNode, "evolve-item", itemNode -> {
            itemInfo = itemNode.getAttributes();
            itemId = this.parseInt(itemInfo, "id");
            count = this.parseInt(itemInfo, "count");
            elementalSpiritTemplate2.addItemToEvolve(itemId, count);
            return;
        });
        final NamedNodeMap absorbInfo;
        final int itemId2;
        final int experience;
        final ElementalSpiritTemplate elementalSpiritTemplate3;
        this.forEach(spiritNode, "absorb-item", absorbItemNode -> {
            absorbInfo = absorbItemNode.getAttributes();
            itemId2 = this.parseInt(absorbInfo, "id");
            experience = this.parseInt(absorbInfo, "experience");
            elementalSpiritTemplate3.addAbsorbItem(itemId2, experience);
        });
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static ElementalSpiritEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ElementalSpiritEngine.class);
    }
    
    private static class Singleton
    {
        private static final ElementalSpiritEngine INSTANCE;
        
        static {
            INSTANCE = new ElementalSpiritEngine();
        }
    }
}
