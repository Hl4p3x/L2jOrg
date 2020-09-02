// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.quest;

import org.l2j.gameserver.model.Location;
import java.util.Objects;
import org.w3c.dom.NamedNodeMap;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.data.database.announce.Announce;
import org.l2j.gameserver.data.database.announce.manager.AnnouncementsManager;
import org.l2j.gameserver.data.database.announce.EventAnnouncement;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.instancemanager.EventShrineManager;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.datatables.drop.EventDropList;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import io.github.joealisson.primitive.HashIntSet;
import java.util.LinkedList;
import java.util.ArrayList;
import org.l2j.commons.util.DateRange;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.datatables.drop.EventDropHolder;
import java.util.List;
import org.slf4j.Logger;

public class LongTimeEvent extends Quest
{
    private static final Logger LOGGER;
    private final List<NpcSpawn> spawnList;
    private final List<EventDropHolder> dropList;
    private final IntSet itemsToDestroy;
    private String name;
    private String startMessage;
    private String endMessage;
    private int enterAnnounceId;
    private DateRange period;
    private boolean enableShrines;
    
    protected LongTimeEvent() {
        super(-1);
        this.spawnList = new ArrayList<NpcSpawn>();
        this.dropList = new LinkedList<EventDropHolder>();
        this.itemsToDestroy = (IntSet)new HashIntSet();
        this.enterAnnounceId = -1;
        this.period = DateRange.STARTED_DAY;
        this.enableShrines = false;
        final EventParser parser = new EventParser();
        parser.load();
        final LocalDateTime today = LocalDateTime.now();
        if (this.period.isWithinRange(today)) {
            this.startEvent();
        }
        else if (this.period.isAfter(today)) {
            ThreadPool.schedule(this::startEvent, this.period.secondsToStart(today), TimeUnit.SECONDS);
            LongTimeEvent.LOGGER.info("Event {} will be started at {}", (Object)this.name, (Object)this.period.getStartDate());
        }
        else {
            this.destroyItemsOnEnd();
            LongTimeEvent.LOGGER.info("Event {} has passed... Ignored ", (Object)this.name);
        }
    }
    
    protected void startEvent() {
        LongTimeEvent.LOGGER.info("Event {} active until {}", (Object)this.name, (Object)this.period.getEndDate());
        this.dropList.forEach(drop -> EventDropList.getInstance().addGlobalDrop(drop, this.period));
        final long eventEnd = this.period.millisToEnd();
        this.spawnList.forEach(spawn -> AbstractScript.addSpawn(spawn.npcId, spawn.loc.getX(), spawn.loc.getY(), spawn.loc.getZ(), spawn.loc.getHeading(), false, eventEnd, false));
        if (this.enableShrines) {
            EventShrineManager.getInstance().setEnabled(true);
        }
        if (Util.isNotEmpty(this.startMessage)) {
            Broadcast.toAllOnlinePlayers(this.startMessage);
            final EventAnnouncement announce = new EventAnnouncement(this.period, this.startMessage);
            AnnouncementsManager.getInstance().addAnnouncement(announce);
            this.enterAnnounceId = announce.getId();
        }
        ThreadPool.schedule((Runnable)new ScheduleEnd(), eventEnd);
    }
    
    private void destroyItemsOnEnd() {
        this.itemsToDestroy.forEach(itemId -> {
            World.getInstance().forEachPlayer(player -> player.destroyItemByItemId(this.name, itemId, -1L, player, true));
            ((ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class)).deleteAllItemsById(itemId);
        });
    }
    
    public boolean isEventPeriod() {
        return this.period.isWithinRange(LocalDateTime.now());
    }
    
    protected String getConfigPath() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getPath());
    }
    
    @Override
    public String getHtml(final Player player, String fileName) {
        if (!fileName.startsWith("data/")) {
            fileName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, fileName);
        }
        return super.getHtml(player, fileName);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)LongTimeEvent.class);
    }
    
    private class EventParser extends GameXmlReader
    {
        protected Path getSchemaFilePath() {
            return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/eventConfig.xsd");
        }
        
        public void load() {
            this.parseDatapackFile(LongTimeEvent.this.getConfigPath());
            this.releaseResources();
        }
        
        public void parseDocument(final Document doc, final File f) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: aload_1         /* doc */
            //     2: ldc             "event"
            //     4: aload_0         /* this */
            //     5: invokedynamic   BootstrapMethod #0, accept:(Lorg/l2j/gameserver/model/quest/LongTimeEvent$EventParser;)Ljava/util/function/Consumer;
            //    10: invokevirtual   org/l2j/gameserver/model/quest/LongTimeEvent$EventParser.forEach:(Lorg/w3c/dom/Node;Ljava/lang/String;Ljava/util/function/Consumer;)V
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
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:576)
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
        
        private void parseSpawns(final Node node) {
            final int npcId;
            this.forEach(node, "spawn", spawnNode -> {
                npcId = this.parseInt(spawnNode.getAttributes(), "npc");
                if (!NpcData.getInstance().existsNpc(npcId)) {
                    LongTimeEvent.LOGGER.warn("{} event: Npc Id {} not found", (Object)LongTimeEvent.this.getScriptName(), (Object)npcId);
                }
                else {
                    LongTimeEvent.this.spawnList.add(new NpcSpawn(npcId, this.parseLocation(spawnNode)));
                }
            });
        }
        
        private void parseDrop(final Node node) {
            final NamedNodeMap attrs;
            final int id;
            final int min;
            final int max;
            final double chance;
            final int minLevel;
            final int maxLevel;
            final IntSet monsters;
            this.forEach(node, "item", itemNode -> {
                attrs = itemNode.getAttributes();
                id = this.parseInt(attrs, "id");
                min = this.parseInt(attrs, "min");
                max = this.parseInt(attrs, "max");
                chance = this.parseDouble(attrs, "chance");
                minLevel = this.parseInt(attrs, "min-level");
                maxLevel = this.parseInt(attrs, "max-level");
                monsters = this.parseIntSet(attrs, "monsters");
                LongTimeEvent.this.dropList.add(new EventDropHolder(id, min, max, chance, minLevel, maxLevel, (IntCollection)monsters));
            });
        }
    }
    
    protected static class NpcSpawn
    {
        protected final Location loc;
        protected final int npcId;
        
        protected NpcSpawn(final int pNpcId, final Location spawnLoc) {
            this.loc = spawnLoc;
            this.npcId = pNpcId;
        }
    }
    
    protected class ScheduleEnd implements Runnable
    {
        @Override
        public void run() {
            if (LongTimeEvent.this.enableShrines) {
                EventShrineManager.getInstance().setEnabled(false);
            }
            LongTimeEvent.this.destroyItemsOnEnd();
            if (Util.isNotEmpty(LongTimeEvent.this.endMessage)) {
                Broadcast.toAllOnlinePlayers(LongTimeEvent.this.endMessage);
            }
            if (LongTimeEvent.this.enterAnnounceId != -1) {
                AnnouncementsManager.getInstance().deleteAnnouncement(LongTimeEvent.this.enterAnnounceId);
            }
        }
    }
}
