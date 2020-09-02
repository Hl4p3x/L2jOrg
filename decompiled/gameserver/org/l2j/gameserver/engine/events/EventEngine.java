// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.events;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.eventengine.drop.NormalDrop;
import org.l2j.gameserver.model.eventengine.drop.EventDropItem;
import org.l2j.gameserver.model.eventengine.drop.EventDropGroup;
import org.l2j.gameserver.model.eventengine.drop.GroupedDrop;
import org.l2j.gameserver.model.eventengine.drop.EventDrops;
import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.model.eventengine.drop.IEventDrop;
import java.util.List;
import java.util.Set;
import org.l2j.gameserver.model.eventengine.conditions.HaventRunConditionalScheduler;
import org.l2j.gameserver.model.eventengine.conditions.BetweenConditionalScheduler;
import org.l2j.gameserver.model.eventengine.EventMethodNotification;
import java.util.ArrayList;
import org.l2j.gameserver.model.eventengine.IConditionalEventScheduler;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;
import org.l2j.gameserver.model.StatsSet;
import java.util.LinkedHashMap;
import java.lang.reflect.Method;
import org.w3c.dom.NamedNodeMap;
import java.util.Objects;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import java.lang.reflect.Modifier;
import org.w3c.dom.Node;
import java.util.function.Consumer;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class EventEngine extends GameXmlReader
{
    private static final Logger LOGGER;
    
    private EventEngine() {
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/events.xsd");
    }
    
    public void load() {
        this.parseDatapackDirectory("data/events", true);
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "event", (Consumer)this::parseEvent));
    }
    
    private void parseEvent(final Node eventNode) {
        final NamedNodeMap attr = eventNode.getAttributes();
        final String eventName = this.parseString(attr, "name");
        final String className = this.parseString(attr, "class");
        AbstractEventManager<?> eventManager = null;
        try {
            final Class<?> clazz = Class.forName(className);
            for (final Method method : clazz.getMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && AbstractEventManager.class.isAssignableFrom(method.getReturnType()) && method.getParameterCount() == 0) {
                    eventManager = (AbstractEventManager<?>)method.invoke(null, new Object[0]);
                    break;
                }
            }
            if (eventManager == null) {
                throw new NoSuchMethodError("Couldn't method that gives instance of AbstractEventManager!");
            }
        }
        catch (Exception e) {
            EventEngine.LOGGER.warn("Couldn't locate event manager {} instance for event: {}!", new Object[] { className, eventName, e });
            return;
        }
        for (Node child = eventNode.getFirstChild(); Objects.nonNull(child); child = child.getNextSibling()) {
            final String nodeName = child.getNodeName();
            switch (nodeName) {
                case "variables": {
                    this.parseVariables(eventManager, child);
                    break;
                }
                case "scheduler": {
                    this.parseScheduler(eventManager, child);
                    break;
                }
                case "rewards": {
                    this.parseRewards(eventManager, child);
                    break;
                }
            }
        }
        eventManager.setName(eventName);
        eventManager.onInitialized();
        eventManager.startScheduler();
        eventManager.startConditionalSchedulers();
        EventEngine.LOGGER.info("{}:[{}] Initialized", (Object)eventName, (Object)eventManager.getClass().getSimpleName());
    }
    
    private void parseVariables(final AbstractEventManager<?> eventManager, final Node innerNode) {
        final StatsSet variables = new StatsSet((Supplier<Map<String, Object>>)LinkedHashMap::new);
        for (Node variableNode = innerNode.getFirstChild(); variableNode != null; variableNode = variableNode.getNextSibling()) {
            if ("variable".equals(variableNode.getNodeName())) {
                variables.set(this.parseString(variableNode.getAttributes(), "name"), this.parseString(variableNode.getAttributes(), "value"));
            }
            else if ("list".equals(variableNode.getNodeName())) {
                this.parseListVariables(eventManager, variables, variableNode);
            }
            else if ("map".equals(variableNode.getNodeName())) {
                this.parseMapVariables(eventManager, variables, variableNode);
            }
        }
        eventManager.setVariables(variables);
    }
    
    private void parseScheduler(final AbstractEventManager<?> eventManager, final Node innerNode) {
        final Set<EventScheduler> schedulers = new LinkedHashSet<EventScheduler>();
        final Set<IConditionalEventScheduler> conditionalSchedulers = new LinkedHashSet<IConditionalEventScheduler>();
        for (Node scheduleNode = innerNode.getFirstChild(); scheduleNode != null; scheduleNode = scheduleNode.getNextSibling()) {
            if ("schedule".equals(scheduleNode.getNodeName())) {
                final StatsSet params = new StatsSet((Supplier<Map<String, Object>>)LinkedHashMap::new);
                final NamedNodeMap attrs = scheduleNode.getAttributes();
                for (int i = 0; i < attrs.getLength(); ++i) {
                    final Node node = attrs.item(i);
                    params.set(node.getNodeName(), node.getNodeValue());
                }
                final EventScheduler scheduler = new EventScheduler(eventManager, params);
                for (Node eventNode = scheduleNode.getFirstChild(); eventNode != null; eventNode = eventNode.getNextSibling()) {
                    if ("event".equals(eventNode.getNodeName())) {
                        String methodName = this.parseString(eventNode.getAttributes(), "name");
                        if (methodName.charAt(0) == '#') {
                            methodName = methodName.substring(1);
                        }
                        final List<Object> args = new ArrayList<Object>();
                        for (Node argsNode = eventNode.getFirstChild(); argsNode != null; argsNode = argsNode.getNextSibling()) {
                            if ("arg".equals(argsNode.getNodeName())) {
                                final String type = this.parseString(argsNode.getAttributes(), "type");
                                final Object value = this.parseObject(eventManager, type, argsNode.getTextContent());
                                if (value != null) {
                                    args.add(value);
                                }
                            }
                        }
                        try {
                            scheduler.addEventNotification(new EventMethodNotification(eventManager, methodName, args));
                        }
                        catch (Exception e) {
                            EventEngine.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), eventManager.getClass().getSimpleName()), (Throwable)e);
                        }
                    }
                }
                schedulers.add(scheduler);
            }
            else if ("conditionalSchedule".equals(scheduleNode.getNodeName())) {
                final StatsSet params = new StatsSet((Supplier<Map<String, Object>>)LinkedHashMap::new);
                final NamedNodeMap attrs = scheduleNode.getAttributes();
                for (int i = 0; i < attrs.getLength(); ++i) {
                    final Node node = attrs.item(i);
                    params.set(node.getNodeName(), node.getNodeValue());
                }
                for (Node eventNode2 = scheduleNode.getFirstChild(); eventNode2 != null; eventNode2 = eventNode2.getNextSibling()) {
                    if ("run".equals(eventNode2.getNodeName())) {
                        final String name = this.parseString(eventNode2.getAttributes(), "name");
                        final String upperCase;
                        final String ifType = upperCase = this.parseString(eventNode2.getAttributes(), "if", "BETWEEN").toUpperCase();
                        switch (upperCase) {
                            case "BETWEEN": {
                                final List<String> names = new ArrayList<String>(2);
                                for (Node innerData = eventNode2.getFirstChild(); innerData != null; innerData = innerData.getNextSibling()) {
                                    if ("name".equals(innerData.getNodeName())) {
                                        names.add(innerData.getTextContent());
                                    }
                                }
                                if (names.size() != 2) {
                                    EventEngine.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/util/List;I)Ljava/lang/String;, eventManager.getClass().getSimpleName(), names, names.size()));
                                    break;
                                }
                                conditionalSchedulers.add(new BetweenConditionalScheduler(eventManager, name, names.get(0), names.get(1)));
                                break;
                            }
                            case "HAVENT_RUN": {
                                conditionalSchedulers.add(new HaventRunConditionalScheduler(eventManager, name));
                                break;
                            }
                        }
                    }
                }
            }
        }
        eventManager.setSchedulers(schedulers);
        eventManager.setConditionalSchedulers(conditionalSchedulers);
    }
    
    private void parseRewards(final AbstractEventManager<?> eventManager, final Node innerNode) {
        final Map<String, IEventDrop> rewards = new LinkedHashMap<String, IEventDrop>();
        String name;
        EventDrops dropType;
        GroupedDrop droplist;
        EventDropGroup group;
        NamedNodeMap attrs;
        int id;
        int min;
        int max;
        double chance;
        final EventDropGroup eventDropGroup;
        final Map<String, GroupedDrop> map;
        NormalDrop droplist2;
        NamedNodeMap attrs2;
        int id2;
        int min2;
        int max2;
        double chance2;
        final NormalDrop normalDrop;
        this.forEach(innerNode, x$0 -> XmlReader.isNode(x$0), rewardsNode -> {
            if ("reward".equalsIgnoreCase(rewardsNode.getNodeName())) {
                name = this.parseString(rewardsNode.getAttributes(), "name");
                dropType = (EventDrops)this.parseEnum(rewardsNode.getAttributes(), (Class)EventDrops.class, "type");
                switch (dropType) {
                    case GROUPED: {
                        droplist = dropType.newInstance();
                        this.forEach(rewardsNode, "group", groupsNode -> {
                            group = new EventDropGroup(this.parseDouble(groupsNode.getAttributes(), "chance"));
                            this.forEach(groupsNode, "item", itemNode -> {
                                attrs = itemNode.getAttributes();
                                id = this.parseInteger(attrs, "id");
                                min = this.parseInteger(attrs, "min");
                                max = this.parseInteger(attrs, "max");
                                chance = this.parseDouble(attrs, "chance");
                                eventDropGroup.addItem(new EventDropItem(id, min, max, chance));
                            });
                            return;
                        });
                        map.put(name, droplist);
                        break;
                    }
                    case NORMAL: {
                        droplist2 = dropType.newInstance();
                        this.forEach(rewardsNode, "item", itemNode -> {
                            attrs2 = itemNode.getAttributes();
                            id2 = this.parseInteger(attrs2, "id");
                            min2 = this.parseInteger(attrs2, "min");
                            max2 = this.parseInteger(attrs2, "max");
                            chance2 = this.parseDouble(attrs2, "chance");
                            normalDrop.addItem(new EventDropItem(id2, min2, max2, chance2));
                            return;
                        });
                        map.put(name, (GroupedDrop)droplist2);
                        break;
                    }
                }
            }
            return;
        });
        eventManager.setRewards(rewards);
    }
    
    private void parseListVariables(final AbstractEventManager<?> eventManager, final StatsSet variables, final Node variableNode) {
        final String name = this.parseString(variableNode.getAttributes(), "name");
        final String type = this.parseString(variableNode.getAttributes(), "type");
        final Class<?> classType = this.getClassByName(eventManager, type);
        final List<?> values = newList(classType);
        final String s = type;
        switch (s) {
            case "Byte":
            case "Short":
            case "Integer":
            case "Float":
            case "Long":
            case "Double":
            case "String": {
                for (Node stringNode = variableNode.getFirstChild(); stringNode != null; stringNode = stringNode.getNextSibling()) {
                    if ("value".equals(stringNode.getNodeName())) {
                        values.add(this.parseObject(eventManager, type, stringNode.getTextContent()));
                    }
                }
                break;
            }
            case "ItemHolder": {
                for (Node stringNode = variableNode.getFirstChild(); stringNode != null; stringNode = stringNode.getNextSibling()) {
                    if ("item".equals(stringNode.getNodeName())) {
                        values.add(new ItemHolder(this.parseInteger(stringNode.getAttributes(), "id"), this.parseLong(stringNode.getAttributes(), "count", Long.valueOf(1L))));
                    }
                }
                break;
            }
            case "SkillHolder": {
                for (Node stringNode = variableNode.getFirstChild(); stringNode != null; stringNode = stringNode.getNextSibling()) {
                    if ("skill".equals(stringNode.getNodeName())) {
                        values.add(new SkillHolder(this.parseInteger(stringNode.getAttributes(), "id"), this.parseInteger(stringNode.getAttributes(), "level", Integer.valueOf(1))));
                    }
                }
                break;
            }
            case "Location": {
                for (Node stringNode = variableNode.getFirstChild(); stringNode != null; stringNode = stringNode.getNextSibling()) {
                    if ("location".equals(stringNode.getNodeName())) {
                        values.add(new Location(this.parseInteger(stringNode.getAttributes(), "x"), this.parseInteger(stringNode.getAttributes(), "y"), this.parseInteger(stringNode.getAttributes(), "z", this.parseInteger(stringNode.getAttributes(), "heading", Integer.valueOf(0)))));
                    }
                }
                break;
            }
            default: {
                EventEngine.LOGGER.info("Unhandled list case: {} for event: {}", (Object)type, (Object)eventManager.getClass().getSimpleName());
                break;
            }
        }
        variables.set(name, values);
    }
    
    private void parseMapVariables(final AbstractEventManager<?> eventManager, final StatsSet variables, final Node variableNode) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_3         /* variableNode */
        //     2: invokeinterface org/w3c/dom/Node.getAttributes:()Lorg/w3c/dom/NamedNodeMap;
        //     7: ldc             "name"
        //     9: invokevirtual   org/l2j/gameserver/engine/events/EventEngine.parseString:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)Ljava/lang/String;
        //    12: astore          name
        //    14: aload_0         /* this */
        //    15: aload_3         /* variableNode */
        //    16: invokeinterface org/w3c/dom/Node.getAttributes:()Lorg/w3c/dom/NamedNodeMap;
        //    21: ldc_w           "keyType"
        //    24: invokevirtual   org/l2j/gameserver/engine/events/EventEngine.parseString:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)Ljava/lang/String;
        //    27: astore          keyType
        //    29: aload_0         /* this */
        //    30: aload_3         /* variableNode */
        //    31: invokeinterface org/w3c/dom/Node.getAttributes:()Lorg/w3c/dom/NamedNodeMap;
        //    36: ldc_w           "valueType"
        //    39: invokevirtual   org/l2j/gameserver/engine/events/EventEngine.parseString:(Lorg/w3c/dom/NamedNodeMap;Ljava/lang/String;)Ljava/lang/String;
        //    42: astore          valueType
        //    44: aload_0         /* this */
        //    45: aload_1         /* eventManager */
        //    46: aload           keyType
        //    48: invokevirtual   org/l2j/gameserver/engine/events/EventEngine.getClassByName:(Lorg/l2j/gameserver/model/eventengine/AbstractEventManager;Ljava/lang/String;)Ljava/lang/Class;
        //    51: astore          keyClass
        //    53: aload_0         /* this */
        //    54: aload_1         /* eventManager */
        //    55: aload           valueType
        //    57: invokevirtual   org/l2j/gameserver/engine/events/EventEngine.getClassByName:(Lorg/l2j/gameserver/model/eventengine/AbstractEventManager;Ljava/lang/String;)Ljava/lang/Class;
        //    60: astore          valueClass
        //    62: aload           keyClass
        //    64: aload           valueClass
        //    66: invokestatic    org/l2j/gameserver/engine/events/EventEngine.newMap:(Ljava/lang/Class;Ljava/lang/Class;)Ljava/util/Map;
        //    69: astore          map
        //    71: aload_0         /* this */
        //    72: aload_3         /* variableNode */
        //    73: invokedynamic   BootstrapMethod #6, test:()Ljava/util/function/Predicate;
        //    78: aload_0         /* this */
        //    79: aload           map
        //    81: aload_1         /* eventManager */
        //    82: aload           keyType
        //    84: aload           valueType
        //    86: aload           name
        //    88: invokedynamic   BootstrapMethod #7, accept:(Lorg/l2j/gameserver/engine/events/EventEngine;Ljava/util/Map;Lorg/l2j/gameserver/model/eventengine/AbstractEventManager;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/util/function/Consumer;
        //    93: invokevirtual   org/l2j/gameserver/engine/events/EventEngine.forEach:(Lorg/w3c/dom/Node;Ljava/util/function/Predicate;Ljava/util/function/Consumer;)V
        //    96: aload_2         /* variables */
        //    97: aload           name
        //    99: aload           map
        //   101: invokevirtual   org/l2j/gameserver/model/StatsSet.set:(Ljava/lang/String;Ljava/lang/Object;)Lorg/l2j/gameserver/model/StatsSet;
        //   104: pop            
        //   105: return         
        //    MethodParameters:
        //  Name          Flags  
        //  ------------  -----
        //  eventManager  
        //  variables     
        //  variableNode  
        //    Signature:
        //  (Lorg/l2j/gameserver/model/eventengine/AbstractEventManager<*>;Lorg/l2j/gameserver/model/StatsSet;Lorg/w3c/dom/Node;)V
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
    
    private Class<?> getClassByName(final AbstractEventManager<?> eventManager, final String name) {
        switch (name) {
            case "Byte": {
                return Byte.class;
            }
            case "Short": {
                return Short.class;
            }
            case "Integer": {
                return Integer.class;
            }
            case "Float": {
                return Float.class;
            }
            case "Long": {
                return Long.class;
            }
            case "Double": {
                return Double.class;
            }
            case "String": {
                return String.class;
            }
            case "ItemHolder": {
                return ItemHolder.class;
            }
            case "SkillHolder": {
                return SkillHolder.class;
            }
            case "Location": {
                return Location.class;
            }
            default: {
                EventEngine.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, eventManager.getClass().getSimpleName()));
                return Object.class;
            }
        }
    }
    
    private Object parseObject(final AbstractEventManager<?> eventManager, final String type, final String value) {
        switch (type) {
            case "Byte": {
                return Byte.decode(value);
            }
            case "Short": {
                return Short.decode(value);
            }
            case "Integer": {
                return Integer.decode(value);
            }
            case "Float": {
                return Float.parseFloat(value);
            }
            case "Long": {
                return Long.decode(value);
            }
            case "Double": {
                return Double.parseDouble(value);
            }
            case "String": {
                return value;
            }
            default: {
                EventEngine.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, type, eventManager.getClass().getSimpleName()));
                return null;
            }
        }
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static EventEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static <T> List<T> newList(final Class<T> type) {
        return new ArrayList<T>();
    }
    
    private static <K, V> Map<K, V> newMap(final Class<K> keyClass, final Class<V> valueClass) {
        return new LinkedHashMap<K, V>();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)EventEngine.class);
    }
    
    private static class Singleton
    {
        private static final EventEngine INSTANCE;
        
        static {
            INSTANCE = new EventEngine();
        }
    }
}
