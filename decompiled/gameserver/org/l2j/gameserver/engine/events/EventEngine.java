// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.events;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.eventengine.EventMethodNotification;
import java.util.Map;
import org.l2j.gameserver.model.StatsSet;
import org.w3c.dom.NodeList;
import org.l2j.gameserver.model.eventengine.conditions.BetweenConditionalScheduler;
import org.l2j.gameserver.model.eventengine.conditions.HaventRunConditionalScheduler;
import java.util.Set;
import org.l2j.gameserver.model.eventengine.IConditionalEventScheduler;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import java.util.LinkedHashSet;
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
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/events/events.xsd");
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
                case "scheduler": {
                    this.parseScheduler(eventManager, child);
                    break;
                }
                case "config": {
                    eventManager.config(this, child);
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
    
    private void parseScheduler(final AbstractEventManager<?> eventManager, final Node innerNode) {
        final Set<EventScheduler> schedulers = new LinkedHashSet<EventScheduler>();
        final Set<IConditionalEventScheduler> conditionalSchedulers = new LinkedHashSet<IConditionalEventScheduler>();
        for (Node node = innerNode.getFirstChild(); Objects.nonNull(node); node = node.getNextSibling()) {
            if ("schedule".equals(node.getNodeName())) {
                schedulers.add(this.parseSchedule(eventManager, node));
            }
            else if ("conditionalSchedule".equals(node.getNodeName())) {
                this.parseConditinalSchedule(eventManager, conditionalSchedulers, node);
            }
        }
        eventManager.setSchedulers(schedulers);
        eventManager.setConditionalSchedulers(conditionalSchedulers);
    }
    
    private void parseConditinalSchedule(final AbstractEventManager<?> eventManager, final Set<IConditionalEventScheduler> conditionalSchedulers, final Node node) {
        for (Node eventNode = node.getFirstChild(); Objects.nonNull(eventNode); eventNode = eventNode.getNextSibling()) {
            if ("run".equals(eventNode.getNodeName())) {
                final String name = this.parseString(eventNode.getAttributes(), "name");
                final String upperCase;
                final String ifType = upperCase = this.parseString(eventNode.getAttributes(), "if", "BETWEEN").toUpperCase();
                switch (upperCase) {
                    case "BETWEEN": {
                        this.parseBetweenSchedule(eventManager, conditionalSchedulers, eventNode, name);
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
    
    private void parseBetweenSchedule(final AbstractEventManager<?> eventManager, final Set<IConditionalEventScheduler> conditionalSchedulers, final Node eventNode, final String name) {
        final NodeList childs = eventNode.getChildNodes();
        if (childs.getLength() != 2) {
            EventEngine.LOGGER.warn("Event: {} has incorrect amount of schedulers expected: 2 found: {}", (Object)eventManager.getName(), (Object)childs.getLength());
        }
        else {
            conditionalSchedulers.add(new BetweenConditionalScheduler(eventManager, name, childs.item(0).getTextContent(), childs.item(1).getTextContent()));
        }
    }
    
    private EventScheduler parseSchedule(final AbstractEventManager<?> eventManager, final Node scheduleNode) {
        final StatsSet params = new StatsSet(this.parseAttributes(scheduleNode));
        final EventScheduler scheduler = new EventScheduler(eventManager, params);
        try {
            scheduler.addEventNotification(new EventMethodNotification(eventManager, params.getString("event")));
        }
        catch (Exception e) {
            EventEngine.LOGGER.warn("Couldn't add event notification for {}", (Object)eventManager.getClass(), (Object)e);
        }
        return scheduler;
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static EventEngine getInstance() {
        return Singleton.INSTANCE;
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
