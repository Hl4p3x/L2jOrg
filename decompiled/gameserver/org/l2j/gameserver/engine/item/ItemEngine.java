// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.item;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.xml.impl.EnsoulData;
import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.data.xml.impl.AugmentationEngine;
import org.l2j.gameserver.data.xml.impl.ItemCrystallizationData;
import org.l2j.gameserver.data.xml.impl.EnchantItemOptionsData;
import java.util.function.Consumer;
import org.l2j.gameserver.handler.ItemHandler;
import java.util.ServiceLoader;
import org.l2j.gameserver.handler.IItemHandler;
import java.util.Collection;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.util.GameUtils;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.item.OnItemCreate;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.EventMonster;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.commission.CommissionItemType;
import org.l2j.gameserver.model.item.AutoUseType;
import org.l2j.gameserver.model.item.type.ActionType;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.model.item.Armor;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.conditions.ConditionLogicAnd;
import org.l2j.gameserver.model.conditions.ConditionPlayerInsideZoneId;
import org.l2j.gameserver.model.conditions.ConditionPlayerFlyMounted;
import org.l2j.gameserver.model.conditions.ConditionPlayerSex;
import org.l2j.gameserver.model.conditions.ConditionPlayerHasCastle;
import org.l2j.gameserver.model.conditions.ConditionPlayerPledgeClass;
import org.l2j.gameserver.model.conditions.ConditionPlayerIsHero;
import org.l2j.gameserver.model.conditions.ConditionPlayerChaotic;
import org.l2j.gameserver.model.conditions.ConditionPlayerMaxLevel;
import org.l2j.gameserver.model.conditions.ConditionPlayerMinLevel;
import java.util.Objects;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.stats.functions.FuncTemplate;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.enums.ItemSkillType;
import org.w3c.dom.NamedNodeMap;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.item.ItemTemplate;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class ItemEngine extends GameXmlReader
{
    private static final Logger LOGGER;
    private static final Logger LOGGER_ITEMS;
    private final IntMap<ItemTemplate> items;
    
    private ItemEngine() {
        this.items = (IntMap<ItemTemplate>)new HashIntMap(13700);
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/items/items.xsd");
    }
    
    public void load() {
        this.items.clear();
        this.parseDatapackDirectory("data/items", true);
        ItemEngine.LOGGER.info("Loaded {} Items", (Object)this.items.size());
        this.releaseResources();
    }
    
    protected void parseDocument(final Document doc, final File f) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1         /* doc */
        //     2: ldc             "list"
        //     4: aload_0         /* this */
        //     5: invokedynamic   BootstrapMethod #0, accept:(Lorg/l2j/gameserver/engine/item/ItemEngine;)Ljava/util/function/Consumer;
        //    10: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.forEach:(Lorg/w3c/dom/Node;Ljava/lang/String;Ljava/util/function/Consumer;)V
        //    13: return         
        //    MethodParameters:
        //  Name  Flags  
        //  ----  -----
        //  doc   
        //  f     
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void parseWeapon(final Node weaponNode) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokeinterface org/w3c/dom/Node.getAttributes:()Lorg/w3c/dom/NamedNodeMap;
        //     6: astore_2        /* attrs */
        //     7: new             Lorg/l2j/gameserver/model/item/Weapon;
        //    10: dup            
        //    11: aload_0         /* this */
        //    12: aload_2         /* attrs */
        //    13: ldc             "id"
        //    15: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)I
        //    18: aload_0         /* this */
        //    19: aload_2         /* attrs */
        //    20: ldc             "name"
        //    22: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseString:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)Ljava/lang/String;
        //    25: aload_0         /* this */
        //    26: aload_2         /* attrs */
        //    27: ldc             Lorg/l2j/gameserver/model/item/type/WeaponType;.class
        //    29: ldc             "type"
        //    31: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseEnum:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;
        //    34: checkcast       Lorg/l2j/gameserver/model/item/type/WeaponType;
        //    37: aload_0         /* this */
        //    38: aload_2         /* attrs */
        //    39: ldc             Lorg/l2j/gameserver/model/item/BodyPart;.class
        //    41: ldc             "body-part"
        //    43: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseEnum:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;
        //    46: checkcast       Lorg/l2j/gameserver/model/item/BodyPart;
        //    49: invokespecial   org/l2j/gameserver/model/item/Weapon.<init>:(ILjava/lang/String;Lorg/l2j/gameserver/model/item/type/WeaponType;Lorg/l2j/gameserver/model/item/BodyPart;)V
        //    52: astore_3        /* weapon */
        //    53: aload_3         /* weapon */
        //    54: aload_0         /* this */
        //    55: aload_2         /* attrs */
        //    56: ldc             "display-id"
        //    58: aload_3         /* weapon */
        //    59: invokevirtual   org/l2j/gameserver/model/item/Weapon.getId:()I
        //    62: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;I)I
        //    65: invokevirtual   org/l2j/gameserver/model/item/Weapon.setDisplayId:(I)V
        //    68: aload_3         /* weapon */
        //    69: aload_0         /* this */
        //    70: aload_2         /* attrs */
        //    71: ldc             "magic"
        //    73: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseBoolean:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)Z
        //    76: invokevirtual   org/l2j/gameserver/model/item/Weapon.setMagic:(Z)V
        //    79: aload_0         /* this */
        //    80: aload_1         /* weaponNode */
        //    81: aload_0         /* this */
        //    82: aload_3         /* weapon */
        //    83: invokedynamic   BootstrapMethod #1, accept:(Lorg/l2j/gameserver/engine/item/ItemEngine;Lorg/l2j/gameserver/model/item/Weapon;)Ljava/util/function/Consumer;
        //    88: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.forEach:(Lorg/w3c/dom/Node;Ljava/util/function/Consumer;)V
        //    91: aload_0         /* this */
        //    92: getfield        org/l2j/gameserver/engine/item/ItemEngine.items:Lio/github/joealisson/primitive/IntMap;
        //    95: aload_3         /* weapon */
        //    96: invokevirtual   org/l2j/gameserver/model/item/Weapon.getId:()I
        //    99: aload_3         /* weapon */
        //   100: invokeinterface io/github/joealisson/primitive/IntMap.put:(ILjava/lang/Object;)Ljava/lang/Object;
        //   105: pop            
        //   106: return         
        //    MethodParameters:
        //  Name        Flags  
        //  ----------  -----
        //  weaponNode  
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void parseCrystalType(final ItemTemplate weapon, final Node node) {
        final NamedNodeMap attr = node.getAttributes();
        weapon.setCrystalType((CrystalType)this.parseEnum(attr, (Class)CrystalType.class, "type", (Enum)CrystalType.NONE));
        weapon.setCrystalCount(this.parseInt(attr, "count"));
    }
    
    private void parseItemSkills(final ItemTemplate item, final Node node) {
        final NamedNodeMap attr;
        final ItemSkillType type;
        this.forEach(node, "skill", skillNode -> {
            attr = skillNode.getAttributes();
            type = (ItemSkillType)this.parseEnum(attr, (Class)ItemSkillType.class, "type");
            item.addSkill(new ItemSkillHolder(this.parseInt(attr, "id"), this.parseInt(attr, "level"), type, this.parseInt(attr, "chance"), this.parseInt(attr, "value")));
        });
    }
    
    private void parseItemStats(final ItemTemplate item, final Node node) {
        final NamedNodeMap attr;
        final Stat type;
        final double value;
        this.forEach(node, "stat", statNode -> {
            attr = statNode.getAttributes();
            type = (Stat)this.parseEnum(attr, (Class)Stat.class, "type");
            value = this.parseDouble(attr, "value");
            item.addFunctionTemplate(new FuncTemplate(null, null, "add", 0, type, value));
        });
    }
    
    private void parseItemCondition(final ItemTemplate item, final Node node) {
        Condition condition = null;
        for (Node n = node.getFirstChild(); Objects.nonNull(n); n = n.getNextSibling()) {
            final String nodeName = n.getNodeName();
            Condition playerCondition = null;
            switch (nodeName) {
                case "player": {
                    playerCondition = this.parsePlayerCondition(n);
                    break;
                }
                default: {
                    playerCondition = condition;
                    break;
                }
            }
            final Condition temp = playerCondition;
            condition = this.and(condition, temp);
        }
        if (Objects.nonNull(condition)) {
            final NamedNodeMap attr = node.getAttributes();
            final String msg = this.parseString(attr, "msg");
            final int msgId = this.parseInt(attr, "msg-id");
            if (Objects.nonNull(msg)) {
                condition.setMessage(msg);
            }
            else if (Objects.nonNull(msgId)) {
                condition.setMessageId(msgId);
                if (this.parseBoolean(attr, "add-name")) {
                    condition.addName();
                }
            }
            item.attachCondition(condition);
        }
    }
    
    private Condition parsePlayerCondition(final Node playerNode) {
        final NamedNodeMap attrs = playerNode.getAttributes();
        Condition playerCondition = null;
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Node attr = attrs.item(i);
            final String nodeName = attr.getNodeName();
            Condition condition = null;
            switch (nodeName) {
                case "level-min": {
                    condition = this.and(playerCondition, new ConditionPlayerMinLevel(this.parseInt(attr)));
                    break;
                }
                case "level-max": {
                    condition = this.and(playerCondition, new ConditionPlayerMaxLevel(this.parseInt(attr)));
                    break;
                }
                case "chaotic": {
                    condition = this.and(playerCondition, ConditionPlayerChaotic.of(this.parseBoolean(attr)));
                    break;
                }
                case "hero": {
                    condition = this.and(playerCondition, ConditionPlayerIsHero.of(this.parseBoolean(attr)));
                    break;
                }
                case "pledge-class": {
                    condition = this.and(playerCondition, new ConditionPlayerPledgeClass(this.parseInt(attr)));
                    break;
                }
                case "castle": {
                    condition = this.and(playerCondition, new ConditionPlayerHasCastle(this.parseInt(attr)));
                    break;
                }
                case "sex": {
                    condition = this.and(playerCondition, ConditionPlayerSex.of(this.parseInt(attr)));
                    break;
                }
                case "flying": {
                    condition = this.and(playerCondition, ConditionPlayerFlyMounted.of(this.parseBoolean(attr)));
                    break;
                }
                case "zone": {
                    condition = this.and(playerCondition, new ConditionPlayerInsideZoneId(this.parseIntList(attr)));
                    break;
                }
                default: {
                    condition = playerCondition;
                    break;
                }
            }
            playerCondition = condition;
        }
        return playerCondition;
    }
    
    private Condition and(final Condition c, final Condition c2) {
        if (Objects.isNull(c)) {
            return c2;
        }
        if (Objects.isNull(c2)) {
            return c;
        }
        if (c instanceof ConditionLogicAnd) {
            ((ConditionLogicAnd)c).add(c2);
            return c;
        }
        if (c2 instanceof ConditionLogicAnd) {
            ((ConditionLogicAnd)c2).add(c);
            return c2;
        }
        return new ConditionLogicAnd(new Condition[] { c, c2 });
    }
    
    private void parseItemRestriction(final ItemTemplate item, final Node node) {
        final NamedNodeMap attr = node.getAttributes();
        item.setFreightable(this.parseBoolean(attr, "freightable"));
        item.setOlympiadRestricted(this.parseBoolean(attr, "olympiad-restricted"));
        item.setStackable(this.parseBoolean(attr, "stackable"));
        item.setDestroyable(this.parseBoolean(attr, "destroyable"));
        item.setTradable(this.parseBoolean(attr, "tradable"));
        item.setDropable(this.parseBoolean(attr, "dropable"));
        item.setSellable(this.parseBoolean(attr, "sellable"));
        item.setDepositable(this.parseBoolean(attr, "depositable"));
    }
    
    private void parseWeaponConsume(final Weapon weapon, final Node node) {
        final NamedNodeMap attr = node.getAttributes();
        weapon.setSoulshots(this.parseInt(attr, "soulshots"));
        weapon.setSpiritshots(this.parseInt(attr, "spiritshots"));
        weapon.setManaConsume(this.parseInt(attr, "mana"));
    }
    
    private void parseWeaponDamage(final Weapon weapon, final Node node) {
        final NamedNodeMap attr = node.getAttributes();
        weapon.setDamageRadius(this.parseInt(attr, "radius"));
        weapon.setDamageAngle(this.parseInt(attr, "angle"));
    }
    
    private void parseWeaponAttributes(final Weapon weapon, final Node node) {
        final NamedNodeMap attr = node.getAttributes();
        this.parseCommonAttributes(weapon, node);
        weapon.setEnchantable(this.parseBoolean(attr, "enchant-enabled"));
        weapon.setChangeWeapon(this.parseInt(attr, "change-weapon"));
        weapon.setCanAttack(this.parseBoolean(attr, "can-attack"));
        weapon.setRestrictSkills(this.parseBoolean(attr, "restrict-skills"));
        weapon.setEquipReuseDelay(this.parseInt(attr, "equip-reuse-delay"));
    }
    
    private void parseArmor(final Node armorNode) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokeinterface org/w3c/dom/Node.getAttributes:()Lorg/w3c/dom/NamedNodeMap;
        //     6: astore_2        /* attrs */
        //     7: new             Lorg/l2j/gameserver/model/item/Armor;
        //    10: dup            
        //    11: aload_0         /* this */
        //    12: aload_2         /* attrs */
        //    13: ldc             "id"
        //    15: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)I
        //    18: aload_0         /* this */
        //    19: aload_2         /* attrs */
        //    20: ldc             "name"
        //    22: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseString:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)Ljava/lang/String;
        //    25: aload_0         /* this */
        //    26: aload_2         /* attrs */
        //    27: ldc_w           Lorg/l2j/gameserver/model/item/type/ArmorType;.class
        //    30: ldc             "type"
        //    32: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseEnum:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;
        //    35: checkcast       Lorg/l2j/gameserver/model/item/type/ArmorType;
        //    38: aload_0         /* this */
        //    39: aload_2         /* attrs */
        //    40: ldc             Lorg/l2j/gameserver/model/item/BodyPart;.class
        //    42: ldc             "body-part"
        //    44: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseEnum:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;
        //    47: checkcast       Lorg/l2j/gameserver/model/item/BodyPart;
        //    50: invokespecial   org/l2j/gameserver/model/item/Armor.<init>:(ILjava/lang/String;Lorg/l2j/gameserver/model/item/type/ArmorType;Lorg/l2j/gameserver/model/item/BodyPart;)V
        //    53: astore_3        /* armor */
        //    54: aload_3         /* armor */
        //    55: aload_0         /* this */
        //    56: aload_2         /* attrs */
        //    57: ldc             "display-id"
        //    59: aload_3         /* armor */
        //    60: invokevirtual   org/l2j/gameserver/model/item/Armor.getId:()I
        //    63: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;I)I
        //    66: invokevirtual   org/l2j/gameserver/model/item/Armor.setDisplayId:(I)V
        //    69: aload_0         /* this */
        //    70: aload_1         /* armorNode */
        //    71: aload_0         /* this */
        //    72: aload_3         /* armor */
        //    73: invokedynamic   BootstrapMethod #4, accept:(Lorg/l2j/gameserver/engine/item/ItemEngine;Lorg/l2j/gameserver/model/item/Armor;)Ljava/util/function/Consumer;
        //    78: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.forEach:(Lorg/w3c/dom/Node;Ljava/util/function/Consumer;)V
        //    81: aload_0         /* this */
        //    82: getfield        org/l2j/gameserver/engine/item/ItemEngine.items:Lio/github/joealisson/primitive/IntMap;
        //    85: aload_3         /* armor */
        //    86: invokevirtual   org/l2j/gameserver/model/item/Armor.getId:()I
        //    89: aload_3         /* armor */
        //    90: invokeinterface io/github/joealisson/primitive/IntMap.put:(ILjava/lang/Object;)Ljava/lang/Object;
        //    95: pop            
        //    96: return         
        //    MethodParameters:
        //  Name       Flags  
        //  ---------  -----
        //  armorNode  
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void parseArmorAttributes(final Armor armor, final Node node) {
        this.parseCommonAttributes(armor, node);
        final NamedNodeMap attr = node.getAttributes();
        armor.setEnchantable(this.parseBoolean(attr, "enchant-enabled"));
        armor.setEquipReuseDelay(this.parseInt(attr, "equip-reuse-delay"));
    }
    
    private void parseItem(final Node itemNode) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokeinterface org/w3c/dom/Node.getAttributes:()Lorg/w3c/dom/NamedNodeMap;
        //     6: astore_2        /* attrs */
        //     7: new             Lorg/l2j/gameserver/model/item/EtcItem;
        //    10: dup            
        //    11: aload_0         /* this */
        //    12: aload_2         /* attrs */
        //    13: ldc             "id"
        //    15: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)I
        //    18: aload_0         /* this */
        //    19: aload_2         /* attrs */
        //    20: ldc             "name"
        //    22: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseString:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)Ljava/lang/String;
        //    25: aload_0         /* this */
        //    26: aload_2         /* attrs */
        //    27: ldc_w           Lorg/l2j/gameserver/model/item/type/EtcItemType;.class
        //    30: ldc             "type"
        //    32: getstatic       org/l2j/gameserver/model/item/type/EtcItemType.NONE:Lorg/l2j/gameserver/model/item/type/EtcItemType;
        //    35: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseEnum:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Enum;)Ljava/lang/Enum;
        //    38: checkcast       Lorg/l2j/gameserver/model/item/type/EtcItemType;
        //    41: invokespecial   org/l2j/gameserver/model/item/EtcItem.<init>:(ILjava/lang/String;Lorg/l2j/gameserver/model/item/type/EtcItemType;)V
        //    44: astore_3        /* item */
        //    45: aload_3         /* item */
        //    46: aload_0         /* this */
        //    47: aload_2         /* attrs */
        //    48: ldc             "display-id"
        //    50: aload_3         /* item */
        //    51: invokevirtual   org/l2j/gameserver/model/item/EtcItem.getId:()I
        //    54: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;I)I
        //    57: invokevirtual   org/l2j/gameserver/model/item/EtcItem.setDisplayId:(I)V
        //    60: aload_0         /* this */
        //    61: aload_1         /* itemNode */
        //    62: aload_0         /* this */
        //    63: aload_3         /* item */
        //    64: invokedynamic   BootstrapMethod #5, accept:(Lorg/l2j/gameserver/engine/item/ItemEngine;Lorg/l2j/gameserver/model/item/EtcItem;)Ljava/util/function/Consumer;
        //    69: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.forEach:(Lorg/w3c/dom/Node;Ljava/util/function/Consumer;)V
        //    72: aload_3         /* item */
        //    73: invokevirtual   org/l2j/gameserver/model/item/EtcItem.fillType2:()V
        //    76: aload_0         /* this */
        //    77: getfield        org/l2j/gameserver/engine/item/ItemEngine.items:Lio/github/joealisson/primitive/IntMap;
        //    80: aload_3         /* item */
        //    81: invokevirtual   org/l2j/gameserver/model/item/EtcItem.getId:()I
        //    84: aload_3         /* item */
        //    85: invokeinterface io/github/joealisson/primitive/IntMap.put:(ILjava/lang/Object;)Ljava/lang/Object;
        //    90: pop            
        //    91: return         
        //    MethodParameters:
        //  Name      Flags  
        //  --------  -----
        //  itemNode  
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void parseItemCrystal(final EtcItem item, final Node node) {
        item.setCrystalType((CrystalType)this.parseEnum(node.getAttributes(), (Class)CrystalType.class, "type", (Enum)CrystalType.NONE));
    }
    
    private void parseTransformationBook(final EtcItem item, final Node node) {
        item.setHandler("TransformationBook");
        item.addSkill(new ItemSkillHolder(this.parseInt(node.getAttributes(), "skill"), 1, ItemSkillType.NORMAL, 100, 0));
    }
    
    private void parseItemExtract(final EtcItem item, final Node node) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ldc_w           "ExtractableItems"
        //     4: invokevirtual   org/l2j/gameserver/model/item/EtcItem.setHandler:(Ljava/lang/String;)V
        //     7: aload_1         /* item */
        //     8: aload_0         /* this */
        //     9: aload_2         /* node */
        //    10: invokeinterface org/w3c/dom/Node.getAttributes:()Lorg/w3c/dom/NamedNodeMap;
        //    15: ldc_w           "max"
        //    18: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)I
        //    21: invokevirtual   org/l2j/gameserver/model/item/EtcItem.setExtractableMax:(I)V
        //    24: aload_2         /* node */
        //    25: invokeinterface org/w3c/dom/Node.getFirstChild:()Lorg/w3c/dom/Node;
        //    30: astore_3        /* itemNode */
        //    31: aload_3         /* itemNode */
        //    32: invokestatic    java/util/Objects.nonNull:(Ljava/lang/Object;)Z
        //    35: ifeq            135
        //    38: aload_3         /* itemNode */
        //    39: invokeinterface org/w3c/dom/Node.getNodeName:()Ljava/lang/String;
        //    44: ldc_w           "item"
        //    47: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    50: ifeq            125
        //    53: aload_3         /* itemNode */
        //    54: invokeinterface org/w3c/dom/Node.getAttributes:()Lorg/w3c/dom/NamedNodeMap;
        //    59: astore          attr
        //    61: aload_1         /* item */
        //    62: new             new            !!! ERROR
        //    65: dup            
        //    66: aload_0         /* this */
        //    67: aload           attr
        //    69: ldc             "id"
        //    71: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)I
        //    74: aload_0         /* this */
        //    75: aload           attr
        //    77: ldc_w           "min-count"
        //    80: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)I
        //    83: aload_0         /* this */
        //    84: aload           attr
        //    86: ldc_w           "min-count"
        //    89: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)I
        //    92: aload_0         /* this */
        //    93: aload           attr
        //    95: ldc_w           "chance"
        //    98: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseDouble:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)D
        //   101: aload_0         /* this */
        //   102: aload           attr
        //   104: ldc_w           "min-enchant"
        //   107: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)I
        //   110: aload_0         /* this */
        //   111: aload           attr
        //   113: ldc_w           "max-enchant"
        //   116: invokevirtual   org/l2j/gameserver/engine/item/ItemEngine.parseInt:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)I
        //   119: invokespecial   invokespecial  !!! ERROR
        //   122: invokevirtual   org/l2j/gameserver/model/item/EtcItem.addCapsuledItem:(invokevirtual  !!! ERROR
        //   125: aload_3         /* itemNode */
        //   126: invokeinterface org/w3c/dom/Node.getNextSibling:()Lorg/w3c/dom/Node;
        //   131: astore_3        /* itemNode */
        //   132: goto            31
        //   135: return         
        //    MethodParameters:
        //  Name  Flags  
        //  ----  -----
        //  item  
        //  node  
        //    StackMapTable: 00 03 FC 00 1F 07 00 55 FB 00 5D FA 00 09
        // 
        // The error that occurred was:
        // 
        // java.lang.reflect.GenericSignatureFormatError
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.error(SignatureParser.java:70)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseFormalParameters(SignatureParser.java:416)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseMethodTypeSignature(SignatureParser.java:407)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseMethodSignature(SignatureParser.java:88)
        //     at com.strobel.assembler.metadata.MetadataParser.parseMethodSignature(MetadataParser.java:234)
        //     at com.strobel.assembler.metadata.MetadataParser.parseMethod(MetadataParser.java:166)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupMethod(ClassFileReader.java:1303)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupMethodHandle(ClassFileReader.java:1258)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookup(ClassFileReader.java:1352)
        //     at com.strobel.assembler.ir.MetadataReader.readAttributeCore(MetadataReader.java:306)
        //     at com.strobel.assembler.metadata.ClassFileReader.readAttributeCore(ClassFileReader.java:261)
        //     at com.strobel.assembler.ir.MetadataReader.inflateAttributes(MetadataReader.java:439)
        //     at com.strobel.assembler.metadata.ClassFileReader.visitAttributes(ClassFileReader.java:1134)
        //     at com.strobel.assembler.metadata.ClassFileReader.readClass(ClassFileReader.java:439)
        //     at com.strobel.assembler.metadata.ClassFileReader.readClass(ClassFileReader.java:377)
        //     at com.strobel.assembler.metadata.MetadataSystem.resolveType(MetadataSystem.java:129)
        //     at com.strobel.decompiler.NoRetryMetadataSystem.resolveType(DecompilerDriver.java:476)
        //     at com.strobel.assembler.metadata.MetadataSystem.resolveCore(MetadataSystem.java:81)
        //     at com.strobel.assembler.metadata.MetadataResolver.resolve(MetadataResolver.java:104)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.resolve(CoreMetadataFactory.java:616)
        //     at com.strobel.assembler.metadata.MetadataResolver.resolve(MetadataResolver.java:128)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.resolve(CoreMetadataFactory.java:626)
        //     at com.strobel.assembler.metadata.MethodReference.resolve(MethodReference.java:177)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2438)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:586)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:397)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void parseSkillReducer(final EtcItem item, final Node node) {
        final NamedNodeMap attr = node.getAttributes();
        item.setHandler(this.parseString(attr, "type"));
        this.parseItemSkills(item, node);
    }
    
    private void parseItemAction(final EtcItem item, final Node node) {
        final NamedNodeMap attr = node.getAttributes();
        item.setHandler(this.parseString(attr, "handler"));
    }
    
    private void parseItemAttributes(final EtcItem item, final Node node) {
        this.parseCommonAttributes(item, node);
        final NamedNodeMap attr = node.getAttributes();
        item.setImmediateEffect(this.parseBoolean(attr, "immediate-effect"));
        item.setExImmediateEffect(this.parseBoolean(attr, "ex-immediate-effect"));
        item.setQuestItem(this.parseBoolean(attr, "quest-item"));
        item.setInfinite(this.parseBoolean(attr, "infinite"));
        item.setSelfResurrection(this.parseBoolean(attr, "self-resurrection"));
        item.setAction((ActionType)this.parseEnum(attr, (Class)ActionType.class, "action"));
        item.setAutoUseType((AutoUseType)this.parseEnum(attr, (Class)AutoUseType.class, "auto-use"));
    }
    
    private void parseCommonAttributes(final ItemTemplate item, final Node node) {
        final NamedNodeMap attr = node.getAttributes();
        item.setWeight(this.parseInt(attr, "weight"));
        item.setPrice(this.parseLong(attr, "price"));
        item.setCommissionType((CommissionItemType)this.parseEnum(attr, (Class)CommissionItemType.class, "commission-type", (Enum)CommissionItemType.OTHER_ITEM));
        item.setReuseDelay(this.parseInt(attr, "reuse-delay"));
        item.setReuseGroup(this.parseInt(attr, "reuse-group"));
        item.setDuration(this.parseLong(attr, "duration"));
        item.setForNpc(this.parseBoolean(attr, "for-npc"));
    }
    
    public ItemTemplate getTemplate(final int id) {
        return (ItemTemplate)this.items.get(id);
    }
    
    public Item createItem(final String process, final int itemId, final long count, final Creature actor, final Object reference) {
        final ItemTemplate template = (ItemTemplate)this.items.get(itemId);
        Objects.requireNonNull(template, "The itemId should be a existent template id");
        final Item item = new Item(IdFactory.getInstance().getNextId(), template);
        final CharacterSettings characterSettings = (CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class);
        if (process.equalsIgnoreCase("loot") && !characterSettings.isAutoLoot(itemId)) {
            if (reference instanceof Attackable && ((Attackable)reference).isRaid()) {
                final Attackable raid = (Attackable)reference;
                if (raid.getFirstCommandChannelAttacked() != null && !characterSettings.autoLootRaid()) {
                    item.setOwnerId(raid.getFirstCommandChannelAttacked().getLeaderObjectId());
                    final ScheduledFuture<?> itemLootShedule = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ResetOwner(item), (long)characterSettings.raidLootPrivilegeTime());
                    item.setItemLootShedule(itemLootShedule);
                }
            }
            else if (!characterSettings.autoLoot() || (reference instanceof EventMonster && ((EventMonster)reference).eventDropOnGround())) {
                item.setOwnerId(actor.getObjectId());
                final ScheduledFuture<?> itemLootShedule = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ResetOwner(item), 15000L);
                item.setItemLootShedule(itemLootShedule);
            }
        }
        World.getInstance().addObject(item);
        if (item.isStackable() && count > 1L) {
            item.setCount(count);
        }
        final GeneralSettings generalSettings = (GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class);
        if (generalSettings.logItems() && !process.equals("Reset") && (!generalSettings.smallLogItems() || item.isEquipable() || item.getId() == 57)) {
            ItemEngine.LOGGER_ITEMS.info("CREATE: {}, item {}:+{} {} ({}), Previous count{}, {}", new Object[] { process, item.getObjectId(), item.getEnchantLevel(), item.getTemplate().getName(), item.getCount(), actor, reference });
        }
        this.auditGM(process, itemId, count, actor, reference, item);
        EventDispatcher.getInstance().notifyEventAsync(new OnItemCreate(process, item, actor, reference), item.getTemplate());
        return item;
    }
    
    private void auditGM(final String process, final int itemId, final long count, final Creature actor, final Object reference, final Item item) {
        if (GameUtils.isGM(actor) && ((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).auditGM()) {
            String referenceName = "no-reference";
            if (reference instanceof WorldObject) {
                referenceName = Objects.requireNonNullElse(((WorldObject)reference).getName(), "no-name");
            }
            else if (reference instanceof String) {
                referenceName = reference.toString();
            }
            final String targetName = (actor.getTarget() != null) ? actor.getTarget().getName() : "no-target";
            GMAudit.auditGMAction(actor.toString(), String.format("%s (id: %d count: %d name: %s objectId: %d)", process, itemId, count, item.getName(), item.getObjectId()), targetName, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, referenceName));
        }
    }
    
    public void destroyItem(final String process, final Item item, final Player actor, final Object reference) {
        synchronized (item) {
            final long old = item.getCount();
            item.setItemLocation(ItemLocation.VOID);
            item.setCount(0L);
            item.setOwnerId(0);
            item.setLastChange(3);
            World.getInstance().removeObject(item);
            IdFactory.getInstance().releaseId(item.getObjectId());
            final GeneralSettings generalSettings = (GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class);
            if (generalSettings.logItems() && (!generalSettings.smallLogItems() || item.isEquipable() || item.getId() == 57)) {
                ItemEngine.LOGGER_ITEMS.info("DELETE: {}, item {}:+{} {} ({}), Previous Count ({}), {}, {}", new Object[] { process, item.getObjectId(), item.getEnchantLevel(), item.getTemplate().getName(), item.getCount(), old, actor, reference });
            }
            this.auditGM(process, item.getId(), item.getCount(), actor, reference, item);
            ((PetDAO)DatabaseAccess.getDAO((Class)PetDAO.class)).deleteByItem(item.getObjectId());
        }
    }
    
    public void reload() {
        this.load();
    }
    
    public Collection<ItemTemplate> getAllItems() {
        return (Collection<ItemTemplate>)this.items.values();
    }
    
    public static void init() {
        final ServiceLoader<IItemHandler> load = ServiceLoader.load(IItemHandler.class);
        final ItemHandler instance = ItemHandler.getInstance();
        Objects.requireNonNull(instance);
        load.forEach(instance::registerHandler);
        getInstance().load();
        EnchantItemEngine.init();
        EnchantItemOptionsData.init();
        ItemCrystallizationData.init();
        AugmentationEngine.init();
        VariationData.init();
        EnsoulData.init();
    }
    
    public static ItemEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ItemEngine.class);
        LOGGER_ITEMS = LoggerFactory.getLogger("item");
    }
    
    private static class ResetOwner implements Runnable
    {
        Item _item;
        
        private ResetOwner(final Item item) {
            this._item = item;
        }
        
        @Override
        public void run() {
            this._item.setOwnerId(0);
            this._item.setItemLootShedule(null);
        }
    }
    
    private static class Singleton
    {
        private static final ItemEngine INSTANCE;
        
        static {
            INSTANCE = new ItemEngine();
        }
    }
}
