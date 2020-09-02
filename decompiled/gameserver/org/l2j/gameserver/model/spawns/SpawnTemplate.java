// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.spawns;

import java.util.Iterator;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.function.Predicate;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.quest.Quest;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import org.l2j.gameserver.world.zone.type.BannedSpawnTerritory;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;
import java.util.List;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.interfaces.IParameterized;
import org.l2j.gameserver.model.interfaces.ITerritorized;

public class SpawnTemplate implements Cloneable, ITerritorized, IParameterized<StatsSet>
{
    private final String _name;
    private final String _ai;
    private final boolean _spawnByDefault;
    private final String filePath;
    private final List<SpawnGroup> groups;
    private List<SpawnTerritory> _territories;
    private List<BannedSpawnTerritory> _bannedTerritories;
    private StatsSet _parameters;
    
    public SpawnTemplate(final StatsSet set, final String file) {
        this(set.getString("name", null), set.getString("ai", null), set.getBoolean("spawnByDefault", true), file);
    }
    
    private SpawnTemplate(final String name, final String ai, final boolean spawnByDefault, final String file) {
        this.groups = new LinkedList<SpawnGroup>();
        this._name = name;
        this._ai = ai;
        this._spawnByDefault = spawnByDefault;
        this.filePath = file;
    }
    
    public String getName() {
        return this._name;
    }
    
    public String getAI() {
        return this._ai;
    }
    
    public boolean isSpawningByDefault() {
        return this._spawnByDefault;
    }
    
    public String getFilePath() {
        return this.filePath;
    }
    
    @Override
    public void addTerritory(final SpawnTerritory territory) {
        if (this._territories == null) {
            this._territories = new ArrayList<SpawnTerritory>();
        }
        this._territories.add(territory);
    }
    
    @Override
    public List<SpawnTerritory> getTerritories() {
        return (this._territories != null) ? this._territories : Collections.emptyList();
    }
    
    @Override
    public void addBannedTerritory(final BannedSpawnTerritory territory) {
        if (this._bannedTerritories == null) {
            this._bannedTerritories = new ArrayList<BannedSpawnTerritory>();
        }
        this._bannedTerritories.add(territory);
    }
    
    @Override
    public List<BannedSpawnTerritory> getBannedTerritories() {
        return (this._bannedTerritories != null) ? this._bannedTerritories : Collections.emptyList();
    }
    
    public void addGroup(final SpawnGroup group) {
        this.groups.add(group);
    }
    
    public List<SpawnGroup> getGroups() {
        return this.groups;
    }
    
    public List<SpawnGroup> getGroupsByName(final String name) {
        return this.groups.stream().filter(group -> String.valueOf(group.getName()).equalsIgnoreCase(name)).collect((Collector<? super Object, ?, List<SpawnGroup>>)Collectors.toList());
    }
    
    @Override
    public StatsSet getParameters() {
        return this._parameters;
    }
    
    @Override
    public void setParameters(final StatsSet parameters) {
        this._parameters = parameters;
    }
    
    public void notifyEvent(final Consumer<Quest> event) {
        if (this._ai != null) {
            final Quest script = QuestManager.getInstance().getQuest(this._ai);
            if (script != null) {
                event.accept(script);
            }
        }
    }
    
    public void spawn(final Predicate<SpawnGroup> groupFilter, final Instance instance) {
        this.groups.parallelStream().filter((Predicate<? super Object>)groupFilter).forEach(group -> group.spawnAll(instance));
    }
    
    public void spawnAll(final Instance instance) {
        this.spawn(SpawnGroup::isSpawningByDefault, instance);
    }
    
    public void notifyActivate() {
        this.notifyEvent(script -> script.onSpawnActivate(this));
    }
    
    public void spawnAllIncludingNotDefault(final Instance instance) {
        this.groups.forEach(group -> group.spawnAll(instance));
    }
    
    public void despawn(final Predicate<SpawnGroup> groupFilter) {
        this.groups.stream().filter((Predicate<? super Object>)groupFilter).forEach(SpawnGroup::despawnAll);
        this.notifyEvent(script -> script.onSpawnDeactivate(this));
    }
    
    public void despawnAll() {
        this.groups.forEach(SpawnGroup::despawnAll);
        this.notifyEvent(script -> script.onSpawnDeactivate(this));
    }
    
    public SpawnTemplate clone() {
        final SpawnTemplate template = new SpawnTemplate(this._name, this._ai, this._spawnByDefault, this.filePath);
        template.setParameters(this._parameters);
        for (final BannedSpawnTerritory territory : this.getBannedTerritories()) {
            template.addBannedTerritory(territory);
        }
        for (final SpawnTerritory territory2 : this.getTerritories()) {
            template.addTerritory(territory2);
        }
        for (final SpawnGroup group : this.groups) {
            template.addGroup(group.clone());
        }
        return template;
    }
}
