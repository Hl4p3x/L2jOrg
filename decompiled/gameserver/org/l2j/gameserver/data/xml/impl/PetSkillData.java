// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.Summon;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.holders.SkillHolder;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class PetSkillData extends GameXmlReader
{
    private static Logger LOGGER;
    private final Map<Integer, Map<Long, SkillHolder>> _skillTrees;
    
    private PetSkillData() {
        this._skillTrees = new HashMap<Integer, Map<Long, SkillHolder>>();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/PetSkillData.xsd");
    }
    
    public void load() {
        this._skillTrees.clear();
        this.parseDatapackFile("data/PetSkillData.xml");
        PetSkillData.LOGGER.info("Loaded {} skills.", (Object)this._skillTrees.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("skill".equalsIgnoreCase(d.getNodeName())) {
                        final NamedNodeMap attrs = d.getAttributes();
                        final int npcId = this.parseInt(attrs, "npcId");
                        final int skillId = this.parseInt(attrs, "skillId");
                        final int skillLvl = this.parseInt(attrs, "skillLvl");
                        final Map<Long, SkillHolder> skillTree = this._skillTrees.computeIfAbsent(Integer.valueOf(npcId), k -> new HashMap());
                        if (SkillEngine.getInstance().getSkill(skillId, (skillLvl == 0) ? 1 : skillLvl) != null) {
                            skillTree.put(SkillEngine.skillHashCode(skillId, skillLvl + 1), new SkillHolder(skillId, skillLvl));
                        }
                        else {
                            PetSkillData.LOGGER.info("Could not find skill with id {}, level {} for NPC  {}", new Object[] { skillId, skillLvl, npcId });
                        }
                    }
                }
            }
        }
    }
    
    public int getAvailableLevel(final Summon pet, final int skillId) {
        int lvl = 0;
        if (!this._skillTrees.containsKey(pet.getId())) {
            PetSkillData.LOGGER.warn("Pet id {} does not have any skills assigned.", (Object)pet.getId());
            return lvl;
        }
        for (final SkillHolder skillHolder : this._skillTrees.get(pet.getId()).values()) {
            if (skillHolder.getSkillId() != skillId) {
                continue;
            }
            if (skillHolder.getLevel() == 0) {
                if (pet.getLevel() < 70) {
                    lvl = pet.getLevel() / 10;
                    if (lvl <= 0) {
                        lvl = 1;
                    }
                }
                else {
                    lvl = 7 + (pet.getLevel() - 70) / 5;
                }
                final int maxLvl = SkillEngine.getInstance().getMaxLevel(skillHolder.getSkillId());
                if (lvl > maxLvl) {
                    lvl = maxLvl;
                    break;
                }
                break;
            }
            else {
                if (1 > pet.getLevel() || skillHolder.getLevel() <= lvl) {
                    continue;
                }
                lvl = skillHolder.getLevel();
            }
        }
        return lvl;
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static PetSkillData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        PetSkillData.LOGGER = LoggerFactory.getLogger((Class)PetSkillData.class);
    }
    
    private static class Singleton
    {
        private static final PetSkillData INSTANCE;
        
        static {
            INSTANCE = new PetSkillData();
        }
    }
}
