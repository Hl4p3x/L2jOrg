// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver;

import java.util.HashMap;
import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.HashIntMap;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import org.l2j.gameserver.model.conditions.ConditionTargetLevelRange;
import org.l2j.gameserver.model.conditions.ConditionLogicNot;
import java.util.Arrays;
import org.l2j.gameserver.model.conditions.ConditionLogicAnd;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.IntFunction;
import org.l2j.gameserver.model.conditions.ConditionPlayerInsideZoneId;
import org.l2j.gameserver.model.conditions.ConditionPlayerFlyMounted;
import org.l2j.gameserver.model.conditions.ConditionPlayerSex;
import org.l2j.gameserver.model.conditions.ConditionPlayerHasCastle;
import org.l2j.gameserver.model.conditions.ConditionPlayerPledgeClass;
import org.l2j.gameserver.model.conditions.ConditionPlayerIsHero;
import org.l2j.gameserver.model.conditions.ConditionPlayerState;
import org.l2j.gameserver.model.conditions.ConditionPlayerMinLevel;
import org.l2j.gameserver.model.conditions.Condition;
import java.util.Comparator;
import org.l2j.gameserver.model.stats.functions.FuncTemplate;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.model.ExtractableProduct;
import java.util.Iterator;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import java.util.List;
import org.l2j.gameserver.model.item.type.ActionType;
import org.l2j.gameserver.model.commission.CommissionItemType;
import java.util.Collection;
import org.l2j.commons.util.Util;
import java.io.IOException;
import java.io.BufferedWriter;
import org.l2j.gameserver.model.item.ItemTemplate;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.model.item.Armor;
import org.l2j.gameserver.model.item.Weapon;
import java.util.Objects;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Path;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import java.util.regex.Pattern;
import org.l2j.gameserver.engine.item.ItemEngine;
import java.util.Map;
import io.github.joealisson.primitive.IntIntMap;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public class ItemChecker
{
    static Logger LOGGER;
    static IntMap<Item> items;
    static IntIntMap autoUse;
    static Map<String, String> bodyParts;
    static Map<String, String> armorTypes;
    static Map<String, String> crystalTypes;
    static Map<String, String> itemTypes;
    static Map<String, String> weaponTypes;
    static IntMap<String> autoUseTypes;
    static Item processingItem;
    private static ItemEngine itemEngine;
    static int step;
    static Pattern itemNamePattern;
    static Pattern autoUsePattern;
    static Pattern weaponPattern;
    static Pattern itemStatPattern;
    static Pattern etcItemPattern;
    static Pattern itemInfoPattern;
    static Pattern armorPattern;
    
    public static void test() {
        try {
            SkillEngine.init();
            ItemEngine.init();
            ItemChecker.itemEngine = ItemEngine.getInstance();
            fillName();
            fillPrice();
            fillStat();
            fillItem();
            fillArmor();
            fillWeapon();
            Files.createDirectories(Path.of("new-items", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
            for (int start = 0, end = start + ItemChecker.step, max = ItemChecker.items.keySet().stream().max().orElse(0); start < max; start += ItemChecker.step, end += ItemChecker.step) {
                processFile(start, end);
            }
            ItemChecker.LOGGER.info("Parsed");
        }
        catch (Exception | StackOverflowError ex) {
            final Throwable t;
            final Throwable e = t;
            ItemChecker.LOGGER.error("ERROR PROCESSING ITEM {}", (Object)ItemChecker.processingItem);
            ItemChecker.LOGGER.error(e.getMessage(), e);
        }
    }
    
    private static void processFile(final int start, final int end) throws IOException {
        final String file = String.format("%05d-%05d.xml", start, end - 1);
        final StringBuilder content = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<list xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://l2j.org\" xsi:schemaLocation=\"http://l2j.org items.xsd\">\n");
        boolean isNotEmpty = false;
        for (int i = start; i < end; ++i) {
            final Item item = (Item)ItemChecker.items.get(i);
            if (!Objects.isNull(item)) {
                ItemChecker.processingItem = item;
                final ItemTemplate template = ItemChecker.itemEngine.getTemplate(item.id);
                if (Objects.nonNull(template)) {
                    if (template instanceof Weapon) {
                        processWeapon(item, content);
                    }
                    else if (template instanceof Armor) {
                        processArmor(item, content);
                    }
                    else if (template instanceof EtcItem) {
                        processItem(item, content);
                    }
                    else {
                        ItemChecker.LOGGER.error("Item com tipo n\u00e3o encontrado {} with template {}", (Object)item, (Object)template);
                    }
                }
                else {
                    switch (item.itemProcessType) {
                        case NONE: {
                            ItemChecker.LOGGER.error("Item com tipo n\u00e3o encontrado {}", (Object)item);
                            break;
                        }
                        case ETC: {
                            processItem(item, content);
                            break;
                        }
                        case ARMOR: {
                            processArmor(item, content);
                            break;
                        }
                        case WEAPON: {
                            processWeapon(item, content);
                            break;
                        }
                    }
                }
                isNotEmpty = true;
            }
        }
        if (isNotEmpty) {
            final BufferedWriter writer = Files.newBufferedWriter(Path.of("new-items/", file), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            content.append("</list>");
            writer.write(content.toString());
            writer.flush();
            writer.close();
        }
    }
    
    private static void processItem(final Item item, final StringBuilder content) {
        final EtcItem template = (EtcItem)ItemChecker.itemEngine.getTemplate(item.id);
        if (Objects.nonNull(template)) {
            if (!item.name.equalsIgnoreCase(template.getName())) {
                ItemChecker.LOGGER.info("Changing name of Item {} to {}", (Object)template, (Object)item.name);
            }
            if (!item.type.equalsIgnoreCase(template.getItemType().name())) {
                ItemChecker.LOGGER.info("Changing type of Item {} ({}) to {}", new Object[] { template, template.getItemType(), item.type });
            }
        }
        content.append(String.format("\t<item id=\"%d\" name=\"%s\" type=\"%s\" icon=\"%s\">\n", item.id, item.name, item.type, item.icon));
        if (Util.isNotEmpty(item.description)) {
            content.append("\t<!-- ").append(item.description.replace("--", "-")).append(" -->\n");
        }
        content.append("\t\t<restriction");
        if (Objects.nonNull(template) && template.isOlyRestrictedItem()) {
            content.append(" olympiad-restricted=\"true\"");
        }
        if (item.stackable) {
            content.append(" stackable=\"true\"");
        }
        if (!item.destroyable) {
            content.append(" destroyable=\"false\"");
        }
        if (!item.tradable) {
            content.append(" tradable=\"false\"");
        }
        if (!item.dropable) {
            content.append(" dropable=\"false\"");
        }
        if (!item.sellable) {
            content.append(" sellable=\"false\"");
        }
        if (!item.privateSellable) {
            content.append(" private-sellable=\"false\"");
        }
        if ((item.keepType & 0x1) == 0x0) {
            content.append(" depositable=\"false\"");
        }
        if ((item.keepType & 0x2) == 0x0) {
            content.append(" clan-depositable=\"false\"");
        }
        if ((item.keepType & 0x4) == 0x0) {
            content.append(" castle-depositable=\"false\"");
        }
        if ((item.keepType & 0x4) == 0x0) {
            content.append(" freightable=\"false\"");
        }
        content.append("/>\n");
        if (Objects.nonNull(template) && !Util.isNullOrEmpty((Collection)template.getConditions())) {
            content.append("\t\t<condition").append(messageFromConditions(template.getConditions())).append(">\n");
            parseConditions(template.getConditions(), content);
            content.append("\t\t</condition>\n");
        }
        content.append("\t\t<attributes");
        if (item.weight > 0) {
            content.append(" weight=\"").append(item.weight).append("\"");
        }
        if (item.price > 0L) {
            content.append(" price=\"").append(item.price).append("\"");
        }
        if (Objects.nonNull(template)) {
            if (template.getCommissionItemType() != CommissionItemType.OTHER_ITEM) {
                content.append(String.format(" commission-type=\"%s\"", template.getCommissionItemType()));
            }
            if (template.getReuseDelay() > 0) {
                content.append(String.format(" reuse-delay=\"%d\"", template.getReuseDelay()));
            }
            if (template.getSharedReuseGroup() > 0) {
                content.append(String.format(" reuse-group=\"%d\"", template.getSharedReuseGroup()));
            }
            if (template.getTime() > 0L) {
                content.append(String.format(" duration=\"%d\"", template.getTime()));
            }
            if (template.isForNpc()) {
                content.append(" for-npc=\"true\"");
            }
            if (template.hasImmediateEffect()) {
                content.append(" immediate-effect=\"true\"");
            }
            if (template.hasExImmediateEffect()) {
                content.append(" ex-immediate-effect=\"true\"");
            }
        }
        if (item.isQuest) {
            content.append(" quest-item=\"true\"");
        }
        if (Objects.nonNull(template)) {
            if (template.isInfinite()) {
                content.append(" infinite=\"true\"");
            }
            if (template.isSelfResurrection()) {
                content.append(" self-resurrection=\"true\"");
            }
            if (template.getDefaultAction() != ActionType.NONE) {
                content.append(" action=\"").append(template.getDefaultAction()).append("\"");
            }
        }
        if (ItemChecker.autoUse.containsKey(item.id)) {
            content.append(" auto-use=\"").append((String)ItemChecker.autoUseTypes.getOrDefault(ItemChecker.autoUse.get(item.id), (Object)"NONE")).append("\"");
        }
        content.append("/>\n");
        if (item.type.equals("ARROW") || item.type.equals("BOLT")) {
            content.append("\t\t<crystal ").append("type=\"").append(item.grade).append("\"/>\n");
        }
        if (Objects.nonNull(template) && Objects.nonNull(template.getHandlerName())) {
            final String handlerName = template.getHandlerName();
            switch (handlerName) {
                case "ExtractableItems": {
                    parseExtractable(template, content);
                    break;
                }
                case "Elixir":
                case "SummonItems":
                case "ItemSkills":
                case "SoulShots":
                case "SpiritShot":
                case "BlessedSoulShots":
                case "BlessedSpiritShot":
                case "BeastSpiritShot":
                case "BeastSoulShot":
                case "FishShots": {
                    parseSkillReducer(template, content);
                    break;
                }
                default: {
                    content.append("\t\t<action handler=\"").append(template.getHandlerName()).append("\"/>\n");
                    break;
                }
            }
        }
        content.append("\t</item>\n\n");
    }
    
    private static void parseSkillReducer(final EtcItem template, final StringBuilder content) {
        content.append("\t\t<skill-reducer type=\"").append(template.getHandlerName()).append("\">\n");
        parseSkills(content, template.getAllSkills());
        content.append("\t\t</skill-reducer>\n");
    }
    
    private static void parseSkills(final StringBuilder content, final List<ItemSkillHolder> allSkills) {
        for (final ItemSkillHolder skill : allSkills) {
            content.append("\t\t\t<skill id=\"").append(skill.getSkillId()).append("\" level=\"").append(skill.getLevel()).append("\"");
            if (skill.getType() != ItemSkillType.NORMAL) {
                content.append(" type=\"").append(skill.getType()).append("\"");
            }
            if (skill.getChance() != 100) {
                content.append(" chance=\"").append(skill.getChance()).append("\"");
            }
            if (skill.getValue() != 0) {
                content.append(" value=\"").append(skill.getValue()).append("\"");
            }
            content.append("/>");
            if (Objects.nonNull(skill.getSkill())) {
                content.append("  <!-- ").append(skill.getSkill().getName()).append(" -->\n");
            }
            else {
                content.append("  <!-- TODO Skill not found -->\n");
            }
        }
    }
    
    private static void parseExtractable(final EtcItem template, final StringBuilder content) {
        content.append("\t\t<extract ");
        if (template.getExtractableCountMax() > 0) {
            content.append(" max=\"").append(template.getExtractableCountMax()).append("\"");
        }
        content.append(">\n");
        for (final ExtractableProduct item : template.getExtractableItems()) {
            content.append("\t\t\t<item id=\"").append(item.getId()).append("\"");
            if (item.getMin() > 1) {
                content.append(" min-count=\"").append(item.getMin()).append("\"");
            }
            if (item.getMax() > 1) {
                content.append(" max-count=\"").append(item.getMax()).append("\"");
            }
            if (item.getChance() != 100) {
                content.append(" chance=\"").append(item.getChance()).append("\"");
            }
            if (item.getMinEnchant() > 0) {
                content.append(" min-enchant=\"").append(item.getMinEnchant()).append("\"");
            }
            if (item.getMaxEnchant() > 0) {
                content.append(" max-enchant=\"").append(item.getMaxEnchant()).append("\"");
            }
            content.append("/>  <!-- ").append(((Item)ItemChecker.items.get(item.getId())).name).append(" -->\n");
        }
        content.append("\t\t</extract>\n");
    }
    
    private static void processArmor(final Item item, final StringBuilder content) {
        final Armor template = (Armor)ItemChecker.itemEngine.getTemplate(item.id);
        if (Objects.nonNull(template)) {
            if (!item.name.equalsIgnoreCase(template.getName())) {
                ItemChecker.LOGGER.info("Changing name of Armor {} to {}", (Object)template, (Object)item.name);
            }
            if (!item.type.equalsIgnoreCase(template.getItemType().name())) {
                if (item.type.equalsIgnoreCase("NONE") && template.getItemType().equals(ArmorType.SHIELD)) {
                    item.type = ArmorType.SHIELD.name();
                }
                else {
                    ItemChecker.LOGGER.info("Changing type of Item {} ({}) to {}", new Object[] { template, template.getItemType(), item.type });
                }
            }
        }
        content.append(String.format("\t<armor id=\"%d\" name=\"%s\" type=\"%s\" body-part=\"%s\" icon=\"%s\">\n", item.id, item.name, item.type, item.bodyPart, item.icon));
        if (Util.isNotEmpty(item.description)) {
            content.append("\t<!-- ").append(item.description).append(" -->\n");
        }
        content.append("\t\t<restriction");
        if (Objects.nonNull(template)) {
            if (!template.isFreightable()) {
                content.append(" freightable=\"false\"");
            }
            if (template.isOlyRestrictedItem()) {
                content.append(" olympiad-restricted=\"true\"");
            }
        }
        if (item.stackable) {
            content.append(" stackable=\"true\"");
        }
        if (!item.destroyable) {
            content.append(" destroyable=\"false\"");
        }
        if (!item.tradable) {
            content.append(" tradable=\"false\"");
        }
        if (!item.dropable) {
            content.append(" dropable=\"false\"");
        }
        if (!item.sellable) {
            content.append(" sellable=\"false\"");
        }
        if (!item.privateSellable) {
            content.append(" private-sellable=\"false\"");
        }
        content.append("/>\n");
        if (Objects.nonNull(template) && !Util.isNullOrEmpty((Collection)template.getConditions())) {
            content.append("\t\t<condition").append(messageFromConditions(template.getConditions())).append(">\n");
            parseConditions(template.getConditions(), content);
            content.append("\t\t</condition>\n");
        }
        content.append("\t\t<attributes");
        if (item.weight > 0) {
            content.append(" weight=\"").append(item.weight).append("\"");
        }
        if (item.price > 0L) {
            content.append(" price=\"").append(item.price).append("\"");
        }
        if (Objects.nonNull(template)) {
            if (template.getCommissionItemType() != CommissionItemType.OTHER_ITEM) {
                content.append(String.format(" commission-type=\"%s\"", template.getCommissionItemType()));
            }
            if (template.getReuseDelay() > 0) {
                content.append(String.format(" reuse-delay=\"%d\"", template.getReuseDelay()));
            }
            if (template.getSharedReuseGroup() > 0) {
                content.append(String.format(" reuse-group=\"%d\"", template.getSharedReuseGroup()));
            }
            if (template.getTime() > 0L) {
                content.append(String.format(" duration=\"%d\"", template.getTime()));
            }
            if (template.isForNpc()) {
                content.append(" for-npc=\"true\"");
            }
            if (!template.isEnchantable()) {
                content.append(" enchant-enabled=\"false\"");
            }
            if (template.getEquipReuseDelay() > 0) {
                content.append(String.format(" equip-reuse-delay=\"%d\"", template.getEquipReuseDelay()));
            }
        }
        content.append(" />\n");
        content.append("\t\t<crystal ");
        if (!item.grade.equalsIgnoreCase("NONE")) {
            content.append(String.format("type=\"%s\"", item.grade));
            if (Objects.nonNull(template) && template.getCrystalCount() > 0) {
                content.append(String.format(" count=\"%d\"", template.getCrystalCount()));
            }
        }
        content.append("/>\n");
        if (Objects.nonNull(template) && !Util.isNullOrEmpty((Collection)template.getFunctionTemplates())) {
            for (final FuncTemplate function : template.getFunctionTemplates()) {
                item.stats.putIfAbsent(function.getStat().name(), (float)function.getValue());
            }
        }
        if (item.stats.values().stream().anyMatch(v -> v != 0.0f)) {
            content.append("\t\t<stats>\n");
            item.stats.entrySet().stream().filter(e -> e.getValue() != 0.0f).sorted((Comparator<? super Object>)Map.Entry.comparingByKey()).forEach(entry -> content.append("\t\t\t<stat type=\"").append(entry.getKey()).append("\" value=\"").append(entry.getValue()).append("\"/>\n"));
            content.append("\t\t</stats>\n");
        }
        if (Objects.nonNull(template) && !Util.isNullOrEmpty((Collection)template.getAllSkills())) {
            content.append("\t\t<skills>\n");
            parseSkills(content, template.getAllSkills());
            content.append("\t\t</skills>\n");
        }
        content.append("\t</armor>\n\n");
    }
    
    private static void processWeapon(final Item item, final StringBuilder content) {
        final Weapon template = (Weapon)ItemChecker.itemEngine.getTemplate(item.id);
        if (Objects.nonNull(template)) {
            if (!item.name.equalsIgnoreCase(template.getName())) {
                ItemChecker.LOGGER.info("Changing name of weapon {} to {}", (Object)template, (Object)item.name);
            }
            if (!item.type.equalsIgnoreCase(template.getItemType().name())) {
                ItemChecker.LOGGER.info("Changing type of weapon {} ({}) to {}", new Object[] { template, template.getItemType(), item.type });
            }
        }
        content.append(String.format("\t<weapon id=\"%d\" name=\"%s\" type=\"%s\" body-part=\"%s\" icon=\"%s\"", item.id, item.name, item.type, item.bodyPart, item.icon));
        if (Objects.nonNull(template) && item.isMagic != template.isMagicWeapon()) {
            ItemChecker.LOGGER.info("Changing is magic of weapon {} to {}", (Object)template, (Object)item.type);
        }
        if (item.isMagic) {
            content.append(" magic=\"true\"");
        }
        if (item.manaConsume > 0) {
            content.append(String.format(" mana-consume=\"%d\"", item.manaConsume));
        }
        content.append(">\n");
        if (Util.isNotEmpty(item.description)) {
            content.append("\t<!-- ").append(item.description).append(" -->\n");
        }
        content.append("\t\t<restriction");
        if (Objects.nonNull(template)) {
            if (!template.isFreightable()) {
                content.append(" freightable=\"false\"");
            }
            if (template.isOlyRestrictedItem()) {
                content.append(" olympiad-restricted=\"true\"");
            }
        }
        if (item.stackable) {
            content.append(" stackable=\"true\"");
        }
        if (!item.destroyable) {
            content.append(" destroyable=\"false\"");
        }
        if (!item.tradable) {
            content.append(" tradable=\"false\"");
        }
        if (!item.dropable) {
            content.append(" dropable=\"false\"");
        }
        if (!item.sellable) {
            content.append(" sellable=\"false\"");
        }
        if (!item.privateSellable) {
            content.append(" private-sellable=\"false\"");
        }
        if (item.isHero) {
            content.append(" hero=\"true\"");
        }
        content.append("/>\n");
        if (Objects.nonNull(template) && !Util.isNullOrEmpty((Collection)template.getConditions())) {
            content.append("\t\t<condition").append(messageFromConditions(template.getConditions())).append(">\n");
            parseConditions(template.getConditions(), content);
            content.append("\t\t</condition>\n");
        }
        content.append("\t\t<attributes");
        if (item.weight > 0) {
            content.append(" weight=\"").append(item.weight).append("\"");
        }
        if (item.price > 0L) {
            content.append(" price=\"").append(item.price).append("\"");
        }
        if (Objects.nonNull(template)) {
            if (template.getCommissionItemType() != CommissionItemType.OTHER_WEAPON) {
                content.append(String.format(" commission-type=\"%s\"", template.getCommissionItemType()));
            }
            if (template.getReuseDelay() > 0) {
                content.append(String.format(" reuse-delay=\"%d\"", template.getReuseDelay()));
            }
            if (template.getSharedReuseGroup() > 0) {
                content.append(String.format(" reuse-group=\"%d\"", template.getSharedReuseGroup()));
            }
            if (template.getTime() > 0L) {
                content.append(String.format(" duration=\"%d\"", template.getTime()));
            }
            if (template.isForNpc()) {
                content.append(" for-npc=\"true\"");
            }
            if (!template.isEnchantable()) {
                content.append(" enchant-enabled=\"false\"");
            }
            if (template.getChangeWeaponId() > 0) {
                content.append(String.format(" change-weapon=\"%d\"", template.getChangeWeaponId()));
            }
            if (!template.isAttackWeapon()) {
                content.append(" can-attack=\"false\"");
            }
            if (template.useWeaponSkillsOnly()) {
                content.append(" restrict-skills=\"true\"");
            }
            if (template.getEquipReuseDelay() > 0) {
                content.append(String.format(" equip-reuse-delay=\"%d\"", template.getEquipReuseDelay()));
            }
        }
        content.append("/>\n");
        content.append("\t\t<crystal ");
        if (!item.grade.equalsIgnoreCase("NONE")) {
            content.append(String.format("type=\"%s\"", item.grade));
            if (Objects.nonNull(template) && template.getCrystalCount() > 0) {
                content.append(String.format(" count=\"%d\"", template.getCrystalCount()));
            }
        }
        content.append("/>\n");
        if (Objects.nonNull(template)) {
            content.append(String.format("\t\t<damage radius=\"%d\" angle=\"%d\"/>\n", template.getBaseAttackRadius(), template.getBaseAttackAngle()));
        }
        if (Objects.nonNull(template) && !Util.isNullOrEmpty((Collection)template.getFunctionTemplates())) {
            for (final FuncTemplate function : template.getFunctionTemplates()) {
                item.stats.putIfAbsent(function.getStat().name(), (float)function.getValue());
            }
        }
        if (item.stats.values().stream().anyMatch(v -> v != 0.0f)) {
            content.append("\t\t<stats>\n");
            item.stats.entrySet().stream().filter(e -> e.getValue() != 0.0f).sorted((Comparator<? super Object>)Map.Entry.comparingByKey()).forEach(entry -> content.append("\t\t\t<stat type=\"").append(entry.getKey()).append("\" value=\"").append(entry.getValue()).append("\"/>\n"));
            content.append("\t\t</stats>\n");
        }
        if (Objects.nonNull(template) && !Util.isNullOrEmpty((Collection)template.getAllSkills())) {
            content.append("\t\t<skills>\n");
            parseSkills(content, template.getAllSkills());
            content.append("\t\t</skills>\n");
        }
        content.append("\t</weapon>\n\n");
    }
    
    private static String messageFromConditions(final List<Condition> conditions) {
        boolean addName = false;
        String msg = null;
        int msgId = 0;
        for (final Condition condition : conditions) {
            addName |= condition.isAddName();
            if (Util.isNotEmpty(condition.getMessage())) {
                msg = condition.getMessage();
            }
            if (condition.getMessageId() > 0) {
                msgId = condition.getMessageId();
            }
        }
        final StringBuilder builder = new StringBuilder();
        if (addName) {
            builder.append(" add-name=\"true\"");
        }
        if (Util.isNotEmpty(msg)) {
            builder.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, msg));
        }
        if (msgId > 0) {
            builder.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, msgId));
        }
        return builder.toString();
    }
    
    private static void parseConditions(final List<Condition> conditions, final StringBuilder content) {
        final StringBuilder conditionBuilder = new StringBuilder();
        final StringBuilder playerConditions = new StringBuilder();
        for (final Condition condition : conditions) {
            if (condition instanceof ConditionPlayerMinLevel) {
                playerConditions.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((ConditionPlayerMinLevel)condition)._level));
            }
            else if (condition instanceof ConditionPlayerState) {
                final ConditionPlayerState cond = (ConditionPlayerState)condition;
                switch (cond._check) {
                    case CHAOTIC: {
                        playerConditions.append(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, cond._required));
                        continue;
                    }
                    default: {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/base/PlayerState;)Ljava/lang/String;, cond._check));
                        continue;
                    }
                }
            }
            else if (condition instanceof ConditionPlayerIsHero) {
                playerConditions.append(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, ((ConditionPlayerIsHero)condition).isHero));
            }
            else if (condition instanceof ConditionPlayerPledgeClass) {
                playerConditions.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((ConditionPlayerPledgeClass)condition)._pledgeClass));
            }
            else if (condition instanceof ConditionPlayerHasCastle) {
                playerConditions.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((ConditionPlayerHasCastle)condition)._castle));
            }
            else if (condition instanceof ConditionPlayerSex) {
                playerConditions.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (((ConditionPlayerSex)condition)._sex == 1) ? "FEMALE" : "MALE"));
            }
            else if (condition instanceof ConditionPlayerFlyMounted) {
                playerConditions.append(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, ((ConditionPlayerFlyMounted)condition)._val));
            }
            else if (condition instanceof ConditionPlayerInsideZoneId) {
                playerConditions.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)((ConditionPlayerInsideZoneId)condition).zones.stream().mapToObj((IntFunction<?>)String::valueOf).collect((Collector<? super Object, ?, String>)Collectors.joining(" "))));
            }
            else if (condition instanceof ConditionLogicAnd) {
                parseConditions(Arrays.asList(((ConditionLogicAnd)condition).conditions), conditionBuilder);
            }
            else if (condition instanceof ConditionLogicNot) {
                conditionBuilder.append("\t\t\t<not>\n\t");
                parseConditions(List.of(((ConditionLogicNot)condition)._condition), conditionBuilder);
                conditionBuilder.append("\t\t\t</not>\n");
            }
            else if (condition instanceof ConditionTargetLevelRange) {
                playerConditions.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((ConditionTargetLevelRange)condition)._levels[0]));
                playerConditions.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((ConditionTargetLevelRange)condition)._levels[1]));
            }
            else {
                ItemChecker.LOGGER.error("Not parsed Condition {}", (Object)condition);
            }
        }
        if (playerConditions.length() > 0) {
            conditionBuilder.append("\t\t\t<player").append((CharSequence)playerConditions).append("/>\n");
        }
        content.append(conditionBuilder.toString());
    }
    
    private static void fillAuto() throws IOException {
        final BufferedReader reader = Files.newBufferedReader(Path.of("/home/alisson/autouseItem.csv", new String[0]));
        String line = reader.readLine();
        while (Objects.nonNull(line = reader.readLine())) {
            final String[] values = line.split("\t");
            ItemChecker.autoUse.put(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
        }
    }
    
    private static void fillName() throws IOException {
        final BufferedReader reader = Files.newBufferedReader(Path.of("res/itemname.txt", new String[0]));
        String line;
        while (Objects.nonNull(line = reader.readLine())) {
            Matcher matcher = ItemChecker.itemNamePattern.matcher(line);
            if (matcher.matches()) {
                final Item item = new Item();
                item.id = Integer.parseInt(matcher.group(1));
                item.name = matcher.group(2).replace("<", "(").replace(">", ")").replace("&", "&amp;").replaceAll("Lineage\\s?II\\s?", "").replace("NC ", "");
                item.description = matcher.group(3);
                item.action = matcher.group(4);
                item.tradable = !"0".equals(matcher.group(5));
                item.dropable = !"0".equals(matcher.group(6));
                item.destroyable = !"0".equals(matcher.group(7));
                item.privateSellable = !"0".equals(matcher.group(8));
                item.keepType = Integer.parseInt(matcher.group(9));
                item.sellable = !"0".equals(matcher.group(10));
                item.commissionable = !"0".equals(matcher.group(11));
                ItemChecker.items.put(item.id, (Object)item);
            }
            else {
                if (!(matcher = ItemChecker.autoUsePattern.matcher(line)).matches()) {
                    continue;
                }
                ItemChecker.autoUse.put(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            }
        }
    }
    
    private static void fillWeapon() throws IOException {
        final BufferedReader reader = Files.newBufferedReader(Path.of("res/weapon.txt", new String[0]));
        String line;
        while (Objects.nonNull(line = reader.readLine())) {
            final Matcher matcher = ItemChecker.weaponPattern.matcher(line);
            if (matcher.matches()) {
                final int id = Integer.parseInt(matcher.group(1));
                final Item item = (Item)ItemChecker.items.get(id);
                item.icon = matcher.group(2);
                item.weight = Integer.parseInt(matcher.group(3));
                item.crystallizable = !"0".equalsIgnoreCase(matcher.group(4));
                item.bodyPart = ItemChecker.bodyParts.get(matcher.group(5));
                item.stats.put("RANDOM_DAMAGE", Float.parseFloat(matcher.group(6)));
                item.type = ItemChecker.weaponTypes.get(matcher.group(7));
                item.grade = ItemChecker.crystalTypes.get(matcher.group(8));
                if (Objects.isNull(item.grade)) {
                    ItemChecker.LOGGER.warn("Weapon ({}) grade is null", (Object)id);
                    item.grade = "NONE";
                }
                item.manaConsume = Integer.parseInt(matcher.group(9));
                item.isHero = "1".equals(matcher.group(10));
                item.isMagic = "1".equals(matcher.group(11));
                item.itemProcessType = ItemProcessType.WEAPON;
            }
        }
    }
    
    private static void fillStat() throws IOException {
        final BufferedReader reader = Files.newBufferedReader(Path.of("res/item_stats.txt", new String[0]));
        String line;
        while (Objects.nonNull(line = reader.readLine())) {
            final Matcher matcher = ItemChecker.itemStatPattern.matcher(line);
            if (matcher.matches()) {
                final int id = Integer.parseInt(matcher.group(1));
                final Item item = (Item)ItemChecker.items.get(id);
                item.stats.put("PHYSICAL_DEFENCE", Float.parseFloat(matcher.group(2)));
                item.stats.put("MAGICAL_DEFENCE", Float.parseFloat(matcher.group(3)));
                item.stats.put("PHYSICAL_ATTACK", Float.parseFloat(matcher.group(4)));
                item.stats.put("MAGIC_ATTACK", Float.parseFloat(matcher.group(5)));
                item.stats.put("PHYSICAL_ATTACK_SPEED", Float.parseFloat(matcher.group(6)));
                item.stats.put("ACCURACY", Float.parseFloat(matcher.group(7)));
                item.stats.put("ACCURACY_MAGIC", Float.parseFloat(matcher.group(8)));
                item.stats.put("CRITICAL_RATE", Float.parseFloat(matcher.group(9)));
                item.stats.put("MAGIC_CRITICAL_RATE", Float.parseFloat(matcher.group(10)));
                item.stats.put("MAGIC_ATTACK_SPEED", Float.parseFloat(matcher.group(11)));
                item.stats.put("SHIELD_DEFENCE", Float.parseFloat(matcher.group(12)));
                item.stats.put("SHIELD_DEFENCE_RATE", Float.parseFloat(matcher.group(13)));
                item.stats.put("EVASION_RATE", Float.parseFloat(matcher.group(14)));
                item.stats.put("MAGIC_EVASION_RATE", Float.parseFloat(matcher.group(15)));
            }
        }
    }
    
    private static void fillItem() throws IOException {
        final BufferedReader reader = Files.newBufferedReader(Path.of("res/etcitem.txt", new String[0]));
        String line;
        while (Objects.nonNull(line = reader.readLine())) {
            final Matcher matcher = ItemChecker.etcItemPattern.matcher(line);
            if (matcher.matches()) {
                final int id = Integer.parseInt(matcher.group(1));
                final Item item = (Item)ItemChecker.items.get(id);
                item.icon = matcher.group(2);
                item.weight = Integer.parseInt(matcher.group(3));
                item.crystallizable = false;
                item.isQuest = !matcher.group(5).isBlank();
                item.stackable = !"0".equals(matcher.group(6));
                item.type = ItemChecker.itemTypes.get(matcher.group(7));
                if (Objects.isNull(item.type)) {
                    ItemChecker.LOGGER.warn("item ({}) type is null", (Object)id);
                    item.type = "NONE";
                }
                if (item.type.equals("ARROW") || item.type.equals("BOLT")) {
                    item.grade = ItemChecker.crystalTypes.getOrDefault(matcher.group(8), "NONE");
                }
                else {
                    item.grade = "NONE";
                }
                item.itemProcessType = ItemProcessType.ETC;
            }
        }
    }
    
    private static void fillPrice() throws IOException {
        final BufferedReader reader = Files.newBufferedReader(Path.of("res/item_baseinfo.txt", new String[0]));
        String line;
        while (Objects.nonNull(line = reader.readLine())) {
            final Matcher matcher = ItemChecker.itemInfoPattern.matcher(line);
            if (matcher.matches()) {
                final int id = Integer.parseInt(matcher.group(1));
                final Item item = (Item)ItemChecker.items.get(id);
                item.price = Long.parseLong(matcher.group(2));
            }
        }
    }
    
    private static void fillArmor() throws IOException {
        final BufferedReader reader = Files.newBufferedReader(Path.of("res/armor.txt", new String[0]));
        String line;
        while (Objects.nonNull(line = reader.readLine())) {
            final Matcher matcher = ItemChecker.armorPattern.matcher(line);
            if (matcher.matches()) {
                final int id = Integer.parseInt(matcher.group(1));
                final Item item = (Item)ItemChecker.items.get(id);
                item.icon = matcher.group(2);
                item.weight = Integer.parseInt(matcher.group(3));
                item.crystallizable = !"0".equalsIgnoreCase(matcher.group(4));
                item.bodyPart = ItemChecker.bodyParts.get(matcher.group(5));
                item.type = ItemChecker.armorTypes.get(matcher.group(6));
                if (Objects.isNull(item.type)) {
                    item.type = "NONE";
                    ItemChecker.LOGGER.warn("Armor ({}) type is null", (Object)id);
                }
                item.grade = ItemChecker.crystalTypes.get(matcher.group(7));
                if (Objects.isNull(item.grade)) {
                    ItemChecker.LOGGER.warn("Armor ({}) grade is null", (Object)id);
                    item.grade = "NONE";
                }
                item.stats.put("MAX_MP", Float.parseFloat(matcher.group(8)));
                item.itemProcessType = ItemProcessType.ARMOR;
            }
        }
    }
    
    static {
        ItemChecker.LOGGER = LoggerFactory.getLogger((Class)ItemChecker.class);
        ItemChecker.items = (IntMap<Item>)new HashIntMap(13000);
        ItemChecker.autoUse = (IntIntMap)new HashIntIntMap();
        ItemChecker.bodyParts = new HashMap<String, String>();
        ItemChecker.armorTypes = new HashMap<String, String>();
        ItemChecker.crystalTypes = new HashMap<String, String>();
        ItemChecker.itemTypes = new HashMap<String, String>();
        ItemChecker.weaponTypes = new HashMap<String, String>();
        (ItemChecker.autoUseTypes = (IntMap<String>)new HashIntMap()).put(0, (Object)"NONE");
        ItemChecker.autoUseTypes.put(1, (Object)"SUPPLY");
        ItemChecker.autoUseTypes.put(2, (Object)"HEALING");
        ItemChecker.bodyParts.put("0", "UNDERWEAR");
        ItemChecker.bodyParts.put("1", "EAR");
        ItemChecker.bodyParts.put("3", "NECK");
        ItemChecker.bodyParts.put("4", "FINGER");
        ItemChecker.bodyParts.put("6", "HEAD");
        ItemChecker.bodyParts.put("7", "TWO_HAND");
        ItemChecker.bodyParts.put("8", "FULL_ARMOR");
        ItemChecker.bodyParts.put("9", "ALL_DRESS");
        ItemChecker.bodyParts.put("10", "HAIR_ALL");
        ItemChecker.bodyParts.put("11", "RIGHT_BRACELET");
        ItemChecker.bodyParts.put("12", "LEFT_BRACELET");
        ItemChecker.bodyParts.put("13", "TALISMAN");
        ItemChecker.bodyParts.put("19", "BELT");
        ItemChecker.bodyParts.put("21", "BROOCH_JEWEL");
        ItemChecker.bodyParts.put("27", "AGATHION");
        ItemChecker.bodyParts.put("54", "GLOVES");
        ItemChecker.bodyParts.put("55", "CHEST");
        ItemChecker.bodyParts.put("56", "LEGS");
        ItemChecker.bodyParts.put("57", "FEET");
        ItemChecker.bodyParts.put("58", "BACK");
        ItemChecker.bodyParts.put("59", "HAIR");
        ItemChecker.bodyParts.put("60", "HAIR2");
        ItemChecker.bodyParts.put("61", "RIGHT_HAND");
        ItemChecker.bodyParts.put("62", "LEFT_HAND");
        ItemChecker.armorTypes.put("0", "NONE");
        ItemChecker.armorTypes.put("1", "LIGHT");
        ItemChecker.armorTypes.put("2", "HEAVY");
        ItemChecker.armorTypes.put("3", "MAGIC");
        ItemChecker.armorTypes.put("4", "SIGIL");
        ItemChecker.crystalTypes.put("0", "NONE");
        ItemChecker.crystalTypes.put("1", "D");
        ItemChecker.crystalTypes.put("2", "C");
        ItemChecker.crystalTypes.put("3", "B");
        ItemChecker.crystalTypes.put("4", "A");
        ItemChecker.crystalTypes.put("5", "S");
        ItemChecker.itemTypes.put("0", "NONE");
        ItemChecker.itemTypes.put("1", "SCROLL");
        ItemChecker.itemTypes.put("2", "ARROW");
        ItemChecker.itemTypes.put("3", "POTION");
        ItemChecker.itemTypes.put("4", "SPELLBOOK");
        ItemChecker.itemTypes.put("5", "RECIPE");
        ItemChecker.itemTypes.put("6", "MATERIAL");
        ItemChecker.itemTypes.put("7", "PET_COLLAR");
        ItemChecker.itemTypes.put("8", "CASTLE_GUARD");
        ItemChecker.itemTypes.put("9", "DYE");
        ItemChecker.itemTypes.put("10", "SEED");
        ItemChecker.itemTypes.put("11", "SEED2");
        ItemChecker.itemTypes.put("12", "HARVEST");
        ItemChecker.itemTypes.put("13", "LOTTO");
        ItemChecker.itemTypes.put("14", "RACE_TICKET");
        ItemChecker.itemTypes.put("15", "TICKET_OF_LORD");
        ItemChecker.itemTypes.put("16", "LURE");
        ItemChecker.itemTypes.put("17", "CROP");
        ItemChecker.itemTypes.put("18", "MATURECROP");
        ItemChecker.itemTypes.put("19", "ENCHANT_WEAPON");
        ItemChecker.itemTypes.put("20", "ENCHANT_ARMOR");
        ItemChecker.itemTypes.put("21", "BLESSED_ENCHANT_WEAPON");
        ItemChecker.itemTypes.put("22", "BLESSED_ENCHANT_ARMOR");
        ItemChecker.itemTypes.put("23", "COUPON");
        ItemChecker.itemTypes.put("24", "ELIXIR");
        ItemChecker.itemTypes.put("25", "ENCHT_ATTR");
        ItemChecker.itemTypes.put("26", "ENCHT_ATTR_CURSED");
        ItemChecker.itemTypes.put("27", "BOLT");
        ItemChecker.itemTypes.put("28", "INC_PROP_ENCHANT_WEAPON");
        ItemChecker.itemTypes.put("29", "INC_PROP_ENCHANT_ARMOR");
        ItemChecker.itemTypes.put("30", "ENCHT_ATTR_CRYSTAL_ENCHANT_ARMOR");
        ItemChecker.itemTypes.put("31", "ENCHT_ATTR_CRYSTAL_ENCHANT_WEAPON");
        ItemChecker.itemTypes.put("32", "ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_ARMOR");
        ItemChecker.itemTypes.put("33", "ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WEAPON");
        ItemChecker.itemTypes.put("34", "RUNE");
        ItemChecker.itemTypes.put("35", "RUNE_SELECT");
        ItemChecker.itemTypes.put("36", "TELEPORT_BOOKMARK");
        ItemChecker.itemTypes.put("37", "CHANGE_ATTR");
        ItemChecker.itemTypes.put("38", "SOULSHOT");
        ItemChecker.itemTypes.put("39", "SHAPE_SHIFTING_WEAPON");
        ItemChecker.itemTypes.put("40", "BLESS_SHAPE_SHIFTING_WEAPON");
        ItemChecker.itemTypes.put("41", "SHAPE_SHIFTING_WEAPON_FIXED");
        ItemChecker.itemTypes.put("42", "SHAPE_SHIFTING_ARMOR");
        ItemChecker.itemTypes.put("43", "BLESS_SHAPE_SHIFTING_ARMOR");
        ItemChecker.itemTypes.put("44", "SHAPE_SHIFTING_ARMOR_FIXED");
        ItemChecker.itemTypes.put("45", "SHAPE_SHIFTING_HAIR_ACC");
        ItemChecker.itemTypes.put("46", "BLESS_SHAPE_SHIFTING_HAIR_ACC");
        ItemChecker.itemTypes.put("47", "SHAPE_SHIFTING_HAIR_ACC_FIXED");
        ItemChecker.itemTypes.put("48", "RESTORE_SHAPE_SHIFTING_WEAPON");
        ItemChecker.itemTypes.put("49", "RESTORE_SHAPE_SHIFTING_ARMOR");
        ItemChecker.itemTypes.put("50", "RESTORE_SHAPE_SHIFTING_HAIR_ACC");
        ItemChecker.itemTypes.put("51", "RESTORE_SHAPE_SHIFTING_ALL_ITEM");
        ItemChecker.itemTypes.put("52", "BLESS_INC_PROP_ENCHANT_WEAPON");
        ItemChecker.itemTypes.put("53", "BLESS_INC_PROP_ENCHANT_ARMOR");
        ItemChecker.itemTypes.put("54", "CARD_EVENT");
        ItemChecker.itemTypes.put("55", "SHAPE_SHIFTING_ALL_ITEM_FIXED");
        ItemChecker.itemTypes.put("56", "MULTI_ENCHANT_WEAPON");
        ItemChecker.itemTypes.put("57", "MULTI_ENCHANT_ARMOR");
        ItemChecker.itemTypes.put("58", "MULTI_INC_PROB_ENCHANT_WEAPON");
        ItemChecker.itemTypes.put("59", "MULTI_INC_PROB_ENCHANT_ARMOR");
        ItemChecker.itemTypes.put("60", "ENSOUL_STONE");
        ItemChecker.itemTypes.put("61", "NICK_COLOR_OLD");
        ItemChecker.itemTypes.put("62", "NICK_COLOR_NEW");
        ItemChecker.itemTypes.put("63", "ENCHANT_AGATHION");
        ItemChecker.itemTypes.put("64", "BLESS_ENCHANT_AGATHION");
        ItemChecker.itemTypes.put("65", "MULTI_ENCHANT_AGATHION");
        ItemChecker.itemTypes.put("66", "ANCIENT_CRYSTAL_ENCHANT_AGATHION");
        ItemChecker.itemTypes.put("67", "INC_ENCHANT_PROP_AGATHION");
        ItemChecker.itemTypes.put("68", "BLESS_INC_ENCHANT_PROP_AGATHION");
        ItemChecker.itemTypes.put("69", "MULTI_INC_ENCHANT_PROB_AGATHION");
        ItemChecker.itemTypes.put("70", "SEAL_SCROLL");
        ItemChecker.itemTypes.put("71", "UNSEAL_SCROLL");
        ItemChecker.itemTypes.put("72", "BULLET");
        ItemChecker.itemTypes.put("73", "MAGICLAMP");
        ItemChecker.itemTypes.put("74", "TRANSFORMATION_BOOK");
        ItemChecker.itemTypes.put("75", "TRANSFORMATION_BOOK_BOX_RANDOM");
        ItemChecker.itemTypes.put("76", "TRANSFORMATION_BOOK_BOX_RANDOM_RARE");
        ItemChecker.itemTypes.put("77", "TRANSFORMATION_BOOK_BOX_STANDARD");
        ItemChecker.itemTypes.put("78", "TRANSFORMATION_BOOK_BOX_HIGH_GRADE");
        ItemChecker.itemTypes.put("79", "TRANSFORMATION_BOOK_BOX_RARE");
        ItemChecker.itemTypes.put("80", "TRANSFORMATION_BOOK_BOX_LEGENDARY");
        ItemChecker.itemTypes.put("81", "TRANSFORMATION_BOOK_BOX_MYTHIC");
        ItemChecker.itemTypes.put("82", "POLY_ENCHANT_WEAPON");
        ItemChecker.itemTypes.put("83", "POLY_ENCHANT_ARMOR");
        ItemChecker.itemTypes.put("84", "POLY_INC_ENCHANT_PROP_WEAPON");
        ItemChecker.itemTypes.put("85", "POLY_INC_ENCHANT_ARMOR");
        ItemChecker.itemTypes.put("86", "CURSED_ENCHANT_WEAPON");
        ItemChecker.itemTypes.put("87", "CURSED_ENCHANT_ARMOR");
        ItemChecker.itemTypes.put("88", "VITAL_LEGACY_ITEM_1D");
        ItemChecker.itemTypes.put("89", "VITAL_LEGACY_ITEM_7D");
        ItemChecker.itemTypes.put("90", "VITAL_LEGACY_ITEM_30D");
        ItemChecker.weaponTypes.put("0", "NONE");
        ItemChecker.weaponTypes.put("1", "SWORD");
        ItemChecker.weaponTypes.put("2", "TWO_HAND_SWORD");
        ItemChecker.weaponTypes.put("3", "MAGIC_SWORD");
        ItemChecker.weaponTypes.put("4", "BLUNT");
        ItemChecker.weaponTypes.put("5", "HAMMER");
        ItemChecker.weaponTypes.put("6", "ROD");
        ItemChecker.weaponTypes.put("7", "STAFF");
        ItemChecker.weaponTypes.put("8", "DAGGER");
        ItemChecker.weaponTypes.put("9", "SPEAR");
        ItemChecker.weaponTypes.put("10", "FIST");
        ItemChecker.weaponTypes.put("11", "BOW");
        ItemChecker.weaponTypes.put("12", "ETC");
        ItemChecker.weaponTypes.put("13", "DUAL");
        ItemChecker.weaponTypes.put("15", "FISHING_ROD");
        ItemChecker.weaponTypes.put("16", "RAPIER");
        ItemChecker.weaponTypes.put("17", "CROSSBOW");
        ItemChecker.weaponTypes.put("18", "ANCIENT_SWORD");
        ItemChecker.weaponTypes.put("20", "DUAL_DAGGER");
        ItemChecker.weaponTypes.put("22", "TWO_HAND_CROSSBOW");
        ItemChecker.weaponTypes.put("23", "DUAL_BLUNT");
        ItemChecker.step = 100;
        ItemChecker.itemNamePattern = Pattern.compile("^.*id=(\\d+?)\\sname=\\[(.*?)].*description=\\[(.*?)].*default_action=\\[(.*?)].*is_trade=(\\d+?)\\s.*is_drop=(\\d+?)\\s.*is_destruct=(\\d+?)\\s.*is_private_store=(\\d+)\\skeep_type=(\\d+)\\sis_npctrade=(\\d+)\\s.*is_commission_store=(\\d+).*");
        ItemChecker.autoUsePattern = Pattern.compile("^.*Item_id=(\\d+)\\sIs_Use=(\\d+)\\s.*");
        ItemChecker.weaponPattern = Pattern.compile("^.*object_id=(\\d+).*?\\sicon=\\{\\[(.*?)].*\\sweight=(\\d+).*?\\scrystallizable=(\\d+).*\\sbody_part=(\\d+).*\\srandom_damage=(\\d+)\\sweapon_type=(\\d+)\\scrystal_type=(\\d+)\\smp_consume=(\\d+).*\\scan_equip_hero=(.*?)\\sis_magic_weapon=(\\d+).*");
        ItemChecker.itemStatPattern = Pattern.compile("^.*object_id=(\\d+)\\spDefense=(\\d+)\\smDefense=(\\d+)\\spAttack=(\\d+)\\smAttack=(\\d+)\\spAttackSpeed=(\\d+)\\spHit=(.*?)\\smHit=(.*?)\\spCritical=(.*?)\\smCritical=(.*?)\\sspeed=(\\d+)\\sShieldDefense=(\\d+)\\sShieldDefenseRate=(\\d+)\\spavoid=(.*?)\\smavoid=(.*?)\\s.*");
        ItemChecker.etcItemPattern = Pattern.compile("^.*object_id=(\\d+)\\s.*icon=\\{\\[(.*?)].*weight=(\\d+)\\s.*crystallizable=(\\d+)\\srelated_quest_id=\\{(.*?)}.*consume_type=(\\d+)\\setcitem_type=(\\d+)\\scrystal_type=(\\d+).*");
        ItemChecker.itemInfoPattern = Pattern.compile("^.*id=(\\d+?)\\sdefault_price=(\\d+)\\s.*");
        ItemChecker.armorPattern = Pattern.compile("^.*object_id=(\\d+).*?\\sicon=\\{\\[(.*?)].*\\sweight=(\\d+).*\\scrystallizable=(\\d+).*\\sbody_part=(\\d+).*\\sarmor_type=(\\d+)\\scrystal_type=(\\d+)\\smp_bonus=(\\d+).*");
    }
    
    static class Item
    {
        public int keepType;
        int id;
        String name;
        String description;
        String action;
        boolean tradable;
        public boolean privateSellable;
        boolean dropable;
        boolean destroyable;
        boolean depositable;
        boolean sellable;
        boolean commissionable;
        long price;
        String icon;
        int weight;
        boolean crystallizable;
        boolean isQuest;
        boolean isBlessed;
        boolean stackable;
        String grade;
        String type;
        String bodyPart;
        int randomDamage;
        int manaConsume;
        int ssCount;
        int spCount;
        boolean isHero;
        boolean isMagic;
        Map<String, Float> stats;
        ItemProcessType itemProcessType;
        
        Item() {
            this.stats = new HashMap<String, Float>();
            this.itemProcessType = ItemProcessType.NONE;
        }
        
        @Override
        public String toString() {
            return invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, this.id, this.name);
        }
    }
    
    enum ItemProcessType
    {
        WEAPON, 
        ARMOR, 
        ETC, 
        NONE;
    }
}
