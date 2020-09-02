// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.StringWriter;
import org.w3c.dom.NamedNodeMap;
import java.util.ArrayList;
import org.w3c.dom.Element;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import javax.xml.transform.TransformerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;
import javax.xml.transform.Transformer;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.LoggerFactory;
import io.github.joealisson.primitive.HashIntMap;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import io.github.joealisson.primitive.Containers;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.stats.TraitType;
import org.l2j.gameserver.enums.NextActionType;
import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.skills.AbnormalType;
import java.util.function.ToIntFunction;
import java.util.function.IntSupplier;
import org.l2j.gameserver.engine.skill.api.Skill;
import javax.xml.transform.TransformerException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Collection;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.skills.SkillOperateType;
import java.io.BufferedWriter;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.util.Objects;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Path;
import org.l2j.gameserver.model.item.type.WeaponType;
import java.util.regex.Pattern;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public class SkillCheckerWithEffects
{
    static Logger logger;
    static IntMap<String> autoSkills;
    static IntMap<IntMap<SkillModel>> skills;
    static IntMap<String> skillTypes;
    static IntMap<String> skillProperty;
    static int step;
    static SkillEngine skillData;
    static SkillEffectReader effectReader;
    private static final Pattern SKILL_CONDITIONS_PATTERN;
    private static final WeaponType[] WEAPON_TYPES;
    private static final Pattern SKILL_PATTERN;
    private static final Pattern AUTO_SKILL_PATTERN;
    private static final Pattern SKILL_NAME_PATTERN;
    
    public static void doCheck() throws IOException, TransformerConfigurationException {
        SkillCheckerWithEffects.effectReader = new SkillEffectReader();
        SkillEngine.init();
        SkillCheckerWithEffects.skillData = SkillEngine.getInstance();
        fillSkillName();
        fillSkillData();
        fillSkillConditions();
        Files.createDirectories(Path.of("new-skills", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        Files.createDirectories(Path.of("new-effects", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        Files.createDirectories(Path.of("new-effects/stat", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        for (int start = 0, end = start + SkillCheckerWithEffects.step, maxId = SkillCheckerWithEffects.skills.keySet().stream().max().orElse(0); start < maxId; start += SkillCheckerWithEffects.step, end += SkillCheckerWithEffects.step) {
            processFile(start, end);
        }
    }
    
    private static void processFile(final int start, final int end) throws IOException {
        final String file = String.format("%05d-%05d.xml", start, end - 1);
        final StringBuilder content = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<list xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://l2j.org\" xsi:schemaLocation=\"http://l2j.org skills.xsd\">\n");
        boolean isNotEmpty = false;
        for (int i = start; i < end; ++i) {
            final IntMap<SkillModel> skillLevels = (IntMap<SkillModel>)SkillCheckerWithEffects.skills.get(i);
            if (!Objects.isNull(skillLevels)) {
                final StringBuilder skillContent = processSkill(skillLevels);
                if (skillContent.length() > 0) {
                    content.append((CharSequence)skillContent).append("\n");
                    isNotEmpty = true;
                }
            }
        }
        if (isNotEmpty) {
            final BufferedWriter writer = Files.newBufferedWriter(Path.of("new-skills/", file), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            content.append("</list>");
            writer.write(content.toString());
            writer.flush();
            writer.close();
        }
    }
    
    private static StringBuilder processSkill(final IntMap<SkillModel> skillLevels) {
        final StringBuilder content = new StringBuilder();
        final int maxLevel = skillLevels.keySet().stream().max().orElse(0);
        if (maxLevel > 0) {
            final SkillModel baseSkill = (SkillModel)skillLevels.get(1);
            content.append("\t<skill id=\"").append(baseSkill.id).append("\" ").append("name=\"").append(baseSkill.name).append("\" ").append("max-level=\"").append(maxLevel).append("\" ");
            final Skill refSkill = SkillCheckerWithEffects.skillData.getSkill(baseSkill.id, 1);
            if (baseSkill.debuff) {
                content.append("debuff=\"true\" ");
            }
            if (Objects.nonNull(refSkill) && refSkill.getOperateType() != SkillOperateType.P) {
                content.append("action=\"").append(refSkill.getOperateType()).append("\"");
            }
            final String skillType = (String)SkillCheckerWithEffects.skillTypes.getOrDefault(baseSkill.isMagic, (Object)"PHYSIC");
            if (!skillType.equals("PHYSIC")) {
                content.append(" type=\"").append(skillType).append("\"");
            }
            content.append(">\n");
            content.append((CharSequence)parseSkillDescriptions(skillLevels));
            content.append((CharSequence)parseSkillsIcons(baseSkill, skillLevels, maxLevel));
            content.append((CharSequence)parseSkillAttributes(baseSkill, skillLevels, maxLevel, refSkill));
            content.append((CharSequence)parseSkillConsume(baseSkill, skillLevels, maxLevel, refSkill));
            parseSkillTarget(content, refSkill);
            if (Objects.nonNull(refSkill)) {
                parseSkillAbnormal(content, maxLevel, refSkill);
                if (!Util.isNullOrEmpty((Collection)refSkill.getAbnormalResists())) {
                    content.append("\t\t<resist-abnormals>").append(refSkill.getAbnormalResists().stream().map((Function<? super Object, ?>)Objects::toString).collect((Collector<? super Object, ?, String>)Collectors.joining(" "))).append("</resist-abnormals>\n");
                }
                if (refSkill.isChanneling()) {
                    content.append("\t\t<channeling skill=\"").append(refSkill.getChannelingSkillId()).append("\" mp-consume=\"").append(refSkill.getMpPerChanneling()).append("\" initial-delay=\"").append(refSkill.getChannelingTickInitialDelay() / 1000L).append("\" interval=\"").append(refSkill.getChannelingTickInterval() / 1000L).append("\"/>\n");
                }
                try {
                    content.append((CharSequence)parseSkillConditions(baseSkill, skillLevels, maxLevel));
                    content.append("\t\t").append((CharSequence)parseSkillEffects(baseSkill, maxLevel));
                }
                catch (TransformerException e) {
                    SkillCheckerWithEffects.logger.error(e.getMessage(), (Throwable)e);
                }
            }
            content.append("\t</skill>");
        }
        return content;
    }
    
    private static StringBuilder parseSkillEffects(final SkillModel baseSkill, final int maxLevel) throws TransformerException {
        final StringBuilder builder = new StringBuilder();
        final String effects = SkillCheckerWithEffects.effectReader.toEffectsXmlString(baseSkill.id);
        if (Util.isNotEmpty(effects)) {
            builder.append("\t\t").append(effects);
        }
        return builder;
    }
    
    private static StringBuilder parseSkillConditions(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) throws TransformerException {
        final StringBuilder builder = new StringBuilder();
        String conditions = SkillCheckerWithEffects.effectReader.toConditionsXmlString(baseSkill.id).replace("<conditions>", "").replace("</conditions>", "").replaceAll("(^\\s+|\\s+$)", "");
        final StringBuilder parsedConditions = new StringBuilder();
        if (baseSkill.equipType == 1 && !conditions.contains("EquipShield")) {
            parsedConditions.append("<condition name=\"EquipShield\"/>\n");
        }
        if (!baseSkill.attackItemTypes.isEmpty()) {
            if (!conditions.contains("<weapon")) {
                parsedConditions.append("<weapon>\n\t<type>").append(baseSkill.attackItemTypes.stream().map((Function<? super Object, ?>)Objects::toString).collect((Collector<? super Object, ?, String>)Collectors.joining(" "))).append("</type>\n</weapon>");
            }
            else {
                conditions = conditions.replaceFirst("(<type>).*?(</type>)", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)baseSkill.attackItemTypes.stream().map((Function<? super Object, ?>)Objects::toString).collect((Collector<? super Object, ?, String>)Collectors.joining(" "))));
            }
        }
        if (baseSkill.statType == 1 && !conditions.contains("remain-status")) {
            parsedConditions.append("<remain-status amount=\"").append(baseSkill.statPercent).append("\" ");
            if (baseSkill.statUP) {
                parsedConditions.append("lower=\"false\"");
            }
            parsedConditions.append(" stat=\"HP\"/>\n");
        }
        else if (baseSkill.statType == 2 && !conditions.contains("remain-status")) {
            parsedConditions.append("<remain-status amount=\"").append(baseSkill.statPercent).append("\" ");
            if (baseSkill.statUP) {
                parsedConditions.append("lower=\"false\"");
            }
            parsedConditions.append(" stat=\"MP\"/>\n");
        }
        else if (baseSkill.statType == 3 && !conditions.contains("remain-status")) {
            parsedConditions.append("<remain-status amount=\"").append(baseSkill.statPercent).append("\" ");
            if (baseSkill.statUP) {
                parsedConditions.append("lower=\"false\"");
            }
            parsedConditions.append(" stat=\"CP\"/>\n");
        }
        parsedConditions.append(conditions);
        if (parsedConditions.length() > 0) {
            builder.append("\t\t").append("<conditions>\n\t\t\t").append((CharSequence)parsedConditions).append("\n\t\t</conditions>");
        }
        return builder;
    }
    
    private static StringBuilder parseSkillConsume(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel, final Skill refSkill) {
        final StringBuilder content = new StringBuilder();
        boolean hasAttr = false;
        if (Objects.nonNull(refSkill) && (refSkill.getMaxSoulConsumeCount() > 0 || refSkill.getChargeConsumeCount() > 0)) {
            hasAttr = true;
        }
        final StringBuilder elements = new StringBuilder();
        elements.append((CharSequence)parseSkillsManaInitConsume(baseSkill, skillLevels, maxLevel));
        elements.append((CharSequence)parseSkillManaConsume(baseSkill, skillLevels, maxLevel));
        elements.append((CharSequence)parseSkillHpConsume(baseSkill, skillLevels, maxLevel));
        elements.append((CharSequence)parseSkillItemConsume(baseSkill, skillLevels, maxLevel));
        elements.append((CharSequence)parseSkillItemCountConsume(baseSkill, skillLevels, maxLevel));
        if (hasAttr) {
            content.append("\t\t<consume");
            if (refSkill.getMaxSoulConsumeCount() > 0) {
                content.append(" soul=\"").append(refSkill.getMaxSoulConsumeCount()).append("\"");
            }
            if (refSkill.getChargeConsumeCount() > 0) {
                content.append(" charge=\"").append(refSkill.getChargeConsumeCount()).append("\"");
            }
            if (elements.length() == 0) {
                content.append("/>\n");
            }
            else {
                content.append(">\n").append((CharSequence)elements).append("\t\t</consume>\n");
            }
        }
        else if (elements.length() > 0) {
            content.append("\t\t<consume>\n").append((CharSequence)elements).append("\t\t</consume>\n");
        }
        return content;
    }
    
    private static StringBuilder parseSkillItemCountConsume(final SkillModel refSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) {
        return parseSkillMappedInt("item-count", refSkill, skillLevels, maxLevel, () -> refSkill.itemCount, skill -> skill.itemCount);
    }
    
    private static StringBuilder parseSkillMappedInt(final String name, final Skill baseSkill, final int maxLevel, final IntSupplier initialSupplier, final ToIntFunction<Skill> valueFunction) {
        final StringBuilder content = new StringBuilder();
        int lastValue = initialSupplier.getAsInt();
        if (lastValue == 0) {
            boolean goAhead = false;
            for (int i = baseSkill.getLevel() + 1; i <= maxLevel; ++i) {
                if (SkillCheckerWithEffects.skillData.getMaxLevel(baseSkill.getId()) < i) {
                    return content;
                }
                final Skill skill = SkillCheckerWithEffects.skillData.getSkill(baseSkill.getId(), i);
                if (Objects.nonNull(skill) && valueFunction.applyAsInt(skill) != lastValue) {
                    goAhead = true;
                    break;
                }
            }
            if (!goAhead) {
                return content;
            }
        }
        content.append("\t\t\t<").append(name).append(" initial=\"").append(lastValue).append("\"");
        final StringBuilder levelInfo = new StringBuilder();
        for (int i = baseSkill.getLevel() + 1; i <= maxLevel && SkillCheckerWithEffects.skillData.getMaxLevel(baseSkill.getId()) >= i; ++i) {
            final Skill skill = SkillCheckerWithEffects.skillData.getSkill(baseSkill.getId(), i);
            if (Objects.nonNull(skill)) {
                if (valueFunction.applyAsInt(skill) != lastValue) {
                    lastValue = valueFunction.applyAsInt(skill);
                    levelInfo.append("\t\t\t\t<value level=\"").append(i).append("\">").append(lastValue).append("</value>\n");
                }
            }
        }
        if (levelInfo.length() > 0) {
            content.append(">\n").append((CharSequence)levelInfo).append("\t\t\t</").append(name).append(">\n");
        }
        else {
            content.append("/>\n");
        }
        return content;
    }
    
    private static StringBuilder parseSkillItemConsume(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) {
        return parseSkillMappedInt("item", baseSkill, skillLevels, maxLevel, () -> baseSkill.itemId, skill -> skill.itemId);
    }
    
    private static void parseSkillAbnormal(final StringBuilder content, final int maxLevel, final Skill refSkill) {
        if (Objects.nonNull(refSkill) && refSkill.getAbnormalTime() != 0) {
            content.append("\t\t<abnormal ");
            if (refSkill.getAbnormalType() != AbnormalType.NONE) {
                content.append("type=\"").append(refSkill.getAbnormalType()).append("\" ");
            }
            if (Objects.nonNull(refSkill.getAbnormalVisualEffect())) {
                content.append("visual=\"").append(refSkill.getAbnormalVisualEffect().stream().map((Function<? super Object, ?>)Objects::toString).collect((Collector<? super Object, ?, String>)Collectors.joining(" "))).append("\" ");
            }
            if (refSkill.getSubordinationAbnormalType() != AbnormalType.NONE) {
                content.append("subordination=\"").append(refSkill.getSubordinationAbnormalType()).append("\" ");
            }
            if (refSkill.isAbnormalInstant()) {
                content.append("instant=\"true\"");
            }
            content.append(">\n");
            content.append((CharSequence)parseSkillAbnormalLevel(refSkill, maxLevel));
            content.append((CharSequence)parseSkillAbnormalTime(refSkill, maxLevel));
            content.append((CharSequence)parseSkillAbnormalChance(refSkill, maxLevel));
            content.append("\t\t</abnormal>\n");
        }
    }
    
    private static StringBuilder parseSkillAbnormalChance(final Skill baseSkill, final int maxLevel) {
        final String name = "chance";
        Objects.requireNonNull(baseSkill);
        return parseSkillMappedInt(name, baseSkill, maxLevel, baseSkill::getActivateRate, Skill::getActivateRate);
    }
    
    private static StringBuilder parseSkillAbnormalTime(final Skill baseSkill, final int maxLevel) {
        final String name = "time";
        Objects.requireNonNull(baseSkill);
        return parseSkillMappedInt(name, baseSkill, maxLevel, baseSkill::getAbnormalTime, Skill::getAbnormalTime);
    }
    
    private static StringBuilder parseSkillAbnormalLevel(final Skill baseSkill, final int maxLevel) {
        final String name = "level";
        Objects.requireNonNull(baseSkill);
        return parseSkillMappedInt(name, baseSkill, maxLevel, baseSkill::getAbnormalLvl, Skill::getAbnormalLvl);
    }
    
    private static void parseSkillTarget(final StringBuilder content, final Skill refSkill) {
        content.append("\t\t<target ");
        boolean hasFanRange = false;
        if (Objects.nonNull(refSkill)) {
            if (refSkill.getTargetType() != TargetType.SELF) {
                content.append("type=\"").append(refSkill.getTargetType()).append("\" ");
            }
            if (refSkill.affectScope != AffectScope.SINGLE) {
                content.append("scope=\"").append(refSkill.affectScope).append("\" ");
            }
            if (refSkill.getAffectObject() != AffectObject.ALL) {
                content.append("object=\"").append(refSkill.getAffectObject()).append("\" ");
            }
            if (refSkill.getAffectRange() > 0) {
                content.append("range=\"").append(refSkill.getAffectRange()).append("\" ");
            }
            if (refSkill.affectMin > 0 || refSkill.affectRandom > 0) {
                content.append("affect-min=\"").append(refSkill.affectMin).append("\" affect-random=\"").append(refSkill.affectRandom).append("\" ");
            }
            if (refSkill.getFanStartAngle() != 0 || refSkill.getFanRadius() != 0 || refSkill.getFanAngle() != 0) {
                hasFanRange = true;
                content.append(">\n\t\t\t<fan-range start-angle=\"").append(refSkill.getFanStartAngle()).append("\" radius=\"").append(refSkill.getFanRadius()).append("\" angle=\"").append(refSkill.getFanAngle()).append("\"/>\n");
            }
        }
        if (hasFanRange) {
            content.append("\t\t</target>\n");
        }
        else {
            content.append("/>\n");
        }
    }
    
    private static StringBuilder parseSkillAttributes(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel, final Skill refSkill) {
        final StringBuilder content = new StringBuilder();
        content.append("\t\t<attributes ");
        final String property = (String)SkillCheckerWithEffects.skillProperty.getOrDefault(baseSkill.magicType, (Object)"NONE");
        if (!property.equals("NONE")) {
            content.append("property=\"").append(property).append("\" ");
        }
        if (Objects.nonNull(refSkill)) {
            if (refSkill.isStaticReuse()) {
                content.append("static-reuse=\"true\" ");
            }
            if (refSkill.getNextAction() != NextActionType.NONE) {
                content.append("next-action=\"").append(refSkill.getNextAction()).append("\" ");
            }
            if (refSkill.getMagicCriticalRate() != 0.0) {
                content.append("magic-critical-rate=\"").append(refSkill.getMagicCriticalRate()).append("\" ");
            }
            if (refSkill.getTrait() != TraitType.NONE) {
                content.append("trait=\"").append(refSkill.getTrait()).append("\" ");
            }
            if (refSkill.isStayAfterDeath()) {
                content.append("stay-after-death=\"true\" ");
            }
            if (refSkill.getDisplayId() != baseSkill.id) {
                content.append("display-id=\"").append(refSkill.getDisplayId()).append("\" ");
            }
            if (refSkill.getHitCancelTime() > 0.0) {
                content.append("hit-cancel-time=\"").append(refSkill.getHitCancelTime()).append("\" ");
            }
            if (refSkill.getLevelBonusRate() > 0) {
                content.append("level-bonus-rate=\"").append(refSkill.getLevelBonusRate()).append("\" ");
            }
            if (refSkill.isRemovedOnAnyActionExceptMove()) {
                content.append("remove-on-action=\"true\" ");
            }
            if (refSkill.isRemovedOnDamage()) {
                content.append("remove-on-damage=\"true\" ");
            }
            if (refSkill.isBlockedInOlympiad()) {
                content.append("blocked-on-olympiad=\"true\" ");
            }
            if (refSkill.isSuicideAttack()) {
                content.append("suicide=\"true\" ");
            }
            if (refSkill.isTriggeredSkill()) {
                content.append("triggered=\"true\" ");
            }
            if (!refSkill.canBeDispelled()) {
                content.append("dispellable=\"false\" ");
            }
            if (refSkill.isExcludedFromCheck()) {
                content.append("check=\"false\" ");
            }
            if (refSkill.isWithoutAction()) {
                content.append("without-action=\"true\" ");
            }
            if (refSkill.canCastWhileDisabled()) {
                content.append("cast-disabled=\"true\"");
            }
            if (!refSkill.isSharedWithSummon()) {
                content.append("no-summon-shared=\"true\" ");
            }
            if (refSkill.isDeleteAbnormalOnLeave()) {
                content.append("remove-abnormal-on-leave=\"true\" ");
            }
            if (refSkill.isIrreplacableBuff()) {
                content.append("irreplacable=\"true\" ");
            }
            if (refSkill.isBlockActionUseSkill()) {
                content.append("block-action-skill=\"true\" ");
            }
        }
        if (SkillCheckerWithEffects.autoSkills.containsKey(baseSkill.id)) {
            content.append("auto-use=\"").append((String)SkillCheckerWithEffects.autoSkills.get(baseSkill.id)).append("\"");
        }
        content.append(">\n");
        if (Objects.nonNull(refSkill) && !refSkill.isPassive()) {
            content.append((CharSequence)parseSkillsMagicLevel(refSkill, maxLevel));
        }
        if (Objects.nonNull(refSkill) && !refSkill.isPassive()) {
            content.append((CharSequence)parseSkillCastRange(baseSkill, skillLevels, maxLevel));
        }
        if (Objects.nonNull(refSkill) && !refSkill.isPassive()) {
            content.append((CharSequence)parseSkillReuse(baseSkill, skillLevels, maxLevel));
        }
        if (Objects.nonNull(refSkill) && !refSkill.isPassive()) {
            content.append((CharSequence)parseSkillCoolTime(baseSkill, skillLevels, maxLevel));
        }
        if (Objects.nonNull(refSkill) && !refSkill.isPassive()) {
            content.append((CharSequence)parseSkillEffectPoint(baseSkill, skillLevels, maxLevel));
        }
        if (Objects.nonNull(refSkill) && !refSkill.isPassive()) {
            content.append((CharSequence)parseSkillEffectRange(refSkill, maxLevel));
        }
        if (Objects.nonNull(refSkill) && !refSkill.isPassive()) {
            content.append((CharSequence)parseSkillHitTime(baseSkill, skillLevels, maxLevel));
        }
        if (Objects.nonNull(refSkill) && !refSkill.isPassive() && (Objects.isNull(refSkill.getAbnormalType()) || refSkill.getAbnormalType() == AbnormalType.NONE)) {
            content.append((CharSequence)parseActivateRate(refSkill, maxLevel));
        }
        if (Objects.nonNull(refSkill) && refSkill.getAttributeType() != AttributeType.NONE) {
            content.append("\n\t\t\t<element type=\"").append(refSkill.getAttributeType()).append("\" value=\"").append(refSkill.getAttributeValue()).append("\"/>\n");
        }
        content.append("\t\t</attributes>\n");
        return content;
    }
    
    private static StringBuilder parseSkillReuse(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) {
        return parseSkillMappedInt("reuse", baseSkill, skillLevels, maxLevel, () -> baseSkill.reuseDelay, skill -> skill.reuseDelay);
    }
    
    private static StringBuilder parseActivateRate(final Skill refSkill, final int maxLevel) {
        final String name = "activate-rate";
        Objects.requireNonNull(refSkill);
        return parseSkillMappedInt(name, refSkill, maxLevel, refSkill::getActivateRate, Skill::getActivateRate);
    }
    
    private static StringBuilder parseSkillHitTime(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) {
        return parseSkillMappedInt("hit-time", baseSkill, skillLevels, maxLevel, () -> baseSkill.hitTime, skill -> skill.hitTime);
    }
    
    private static StringBuilder parseSkillEffectRange(final Skill baseSkill, final int maxLevel) {
        final String name = "effect-range";
        Objects.requireNonNull(baseSkill);
        return parseSkillMappedInt(name, baseSkill, maxLevel, baseSkill::getEffectRange, Skill::getEffectRange);
    }
    
    private static StringBuilder parseSkillEffectPoint(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) {
        return parseSkillMappedInt("effect-point", baseSkill, skillLevels, maxLevel, () -> baseSkill.effectPoint, skill -> skill.effectPoint);
    }
    
    private static StringBuilder parseSkillCoolTime(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) {
        return parseSkillMappedInt("cool-time", baseSkill, skillLevels, maxLevel, () -> baseSkill.coolTime, skill -> skill.coolTime);
    }
    
    private static StringBuilder parseSkillMappedInt(final String name, final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel, final IntSupplier initialSupplier, final ToIntFunction<SkillModel> valueFunction) {
        final StringBuilder content = new StringBuilder();
        if (initialSupplier.getAsInt() == 0 && skillLevels.values().stream().noneMatch(s -> valueFunction.applyAsInt(s) != initialSupplier.getAsInt())) {
            return content;
        }
        content.append("\t\t\t<").append(name).append(" initial=\"").append(initialSupplier.getAsInt()).append("\"");
        if (skillLevels.values().stream().anyMatch(s -> valueFunction.applyAsInt(s) != initialSupplier.getAsInt())) {
            int lastValue = initialSupplier.getAsInt();
            content.append(">\n");
            for (int i = 2; i <= maxLevel; ++i) {
                final SkillModel refSkill = (SkillModel)skillLevels.get(i);
                if (lastValue != valueFunction.applyAsInt(refSkill)) {
                    lastValue = valueFunction.applyAsInt(refSkill);
                    content.append("\t\t\t\t<value level=\"").append(i).append("\">").append(lastValue).append("</value>\n");
                }
            }
            content.append("\t\t\t</").append(name).append(">\n");
        }
        else {
            content.append("/>\n");
        }
        return content;
    }
    
    private static StringBuilder parseSkillCastRange(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) {
        return parseSkillMappedInt("cast-range", baseSkill, skillLevels, maxLevel, () -> baseSkill.castRange, skill -> skill.castRange);
    }
    
    private static StringBuilder parseSkillHpConsume(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) {
        return parseSkillMappedInt("hp", baseSkill, skillLevels, maxLevel, () -> baseSkill.hpConsume, skill -> skill.hpConsume);
    }
    
    private static StringBuilder parseSkillManaConsume(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) {
        return parseSkillMappedInt("mana", baseSkill, skillLevels, maxLevel, () -> baseSkill.manaConsume, skill -> skill.manaConsume);
    }
    
    private static StringBuilder parseSkillsManaInitConsume(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) {
        return parseSkillMappedInt("mana-init", baseSkill, skillLevels, maxLevel, () -> baseSkill.manaInitial, skill -> skill.manaInitial);
    }
    
    private static StringBuilder parseSkillsMagicLevel(final Skill baseSkill, final int maxLevel) {
        final String name = "magic-level";
        Objects.requireNonNull(baseSkill);
        return parseSkillMappedInt(name, baseSkill, maxLevel, baseSkill::getMagicLevel, Skill::getMagicLevel);
    }
    
    private static StringBuilder parseSkillsIcons(final SkillModel baseSkill, final IntMap<SkillModel> skillLevels, final int maxLevel) {
        final StringBuilder content = new StringBuilder();
        content.append("\t\t<icon initial=\"").append(baseSkill.icon).append("\"");
        if (skillLevels.values().stream().anyMatch(s -> !s.icon.equals(baseSkill.icon))) {
            String baseIcon = baseSkill.icon;
            content.append(">\n");
            for (int i = 2; i <= maxLevel; ++i) {
                final SkillModel refSkill = (SkillModel)skillLevels.get(i);
                if (!baseIcon.equals(refSkill.icon)) {
                    content.append("\t\t\t<value level=\"").append(i).append("\">").append(refSkill.icon).append("</value>\n");
                    baseIcon = refSkill.icon;
                }
            }
            content.append("\t\t</icon>\n");
        }
        else {
            content.append("/>\n");
        }
        return content;
    }
    
    private static StringBuilder parseSkillDescriptions(final IntMap<SkillModel> skillLevels) {
        final StringBuilder content = new StringBuilder();
        final SkillModel baseSkill = (SkillModel)skillLevels.get(1);
        if (skillLevels.values().stream().anyMatch(s -> !s.desc.equals(baseSkill.desc))) {
            content.append("\t<!-- \n");
            final int maxLevel = skillLevels.keySet().stream().max().orElse(0);
            String baseDesc = "";
            for (int i = 1; i <= maxLevel; ++i) {
                final SkillModel refSkill = (SkillModel)skillLevels.get(i);
                if (!refSkill.desc.equals(baseDesc)) {
                    content.append("\t\t").append("level ").append(i).append(": ").append(refSkill.desc).append("\n");
                    baseDesc = refSkill.desc;
                }
            }
            content.append("\t -->\n");
        }
        else {
            content.append("\t<!-- ").append(baseSkill.desc).append(" -->\n");
        }
        return content;
    }
    
    private static void fillSkillConditions() throws IOException {
        final BufferedReader reader = Files.newBufferedReader(Path.of("res/skills_conditions.txt", new String[0]));
        String line;
        while (Objects.nonNull(line = reader.readLine())) {
            final Matcher matcher = SkillCheckerWithEffects.SKILL_CONDITIONS_PATTERN.matcher(line);
            if (matcher.matches()) {
                final int id = Integer.parseInt(matcher.group(1));
                final int level = Integer.parseInt(matcher.group(2));
                final SkillModel skill = (SkillModel)((IntMap)SkillCheckerWithEffects.skills.getOrDefault(id, (Object)Containers.emptyIntMap())).get(level);
                if (Objects.isNull(skill)) {
                    SkillCheckerWithEffects.logger.warn("skill name not found {}:{}", (Object)id, (Object)level);
                }
                else {
                    skill.equipType = Integer.parseInt(matcher.group(3));
                    skill.attackItemTypes = parseWeaponTypes(matcher.group(4));
                    skill.statType = Integer.parseInt(matcher.group(5));
                    skill.statPercent = Integer.parseInt(matcher.group(6));
                    skill.statUP = matcher.group(7).equals("1");
                    skill.manaInitial = Integer.parseInt(matcher.group(8));
                    skill.itemId = Integer.parseInt(matcher.group(9));
                    skill.itemCount = Integer.parseInt(matcher.group(10));
                }
            }
        }
    }
    
    private static EnumSet<WeaponType> parseWeaponTypes(final String weapons) {
        final EnumSet<WeaponType> weaponsType = EnumSet.noneOf(WeaponType.class);
        if (Util.isNotEmpty(weapons)) {
            for (final String weapon : weapons.split(";")) {
                weaponsType.add(weaponTypeByOrdinal(Integer.parseInt(weapon)));
            }
        }
        return weaponsType;
    }
    
    private static WeaponType weaponTypeByOrdinal(final int ordinal) {
        return SkillCheckerWithEffects.WEAPON_TYPES[ordinal];
    }
    
    private static void fillSkillData() throws IOException {
        final BufferedReader reader = Files.newBufferedReader(Path.of("res/skills.txt", new String[0]));
        String line;
        while (Objects.nonNull(line = reader.readLine())) {
            Matcher matcher = SkillCheckerWithEffects.SKILL_PATTERN.matcher(line);
            if (matcher.matches()) {
                final int id = Integer.parseInt(matcher.group(1));
                final int level = Integer.parseInt(matcher.group(2));
                final int operateType = Integer.parseInt(matcher.group(3));
                final int magicType = Integer.parseInt(matcher.group(4));
                final int mpConsume = Integer.parseInt(matcher.group(5));
                final int castRange = Integer.parseInt(matcher.group(6));
                final int hitTime = matcher.group(7).contains(".") ? ((int)(Float.parseFloat(matcher.group(7)) * 1000.0f)) : Integer.parseInt(matcher.group(7));
                final int coolTime = (int)(Float.parseFloat(matcher.group(8)) * 1000.0f);
                final int reuseDelay = (int)(Float.parseFloat(matcher.group(9)) * 1000.0f);
                final int effectPoint = Integer.parseInt(matcher.group(10));
                final int isMagic = Integer.parseInt(matcher.group(11));
                final String icon = matcher.group(12);
                final boolean debuff = matcher.group(13).equals("1");
                final int hpConsume = Integer.parseInt(matcher.group(14));
                final SkillModel skill = (SkillModel)((IntMap)SkillCheckerWithEffects.skills.getOrDefault(id, (Object)Containers.emptyIntMap())).get(level);
                if (Objects.isNull(skill)) {
                    SkillCheckerWithEffects.logger.warn("skill name not found {}:{}", (Object)id, (Object)level);
                }
                else {
                    skill.operateType = operateType;
                    skill.magicType = magicType;
                    skill.manaConsume = mpConsume;
                    skill.castRange = castRange;
                    skill.hitTime = hitTime;
                    skill.coolTime = coolTime;
                    skill.reuseDelay = reuseDelay;
                    skill.effectPoint = effectPoint;
                    skill.isMagic = isMagic;
                    skill.icon = icon;
                    skill.debuff = debuff;
                    skill.hpConsume = hpConsume;
                }
            }
            else {
                if (!(matcher = SkillCheckerWithEffects.AUTO_SKILL_PATTERN.matcher(line)).matches()) {
                    continue;
                }
                SkillCheckerWithEffects.autoSkills.put(Integer.parseInt(matcher.group(1)), (Object)(matcher.group(2).equals("1") ? "BUFF" : (matcher.group(2).equals("2") ? "ACTIVE" : "TRANSFORM")));
            }
        }
    }
    
    private static void fillSkillName() throws IOException {
        final BufferedReader reader = Files.newBufferedReader(Path.of("res/skills_name.txt", new String[0]));
        String descTemplate = "";
        int lastId = -1;
        String line;
        while (Objects.nonNull(line = reader.readLine())) {
            final Matcher matcher = SkillCheckerWithEffects.SKILL_NAME_PATTERN.matcher(line);
            if (matcher.matches()) {
                final int id = Integer.parseInt(matcher.group(1));
                if (id != lastId) {
                    descTemplate = "";
                    lastId = id;
                }
                final int level = Integer.parseInt(matcher.group(2));
                final String name = matcher.group(3).trim().replace("<", "(").replace(">", ")").replace("&", "&amp;");
                String desc = "";
                if (Util.isNotEmpty(matcher.group(4).trim())) {
                    descTemplate = matcher.group(4).trim();
                }
                if (Util.isNotEmpty(descTemplate)) {
                    if (Util.isNotEmpty(matcher.group(5).trim())) {
                        desc = parseDesc(descTemplate, matcher.group(5).trim());
                    }
                    else {
                        desc = descTemplate;
                    }
                }
                final String des = desc;
                ((IntMap)SkillCheckerWithEffects.skills.computeIfAbsent(id, i -> new HashIntMap())).computeIfAbsent(level, l -> new SkillModel(id, level, name, des));
            }
        }
    }
    
    private static String parseDesc(String descTemplate, final String params) {
        final String[] data = params.split(";");
        for (int i = 0; i < data.length; ++i) {
            descTemplate = descTemplate.replace(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i + 1), (CharSequence)data[i]);
        }
        return descTemplate;
    }
    
    static {
        SkillCheckerWithEffects.logger = LoggerFactory.getLogger((Class)SkillCheckerWithEffects.class);
        SkillCheckerWithEffects.autoSkills = (IntMap<String>)new HashIntMap();
        SkillCheckerWithEffects.skills = (IntMap<IntMap<SkillModel>>)new HashIntMap();
        SkillCheckerWithEffects.skillTypes = (IntMap<String>)new HashIntMap(4);
        SkillCheckerWithEffects.skillProperty = (IntMap<String>)new HashIntMap(3);
        SkillCheckerWithEffects.skillTypes.put(0, (Object)"PHYSIC");
        SkillCheckerWithEffects.skillTypes.put(1, (Object)"MAGIC");
        SkillCheckerWithEffects.skillTypes.put(2, (Object)"STATIC");
        SkillCheckerWithEffects.skillTypes.put(3, (Object)"DANCE");
        SkillCheckerWithEffects.skillProperty.put(0, (Object)"NONE");
        SkillCheckerWithEffects.skillProperty.put(1, (Object)"PHYSIC");
        SkillCheckerWithEffects.skillProperty.put(2, (Object)"MAGIC");
        SkillCheckerWithEffects.step = 100;
        SKILL_CONDITIONS_PATTERN = Pattern.compile("^.*skill_id=(\\d+)\\tskill_level=(\\d+)\\t.*?\\tequiptype=(\\d+)\\tattackitemtype=\\{(.*?)}\\tstattype=(\\d+)\\tstatpercentage=(\\d+)\\tup=(\\d+).*?\\tmpconsume1=(\\d+).*\\titemid=(\\d+)\\titemnum=(\\d+).*");
        WEAPON_TYPES = WeaponType.values();
        SKILL_PATTERN = Pattern.compile("^.*skill_id=(\\d+)\\tskill_level=(\\d+).*?\\toperate_type=(\\d+).*?\\tMagicType=(\\d+)\\tmp_consume=(\\d+)\\tcast_range=(-?\\d+).*?\\thit_time=(.*?)\\tcool_time=(.*?)\\treuse_delay=(.*?)\\teffect_point=(.*?)\\tis_magic=(\\d+).*?\\ticon=\\[(.*?)].*?\\tdebuff=(.*?)\\t.*?\\thp_consume=(\\d+)\\t.*");
        AUTO_SKILL_PATTERN = Pattern.compile("^.*Item_id=(\\d+)\\tIs_Use=(\\d+)\\t.*");
        SKILL_NAME_PATTERN = Pattern.compile("^.*skill_id=(\\d+)\\tskill_level=(\\d+).*?\\tname=\\[(.*?)]\\tdesc=\\[(.*?)]\\tdesc_param=\\[(.*?)].*");
    }
    
    static class SkillEffectReader extends GameXmlReader
    {
        private final Transformer transformer;
        IntMap<Node> effectsNode;
        IntMap<Node> conditionsNode;
        Map<String, Node> effects;
        Map<String, List<String>> effectsBaseNames;
        private static final List<String> ignoredAttrs;
        
        SkillEffectReader() throws TransformerConfigurationException {
            this.effectsNode = (IntMap<Node>)new HashIntMap();
            this.conditionsNode = (IntMap<Node>)new HashIntMap();
            this.effects = new HashMap<String, Node>();
            this.effectsBaseNames = new HashMap<String, List<String>>();
            (this.transformer = TransformerFactory.newInstance().newTransformer()).setOutputProperty("omit-xml-declaration", "yes");
            this.transformer.setOutputProperty("indent", "yes");
            this.load();
        }
        
        protected Path getSchemaFilePath() {
            return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/skills/skills.xsd");
        }
        
        public void load() {
            this.parseDatapackDirectory("data/skills/", true);
        }
        
        protected void parseDocument(final Document doc, final File f) {
            final int id;
            Node node;
            this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "skill", skillNode -> {
                id = this.parseInt(skillNode.getAttributes(), "id");
                for (node = skillNode.getFirstChild(); Objects.nonNull(node); node = node.getNextSibling()) {
                    if (node.getNodeName().equalsIgnoreCase("effects")) {
                        this.effectsNode.put(id, (Object)node);
                    }
                    else if (node.getNodeName().equalsIgnoreCase("conditions")) {
                        this.conditionsNode.put(id, (Object)node);
                    }
                }
            }));
        }
        
        StringBuilder toNewEffectsArch(final SkillModel skill, final int maxLevel) {
            final Node node = (Node)this.effectsNode.get(skill.id);
            final StringBuilder effectBuilder = new StringBuilder();
            if (Objects.isNull(node)) {
                return effectBuilder;
            }
            for (Node effectNode = node.getFirstChild(); Objects.nonNull(effectNode); effectNode = effectNode.getNextSibling()) {
                effectBuilder.append((CharSequence)this.proccessToNewArch(effectNode, maxLevel));
            }
            return effectBuilder;
        }
        
        private StringBuilder proccessToNewArch(final Node effectNode, final int maxLevel) {
            final boolean hasLeveledInfo = this.hasLeveledinfo(effectNode);
            if (hasLeveledInfo) {
                return this.processLeveledEffect(effectNode, maxLevel);
            }
            final String effectName = this.extractEffectName(effectNode);
            final Node newEffect = effectNode.cloneNode(true);
            ((Element)newEffect).setAttribute("ref-id", effectName);
            this.effects.putIfAbsent(effectName, newEffect);
            this.effectsBaseNames.computeIfAbsent(newEffect.getNodeName(), k -> new ArrayList()).add(effectName);
            return new StringBuilder("<effect reference=\"").append(effectName).append("\"/>\n");
        }
        
        private StringBuilder processLeveledEffect(final Node effectNode, final int maxLevel) {
            final int startLevel = this.parseInt(effectNode.getAttributes(), "start-level");
            final int stopLevel = this.parseInt(effectNode.getAttributes(), "stop-level", maxLevel);
            final StringBuilder createdEffects = new StringBuilder();
            final Map<String, Node> lastUsedNodes = new HashMap<String, Node>();
            for (int i = startLevel; i < stopLevel; ++i) {
                final Node newEffect = effectNode.cloneNode(false);
                for (Node child = effectNode.getFirstChild(); Objects.nonNull(child); child = child.getNextSibling()) {
                    if (this.isLeveledNode(child)) {
                        Node newNode;
                        if (i == startLevel) {
                            newNode = child.cloneNode(false);
                        }
                        else {
                            newNode = this.leveledToInitial(child, i);
                            if (Objects.isNull(newNode)) {
                                newNode = lastUsedNodes.get(child.getNodeName());
                            }
                        }
                        if (Objects.nonNull(newNode)) {
                            lastUsedNodes.put(newNode.getNodeName(), newNode);
                            newEffect.appendChild(newNode);
                        }
                    }
                    else {
                        newEffect.appendChild(child);
                    }
                }
                final String effectName = this.extractEffectName(newEffect);
                ((Element)newEffect).setAttribute("ref-id", effectName);
                this.effects.putIfAbsent(effectName, newEffect);
                this.effectsBaseNames.computeIfAbsent(newEffect.getNodeName(), k -> new ArrayList()).add(effectName);
                createdEffects.append("<effect reference=\"").append(effectName).append("\"/>\n");
            }
            return createdEffects;
        }
        
        private Node leveledToInitial(final Node leveledNode, final int level) {
            for (Node value = leveledNode.getFirstChild(); Objects.nonNull(value); value = value.getNextSibling()) {
                if (this.parseInt(value.getAttributes(), "level") == level) {
                    final Node newNode = leveledNode.cloneNode(false);
                    newNode.getAttributes().getNamedItem("initial").setNodeValue(value.getTextContent());
                    return newNode;
                }
            }
            return null;
        }
        
        private boolean isLeveledNode(final Node child) {
            return Objects.nonNull(child.getAttributes().getNamedItem("initial")) || Util.falseIfNullOrElse((Object)child.getFirstChild(), c -> c.getNodeName().equals("value"));
        }
        
        private boolean hasLeveledinfo(final Node effectNode) {
            for (Node child = effectNode.getFirstChild(); Objects.nonNull(child); child = child.getNextSibling()) {
                if (this.isLeveledNode(child)) {
                    return true;
                }
            }
            return false;
        }
        
        private String extractEffectName(final Node effectNode) {
            final StringBuilder name = new StringBuilder(effectNode.getNodeName().replace("-", "_").toUpperCase());
            final NamedNodeMap attrs = effectNode.getAttributes();
            for (int i = 0; i < attrs.getLength(); ++i) {
                final Node attrNode = attrs.item(i);
                if (!SkillEffectReader.ignoredAttrs.contains(attrNode.getNodeName())) {
                    name.append("_").append(attrNode.getNodeName().replace("-", "_").toUpperCase());
                    name.append("_").append(attrNode.getNodeValue());
                }
            }
            for (Node child = effectNode.getFirstChild(); Objects.nonNull(child); child = child.getNextSibling()) {
                name.append("_").append(child.getNodeName().replace("-", "_").toUpperCase());
                if (this.isLeveledNode(child)) {
                    name.append("_").append(this.parseDouble(child.getAttributes(), "initial"));
                }
                else {
                    name.append("_").append(child.getTextContent().toUpperCase());
                }
            }
            return name.toString();
        }
        
        String toEffectsXmlString(final int id) throws TransformerException {
            final StringWriter writer = new StringWriter();
            final Node node = (Node)this.effectsNode.get(id);
            if (Objects.isNull(node)) {
                return "";
            }
            this.transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString().replace(" xmlns=\"http://l2j.org\"", "");
        }
        
        String toConditionsXmlString(final int id) throws TransformerException {
            final StringWriter writer = new StringWriter();
            final Node node = (Node)this.conditionsNode.get(id);
            if (Objects.isNull(node)) {
                return "";
            }
            this.transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString().replace(" xmlns=\"http://l2j.org\"", "");
        }
        
        static {
            ignoredAttrs = List.of("ref-id", "ticks", "scope", "start-level", "stop-level");
        }
    }
    
    public static class SkillModel
    {
        public boolean statUP;
        public int manaInitial;
        public int itemId;
        public int itemCount;
        int statPercent;
        int statType;
        EnumSet<WeaponType> attackItemTypes;
        int equipType;
        int id;
        int level;
        int operateType;
        int magicType;
        int isMagic;
        String icon;
        boolean debuff;
        int reuseDelay;
        String name;
        String desc;
        int manaConsume;
        int hpConsume;
        int castRange;
        int coolTime;
        int effectPoint;
        int hitTime;
        
        public SkillModel(final int id, final int level, final String name, final String desc) {
            this.attackItemTypes = EnumSet.noneOf(WeaponType.class);
            this.id = id;
            this.level = level;
            this.name = name;
            this.desc = desc;
        }
        
        public String getDesc() {
            return this.desc;
        }
    }
    
    interface EffectNameExtractor
    {
        String extract(final Node effectNode);
        
        String handledEffect();
    }
}
