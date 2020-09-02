// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.commons.util.Util;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.model.PetLevelData;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.gameserver.enums.MountType;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.PetData;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class PetDataTable extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, PetData> pets;
    
    private PetDataTable() {
        this.pets = new HashMap<Integer, PetData>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/PetData.xsd");
    }
    
    public static boolean isMountable(final int npcId) {
        return MountType.findByNpcId(npcId) != MountType.NONE;
    }
    
    public void load() {
        this.pets.clear();
        this.parseDatapackDirectory("data/stats/pets", false);
        PetDataTable.LOGGER.info("Loaded {} Pets.", (Object)this.pets.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final Node n = doc.getFirstChild();
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            if (d.getNodeName().equals("pet")) {
                final int npcId = this.parseInt(d.getAttributes(), "id");
                final int itemId = this.parseInt(d.getAttributes(), "itemId");
                final PetData data = new PetData(npcId, itemId);
                for (Node p = d.getFirstChild(); p != null; p = p.getNextSibling()) {
                    if (p.getNodeName().equals("set")) {
                        final NamedNodeMap attrs = p.getAttributes();
                        final String type = attrs.getNamedItem("name").getNodeValue();
                        if ("food".equals(type)) {
                            for (final String foodId : attrs.getNamedItem("val").getNodeValue().split(";")) {
                                data.addFood(Integer.valueOf(foodId));
                            }
                        }
                        else if ("load".equals(type)) {
                            data.setLoad(this.parseInt(attrs, "val"));
                        }
                        else if ("hungry_limit".equals(type)) {
                            data.setHungryLimit(this.parseInt(attrs, "val"));
                        }
                        else if ("sync_level".equals(type)) {
                            data.setSyncLevel(this.parseInt(attrs, "val") == 1);
                        }
                    }
                    else if (p.getNodeName().equals("skills")) {
                        for (Node s = p.getFirstChild(); s != null; s = s.getNextSibling()) {
                            if (s.getNodeName().equals("skill")) {
                                final NamedNodeMap attrs = s.getAttributes();
                                data.addNewSkill(this.parseInt(attrs, "skillId"), this.parseInt(attrs, "skillLvl"), this.parseInt(attrs, "minLvl"));
                            }
                        }
                    }
                    else if (p.getNodeName().equals("stats")) {
                        for (Node s = p.getFirstChild(); s != null; s = s.getNextSibling()) {
                            if (s.getNodeName().equals("stat")) {
                                final int level = Integer.parseInt(s.getAttributes().getNamedItem("level").getNodeValue());
                                final StatsSet set = new StatsSet();
                                for (Node bean = s.getFirstChild(); bean != null; bean = bean.getNextSibling()) {
                                    if (bean.getNodeName().equals("set")) {
                                        final NamedNodeMap attrs = bean.getAttributes();
                                        if (attrs.getNamedItem("name").getNodeValue().equals("speed_on_ride")) {
                                            set.set("walkSpeedOnRide", attrs.getNamedItem("walk").getNodeValue());
                                            set.set("runSpeedOnRide", attrs.getNamedItem("run").getNodeValue());
                                            set.set("slowSwimSpeedOnRide", attrs.getNamedItem("slowSwim").getNodeValue());
                                            set.set("fastSwimSpeedOnRide", attrs.getNamedItem("fastSwim").getNodeValue());
                                            if (attrs.getNamedItem("slowFly") != null) {
                                                set.set("slowFlySpeedOnRide", attrs.getNamedItem("slowFly").getNodeValue());
                                            }
                                            if (attrs.getNamedItem("fastFly") != null) {
                                                set.set("fastFlySpeedOnRide", attrs.getNamedItem("fastFly").getNodeValue());
                                            }
                                        }
                                        else {
                                            set.set(attrs.getNamedItem("name").getNodeValue(), attrs.getNamedItem("val").getNodeValue());
                                        }
                                    }
                                }
                                data.addNewStat(level, new PetLevelData(set));
                            }
                        }
                    }
                }
                this.pets.put(npcId, data);
            }
        }
    }
    
    public PetData getPetDataByItemId(final int itemId) {
        for (final PetData data : this.pets.values()) {
            if (data.getItemId() == itemId) {
                return data;
            }
        }
        return null;
    }
    
    public PetLevelData getPetLevelData(final int petId, final int petLevel) {
        final PetData pd = this.getPetData(petId);
        if (pd == null) {
            return null;
        }
        if (petLevel > pd.getMaxLevel()) {
            return pd.getPetLevelData(pd.getMaxLevel());
        }
        return pd.getPetLevelData(petLevel);
    }
    
    public PetData getPetData(final int petId) {
        if (!this.pets.containsKey(petId)) {
            PetDataTable.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), petId));
        }
        return this.pets.get(petId);
    }
    
    public int getPetMinLevel(final int petId) {
        return this.pets.get(petId).getMinLevel();
    }
    
    public int getPetItemByNpc(final int npcId) {
        return Util.zeroIfNullOrElse((Object)this.pets.get(npcId), PetData::getItemId);
    }
    
    public static PetDataTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PetDataTable.class);
    }
    
    private static class Singleton
    {
        private static final PetDataTable INSTANCE;
        
        static {
            INSTANCE = new PetDataTable();
        }
    }
}
