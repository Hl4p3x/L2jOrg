// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.skill.api;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.xml.impl.PetSkillData;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.handler.AffectScopeHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.handler.AffectObjectHandler;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.handler.TargetHandler;
import java.util.ServiceLoader;
import org.l2j.gameserver.handler.ITargetTypeHandler;
import java.util.function.IntConsumer;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.engine.skill.SkillAutoUseType;
import org.l2j.gameserver.enums.BasicProperty;
import org.l2j.gameserver.enums.NextActionType;
import org.l2j.gameserver.model.stats.TraitType;
import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.model.skills.targets.TargetType;
import java.util.EnumSet;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.gameserver.model.skills.AbnormalType;
import java.util.Arrays;
import java.util.Set;
import io.github.joealisson.primitive.function.IntBiConsumer;
import io.github.joealisson.primitive.IntMap;
import java.util.Map;
import org.l2j.gameserver.model.skills.EffectScope;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.handler.EffectHandler;
import java.util.function.Function;
import org.l2j.gameserver.handler.SkillConditionHandler;
import org.l2j.gameserver.model.skills.SkillConditionScope;
import org.w3c.dom.NamedNodeMap;
import java.util.Objects;
import org.l2j.gameserver.model.skills.SkillOperateType;
import org.w3c.dom.Node;
import java.util.function.Consumer;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.actor.instance.Player;
import io.github.joealisson.primitive.HashLongMap;
import io.github.joealisson.primitive.LongMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.EffectParser;

public class SkillEngine extends EffectParser
{
    private static final Logger LOGGER;
    private final LongMap<Skill> skills;
    
    private SkillEngine() {
        this.skills = (LongMap<Skill>)new HashLongMap(36800);
    }
    
    public Skill getSkill(final int id, final int level) {
        return (Skill)this.skills.get(skillHashCode(id, level));
    }
    
    public void addSiegeSkills(final Player player) {
        player.addSkill(CommonSkill.IMPRIT_OF_LIGHT.getSkill(), false);
        player.addSkill(CommonSkill.IMPRIT_OF_DARKNESS.getSkill(), false);
        player.addSkill(CommonSkill.BUILD_HEADQUARTERS.getSkill(), false);
        if (player.isNoble()) {
            player.addSkill(CommonSkill.BUILD_ADVANCED_HEADQUARTERS.getSkill(), false);
        }
        if (player.getClan().getCastleId() > 0) {
            player.addSkill(CommonSkill.OUTPOST_CONSTRUCTION.getSkill(), false);
            player.addSkill(CommonSkill.OUTPOST_DEMOLITION.getSkill(), false);
        }
    }
    
    public void removeSiegeSkills(final Player player) {
        player.removeSkill(CommonSkill.IMPRIT_OF_LIGHT.getSkill());
        player.removeSkill(CommonSkill.IMPRIT_OF_DARKNESS.getSkill());
        player.removeSkill(CommonSkill.BUILD_HEADQUARTERS.getSkill());
        player.removeSkill(CommonSkill.BUILD_ADVANCED_HEADQUARTERS.getSkill());
        player.removeSkill(CommonSkill.OUTPOST_CONSTRUCTION.getSkill());
        player.removeSkill(CommonSkill.OUTPOST_DEMOLITION.getSkill());
    }
    
    public int getMaxLevel(final int skillId) {
        return Util.zeroIfNullOrElse((Object)this.skills.get(skillHashCode(skillId, 1)), Skill::getMaxLevel);
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/skills/skills.xsd");
    }
    
    public void load() {
        this.parseDatapackDirectory("data/skills/", true);
        SkillEngine.LOGGER.info("Loaded {} skills", (Object)this.skills.size());
        this.releaseResources();
    }
    
    protected void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", list -> this.forEach(list, "skill", (Consumer)this::parseSkill));
    }
    
    private void parseSkill(final Node skillNode) {
        Skill skill = null;
        try {
            final NamedNodeMap attr = skillNode.getAttributes();
            final int id = this.parseInt(attr, "id");
            final int maxLevel = this.parseInt(attr, "max-level");
            skill = new Skill(id, this.parseString(attr, "name"), maxLevel, this.parseBoolean(attr, "debuff"), (SkillOperateType)this.parseEnum(attr, (Class)SkillOperateType.class, "action"), (SkillType)this.parseEnum(attr, (Class)SkillType.class, "type"));
            this.skills.put(skillHashCode(id, 1), (Object)skill);
            this.parseSkillConstants(skill, skillNode);
            skill.computeSkillAttributes();
            for (Node node = skillNode.getFirstChild(); Objects.nonNull(node); node = node.getNextSibling()) {
                final String nodeName = node.getNodeName();
                switch (nodeName) {
                    case "icon": {
                        this.parseIcon(node, skill, maxLevel);
                        break;
                    }
                    case "attributes": {
                        this.parseSkillAttributes(node, skill, maxLevel);
                        break;
                    }
                    case "consume": {
                        this.parseSkillConsume(node, skill, maxLevel);
                        break;
                    }
                    case "abnormal": {
                        this.parseSkillAbnormal(node, skill, maxLevel);
                        break;
                    }
                    case "conditions": {
                        this.parseConditions(node, skill);
                        break;
                    }
                    case "effects": {
                        this.parseSkillEffects(node, skill, maxLevel);
                        break;
                    }
                }
            }
            this.getOrCloneSkillBasedOnLast(id, maxLevel, true, true);
        }
        catch (Exception e) {
            SkillEngine.LOGGER.error("Could not parse skill info {}", (Object)skill, (Object)e);
        }
    }
    
    private void parseConditions(final Node conditionsNode, final Skill skill) throws CloneNotSupportedException {
        this.parseLeveledCondition(conditionsNode, skill, skill.getMaxLevel());
        for (Node node = conditionsNode.getFirstChild(); Objects.nonNull(node); node = node.getNextSibling()) {
            if (!Objects.nonNull(node.getAttributes().getNamedItem("on-level"))) {
                final SkillCondition cond = "condition".equals(node.getNodeName()) ? this.parseNamedCondition(node) : this.parseCondition(node);
                if (Objects.nonNull(cond)) {
                    final SkillConditionScope scope = (SkillConditionScope)this.parseEnum(node.getAttributes(), (Class)SkillConditionScope.class, "scope");
                    int currentLevel = skill.getMaxLevel();
                    long hash = skillHashCode(skill.getId(), currentLevel);
                    while (currentLevel-- > 0) {
                        Util.doIfNonNull((Object)this.skills.get(hash--), s -> s.addCondition(scope, cond));
                    }
                }
                else {
                    SkillEngine.LOGGER.warn("Could not parse skill's ({}) condition {}", (Object)skill, (Object)node.getNodeName());
                }
            }
        }
    }
    
    private void parseLeveledCondition(final Node conditionsNode, final Skill skill, final int maxLevel) throws CloneNotSupportedException {
        for (Node node = conditionsNode.getFirstChild(); Objects.nonNull(node); node = node.getNextSibling()) {
            final int level = this.parseInt(node.getAttributes(), "on-level", -1);
            if (level >= 1) {
                if (level <= maxLevel) {
                    final SkillCondition cond = "condition".equals(node.getNodeName()) ? this.parseNamedCondition(node) : this.parseCondition(node);
                    if (Objects.nonNull(cond)) {
                        final SkillConditionScope scope = (SkillConditionScope)this.parseEnum(node.getAttributes(), (Class)SkillConditionScope.class, "scope");
                        final Skill sk = this.getOrCloneSkillBasedOnLast(skill.getId(), level);
                        sk.addCondition(scope, cond);
                    }
                    else {
                        SkillEngine.LOGGER.warn("Could not parse skill's ({}) condition {}", (Object)skill, (Object)node.getNodeName());
                    }
                }
            }
        }
    }
    
    private SkillCondition parseCondition(final Node node) {
        final Function<Node, SkillCondition> factory = SkillConditionHandler.getInstance().getHandlerFactory(node.getNodeName());
        return (SkillCondition)Util.computeIfNonNull((Object)factory, f -> f.apply(node));
    }
    
    private SkillCondition parseNamedCondition(final Node node) {
        final Function<Node, SkillCondition> factory = SkillConditionHandler.getInstance().getHandlerFactory(this.parseString(node.getAttributes(), "name"));
        return (SkillCondition)Util.computeIfNonNull((Object)factory, f -> f.apply(node));
    }
    
    private void parseSkillEffects(final Node node, final Skill skill, final int maxLevel) throws CloneNotSupportedException {
        for (Node child = node.getFirstChild(); Objects.nonNull(child); child = child.getNextSibling()) {
            if ("effect".equals(child.getNodeName())) {
                this.parseNamedEffect(child, skill, maxLevel);
            }
            else {
                this.parseEffect(child, skill, maxLevel);
            }
        }
    }
    
    private void parseEffect(final Node node, final Skill skill, final int maxLevel) throws CloneNotSupportedException {
        final Function<StatsSet, AbstractEffect> factory = EffectHandler.getInstance().getHandlerFactory(node.getNodeName());
        if (Objects.isNull(factory)) {
            SkillEngine.LOGGER.error("could not parse skill's {} effect {}", (Object)skill, (Object)node.getNodeName());
            return;
        }
        this.createEffect(factory, node, skill, maxLevel);
    }
    
    private void parseNamedEffect(final Node node, final Skill skill, final int maxLevel) throws CloneNotSupportedException {
        final String effectName = this.parseString(node.getAttributes(), "name");
        final Function<StatsSet, AbstractEffect> factory = EffectHandler.getInstance().getHandlerFactory(effectName);
        if (Objects.isNull(factory)) {
            SkillEngine.LOGGER.error("could not parse skill's {} effect {}", (Object)skill, (Object)effectName);
            return;
        }
        this.createEffect(factory, node, skill, maxLevel);
    }
    
    void createEffect(final Function<StatsSet, AbstractEffect> factory, final Node node, final Skill skill, final int maxLevel) throws CloneNotSupportedException {
        final NamedNodeMap attr = node.getAttributes();
        final int startLevel = this.parseInt(attr, "start-level");
        final int stopLevel = this.parseInt(attr, "stop-level", maxLevel);
        final EffectScope scope = (EffectScope)this.parseEnum(attr, (Class)EffectScope.class, "scope");
        final StatsSet staticStatSet = new StatsSet(this.parseAttributes(node));
        if (node.hasChildNodes()) {
            final IntMap<StatsSet> levelInfo = this.parseEffectChildNodes(node, startLevel, stopLevel, staticStatSet);
            if (levelInfo.isEmpty()) {
                this.addStaticEffect(factory, skill, startLevel, stopLevel, scope, staticStatSet);
            }
            else {
                for (int i = startLevel; i <= stopLevel; ++i) {
                    final Skill sk = this.getOrCloneSkillBasedOnLast(skill.getId(), i, false, true);
                    int j;
                    final IntMap intMap;
                    final StatsSet statsSet = (StatsSet)levelInfo.computeIfAbsent(i, level -> {
                        j = level;
                        while (j > 0) {
                            if (intMap.containsKey(j)) {
                                return (StatsSet)intMap.get(j);
                            }
                            else {
                                --j;
                            }
                        }
                        return new StatsSet();
                    });
                    statsSet.merge(staticStatSet);
                    sk.addEffect(scope, factory.apply(statsSet));
                }
            }
        }
        else {
            this.addStaticEffect(factory, skill, startLevel, stopLevel, scope, staticStatSet);
        }
    }
    
    private void addStaticEffect(final Function<StatsSet, AbstractEffect> factory, final Skill skill, final int startLevel, final int stopLevel, final EffectScope scope, final StatsSet staticStatSet) throws CloneNotSupportedException {
        final AbstractEffect effect = factory.apply(staticStatSet);
        for (int i = startLevel; i <= stopLevel; ++i) {
            final Skill sk = this.getOrCloneSkillBasedOnLast(skill.getId(), i, false, true);
            sk.addEffect(scope, effect);
        }
    }
    
    private void parseSkillAbnormal(final Node node, final Skill skill, final int maxLevel) throws CloneNotSupportedException {
        for (Node child = node.getFirstChild(); Objects.nonNull(child); child = child.getNextSibling()) {
            final String nodeName = child.getNodeName();
            switch (nodeName) {
                case "level": {
                    final Node node2 = child;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node2, skill, maxLevel, skill::setAbnormalLevel, (IntBiConsumer<Skill>)((level, s) -> s.setAbnormalLevel(level)));
                    break;
                }
                case "time": {
                    final Node node3 = child;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node3, skill, maxLevel, skill::setAbnormalTime, (IntBiConsumer<Skill>)((time, s) -> s.setAbnormalTime(time)));
                    break;
                }
                case "chance": {
                    final Node node4 = child;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node4, skill, maxLevel, skill::setAbnormalChance, (IntBiConsumer<Skill>)((chance, s) -> s.setAbnormalChance(chance)));
                    break;
                }
            }
        }
    }
    
    private void parseSkillConsume(final Node node, final Skill skill, final int maxLevel) throws CloneNotSupportedException {
        for (Node child = node.getFirstChild(); Objects.nonNull(child); child = child.getNextSibling()) {
            final String nodeName = child.getNodeName();
            switch (nodeName) {
                case "mana-init": {
                    final Node node2 = child;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node2, skill, maxLevel, skill::setManaInitConsume, (IntBiConsumer<Skill>)((consume, s) -> s.setManaInitConsume(consume)));
                    break;
                }
                case "mana": {
                    final Node node3 = child;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node3, skill, maxLevel, skill::setManaConsume, (IntBiConsumer<Skill>)((consume, s) -> s.setManaConsume(consume)));
                    break;
                }
                case "hp": {
                    final Node node4 = child;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node4, skill, maxLevel, skill::setHpConsume, (IntBiConsumer<Skill>)((consume, s) -> s.setHpConsume(consume)));
                    break;
                }
                case "item": {
                    final Node node5 = child;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node5, skill, maxLevel, skill::setItemConsume, (IntBiConsumer<Skill>)((item, s) -> s.setItemConsume(item)));
                    break;
                }
                case "item-count": {
                    final Node node6 = child;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node6, skill, maxLevel, skill::setItemConsumeCount, (IntBiConsumer<Skill>)((count, s) -> s.setItemConsumeCount(count)));
                    break;
                }
            }
        }
    }
    
    private void parseSkillConstants(final Skill skill, final Node skillNode) {
        for (Node node = skillNode.getFirstChild(); Objects.nonNull(node); node = node.getNextSibling()) {
            final String nodeName = node.getNodeName();
            switch (nodeName) {
                case "attributes": {
                    this.parseCostantsAttributes(node, skill);
                    break;
                }
                case "consume": {
                    this.parseConstantsConsume(node, skill);
                    break;
                }
                case "target": {
                    this.parseConstantsTarget(node, skill);
                    break;
                }
                case "abnormal": {
                    this.parseConstantAbnormal(node, skill);
                    break;
                }
                case "resist-abnormals": {
                    this.parseConstantResistAbnormals(node, skill);
                    break;
                }
                case "channeling": {
                    this.parseConstantsChanneling(node, skill);
                    break;
                }
            }
        }
    }
    
    private void parseConstantsChanneling(final Node node, final Skill skill) {
        final NamedNodeMap attr = node.getAttributes();
        skill.setChannelingSkill(this.parseInt(attr, "skill"));
        skill.setChannelingMpConsume(this.parseInt(attr, "mp-consume"));
        skill.setChannelingInitialDelay(this.parseInt(attr, "initial-delay") * 1000L);
        skill.setChannelingInterval(this.parseInt(attr, "interval") * 1000L);
    }
    
    private void parseConstantResistAbnormals(final Node node, final Skill skill) {
        skill.setResistAbnormals((Set<AbnormalType>)Arrays.stream(node.getTextContent().split(" ")).map((Function<? super String, ?>)AbnormalType::valueOf).collect((Collector<? super Object, ?, Set<? super Object>>)Collectors.toSet()));
    }
    
    private void parseConstantAbnormal(final Node node, final Skill skill) {
        final NamedNodeMap attr = node.getAttributes();
        skill.setAbnormalType((AbnormalType)this.parseEnum(attr, (Class)AbnormalType.class, "type"));
        Util.doIfNonNull((Object)attr.getNamedItem("visual"), v -> skill.setAbnormalVisualEffect(this.parseEnumSet(attr, (Class)AbnormalVisualEffect.class, "visual")));
        skill.setAbnormalSubordination((AbnormalType)this.parseEnum(attr, (Class)AbnormalType.class, "subordination"));
        skill.setAbnormalInstant(this.parseBoolean(attr, "instant"));
    }
    
    private void parseConstantsTarget(final Node node, final Skill skill) {
        final NamedNodeMap attr = node.getAttributes();
        skill.setTargetType((TargetType)this.parseEnum(attr, (Class)TargetType.class, "type"));
        skill.setAffectScope((AffectScope)this.parseEnum(attr, (Class)AffectScope.class, "scope"));
        skill.setAffectObject((AffectObject)this.parseEnum(attr, (Class)AffectObject.class, "object"));
        skill.setAffectRange(this.parseInt(attr, "range"));
        skill.setAffectMin(this.parseInt(attr, "affect-min"));
        skill.setAffectRandom(this.parseInt(attr, "affect-random"));
        final NamedNodeMap attrs;
        this.forEach(node, "fan-range", fanRangeNode -> {
            attrs = fanRangeNode.getAttributes();
            skill.setFanStartAngle(this.parseInt(attrs, "start-angle"));
            skill.setFanRadius(this.parseInt(attrs, "radius"));
            skill.setFanAngle(this.parseInt(attrs, "angle"));
        });
    }
    
    private void parseConstantsConsume(final Node node, final Skill skill) {
        final NamedNodeMap attr = node.getAttributes();
        skill.setSoulConsume(this.parseInt(attr, "soul"));
        skill.setChargeConsume(this.parseInt(attr, "charge"));
    }
    
    private void parseCostantsAttributes(final Node nodeAttributes, final Skill skill) {
        final NamedNodeMap attr = nodeAttributes.getAttributes();
        skill.setTrait((TraitType)this.parseEnum(attr, (Class)TraitType.class, "trait"));
        skill.setNextAction((NextActionType)this.parseEnum(attr, (Class)NextActionType.class, "next-action"));
        skill.setProperty((BasicProperty)this.parseEnum(attr, (Class)BasicProperty.class, "property"));
        skill.setStaticReuse(this.parseBoolean(attr, "static-reuse"));
        skill.setMagicCriticalRate(this.parseDouble(attr, "magic-critical-rate"));
        skill.setStayAfterDeath(this.parseBoolean(attr, "stay-after-death"));
        skill.setDisplayId(this.parseInt(attr, "display-id", skill.getId()));
        skill.setHitCancelTime(this.parseDouble(attr, "hit-cancel-time"));
        skill.setLevelBonusRate(this.parseInt(attr, "level-bonus-rate"));
        skill.setRemoveOnAction(this.parseBoolean(attr, "remove-on-action"));
        skill.setRemoveOnDamage(this.parseBoolean(attr, "remove-on-damage"));
        skill.setBlockedOnOlympiad(this.parseBoolean(attr, "blocked-on-olympiad"));
        skill.setSuicide(this.parseBoolean(attr, "suicide"));
        skill.setTriggered(this.parseBoolean(attr, "triggered"));
        skill.setDispellable(this.parseBoolean(attr, "dispellable"));
        skill.setCheck(this.parseBoolean(attr, "check"));
        skill.setWithoutAction(this.parseBoolean(attr, "without-action"));
        skill.setCanCastDisabled(this.parseBoolean(attr, "cast-disabled"));
        skill.setSummonShared(!this.parseBoolean(attr, "no-summon-shared"));
        skill.setRemoveAbnormalOnLeave(this.parseBoolean(attr, "remove-abnormal-on-leave"));
        skill.setIrreplacable(this.parseBoolean(attr, "irreplacable"));
        skill.setBlockActionSkill(this.parseBoolean(attr, "block-action-skill"));
        skill.setSkillAutoUseType((SkillAutoUseType)this.parseEnum(attr, (Class)SkillAutoUseType.class, "auto-use"));
        this.forEach(nodeAttributes, "element", elementNode -> {
            skill.setAttributeType((AttributeType)this.parseEnum(elementNode.getAttributes(), (Class)AttributeType.class, "type"));
            skill.setAttributeValue(this.parseInt(elementNode.getAttributes(), "value"));
        });
    }
    
    private void parseSkillAttributes(final Node attributesNode, final Skill skill, final int maxLevel) throws CloneNotSupportedException {
        for (Node node = attributesNode.getFirstChild(); Objects.nonNull(node); node = node.getNextSibling()) {
            final String nodeName = node.getNodeName();
            switch (nodeName) {
                case "magic-level": {
                    final Node node2 = node;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node2, skill, maxLevel, skill::setMagicLevel, (IntBiConsumer<Skill>)((magicLevel, s) -> s.setMagicLevel(magicLevel)));
                    break;
                }
                case "cast-range": {
                    final Node node3 = node;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node3, skill, maxLevel, skill::setCastRange, (IntBiConsumer<Skill>)((range, s) -> s.setCastRange(range)));
                    break;
                }
                case "reuse": {
                    final Node node4 = node;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node4, skill, maxLevel, skill::setReuse, (IntBiConsumer<Skill>)((reuse, s) -> s.setReuse(reuse)));
                    break;
                }
                case "cool-time": {
                    final Node node5 = node;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node5, skill, maxLevel, skill::setCoolTime, (IntBiConsumer<Skill>)((time, s) -> s.setCoolTime(time)));
                    break;
                }
                case "effect-point": {
                    final Node node6 = node;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node6, skill, maxLevel, skill::setEffectPoint, (IntBiConsumer<Skill>)((points, s) -> s.setEffectPoint(points)));
                    break;
                }
                case "effect-range": {
                    final Node node7 = node;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node7, skill, maxLevel, skill::setEffectRange, (IntBiConsumer<Skill>)((range, s) -> s.setEffectRange(range)));
                    break;
                }
                case "hit-time": {
                    final Node node8 = node;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node8, skill, maxLevel, skill::setHitTime, (IntBiConsumer<Skill>)((time, s) -> s.setHitTime(time)));
                    break;
                }
                case "activate-rate": {
                    final Node node9 = node;
                    Objects.requireNonNull(skill);
                    this.parseMappedInt(node9, skill, maxLevel, skill::setActivateRate, (IntBiConsumer<Skill>)((rate, s) -> s.setActivateRate(rate)));
                    break;
                }
            }
        }
    }
    
    private void parseMappedInt(final Node node, final Skill skill, final int maxLevel, final IntConsumer setter, final IntBiConsumer<Skill> skillSetter) throws CloneNotSupportedException {
        int lastValue = this.parseInt(node.getAttributes(), "initial");
        int lastLevel = skill.getLevel();
        setter.accept(lastValue);
        for (Node child = node.getFirstChild(); Objects.nonNull(child); child = child.getNextSibling()) {
            if ("value".equals(child.getNodeName())) {
                final int value = Integer.parseInt(child.getTextContent());
                if (lastValue != value) {
                    final int level = this.parseInt(child.getAttributes(), "level");
                    long hash = skillHashCode(skill.getId(), ++lastLevel);
                    for (Skill tmp = (Skill)this.skills.get(hash); Objects.nonNull(tmp) && lastLevel++ < level; tmp = (Skill)this.skills.get(++hash)) {
                        skillSetter.accept(lastValue, (Object)tmp);
                    }
                    lastValue = value;
                    if (level <= maxLevel) {
                        final Skill newSkill = this.getOrCloneSkillBasedOnLast(skill.getId(), level);
                        skillSetter.accept(lastValue, (Object)newSkill);
                        lastLevel = level;
                    }
                }
            }
        }
        if (lastLevel < maxLevel) {
            long hash2 = skillHashCode(skill.getId(), ++lastLevel);
            for (Skill tmp2 = (Skill)this.skills.get(hash2); Objects.nonNull(tmp2) && lastLevel++ <= maxLevel; tmp2 = (Skill)this.skills.get(++hash2)) {
                skillSetter.accept(lastValue, (Object)tmp2);
            }
        }
    }
    
    private void parseIcon(final Node iconNode, final Skill skill, final int maxLevel) throws CloneNotSupportedException {
        String lastValue = this.parseString(iconNode.getAttributes(), "initial");
        skill.setIcon(lastValue);
        for (Node node = iconNode.getFirstChild(); Objects.nonNull(node); node = node.getNextSibling()) {
            if ("value".equals(node.getNodeName())) {
                final String value = node.getTextContent();
                if (!Objects.equals(lastValue, value)) {
                    lastValue = value;
                    final int level = this.parseInt(node.getAttributes(), "level");
                    if (level <= maxLevel) {
                        final Skill newSkill = this.getOrCloneSkillBasedOnLast(skill.getId(), level);
                        newSkill.setIcon(lastValue);
                    }
                }
            }
        }
    }
    
    private Skill getOrCloneSkillBasedOnLast(final int id, final int level) throws CloneNotSupportedException {
        return this.getOrCloneSkillBasedOnLast(id, level, false, false);
    }
    
    private Skill getOrCloneSkillBasedOnLast(final int id, final int level, final boolean keepEffects, final boolean keepConditions) throws CloneNotSupportedException {
        Skill skill = null;
        long hash;
        int currentLevel;
        for (hash = skillHashCode(id, level), currentLevel = level; currentLevel > 0; --currentLevel, --hash) {
            skill = (Skill)this.skills.get(hash);
            if (Objects.nonNull(skill)) {
                break;
            }
        }
        while (Objects.nonNull(skill) && currentLevel < level) {
            skill = skill.clone(keepEffects, keepConditions);
            skill.setLevel(++currentLevel);
            this.skills.put(++hash, (Object)skill);
        }
        return skill;
    }
    
    public static void init() {
        final ServiceLoader<ITargetTypeHandler> load = ServiceLoader.load(ITargetTypeHandler.class);
        final TargetHandler instance = TargetHandler.getInstance();
        Objects.requireNonNull(instance);
        load.forEach(instance::registerHandler);
        final ServiceLoader<IAffectObjectHandler> load2 = ServiceLoader.load(IAffectObjectHandler.class);
        final AffectObjectHandler instance2 = AffectObjectHandler.getInstance();
        Objects.requireNonNull(instance2);
        load2.forEach(instance2::registerHandler);
        final ServiceLoader<IAffectScopeHandler> load3 = ServiceLoader.load(IAffectScopeHandler.class);
        final AffectScopeHandler instance3 = AffectScopeHandler.getInstance();
        Objects.requireNonNull(instance3);
        load3.forEach(instance3::registerHandler);
        final ServiceLoader<SkillConditionFactory> load4 = ServiceLoader.load(SkillConditionFactory.class);
        final SkillConditionHandler instance4 = SkillConditionHandler.getInstance();
        Objects.requireNonNull(instance4);
        load4.forEach(instance4::registerFactory);
        final ServiceLoader<SkillEffectFactory> load5 = ServiceLoader.load(SkillEffectFactory.class);
        final EffectHandler instance5 = EffectHandler.getInstance();
        Objects.requireNonNull(instance5);
        load5.forEach(instance5::registerFactory);
        getInstance().load();
        SkillTreesData.init();
        PetSkillData.init();
    }
    
    public void reload() {
        this.skills.clear();
        this.load();
        SkillTreesData.getInstance().load();
    }
    
    public static long skillHashCode(final int id, final int level) {
        return id * 65536L + level;
    }
    
    public static SkillEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SkillEngine.class);
    }
    
    private static final class Singleton
    {
        private static final SkillEngine INSTANCE;
        
        static {
            INSTANCE = new SkillEngine();
        }
    }
}
