// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.Spawns;

import org.l2j.gameserver.model.spawns.SpawnGroup;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.impl.OnDayNightChange;
import org.l2j.gameserver.world.WorldTimeController;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import java.util.Set;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class DayNightSpawns extends AbstractNpcAI
{
    private static final String NIGHT_GROUP_NAME = "nightTime";
    private static final String DAY_GROUP_NAME = "dayTime";
    private final Set<SpawnTemplate> _templates;
    
    private DayNightSpawns() {
        this._templates = (Set<SpawnTemplate>)ConcurrentHashMap.newKeySet();
    }
    
    public void onSpawnActivate(final SpawnTemplate template) {
        if (this._templates.add(template)) {
            this.manageSpawns(template, WorldTimeController.getInstance().isNight());
        }
    }
    
    public void onSpawnDeactivate(final SpawnTemplate template) {
        this._templates.remove(template);
    }
    
    @RegisterEvent(EventType.ON_DAY_NIGHT_CHANGE)
    @RegisterType(ListenerRegisterType.GLOBAL)
    public void onDayNightChange(final OnDayNightChange event) {
        this._templates.forEach(template -> this.manageSpawns(template, event.isNight()));
    }
    
    private void manageSpawns(final SpawnTemplate template, final boolean isNight) {
        template.getGroups().forEach(group -> {
            if ("nightTime".equalsIgnoreCase(group.getName())) {
                if (!isNight) {
                    group.despawnAll();
                }
                else {
                    group.spawnAll();
                }
            }
            else if ("dayTime".equalsIgnoreCase(group.getName())) {
                if (isNight) {
                    group.despawnAll();
                }
                else {
                    group.spawnAll();
                }
            }
        });
    }
    
    public static AbstractNpcAI provider() {
        return new DayNightSpawns();
    }
}
