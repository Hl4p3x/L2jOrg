// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.time.DayOfWeek;
import org.w3c.dom.Document;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import org.l2j.gameserver.model.SiegeScheduleDate;
import java.util.List;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class SiegeScheduleData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final List<SiegeScheduleDate> scheduleData;
    
    private SiegeScheduleData() {
        this.scheduleData = new ArrayList<SiegeScheduleDate>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return Path.of("config/xsd/siege-schedule.xsd", new String[0]);
    }
    
    public synchronized void load() {
        this.scheduleData.clear();
        this.parseFile(new File("config/siege-schedule.xml"));
        SiegeScheduleData.LOGGER.info("Loaded: {}  siege schedulers.", (Object)this.scheduleData.size());
        if (this.scheduleData.isEmpty()) {
            this.scheduleData.add(new SiegeScheduleDate());
            SiegeScheduleData.LOGGER.info("Loaded: default siege schedulers.");
        }
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final NamedNodeMap attrs;
        final DayOfWeek day;
        this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "schedule", scheduleNode -> {
            attrs = scheduleNode.getAttributes();
            day = (DayOfWeek)this.parseEnum(attrs, (Class)DayOfWeek.class, "day", (Enum)DayOfWeek.SUNDAY);
            this.scheduleData.add(new SiegeScheduleDate(day, this.parseInt(attrs, "hour"), this.parseInt(attrs, "max-concurrent")));
        }));
    }
    
    public List<SiegeScheduleDate> getScheduleDates() {
        return this.scheduleData;
    }
    
    public static SiegeScheduleData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SiegeScheduleData.class);
    }
    
    private static class Singleton
    {
        private static final SiegeScheduleData INSTANCE;
        
        static {
            INSTANCE = new SiegeScheduleData();
        }
    }
}
