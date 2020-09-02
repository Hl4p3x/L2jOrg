// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.Spawns;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.impl.OnDayNightChange;
import java.util.Iterator;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.slf4j.Logger;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class EilhalderVonHellmann extends AbstractNpcAI
{
    private static final Logger LOGGER;
    private static final int EILHALDER_VON_HELLMANN = 25328;
    private NpcSpawnTemplate _template;
    
    private EilhalderVonHellmann() {
        this.addSpawnId(new int[] { 25328 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.equals("delete") && npc != null) {
            npc.deleteMe();
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onSpawn(final Npc npc) {
        if (npc.getSpawn() == null || npc.getSpawn().getNpcSpawnTemplate() == null) {
            this.startQuestTimer("delete", 1000L, npc, (Player)null);
        }
        return super.onSpawn(npc);
    }
    
    public void onSpawnNpc(final SpawnTemplate template, final SpawnGroup group, final Npc npc) {
        EilhalderVonHellmann.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, npc.getName()));
        DBSpawnManager.getInstance().notifySpawnNightNpc(npc);
    }
    
    public void onSpawnDespawnNpc(final SpawnTemplate template, final SpawnGroup group, final Npc npc) {
        EilhalderVonHellmann.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, npc.getName()));
    }
    
    public void onSpawnActivate(final SpawnTemplate template) {
    Label_0088:
        for (final SpawnGroup group : template.getGroups()) {
            for (final NpcSpawnTemplate npc : group.getSpawns()) {
                if (npc.getId() == 25328) {
                    this._template = npc;
                    break Label_0088;
                }
            }
        }
        this.handleBoss(WorldTimeController.getInstance().isNight());
    }
    
    @RegisterEvent(EventType.ON_DAY_NIGHT_CHANGE)
    @RegisterType(ListenerRegisterType.GLOBAL)
    public void onDayNightChange(final OnDayNightChange event) {
        this.handleBoss(event.isNight());
    }
    
    private void handleBoss(final boolean isNight) {
        if (this._template == null) {
            return;
        }
        if (isNight) {
            this._template.spawn((Instance)null);
        }
        else {
            this._template.despawn();
        }
    }
    
    public static AbstractNpcAI provider() {
        return new EilhalderVonHellmann();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)EilhalderVonHellmann.class);
    }
}
