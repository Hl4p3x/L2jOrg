// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.Objects;
import org.l2j.commons.util.CommonUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.Iterator;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.model.effects.EffectType;
import java.util.List;
import java.util.EnumMap;
import org.l2j.gameserver.enums.AISkillScope;
import java.util.Collections;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.holders.DropHolder;
import java.util.ArrayList;
import org.l2j.gameserver.enums.DropType;
import java.util.HashSet;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.function.Function;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.MpRewardAffectType;
import org.l2j.gameserver.enums.MpRewardType;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.gameserver.Config;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.ArrayIntList;
import java.util.Map;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntList;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class NpcData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final IntList masterIDs;
    private final IntMap<NpcTemplate> npcs;
    private final Map<String, Integer> clans;
    
    private NpcData() {
        this.masterIDs = (IntList)new ArrayIntList();
        this.npcs = (IntMap<NpcTemplate>)new HashIntMap();
        this.clans = new HashMap<String, Integer>();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/stats/npcs/npcs.xsd");
    }
    
    public boolean isMaster(final int id) {
        return this.masterIDs.contains(id);
    }
    
    public synchronized void load() {
        this.masterIDs.clear();
        this.parseDatapackDirectory("data/stats/npcs", false);
        NpcData.LOGGER.info("Loaded {} NPCs.", (Object)this.npcs.size());
        if (Config.CUSTOM_NPC_DATA) {
            final int npcCount = this.npcs.size();
            this.parseDatapackDirectory("data/stats/npcs/custom", true);
            NpcData.LOGGER.info("Loaded {} Custom NPCs", (Object)(this.npcs.size() - npcCount));
        }
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node node = doc.getFirstChild(); node != null; node = node.getNextSibling()) {
            if ("list".equalsIgnoreCase(node.getNodeName())) {
                for (Node listNode = node.getFirstChild(); listNode != null; listNode = listNode.getNextSibling()) {
                    if ("npc".equalsIgnoreCase(listNode.getNodeName())) {
                        NamedNodeMap attrs = listNode.getAttributes();
                        final StatsSet set = new StatsSet(new HashMap<String, Object>());
                        final int npcId = this.parseInt(attrs, "id");
                        Map<String, Object> parameters = null;
                        Map<Integer, Skill> skills = null;
                        Set<Integer> clans = null;
                        Set<Integer> ignoreClanNpcIds = null;
                        List<DropHolder> dropLists = null;
                        set.set("id", npcId);
                        set.set("displayId", this.parseInt(attrs, "displayId", npcId));
                        set.set("level", this.parseByte(attrs, "level", (byte)70));
                        set.set("type", this.parseString(attrs, "type", "Npc"));
                        set.set("name", this.parseString(attrs, "name"));
                        set.set("usingServerSideName", this.parseBoolean(attrs, "usingServerSideName"));
                        set.set("title", this.parseString(attrs, "title"));
                        set.set("usingServerSideTitle", this.parseBoolean(attrs, "usingServerSideTitle"));
                        set.set("elementalType", this.parseEnum(attrs, (Class)ElementalType.class, "element"));
                        for (Node npcNode = listNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling()) {
                            attrs = npcNode.getAttributes();
                            final String lowerCase = npcNode.getNodeName().toLowerCase();
                            switch (lowerCase) {
                                case "parameters": {
                                    if (parameters == null) {
                                        parameters = new HashMap<String, Object>();
                                    }
                                    parameters.putAll(this.parseParameters(npcNode));
                                    break;
                                }
                                case "race":
                                case "sex": {
                                    set.set(npcNode.getNodeName(), npcNode.getTextContent().toUpperCase());
                                    break;
                                }
                                case "equipment": {
                                    set.set("chestId", this.parseInt(attrs, "chest"));
                                    set.set("rhandId", this.parseInt(attrs, "rhand"));
                                    set.set("lhandId", this.parseInt(attrs, "lhand"));
                                    set.set("weaponEnchant", this.parseInt(attrs, "weaponEnchant"));
                                    break;
                                }
                                case "acquire": {
                                    set.set("exp", this.parseDouble(attrs, "exp"));
                                    set.set("attribute_exp", this.parseLong(attrs, "attribute_exp"));
                                    set.set("sp", this.parseDouble(attrs, "sp"));
                                    set.set("raidPoints", this.parseDouble(attrs, "raidPoints"));
                                    break;
                                }
                                case "mpreward": {
                                    set.set("mpRewardValue", this.parseInt(attrs, "value"));
                                    set.set("mpRewardType", this.parseEnum(attrs, (Class)MpRewardType.class, "type", (Enum)MpRewardType.DIFF));
                                    set.set("mpRewardTicks", this.parseInt(attrs, "ticks"));
                                    set.set("mpRewardAffectType", this.parseEnum(attrs, (Class)MpRewardAffectType.class, "affects", (Enum)MpRewardAffectType.SOLO));
                                    break;
                                }
                                case "stats": {
                                    set.set("baseSTR", this.parseInt(attrs, "str"));
                                    set.set("baseINT", this.parseInt(attrs, "int"));
                                    set.set("baseDEX", this.parseInt(attrs, "dex"));
                                    set.set("baseWIT", this.parseInt(attrs, "wit"));
                                    set.set("baseCON", this.parseInt(attrs, "con"));
                                    set.set("baseMEN", this.parseInt(attrs, "men"));
                                    for (Node statsNode = npcNode.getFirstChild(); statsNode != null; statsNode = statsNode.getNextSibling()) {
                                        attrs = statsNode.getAttributes();
                                        final String lowerCase2 = statsNode.getNodeName().toLowerCase();
                                        switch (lowerCase2) {
                                            case "vitals": {
                                                set.set("baseHpMax", this.parseDouble(attrs, "hp"));
                                                set.set("baseHpReg", this.parseDouble(attrs, "hpRegen"));
                                                set.set("baseMpMax", this.parseDouble(attrs, "mp"));
                                                set.set("baseMpReg", this.parseDouble(attrs, "mpRegen"));
                                                break;
                                            }
                                            case "attack": {
                                                set.set("basePAtk", this.parseDouble(attrs, "physical"));
                                                set.set("baseMAtk", this.parseDouble(attrs, "magical"));
                                                set.set("baseRndDam", this.parseInt(attrs, "random"));
                                                set.set("baseCritRate", this.parseDouble(attrs, "critical"));
                                                set.set("accuracy", this.parseFloat(attrs, "accuracy"));
                                                set.set("basePAtkSpd", this.parseFloat(attrs, "attackSpeed", Float.valueOf(300.0f)));
                                                set.set("reuseDelay", this.parseInt(attrs, "reuseDelay"));
                                                set.set("baseAtkType", this.parseString(attrs, "type"));
                                                set.set("baseAtkRange", this.parseInt(attrs, "range"));
                                                set.set("distance", this.parseInt(attrs, "distance"));
                                                set.set("width", this.parseInt(attrs, "width"));
                                                break;
                                            }
                                            case "defence": {
                                                set.set("basePDef", this.parseDouble(attrs, "physical"));
                                                set.set("baseMDef", this.parseDouble(attrs, "magical"));
                                                set.set("evasion", this.parseInt(attrs, "evasion"));
                                                set.set("baseShldDef", this.parseInt(attrs, "shield"));
                                                set.set("baseShldRate", this.parseInt(attrs, "shieldRate"));
                                                break;
                                            }
                                            case "abnormalresist": {
                                                set.set("physicalAbnormalResist", this.parseDouble(attrs, "physical", 10.0));
                                                set.set("magicAbnormalResist", this.parseDouble(attrs, "magic", 10.0));
                                                break;
                                            }
                                            case "attribute": {
                                                for (Node attribute_node = statsNode.getFirstChild(); attribute_node != null; attribute_node = attribute_node.getNextSibling()) {
                                                    attrs = attribute_node.getAttributes();
                                                    final String lowerCase3 = attribute_node.getNodeName().toLowerCase();
                                                    switch (lowerCase3) {
                                                        case "attack": {
                                                            final String attackAttributeType = this.parseString(attrs, "type");
                                                            final String upperCase = attackAttributeType.toUpperCase();
                                                            switch (upperCase) {
                                                                case "FIRE": {
                                                                    set.set("baseFire", this.parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                                case "WATER": {
                                                                    set.set("baseWater", this.parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                                case "WIND": {
                                                                    set.set("baseWind", this.parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                                case "EARTH": {
                                                                    set.set("baseEarth", this.parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                                case "DARK": {
                                                                    set.set("baseDark", this.parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                                case "HOLY": {
                                                                    set.set("baseHoly", this.parseInt(attrs, "value"));
                                                                    break;
                                                                }
                                                            }
                                                            break;
                                                        }
                                                        case "defence": {
                                                            set.set("baseFireRes", this.parseInt(attrs, "fire"));
                                                            set.set("baseWaterRes", this.parseInt(attrs, "water"));
                                                            set.set("baseWindRes", this.parseInt(attrs, "wind"));
                                                            set.set("baseEarthRes", this.parseInt(attrs, "earth"));
                                                            set.set("baseHolyRes", this.parseInt(attrs, "holy"));
                                                            set.set("baseDarkRes", this.parseInt(attrs, "dark"));
                                                            set.set("baseElementRes", this.parseInt(attrs, "default"));
                                                            break;
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                            case "speed": {
                                                for (Node speedNode = statsNode.getFirstChild(); speedNode != null; speedNode = speedNode.getNextSibling()) {
                                                    attrs = speedNode.getAttributes();
                                                    final String lowerCase4 = speedNode.getNodeName().toLowerCase();
                                                    switch (lowerCase4) {
                                                        case "walk": {
                                                            final double ground = this.parseDouble(attrs, "ground", 50.0);
                                                            set.set("baseWalkSpd", ground);
                                                            set.set("baseSwimWalkSpd", this.parseDouble(attrs, "swim", ground));
                                                            set.set("baseFlyWalkSpd", this.parseDouble(attrs, "fly", ground));
                                                            break;
                                                        }
                                                        case "run": {
                                                            final double ground = this.parseDouble(attrs, "ground", 120.0);
                                                            set.set("baseRunSpd", ground);
                                                            set.set("baseSwimRunSpd", this.parseDouble(attrs, "swim", ground));
                                                            set.set("baseFlyRunSpd", this.parseDouble(attrs, "fly", ground));
                                                            break;
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                            case "hittime": {
                                                set.set("hitTime", this.parseInt(npcNode, 100));
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                case "status": {
                                    set.set("unique", Util.computeIfNonNull((Object)attrs.getNamedItem("unique"), (Function)this::parseBoolean));
                                    set.set("attackable", this.parseBoolean(attrs, "attackable", true));
                                    set.set("targetable", this.parseBoolean(attrs, "targetable", true));
                                    set.set("talkable", this.parseBoolean(attrs, "talkable", true));
                                    set.set("undying", this.parseBoolean(attrs, "undying", true));
                                    set.set("showName", this.parseBoolean(attrs, "showName", true));
                                    set.set("randomWalk", Util.computeIfNonNull((Object)attrs.getNamedItem("randomWalk"), (Function)this::parseBoolean));
                                    set.set("randomAnimation", this.parseBoolean(attrs, "randomAnimation", true));
                                    set.set("flying", this.parseBoolean(attrs, "flying"));
                                    set.set("canMove", this.parseBoolean(attrs, "canMove", true));
                                    set.set("noSleepMode", this.parseBoolean(attrs, "noSleepMode"));
                                    set.set("passableDoor", this.parseBoolean(attrs, "passableDoor"));
                                    set.set("hasSummoner", this.parseBoolean(attrs, "hasSummoner"));
                                    set.set("canBeSown", this.parseBoolean(attrs, "canBeSown"));
                                    set.set("isDeathPenalty", this.parseBoolean(attrs, "isDeathPenalty"));
                                    break;
                                }
                                case "skilllist": {
                                    skills = new HashMap<Integer, Skill>();
                                    for (Node skillListNode = npcNode.getFirstChild(); skillListNode != null; skillListNode = skillListNode.getNextSibling()) {
                                        if ("skill".equalsIgnoreCase(skillListNode.getNodeName())) {
                                            attrs = skillListNode.getAttributes();
                                            final int skillId = this.parseInt(attrs, "id");
                                            final int skillLevel = this.parseInt(attrs, "level");
                                            final Skill skill = SkillEngine.getInstance().getSkill(skillId, skillLevel);
                                            if (skill != null) {
                                                skills.put(skill.getId(), skill);
                                            }
                                            else {
                                                NpcData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, f.getName(), npcId, skillId, skillLevel));
                                            }
                                        }
                                    }
                                    break;
                                }
                                case "shots": {
                                    set.set("soulShot", this.parseInt(attrs, "soul"));
                                    set.set("spiritShot", this.parseInt(attrs, "spirit"));
                                    set.set("shotShotChance", this.parseInt(attrs, "shotChance"));
                                    set.set("spiritShotChance", this.parseInt(attrs, "spiritChance"));
                                    break;
                                }
                                case "corpsetime": {
                                    set.set("corpseTime", this.parseInt(npcNode, Config.DEFAULT_CORPSE_TIME));
                                    break;
                                }
                                case "excrteffect": {
                                    set.set("exCrtEffect", this.parseBoolean(npcNode, true));
                                    break;
                                }
                                case "snpcprophprate": {
                                    set.set("sNpcPropHpRate", npcNode.getTextContent());
                                    break;
                                }
                                case "ai": {
                                    set.set("aiType", this.parseString(attrs, "type", (String)null));
                                    set.set("aggroRange", this.parseInt(attrs, "aggroRange"));
                                    set.set("clanHelpRange", this.parseInt(attrs, "clanHelpRange"));
                                    set.set("dodge", this.parseInt(attrs, "dodge"));
                                    set.set("isChaos", this.parseBoolean(attrs, "isChaos"));
                                    set.set("isAggressive", this.parseBoolean(attrs, "isAggressive"));
                                    for (Node aiNode = npcNode.getFirstChild(); aiNode != null; aiNode = aiNode.getNextSibling()) {
                                        attrs = aiNode.getAttributes();
                                        final String lowerCase5 = aiNode.getNodeName().toLowerCase();
                                        switch (lowerCase5) {
                                            case "skill": {
                                                set.set("minSkillChance", this.parseInt(attrs, "minChance", 7));
                                                set.set("maxSkillChance", this.parseInt(attrs, "maxChance", 15));
                                                set.set("primarySkillId", this.parseInt(attrs, "primaryId"));
                                                set.set("shortRangeSkillId", this.parseInt(attrs, "shortRangeId"));
                                                set.set("shortRangeSkillChance", this.parseInt(attrs, "shortRangeChance"));
                                                set.set("longRangeSkillId", this.parseInt(attrs, "longRangeId"));
                                                set.set("longRangeSkillChance", this.parseInt(attrs, "longRangeChance"));
                                                break;
                                            }
                                            case "clanlist": {
                                                for (Node clanListNode = aiNode.getFirstChild(); clanListNode != null; clanListNode = clanListNode.getNextSibling()) {
                                                    attrs = clanListNode.getAttributes();
                                                    final String lowerCase6 = clanListNode.getNodeName().toLowerCase();
                                                    switch (lowerCase6) {
                                                        case "clan": {
                                                            if (clans == null) {
                                                                clans = new HashSet<Integer>(1);
                                                            }
                                                            clans.add(this.getOrCreateClanId(clanListNode.getTextContent()));
                                                            break;
                                                        }
                                                        case "ignorenpcid": {
                                                            if (ignoreClanNpcIds == null) {
                                                                ignoreClanNpcIds = new HashSet<Integer>(1);
                                                            }
                                                            ignoreClanNpcIds.add(Integer.parseInt(clanListNode.getTextContent()));
                                                            break;
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                case "droplists": {
                                    for (Node drop_lists_node = npcNode.getFirstChild(); drop_lists_node != null; drop_lists_node = drop_lists_node.getNextSibling()) {
                                        DropType dropType = null;
                                        try {
                                            dropType = Enum.valueOf(DropType.class, drop_lists_node.getNodeName().toUpperCase());
                                        }
                                        catch (Exception ex) {}
                                        if (dropType != null) {
                                            if (dropLists == null) {
                                                dropLists = new ArrayList<DropHolder>();
                                            }
                                            for (Node drop_node = drop_lists_node.getFirstChild(); drop_node != null; drop_node = drop_node.getNextSibling()) {
                                                final NamedNodeMap drop_attrs = drop_node.getAttributes();
                                                if ("item".equals(drop_node.getNodeName().toLowerCase())) {
                                                    final double chance = this.parseDouble(drop_attrs, "chance");
                                                    final DropHolder dropItem = new DropHolder(dropType, this.parseInt(drop_attrs, "id"), this.parseLong(drop_attrs, "min"), this.parseLong(drop_attrs, "max"), (dropType == DropType.LUCKY) ? (chance / 100.0) : chance);
                                                    if (ItemEngine.getInstance().getTemplate(this.parseInt(drop_attrs, "id")) == null) {
                                                        NpcData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.parseInt(drop_attrs, "id")));
                                                    }
                                                    else {
                                                        dropLists.add(dropItem);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                                case "extenddrop": {
                                    final List<Integer> extendDrop = new ArrayList<Integer>();
                                    this.forEach(npcNode, "id", idNode -> extendDrop.add(Integer.parseInt(idNode.getTextContent())));
                                    set.set("extendDrop", extendDrop);
                                    break;
                                }
                                case "collision": {
                                    for (Node collisionNode = npcNode.getFirstChild(); collisionNode != null; collisionNode = collisionNode.getNextSibling()) {
                                        attrs = collisionNode.getAttributes();
                                        final String lowerCase7 = collisionNode.getNodeName().toLowerCase();
                                        switch (lowerCase7) {
                                            case "radius": {
                                                set.set("collision_radius", this.parseDouble(attrs, "normal"));
                                                set.set("collisionRadiusGrown", this.parseDouble(attrs, "grown"));
                                                break;
                                            }
                                            case "height": {
                                                set.set("collision_height", this.parseDouble(attrs, "normal"));
                                                set.set("collisionHeightGrown", this.parseDouble(attrs, "grown"));
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        NpcTemplate template = (NpcTemplate)this.npcs.get(npcId);
                        if (template == null) {
                            template = new NpcTemplate(set);
                            this.npcs.put(template.getId(), (Object)template);
                        }
                        else {
                            template.set(set);
                        }
                        if (parameters != null) {
                            template.setParameters(new StatsSet(Collections.unmodifiableMap((Map<? extends String, ?>)parameters)));
                        }
                        else {
                            template.setParameters(StatsSet.EMPTY_STATSET);
                        }
                        if (skills != null) {
                            Map<AISkillScope, List<Skill>> aiSkillLists = null;
                            for (final Skill skill2 : skills.values()) {
                                if (!skill2.isPassive()) {
                                    if (aiSkillLists == null) {
                                        aiSkillLists = new EnumMap<AISkillScope, List<Skill>>(AISkillScope.class);
                                    }
                                    final List<AISkillScope> aiSkillScopes = new ArrayList<AISkillScope>();
                                    final AISkillScope shortOrLongRangeScope = (skill2.getCastRange() <= 150) ? AISkillScope.SHORT_RANGE : AISkillScope.LONG_RANGE;
                                    if (skill2.isSuicideAttack()) {
                                        aiSkillScopes.add(AISkillScope.SUICIDE);
                                    }
                                    else {
                                        aiSkillScopes.add(AISkillScope.GENERAL);
                                        if (skill2.isContinuous()) {
                                            if (!skill2.isDebuff()) {
                                                aiSkillScopes.add(AISkillScope.BUFF);
                                            }
                                            else {
                                                aiSkillScopes.add(AISkillScope.DEBUFF);
                                                aiSkillScopes.add(AISkillScope.COT);
                                                aiSkillScopes.add(shortOrLongRangeScope);
                                            }
                                        }
                                        else if (skill2.hasAnyEffectType(EffectType.DISPEL, EffectType.DISPEL_BY_SLOT)) {
                                            aiSkillScopes.add(AISkillScope.NEGATIVE);
                                            aiSkillScopes.add(shortOrLongRangeScope);
                                        }
                                        else if (skill2.hasAnyEffectType(EffectType.HEAL)) {
                                            aiSkillScopes.add(AISkillScope.HEAL);
                                        }
                                        else if (skill2.hasAnyEffectType(EffectType.PHYSICAL_ATTACK, EffectType.PHYSICAL_ATTACK_HP_LINK, EffectType.MAGICAL_ATTACK, EffectType.DEATH_LINK, EffectType.HP_DRAIN)) {
                                            aiSkillScopes.add(AISkillScope.ATTACK);
                                            aiSkillScopes.add(AISkillScope.UNIVERSAL);
                                            aiSkillScopes.add(shortOrLongRangeScope);
                                        }
                                        else if (skill2.hasAnyEffectType(EffectType.SLEEP)) {
                                            aiSkillScopes.add(AISkillScope.IMMOBILIZE);
                                        }
                                        else if (skill2.hasAnyEffectType(EffectType.BLOCK_ACTIONS, EffectType.ROOT)) {
                                            aiSkillScopes.add(AISkillScope.IMMOBILIZE);
                                            aiSkillScopes.add(shortOrLongRangeScope);
                                        }
                                        else if (skill2.hasAnyEffectType(EffectType.MUTE, EffectType.BLOCK_CONTROL)) {
                                            aiSkillScopes.add(AISkillScope.COT);
                                            aiSkillScopes.add(shortOrLongRangeScope);
                                        }
                                        else if (skill2.hasAnyEffectType(EffectType.DMG_OVER_TIME, EffectType.DMG_OVER_TIME_PERCENT)) {
                                            aiSkillScopes.add(shortOrLongRangeScope);
                                        }
                                        else if (skill2.hasAnyEffectType(EffectType.RESURRECTION)) {
                                            aiSkillScopes.add(AISkillScope.RES);
                                        }
                                        else {
                                            aiSkillScopes.add(AISkillScope.UNIVERSAL);
                                        }
                                    }
                                    for (final AISkillScope aiSkillScope : aiSkillScopes) {
                                        aiSkillLists.computeIfAbsent(aiSkillScope, k -> new ArrayList()).add(skill2);
                                    }
                                }
                            }
                            template.setSkills(skills);
                            template.setAISkillLists(aiSkillLists);
                        }
                        else {
                            template.setSkills(null);
                            template.setAISkillLists(null);
                        }
                        template.setClans(clans);
                        template.setIgnoreClanNpcIds(ignoreClanNpcIds);
                        if (dropLists != null) {
                            for (final DropHolder dropHolder : dropLists) {
                                switch (dropHolder.getDropType()) {
                                    case DROP:
                                    case LUCKY: {
                                        template.addDrop(dropHolder);
                                        continue;
                                    }
                                    case SPOIL: {
                                        template.addSpoil(dropHolder);
                                        continue;
                                    }
                                }
                            }
                        }
                        if (!template.getParameters().getMinionList("Privates").isEmpty() && template.getParameters().getSet().get("SummonPrivateRate") == null) {
                            this.masterIDs.add(template.getId());
                        }
                    }
                }
            }
        }
    }
    
    private int getOrCreateClanId(final String clanName) {
        Integer id = this.clans.get(clanName);
        if (id == null) {
            id = this.clans.size();
            this.clans.put(clanName, id);
        }
        return id;
    }
    
    public int getClanId(final String clanName) {
        final Integer id = this.clans.get(clanName);
        return (id != null) ? id : -1;
    }
    
    public Set<String> getClansByIds(final Set<Integer> clanIds) {
        final Set<String> result = new HashSet<String>();
        if (clanIds == null) {
            return result;
        }
        for (final Map.Entry<String, Integer> record : this.clans.entrySet()) {
            for (final int id : clanIds) {
                if (record.getValue() == id) {
                    result.add(record.getKey());
                }
            }
        }
        return result;
    }
    
    public NpcTemplate getTemplate(final int id) {
        return (NpcTemplate)this.npcs.get(id);
    }
    
    public NpcTemplate getTemplateByName(final String name) {
        for (final NpcTemplate npcTemplate : this.npcs.values()) {
            if (npcTemplate.getName().equalsIgnoreCase(name)) {
                return npcTemplate;
            }
        }
        return null;
    }
    
    public List<NpcTemplate> getTemplates(final Predicate<NpcTemplate> filter) {
        return this.npcs.values().stream().filter((Predicate<? super Object>)filter).collect((Collector<? super Object, ?, List<NpcTemplate>>)Collectors.toList());
    }
    
    public List<NpcTemplate> getAllOfLevel(final int... lvls) {
        return this.getTemplates(template -> Util.contains(lvls, (int)template.getLevel()));
    }
    
    public List<NpcTemplate> getAllMonstersOfLevel(final int... lvls) {
        return this.getTemplates(template -> Util.contains(lvls, (int)template.getLevel()) && template.isType("Monster"));
    }
    
    public List<NpcTemplate> getAllNpcStartingWith(final String text) {
        return this.getTemplates(template -> template.isType("Npc") && template.getName().startsWith(text));
    }
    
    public List<NpcTemplate> getAllNpcOfClassType(final String... classTypes) {
        return this.getTemplates(template -> CommonUtil.contains(classTypes, template.getType(), true));
    }
    
    public boolean existsNpc(final int npcId) {
        return Objects.nonNull(this.npcs.get(npcId));
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static NpcData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)NpcData.class);
    }
    
    private static class Singleton
    {
        private static final NpcData INSTANCE;
        
        static {
            INSTANCE = new NpcData();
        }
    }
}
