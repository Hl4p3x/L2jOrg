// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.function.Predicate;
import java.util.Arrays;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.Containers;
import org.l2j.gameserver.model.base.AcquireSkillType;
import java.util.HashMap;
import org.l2j.gameserver.model.Clan;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.l2j.gameserver.model.holders.PlayerSkillHolder;
import java.util.Collection;
import java.util.LinkedList;
import org.l2j.gameserver.model.interfaces.ISkillsHolder;
import java.util.Iterator;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.List;
import io.github.joealisson.primitive.HashIntSet;
import org.l2j.gameserver.model.base.SocialClass;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.StatsSet;
import org.w3c.dom.NamedNodeMap;
import io.github.joealisson.primitive.HashLongMap;
import java.util.Objects;
import org.w3c.dom.Node;
import java.util.function.Consumer;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.IntMap;
import java.util.concurrent.atomic.AtomicBoolean;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.SkillLearn;
import io.github.joealisson.primitive.LongMap;
import org.l2j.gameserver.model.base.ClassId;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class SkillTreesData extends GameXmlReader
{
    private static final Logger LOGGER;
    private static final Map<ClassId, LongMap<SkillLearn>> classSkillTrees;
    private static final LongMap<SkillLearn> fishingSkillTree;
    private static final LongMap<SkillLearn> pledgeSkillTree;
    private static final LongMap<SkillLearn> subPledgeSkillTree;
    private static final LongMap<SkillLearn> transformSkillTree;
    private static final LongMap<SkillLearn> commonSkillTree;
    private static final LongMap<SkillLearn> nobleSkillTree;
    private static final LongMap<SkillLearn> heroSkillTree;
    private static final LongMap<SkillLearn> gameMasterSkillTree;
    private static final LongMap<SkillLearn> gameMasterAuraSkillTree;
    private static final Map<ClassId, IntSet> removeSkillCache;
    private static final Map<ClassId, ClassId> parentClassMap;
    private final AtomicBoolean isLoading;
    private IntMap<long[]> _skillsByClassIdHashCodes;
    private IntMap<long[]> _skillsByRaceHashCodes;
    private long[] _allSkillsHashCodes;
    
    private SkillTreesData() {
        this.isLoading = new AtomicBoolean();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/skillTrees.xsd");
    }
    
    public void load() {
        this.isLoading.set(true);
        SkillTreesData.classSkillTrees.clear();
        SkillTreesData.fishingSkillTree.clear();
        SkillTreesData.pledgeSkillTree.clear();
        SkillTreesData.subPledgeSkillTree.clear();
        SkillTreesData.transformSkillTree.clear();
        SkillTreesData.nobleSkillTree.clear();
        SkillTreesData.heroSkillTree.clear();
        SkillTreesData.gameMasterSkillTree.clear();
        SkillTreesData.gameMasterAuraSkillTree.clear();
        SkillTreesData.removeSkillCache.clear();
        this.parseDatapackDirectory("data/skillTrees/", true);
        this.generateCheckArrays();
        this.report();
        this.isLoading.set(false);
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "skillTree", (Consumer)this::parseSkillTree));
    }
    
    private void parseSkillTree(final Node skillTreeNode) {
        final NamedNodeMap attributes = skillTreeNode.getAttributes();
        final String type = this.parseString(attributes, "type");
        final Integer cId = this.parseInteger(attributes, "classId", Integer.valueOf(-1));
        final Integer parentId = this.parseInteger(attributes, "parentClassId", Integer.valueOf(-1));
        final ClassId classId = ClassId.getClassId(cId);
        if (Objects.nonNull(classId) && parentId > -1 && !cId.equals(parentId)) {
            SkillTreesData.parentClassMap.putIfAbsent(classId, ClassId.getClassId(parentId));
        }
        final LongMap<SkillLearn> classSkillTree = (LongMap<SkillLearn>)new HashLongMap();
        this.forEach(skillTreeNode, "skill", skillNode -> this.parseSkill(skillNode, classSkillTree, classId, type));
        if (type.equals("classSkillTree") && cId > -1) {
            final LongMap<SkillLearn> classSkillTrees = SkillTreesData.classSkillTrees.get(classId);
            if (Objects.isNull(classSkillTrees)) {
                SkillTreesData.classSkillTrees.put(classId, classSkillTree);
            }
            else {
                classSkillTrees.putAll((LongMap)classSkillTree);
            }
        }
    }
    
    private void parseSkill(final Node skillNode, final LongMap<SkillLearn> classSkillTree, final ClassId classId, final String type) {
        final SkillLearn skillLearn = new SkillLearn(new StatsSet(this.parseAttributes(skillNode)));
        SkillEngine.getInstance().getSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel());
        for (Node b = skillNode.getFirstChild(); b != null; b = b.getNextSibling()) {
            final NamedNodeMap attrs = b.getAttributes();
            final String nodeName = b.getNodeName();
            switch (nodeName) {
                case "item": {
                    skillLearn.addRequiredItem(new ItemHolder(this.parseInteger(attrs, "id"), this.parseInteger(attrs, "count")));
                    break;
                }
                case "preRequisiteSkill": {
                    skillLearn.addPreReqSkill(new SkillHolder(this.parseInteger(attrs, "id"), this.parseInteger(attrs, "lvl")));
                    break;
                }
                case "race": {
                    skillLearn.addRace(Race.valueOf(b.getTextContent()));
                    break;
                }
                case "residenceId": {
                    skillLearn.addResidenceId(Integer.valueOf(b.getTextContent()));
                    break;
                }
                case "socialClass": {
                    skillLearn.setSocialClass(Enum.valueOf(SocialClass.class, b.getTextContent()));
                    break;
                }
                case "removeSkill": {
                    final int removeSkillId = this.parseInteger(attrs, "id");
                    skillLearn.addRemoveSkills(removeSkillId);
                    SkillTreesData.removeSkillCache.computeIfAbsent(classId, k -> new HashIntSet()).add(removeSkillId);
                    break;
                }
            }
        }
        final long skillHashCode = SkillEngine.skillHashCode(skillLearn.getSkillId(), skillLearn.getSkillLevel());
        switch (type) {
            case "classSkillTree": {
                if (Objects.nonNull(classId)) {
                    classSkillTree.put(skillHashCode, (Object)skillLearn);
                    break;
                }
                SkillTreesData.commonSkillTree.put(skillHashCode, (Object)skillLearn);
                break;
            }
            case "fishingSkillTree": {
                SkillTreesData.fishingSkillTree.put(skillHashCode, (Object)skillLearn);
                break;
            }
            case "pledgeSkillTree": {
                SkillTreesData.pledgeSkillTree.put(skillHashCode, (Object)skillLearn);
                break;
            }
            case "subPledgeSkillTree": {
                SkillTreesData.subPledgeSkillTree.put(skillHashCode, (Object)skillLearn);
                break;
            }
            case "transformSkillTree": {
                SkillTreesData.transformSkillTree.put(skillHashCode, (Object)skillLearn);
                break;
            }
            case "nobleSkillTree": {
                SkillTreesData.nobleSkillTree.put(skillHashCode, (Object)skillLearn);
                break;
            }
            case "heroSkillTree": {
                SkillTreesData.heroSkillTree.put(skillHashCode, (Object)skillLearn);
                break;
            }
            case "gameMasterSkillTree": {
                SkillTreesData.gameMasterSkillTree.put(skillHashCode, (Object)skillLearn);
                break;
            }
            case "gameMasterAuraSkillTree": {
                SkillTreesData.gameMasterAuraSkillTree.put(skillHashCode, (Object)skillLearn);
                break;
            }
            default: {
                SkillTreesData.LOGGER.warn("Unknown Skill Tree type: {}", (Object)type);
                break;
            }
        }
    }
    
    public LongMap<SkillLearn> getCompleteClassSkillTree(ClassId classId) {
        final LongMap<SkillLearn> skillTree = (LongMap<SkillLearn>)new HashLongMap((LongMap)SkillTreesData.commonSkillTree);
        while (classId != null && SkillTreesData.classSkillTrees.get(classId) != null) {
            skillTree.putAll((LongMap)SkillTreesData.classSkillTrees.get(classId));
            classId = SkillTreesData.parentClassMap.get(classId);
        }
        return skillTree;
    }
    
    public LongMap<SkillLearn> getFishingSkillTree() {
        return SkillTreesData.fishingSkillTree;
    }
    
    public List<Skill> getNobleSkillTree() {
        return SkillTreesData.nobleSkillTree.values().stream().map(entry -> SkillEngine.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect((Collector<? super Object, ?, List<Skill>>)Collectors.toList());
    }
    
    public List<Skill> getNobleSkillAutoGetTree() {
        return SkillTreesData.nobleSkillTree.values().stream().filter(SkillLearn::isAutoGet).map(entry -> SkillEngine.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect((Collector<? super Object, ?, List<Skill>>)Collectors.toList());
    }
    
    public List<Skill> getHeroSkillTree() {
        return SkillTreesData.heroSkillTree.values().stream().map(entry -> SkillEngine.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect((Collector<? super Object, ?, List<Skill>>)Collectors.toList());
    }
    
    public List<Skill> getGMSkillTree() {
        return SkillTreesData.gameMasterSkillTree.values().stream().map(entry -> SkillEngine.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect((Collector<? super Object, ?, List<Skill>>)Collectors.toList());
    }
    
    public List<Skill> getGMAuraSkillTree() {
        return SkillTreesData.gameMasterAuraSkillTree.values().stream().map(entry -> SkillEngine.getInstance().getSkill(entry.getSkillId(), entry.getSkillLevel())).collect((Collector<? super Object, ?, List<Skill>>)Collectors.toList());
    }
    
    public boolean hasAvailableSkills(final Player player, final ClassId classId) {
        final LongMap<SkillLearn> skills = this.getCompleteClassSkillTree(classId);
        for (final SkillLearn skill : skills.values()) {
            if (skill.getSkillId() != CommonSkill.DIVINE_INSPIRATION.getId() && !skill.isAutoGet() && !skill.isLearnedByFS()) {
                if (skill.getGetLevel() > player.getLevel()) {
                    continue;
                }
                final Skill oldSkill = player.getKnownSkill(skill.getSkillId());
                if (oldSkill != null && oldSkill.getLevel() == skill.getSkillLevel() - 1) {
                    return true;
                }
                if (oldSkill == null && skill.getSkillLevel() == 1) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public List<SkillLearn> getAvailableSkills(final Player player, final ClassId classId, final boolean includeByFs, final boolean includeAutoGet) {
        return this.getAvailableSkills(player, classId, includeByFs, includeAutoGet, player);
    }
    
    private List<SkillLearn> getAvailableSkills(final Player player, final ClassId classId, final boolean includeByFs, final boolean includeAutoGet, final ISkillsHolder holder) {
        final List<SkillLearn> result = new LinkedList<SkillLearn>();
        final LongMap<SkillLearn> skills = this.getCompleteClassSkillTree(classId);
        if (skills.isEmpty()) {
            SkillTreesData.LOGGER.warn("Skilltree for class {} is not defined!", (Object)classId);
            return result;
        }
        for (final LongMap.Entry<SkillLearn> entry : skills.entrySet()) {
            final SkillLearn skill = (SkillLearn)entry.getValue();
            if ((!skill.isAutoGet() || includeAutoGet) && (!skill.isLearnedByFS() || includeByFs)) {
                if (this.isRemoveSkill(classId, skill.getSkillId())) {
                    continue;
                }
                if (player.getLevel() < skill.getGetLevel()) {
                    continue;
                }
                if (skill.getSkillLevel() > SkillEngine.getInstance().getMaxLevel(skill.getSkillId())) {
                    SkillTreesData.LOGGER.warn("SkillTreesData found learnable skill {} with level higher than max skill level!", (Object)skill.getSkillId());
                }
                else {
                    final Skill oldSkill = holder.getKnownSkill(skill.getSkillId());
                    this.checkSkillLevel(result, skill, oldSkill);
                }
            }
        }
        return result;
    }
    
    public Collection<Skill> getAllAvailableSkills(final Player player, final ClassId classId, final boolean includeByFs, final boolean includeAutoGet) {
        final PlayerSkillHolder holder = new PlayerSkillHolder(player);
        final Set<Integer> removed = new HashSet<Integer>();
        for (int i = 0; i < 1000; ++i) {
            final List<SkillLearn> learnable = this.getAvailableSkills(player, classId, includeByFs, includeAutoGet, holder);
            if (learnable.isEmpty()) {
                break;
            }
            if (learnable.stream().allMatch(skillLearn -> removed.contains(skillLearn.getSkillId()))) {
                break;
            }
            for (final SkillLearn skillLearn2 : learnable) {
                final Skill skill = SkillEngine.getInstance().getSkill(skillLearn2.getSkillId(), skillLearn2.getSkillLevel());
                for (final int skillId : skillLearn2.getRemoveSkills()) {
                    removed.add(skillId);
                    final Skill playerSkillToRemove = player.getKnownSkill(skillId);
                    final Skill holderSkillToRemove = holder.getKnownSkill(skillId);
                    if (playerSkillToRemove != null) {
                        player.removeSkill(playerSkillToRemove);
                    }
                    if (holderSkillToRemove != null) {
                        holder.removeSkill(holderSkillToRemove);
                    }
                }
                if (!removed.contains(skill.getId())) {
                    holder.addSkill(skill);
                }
            }
        }
        return holder.getSkills().values();
    }
    
    public List<SkillLearn> getAvailableAutoGetSkills(final Player player) {
        final List<SkillLearn> result = new ArrayList<SkillLearn>();
        final LongMap<SkillLearn> skills = this.getCompleteClassSkillTree(player.getClassId());
        if (skills.isEmpty()) {
            SkillTreesData.LOGGER.warn("Skill Tree for this class Id({}) is not defined!", (Object)player.getClassId());
            return result;
        }
        final Race race = player.getRace();
        for (final SkillLearn skill : skills.values()) {
            if (!skill.getRaces().isEmpty() && !skill.getRaces().contains(race)) {
                continue;
            }
            if (!skill.isAutoGet() || player.getLevel() < skill.getGetLevel()) {
                continue;
            }
            final Skill oldSkill = player.getKnownSkill(skill.getSkillId());
            if (oldSkill != null) {
                if (oldSkill.getLevel() >= skill.getSkillLevel()) {
                    continue;
                }
                result.add(skill);
            }
            else {
                result.add(skill);
            }
        }
        return result;
    }
    
    public List<SkillLearn> getAvailableFishingSkills(final Player player) {
        final List<SkillLearn> result = new ArrayList<SkillLearn>();
        final Race playerRace = player.getRace();
        for (final SkillLearn skill : SkillTreesData.fishingSkillTree.values()) {
            if (!skill.getRaces().isEmpty() && !skill.getRaces().contains(playerRace)) {
                continue;
            }
            if (!skill.isLearnedByNpc() || player.getLevel() < skill.getGetLevel()) {
                continue;
            }
            final Skill oldSkill = player.getSkills().get(skill.getSkillId());
            this.checkSkillLevel(result, skill, oldSkill);
        }
        return result;
    }
    
    private void checkSkillLevel(final List<SkillLearn> result, final SkillLearn skill, final Skill oldSkill) {
        if (oldSkill != null) {
            if (oldSkill.getLevel() == skill.getSkillLevel() - 1) {
                result.add(skill);
            }
        }
        else if (skill.getSkillLevel() == 1) {
            result.add(skill);
        }
    }
    
    public List<SkillLearn> getAvailablePledgeSkills(final Clan clan) {
        final List<SkillLearn> result = new ArrayList<SkillLearn>();
        for (final SkillLearn skill : SkillTreesData.pledgeSkillTree.values()) {
            if (!skill.isResidencialSkill() && clan.getLevel() >= skill.getGetLevel()) {
                final Skill oldSkill = (Skill)clan.getSkills().get(skill.getSkillId());
                if (oldSkill != null) {
                    if (oldSkill.getLevel() + 1 != skill.getSkillLevel()) {
                        continue;
                    }
                    result.add(skill);
                }
                else {
                    if (skill.getSkillLevel() != 1) {
                        continue;
                    }
                    result.add(skill);
                }
            }
        }
        return result;
    }
    
    public Map<Integer, SkillLearn> getMaxPledgeSkills(final Clan clan, final boolean includeSquad) {
        final Map<Integer, SkillLearn> result = new HashMap<Integer, SkillLearn>();
        for (final SkillLearn skill : SkillTreesData.pledgeSkillTree.values()) {
            if (!skill.isResidencialSkill() && clan.getLevel() >= skill.getGetLevel()) {
                this.checkClanSkillLevel(clan, result, skill);
            }
        }
        if (includeSquad) {
            for (final SkillLearn skill : SkillTreesData.subPledgeSkillTree.values()) {
                if (clan.getLevel() >= skill.getGetLevel()) {
                    this.checkClanSkillLevel(clan, result, skill);
                }
            }
        }
        return result;
    }
    
    private void checkClanSkillLevel(final Clan clan, final Map<Integer, SkillLearn> result, final SkillLearn skill) {
        final Skill oldSkill = (Skill)clan.getSkills().get(skill.getSkillId());
        if (oldSkill == null || oldSkill.getLevel() < skill.getSkillLevel()) {
            result.put(skill.getSkillId(), skill);
        }
    }
    
    public List<SkillLearn> getAvailableSubPledgeSkills(final Clan clan) {
        final List<SkillLearn> result = new ArrayList<SkillLearn>();
        for (final SkillLearn skill : SkillTreesData.subPledgeSkillTree.values()) {
            if (clan.getLevel() >= skill.getGetLevel() && clan.isLearnableSubSkill(skill.getSkillId(), skill.getSkillLevel())) {
                result.add(skill);
            }
        }
        return result;
    }
    
    public List<SkillLearn> getAvailableResidentialSkills(final int residenceId) {
        return SkillTreesData.pledgeSkillTree.values().stream().filter(s -> s.isResidencialSkill() && s.getResidenceIds().contains(residenceId)).collect((Collector<? super Object, ?, List<SkillLearn>>)Collectors.toList());
    }
    
    public SkillLearn getSkillLearn(final AcquireSkillType skillType, final int id, final int lvl, final Player player) {
        SkillLearn sl = null;
        switch (skillType) {
            case CLASS: {
                sl = this.getClassSkill(id, lvl, player.getClassId());
                break;
            }
            case TRANSFORM: {
                sl = this.getTransformSkill(id, lvl);
                break;
            }
            case FISHING: {
                sl = this.getFishingSkill(id, lvl);
                break;
            }
            case PLEDGE: {
                sl = this.getPledgeSkill(id, lvl);
                break;
            }
            case SUBPLEDGE: {
                sl = this.getSubPledgeSkill(id, lvl);
                break;
            }
        }
        return sl;
    }
    
    private SkillLearn getTransformSkill(final int id, final int lvl) {
        return (SkillLearn)SkillTreesData.transformSkillTree.get(SkillEngine.skillHashCode(id, lvl));
    }
    
    public SkillLearn getClassSkill(final int id, final int lvl, final ClassId classId) {
        return (SkillLearn)this.getCompleteClassSkillTree(classId).get(SkillEngine.skillHashCode(id, lvl));
    }
    
    private SkillLearn getFishingSkill(final int id, final int lvl) {
        return (SkillLearn)SkillTreesData.fishingSkillTree.get(SkillEngine.skillHashCode(id, lvl));
    }
    
    public SkillLearn getPledgeSkill(final int id, final int lvl) {
        return (SkillLearn)SkillTreesData.pledgeSkillTree.get(SkillEngine.skillHashCode(id, lvl));
    }
    
    public SkillLearn getSubPledgeSkill(final int id, final int lvl) {
        return (SkillLearn)SkillTreesData.subPledgeSkillTree.get(SkillEngine.skillHashCode(id, lvl));
    }
    
    public int getMinLevelForNewSkill(final Player player, final LongMap<SkillLearn> skillTree) {
        int minLevel = 0;
        if (skillTree.isEmpty()) {
            SkillTreesData.LOGGER.warn(": SkillTree is not defined for getMinLevelForNewSkill!");
        }
        else {
            for (final SkillLearn s : skillTree.values()) {
                if (player.getLevel() < s.getGetLevel() && (minLevel == 0 || minLevel > s.getGetLevel())) {
                    minLevel = s.getGetLevel();
                }
            }
        }
        return minLevel;
    }
    
    public List<SkillLearn> getNextAvailableSkills(final Player player, final ClassId classId, final boolean includeByFs, final boolean includeAutoGet) {
        final LongMap<SkillLearn> completeClassSkillTree = this.getCompleteClassSkillTree(classId);
        final List<SkillLearn> result = new LinkedList<SkillLearn>();
        if (completeClassSkillTree.isEmpty()) {
            return result;
        }
        final int minLevelForNewSkill = this.getMinLevelForNewSkill(player, completeClassSkillTree);
        if (minLevelForNewSkill > 0) {
            for (final SkillLearn skill : completeClassSkillTree.values()) {
                if (includeAutoGet || !skill.isAutoGet()) {
                    if (!includeByFs && skill.isLearnedByFS()) {
                        continue;
                    }
                    if (minLevelForNewSkill > skill.getGetLevel()) {
                        continue;
                    }
                    final Skill oldSkill = player.getKnownSkill(skill.getSkillId());
                    this.checkSkillLevel(result, skill, oldSkill);
                }
            }
        }
        return result;
    }
    
    public void cleanSkillUponAwakening(final Player player) {
        for (final Skill skill : player.getAllSkills()) {
            final int maxLvl = SkillEngine.getInstance().getMaxLevel(skill.getId());
            final long hashCode = SkillEngine.skillHashCode(skill.getId(), maxLvl);
            if (!this.isCurrentClassSkillNoParent(player.getClassId(), hashCode) && !this.isRemoveSkill(player.getClassId(), skill.getId())) {
                player.removeSkill(skill, true, true);
            }
        }
    }
    
    public boolean isHeroSkill(final int skillId, final int skillLevel) {
        return SkillTreesData.heroSkillTree.containsKey(SkillEngine.skillHashCode(skillId, skillLevel));
    }
    
    public boolean isGMSkill(final int skillId, final int skillLevel) {
        final long hashCode = SkillEngine.skillHashCode(skillId, skillLevel);
        return SkillTreesData.gameMasterSkillTree.containsKey(hashCode) || SkillTreesData.gameMasterAuraSkillTree.containsKey(hashCode);
    }
    
    public boolean isClanSkill(final int skillId, final int skillLevel) {
        final long hashCode = SkillEngine.skillHashCode(skillId, skillLevel);
        return SkillTreesData.pledgeSkillTree.containsKey(hashCode) || SkillTreesData.subPledgeSkillTree.containsKey(hashCode);
    }
    
    public boolean isRemoveSkill(final ClassId classId, final int skillId) {
        return SkillTreesData.removeSkillCache.getOrDefault(classId, Containers.emptyIntSet()).contains(skillId);
    }
    
    private boolean isCurrentClassSkillNoParent(final ClassId classId, final Long hashCode) {
        return SkillTreesData.classSkillTrees.getOrDefault(classId, (LongMap<SkillLearn>)Containers.emptyLongMap()).containsKey((long)hashCode);
    }
    
    public void addSkills(final Player gmchar, final boolean auraSkills) {
        final Collection<SkillLearn> skills = (Collection<SkillLearn>)(auraSkills ? SkillTreesData.gameMasterAuraSkillTree.values() : SkillTreesData.gameMasterSkillTree.values());
        final SkillEngine st = SkillEngine.getInstance();
        for (final SkillLearn sl : skills) {
            gmchar.addSkill(st.getSkill(sl.getSkillId(), sl.getSkillLevel()), false);
        }
    }
    
    private void generateCheckArrays() {
        final Set<ClassId> keySet = SkillTreesData.classSkillTrees.keySet();
        this._skillsByClassIdHashCodes = (IntMap<long[]>)new HashIntMap(keySet.size());
        for (final ClassId cls : keySet) {
            final LongMap<SkillLearn> tempMap = this.getCompleteClassSkillTree(cls);
            final long[] array = tempMap.keySet().toArray();
            tempMap.clear();
            Arrays.sort(array);
            this._skillsByClassIdHashCodes.put(cls.getId(), (Object)array);
        }
        final List<Long> list = new ArrayList<Long>();
        this._skillsByRaceHashCodes = (IntMap<long[]>)new HashIntMap(Race.values().length);
        for (final Race r : Race.values()) {
            for (final SkillLearn s : SkillTreesData.fishingSkillTree.values()) {
                if (s.getRaces().contains(r)) {
                    list.add(SkillEngine.skillHashCode(s.getSkillId(), s.getSkillLevel()));
                }
            }
            for (final SkillLearn s : SkillTreesData.transformSkillTree.values()) {
                if (s.getRaces().contains(r)) {
                    list.add(SkillEngine.skillHashCode(s.getSkillId(), s.getSkillLevel()));
                }
            }
            int i = 0;
            final long[] array = new long[list.size()];
            for (final long s2 : list) {
                array[i++] = s2;
            }
            Arrays.sort(array);
            this._skillsByRaceHashCodes.put(r.ordinal(), (Object)array);
            list.clear();
        }
        for (final SkillLearn s3 : SkillTreesData.commonSkillTree.values()) {
            if (s3.getRaces().isEmpty()) {
                list.add(SkillEngine.skillHashCode(s3.getSkillId(), s3.getSkillLevel()));
            }
        }
        for (final SkillLearn s3 : SkillTreesData.fishingSkillTree.values()) {
            if (s3.getRaces().isEmpty()) {
                list.add(SkillEngine.skillHashCode(s3.getSkillId(), s3.getSkillLevel()));
            }
        }
        for (final SkillLearn s3 : SkillTreesData.transformSkillTree.values()) {
            if (s3.getRaces().isEmpty()) {
                list.add(SkillEngine.skillHashCode(s3.getSkillId(), s3.getSkillLevel()));
            }
        }
        this._allSkillsHashCodes = new long[list.size()];
        int j = 0;
        for (final long hashcode : list) {
            this._allSkillsHashCodes[j++] = hashcode;
        }
        Arrays.sort(this._allSkillsHashCodes);
    }
    
    public boolean isSkillAllowed(final Player player, final Skill skill) {
        if (skill.isExcludedFromCheck()) {
            return true;
        }
        if (player.isGM() && skill.isGMSkill()) {
            return true;
        }
        if (this.isLoading.get()) {
            return true;
        }
        final int maxLvl = SkillEngine.getInstance().getMaxLevel(skill.getId());
        final long hashCode = SkillEngine.skillHashCode(skill.getId(), Math.min(skill.getLevel(), maxLvl));
        return Arrays.binarySearch((long[])this._skillsByClassIdHashCodes.get(player.getClassId().getId()), hashCode) >= 0 || Arrays.binarySearch((long[])this._skillsByRaceHashCodes.get(player.getRace().ordinal()), hashCode) >= 0 || Arrays.binarySearch(this._allSkillsHashCodes, hashCode) >= 0;
    }
    
    private void report() {
        final int classSkillTreeCount = SkillTreesData.classSkillTrees.values().stream().mapToInt(LongMap::size).sum();
        final long dwarvenOnlyFishingSkillCount = SkillTreesData.fishingSkillTree.values().stream().filter(s -> s.getRaces().contains(Race.DWARF)).count();
        final long resSkillCount = SkillTreesData.pledgeSkillTree.values().stream().filter(SkillLearn::isResidencialSkill).count();
        SkillTreesData.LOGGER.info("Loaded {} Class Skills for {} Class Skill Trees", (Object)classSkillTreeCount, (Object)SkillTreesData.classSkillTrees.size());
        SkillTreesData.LOGGER.info("Loaded {} Fishing Skills, {} Dwarven only Fishing Skills", (Object)SkillTreesData.fishingSkillTree.size(), (Object)dwarvenOnlyFishingSkillCount);
        SkillTreesData.LOGGER.info("Loaded {} Pledge Skills, {} for Pledge and {} Residential", new Object[] { SkillTreesData.pledgeSkillTree.size(), SkillTreesData.pledgeSkillTree.size() - resSkillCount, resSkillCount });
        SkillTreesData.LOGGER.info("Loaded {} Sub-Pledge Skills.", (Object)SkillTreesData.subPledgeSkillTree.size());
        SkillTreesData.LOGGER.info("Loaded {} Transform Skills.", (Object)SkillTreesData.transformSkillTree.size());
        SkillTreesData.LOGGER.info("Loaded {} Noble Skills.", (Object)SkillTreesData.nobleSkillTree.size());
        SkillTreesData.LOGGER.info("Loaded {} Hero Skills.", (Object)SkillTreesData.heroSkillTree.size());
        SkillTreesData.LOGGER.info("Loaded {} Game Master Skills.", (Object)SkillTreesData.gameMasterSkillTree.size());
        SkillTreesData.LOGGER.info("Loaded {} Game Master Aura Skills.", (Object)SkillTreesData.gameMasterAuraSkillTree.size());
        SkillTreesData.LOGGER.info("Loaded {} Common Skills to all classes.", (Object)SkillTreesData.commonSkillTree.size());
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static SkillTreesData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SkillTreesData.class);
        classSkillTrees = new HashMap<ClassId, LongMap<SkillLearn>>();
        fishingSkillTree = (LongMap)new HashLongMap();
        pledgeSkillTree = (LongMap)new HashLongMap();
        subPledgeSkillTree = (LongMap)new HashLongMap();
        transformSkillTree = (LongMap)new HashLongMap();
        commonSkillTree = (LongMap)new HashLongMap();
        nobleSkillTree = (LongMap)new HashLongMap();
        heroSkillTree = (LongMap)new HashLongMap();
        gameMasterSkillTree = (LongMap)new HashLongMap();
        gameMasterAuraSkillTree = (LongMap)new HashLongMap();
        removeSkillCache = new HashMap<ClassId, IntSet>();
        parentClassMap = new HashMap<ClassId, ClassId>();
    }
    
    private static class Singleton
    {
        protected static final SkillTreesData INSTANCE;
        
        static {
            INSTANCE = new SkillTreesData();
        }
    }
}
