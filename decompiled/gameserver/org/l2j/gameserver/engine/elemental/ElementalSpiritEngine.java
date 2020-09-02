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
        final Byte type = this.parseByte(attributes, "type");
        final Byte stage = this.parseByte(attributes, "stage");
        final Integer npcId = this.parseInteger(attributes, "npc");
        final Integer extractItem = this.parseInteger(attributes, "extract-item");
        final Integer maxCharacteristics = this.parseInteger(attributes, "max-characteristics");
        final ElementalSpiritTemplate template = new ElementalSpiritTemplate(type, stage, npcId, extractItem, maxCharacteristics);
        this.spiritData.computeIfAbsent(type, (Function<? super Byte, ? extends Map<Byte, ElementalSpiritTemplate>>)HashMap::new).put(stage, template);
        final NamedNodeMap levelInfo;
        final Integer level;
        final Integer attack;
        final Integer defense;
        final Integer criticalRate;
        final Integer criticalDamage;
        final Long maxExperience;
        final ElementalSpiritTemplate elementalSpiritTemplate;
        this.forEach(spiritNode, "level", levelNode -> {
            levelInfo = levelNode.getAttributes();
            level = this.parseInteger(levelInfo, "id");
            attack = this.parseInteger(levelInfo, "atk");
            defense = this.parseInteger(levelInfo, "def");
            criticalRate = this.parseInteger(levelInfo, "crit-rate");
            criticalDamage = this.parseInteger(levelInfo, "crit-dam");
            maxExperience = this.parseLong(levelInfo, "max-exp");
            elementalSpiritTemplate.addLevelInfo(level, attack, defense, criticalRate, criticalDamage, maxExperience);
            return;
        });
        final NamedNodeMap itemInfo;
        final Integer itemId;
        final Integer count;
        final ElementalSpiritTemplate elementalSpiritTemplate2;
        this.forEach(spiritNode, "evolve-item", itemNode -> {
            itemInfo = itemNode.getAttributes();
            itemId = this.parseInteger(itemInfo, "id");
            count = this.parseInteger(itemInfo, "count");
            elementalSpiritTemplate2.addItemToEvolve(itemId, count);
            return;
        });
        final NamedNodeMap absorbInfo;
        final Integer itemId2;
        final Integer experience;
        final ElementalSpiritTemplate elementalSpiritTemplate3;
        this.forEach(spiritNode, "absorb-item", absorbItemNode -> {
            absorbInfo = absorbItemNode.getAttributes();
            itemId2 = this.parseInteger(absorbInfo, "id");
            experience = this.parseInteger(absorbInfo, "experience");
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
