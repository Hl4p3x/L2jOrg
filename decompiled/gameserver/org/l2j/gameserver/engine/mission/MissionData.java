// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.mission;

import org.slf4j.LoggerFactory;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.Collection;
import org.w3c.dom.Node;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.ArrayList;
import java.util.Map;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.CHashIntMap;
import java.util.List;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class MissionData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final IntMap<IntMap<MissionPlayerData>> missionsData;
    private final IntMap<List<MissionDataHolder>> missions;
    private boolean available;
    
    private MissionData() {
        this.missionsData = (IntMap<IntMap<MissionPlayerData>>)new CHashIntMap();
        this.missions = (IntMap<List<MissionDataHolder>>)new HashIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/mission.xsd");
    }
    
    public void load() {
        this.missions.clear();
        this.parseDatapackFile("data/mission.xml");
        this.available = !this.missions.isEmpty();
        MissionData.LOGGER.info("Loaded {} missions.", (Object)this.missions.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final StatsSet set;
        final List<ItemHolder> items;
        final int itemId;
        final int itemCount;
        final List<ItemHolder> list;
        final StatsSet set2;
        final StatsSet params;
        final MissionDataHolder holder;
        this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "mission", missionNode -> {
            set = new StatsSet(this.parseAttributes(missionNode));
            items = new ArrayList<ItemHolder>(1);
            this.forEach(missionNode, "reward", itemNode -> {
                itemId = this.parseInteger(itemNode.getAttributes(), "id");
                itemCount = this.parseInteger(itemNode.getAttributes(), "count");
                list.add(new ItemHolder(itemId, itemCount));
                return;
            });
            set.set("rewards", items);
            this.forEach(missionNode, "classes", classesNode -> set.set("classRestriction", classesNode.getTextContent()));
            set.set("handler", "");
            set.set("params", StatsSet.EMPTY_STATSET);
            this.forEach(missionNode, "handler", handlerNode -> {
                set2.set("handler", this.parseString(handlerNode.getAttributes(), "name"));
                params = new StatsSet();
                set2.set("params", params);
                this.forEach(handlerNode, "param", paramNode -> params.set(this.parseString(paramNode.getAttributes(), "name"), paramNode.getTextContent()));
                return;
            });
            holder = new MissionDataHolder(set);
            ((List)this.missions.computeIfAbsent(holder.getId(), k -> new ArrayList())).add(holder);
        }));
    }
    
    public Collection<MissionDataHolder> getMissions() {
        return this.missions.values().stream().flatMap((Function<? super Object, ? extends Stream<?>>)Collection::stream).collect((Collector<? super Object, ?, Collection<MissionDataHolder>>)Collectors.toList());
    }
    
    public Collection<MissionDataHolder> getMissions(final Player player) {
        return this.missions.values().stream().flatMap((Function<? super Object, ? extends Stream<?>>)Collection::stream).filter(o -> o.isDisplayable(player)).collect((Collector<? super Object, ?, Collection<MissionDataHolder>>)Collectors.toList());
    }
    
    public boolean isCompleted(final Player player, final int missionId) {
        final List<MissionDataHolder> mission = (List<MissionDataHolder>)this.missions.get(missionId);
        return Objects.nonNull(mission) && mission.stream().anyMatch(data -> data.isCompleted(player));
    }
    
    public int getAvailableMissionCount(final Player player) {
        return (int)this.missions.values().stream().flatMap(Collection::stream).filter(mission -> mission.isAvailable(player)).count();
    }
    
    public Collection<MissionDataHolder> getMissions(final int id) {
        return (Collection<MissionDataHolder>)this.missions.get(id);
    }
    
    public void clearMissionData(final int id) {
        this.missionsData.values().forEach(map -> map.remove(id));
    }
    
    public void storeMissionData(final int missionId, final MissionPlayerData data) {
        if (Objects.nonNull(data)) {
            ((IntMap)this.missionsData.computeIfAbsent(data.getObjectId(), id -> new CHashIntMap())).putIfAbsent(missionId, (Object)data);
        }
    }
    
    public IntMap<MissionPlayerData> getStoredMissionData(final Player player) {
        return (IntMap<MissionPlayerData>)this.missionsData.computeIfAbsent(player.getObjectId(), id -> new CHashIntMap());
    }
    
    public boolean isAvailable() {
        return this.available;
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static MissionData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)MissionData.class);
    }
    
    private static class Singleton
    {
        private static final MissionData INSTANCE;
        
        static {
            INSTANCE = new MissionData();
        }
    }
}
