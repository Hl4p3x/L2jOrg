// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.List;
import org.w3c.dom.Node;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.handler.ConditionHandler;
import org.l2j.gameserver.model.conditions.ICondition;
import org.l2j.gameserver.model.holders.ExtendDropItemHolder;
import java.util.ArrayList;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.holders.ExtendDropDataHolder;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class ExtendDropData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, ExtendDropDataHolder> _extendDrop;
    
    private ExtendDropData() {
        this._extendDrop = new HashMap<Integer, ExtendDropDataHolder>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/ExtendDrop.xsd");
    }
    
    public void load() {
        this._extendDrop.clear();
        this.parseDatapackFile("data/ExtendDrop.xml");
        ExtendDropData.LOGGER.info("Loaded {} ExtendDrop.", (Object)this._extendDrop.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final StatsSet set;
        final List<ExtendDropItemHolder> items;
        final int itemId;
        final int itemCount;
        final int itemMaxCount;
        final double itemChance;
        final double itemAdditionalChance;
        final List<ExtendDropItemHolder> list;
        final List<ICondition> conditions;
        final String conditionName;
        final StatsSet params;
        final Function<StatsSet, ICondition> conditionFunction;
        final List<ICondition> list2;
        final StatsSet set2;
        final Map<Long, SystemMessageId> systemMessages;
        final long amount;
        final SystemMessageId systemMessageId;
        final Map<Long, SystemMessageId> map;
        this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "drop", dropNode -> {
            set = new StatsSet(this.parseAttributes(dropNode));
            items = new ArrayList<ExtendDropItemHolder>(1);
            this.forEach(dropNode, "items", itemsNode -> this.forEach(itemsNode, "item", itemNode -> {
                itemId = this.parseInteger(itemNode.getAttributes(), "id");
                itemCount = this.parseInteger(itemNode.getAttributes(), "count");
                itemMaxCount = this.parseInteger(itemNode.getAttributes(), "maxCount");
                itemChance = this.parseDouble(itemNode.getAttributes(), "chance");
                itemAdditionalChance = this.parseDouble(itemNode.getAttributes(), "additionalChance");
                list.add(new ExtendDropItemHolder(itemId, itemCount, itemMaxCount, itemChance, itemAdditionalChance));
            }));
            set.set("items", items);
            conditions = new ArrayList<ICondition>(1);
            this.forEach(dropNode, "conditions", conditionsNode -> this.forEach(conditionsNode, "condition", conditionNode -> {
                conditionName = this.parseString(conditionNode.getAttributes(), "name");
                params = (StatsSet)this.parseValue(conditionNode);
                conditionFunction = ConditionHandler.getInstance().getHandlerFactory(conditionName);
                if (conditionFunction != null) {
                    list2.add(conditionFunction.apply(params));
                }
                else {
                    ExtendDropData.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, set2.getInt("id"), conditionName));
                }
            }));
            set.set("conditions", conditions);
            systemMessages = new HashMap<Long, SystemMessageId>();
            this.forEach(dropNode, "systemMessages", systemMessagesNode -> this.forEach(systemMessagesNode, "systemMessage", systemMessageNode -> {
                amount = this.parseLong(systemMessageNode.getAttributes(), "amount");
                systemMessageId = SystemMessageId.getSystemMessageId(this.parseInteger(systemMessageNode.getAttributes(), "id"));
                map.put(amount, systemMessageId);
            }));
            set.set("systemMessages", systemMessages);
            this._extendDrop.put(set.getInt("id"), new ExtendDropDataHolder(set));
        }));
    }
    
    private Object parseValue(Node node) {
        StatsSet statsSet = null;
        List<Object> list = null;
        Object text = null;
        String nodeName;
        String nodeName2;
        int n;
        String value;
        Object value2;
        for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
            nodeName = node.getNodeName();
            nodeName2 = node.getNodeName();
            n = -1;
            switch (nodeName2.hashCode()) {
                case 35879888: {
                    if (nodeName2.equals("#text")) {
                        n = 0;
                        break;
                    }
                    break;
                }
                case 3242771: {
                    if (nodeName2.equals("item")) {
                        n = 1;
                        break;
                    }
                    break;
                }
            }
            switch (n) {
                case 0: {
                    value = node.getNodeValue().trim();
                    if (!value.isEmpty()) {
                        text = value;
                        break;
                    }
                    break;
                }
                case 1: {
                    if (list == null) {
                        list = new LinkedList<Object>();
                    }
                    value2 = this.parseValue(node);
                    if (value2 != null) {
                        list.add(value2);
                        break;
                    }
                    break;
                }
                default: {
                    value2 = this.parseValue(node);
                    if (value2 != null) {
                        if (statsSet == null) {
                            statsSet = new StatsSet();
                        }
                        statsSet.set(nodeName, value2);
                        break;
                    }
                    break;
                }
            }
        }
        if (list != null) {
            if (text != null) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Lorg/w3c/dom/Node;)Ljava/lang/String;, node));
            }
            if (statsSet == null) {
                return list;
            }
            statsSet.set(".", list);
        }
        if (text != null) {
            if (list != null) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Lorg/w3c/dom/Node;)Ljava/lang/String;, node));
            }
            if (statsSet == null) {
                return text;
            }
            statsSet.set(".", text);
        }
        return statsSet;
    }
    
    public ExtendDropDataHolder getExtendDropById(final int id) {
        return this._extendDrop.getOrDefault(id, null);
    }
    
    public static ExtendDropData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ExtendDropData.class);
    }
    
    private static class Singleton
    {
        private static final ExtendDropData INSTANCE;
        
        static {
            INSTANCE = new ExtendDropData();
        }
    }
}
