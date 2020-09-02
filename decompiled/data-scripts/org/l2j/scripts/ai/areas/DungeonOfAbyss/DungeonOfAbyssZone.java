// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.DungeonOfAbyss;

import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.scripts.ai.AbstractNpcAI;

public class DungeonOfAbyssZone extends AbstractNpcAI
{
    private static final Zone ABYSS_WEST_ZONE_1;
    private static final Zone ABYSS_WEST_ZONE_2;
    private static final Zone ABYSS_EAST_ZONE_3;
    private static final Zone ABYSS_EAST_ZONE_4;
    private static final Zone ABYSS_WEST_ZONE_BOSS_1;
    private static final Zone ABYSS_WEST_ZONE_BOSS_2;
    private static final Zone ABYSS_EAST_ZONE_BOSS_3;
    private static final Zone ABYSS_EAST_ZONE_BOSS_4;
    private static final int EXIT_TIME = 3600000;
    private static final int EXIT_TIME_BOSS_ROOM = 1800000;
    private static final Location EXIT_LOCATION_1;
    private static final Location EXIT_LOCATION_2;
    private static final Location EXIT_LOCATION_3;
    private static final Location EXIT_LOCATION_4;
    
    private DungeonOfAbyssZone() {
        this.addEnterZoneId(new int[] { DungeonOfAbyssZone.ABYSS_WEST_ZONE_1.getId(), DungeonOfAbyssZone.ABYSS_WEST_ZONE_2.getId(), DungeonOfAbyssZone.ABYSS_EAST_ZONE_3.getId(), DungeonOfAbyssZone.ABYSS_EAST_ZONE_4.getId(), DungeonOfAbyssZone.ABYSS_WEST_ZONE_BOSS_1.getId(), DungeonOfAbyssZone.ABYSS_WEST_ZONE_BOSS_2.getId(), DungeonOfAbyssZone.ABYSS_EAST_ZONE_BOSS_3.getId(), DungeonOfAbyssZone.ABYSS_EAST_ZONE_BOSS_4.getId() });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.startsWith("EXIT_PLAYER") && player != null) {
            if (event.contains(DungeonOfAbyssZone.ABYSS_WEST_ZONE_1.getName()) && DungeonOfAbyssZone.ABYSS_WEST_ZONE_1.isCreatureInZone((Creature)player)) {
                player.teleToLocation((ILocational)DungeonOfAbyssZone.EXIT_LOCATION_1);
            }
            else if (event.contains(DungeonOfAbyssZone.ABYSS_WEST_ZONE_2.getName()) && DungeonOfAbyssZone.ABYSS_WEST_ZONE_2.isCreatureInZone((Creature)player)) {
                player.teleToLocation((ILocational)DungeonOfAbyssZone.EXIT_LOCATION_2);
            }
            else if (event.contains(DungeonOfAbyssZone.ABYSS_EAST_ZONE_3.getName()) && DungeonOfAbyssZone.ABYSS_EAST_ZONE_3.isCreatureInZone((Creature)player)) {
                player.teleToLocation((ILocational)DungeonOfAbyssZone.EXIT_LOCATION_3);
            }
            else if (event.contains(DungeonOfAbyssZone.ABYSS_EAST_ZONE_4.getName()) && DungeonOfAbyssZone.ABYSS_EAST_ZONE_4.isCreatureInZone((Creature)player)) {
                player.teleToLocation((ILocational)DungeonOfAbyssZone.EXIT_LOCATION_4);
            }
            else if (event.contains(DungeonOfAbyssZone.ABYSS_WEST_ZONE_BOSS_1.getName()) && DungeonOfAbyssZone.ABYSS_WEST_ZONE_BOSS_1.isCreatureInZone((Creature)player)) {
                player.teleToLocation((ILocational)DungeonOfAbyssZone.EXIT_LOCATION_1);
            }
            else if (event.contains(DungeonOfAbyssZone.ABYSS_WEST_ZONE_BOSS_2.getName()) && DungeonOfAbyssZone.ABYSS_WEST_ZONE_BOSS_2.isCreatureInZone((Creature)player)) {
                player.teleToLocation((ILocational)DungeonOfAbyssZone.EXIT_LOCATION_2);
            }
            else if (event.contains(DungeonOfAbyssZone.ABYSS_EAST_ZONE_BOSS_3.getName()) && DungeonOfAbyssZone.ABYSS_EAST_ZONE_BOSS_3.isCreatureInZone((Creature)player)) {
                player.teleToLocation((ILocational)DungeonOfAbyssZone.EXIT_LOCATION_3);
            }
            else if (event.contains(DungeonOfAbyssZone.ABYSS_EAST_ZONE_BOSS_4.getName()) && DungeonOfAbyssZone.ABYSS_EAST_ZONE_BOSS_4.isCreatureInZone((Creature)player)) {
                player.teleToLocation((ILocational)DungeonOfAbyssZone.EXIT_LOCATION_4);
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public String onEnterZone(final Creature creature, final Zone zone) {
        if (GameUtils.isPlayer((WorldObject)creature)) {
            final Player player = creature.getActingPlayer();
            this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, DungeonOfAbyssZone.ABYSS_WEST_ZONE_1.getName(), player.getObjectId()), (Npc)null, player);
            this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, DungeonOfAbyssZone.ABYSS_WEST_ZONE_2.getName(), player.getObjectId()), (Npc)null, player);
            this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, DungeonOfAbyssZone.ABYSS_EAST_ZONE_3.getName(), player.getObjectId()), (Npc)null, player);
            this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, DungeonOfAbyssZone.ABYSS_EAST_ZONE_4.getName(), player.getObjectId()), (Npc)null, player);
            this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, DungeonOfAbyssZone.ABYSS_WEST_ZONE_BOSS_1.getName(), player.getObjectId()), (Npc)null, player);
            this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, DungeonOfAbyssZone.ABYSS_WEST_ZONE_BOSS_2.getName(), player.getObjectId()), (Npc)null, player);
            this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, DungeonOfAbyssZone.ABYSS_EAST_ZONE_BOSS_3.getName(), player.getObjectId()), (Npc)null, player);
            this.cancelQuestTimer(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, DungeonOfAbyssZone.ABYSS_EAST_ZONE_BOSS_4.getName(), player.getObjectId()), (Npc)null, player);
            this.startQuestTimer(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, zone.getName(), player.getObjectId()), zone.getName().contains("boss") ? 1800000L : 3600000L, (Npc)null, player);
        }
        return super.onEnterZone(creature, zone);
    }
    
    public static AbstractNpcAI provider() {
        return new DungeonOfAbyssZone();
    }
    
    static {
        ABYSS_WEST_ZONE_1 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss");
        ABYSS_WEST_ZONE_2 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss 2nd");
        ABYSS_EAST_ZONE_3 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss");
        ABYSS_EAST_ZONE_4 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss 2nd");
        ABYSS_WEST_ZONE_BOSS_1 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss Boss Room");
        ABYSS_WEST_ZONE_BOSS_2 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss 2nd Boss Room");
        ABYSS_EAST_ZONE_BOSS_3 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss Boss Room");
        ABYSS_EAST_ZONE_BOSS_4 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss 2nd Boss Room");
        EXIT_LOCATION_1 = new Location(-120019, -182575, -6751);
        EXIT_LOCATION_2 = new Location(-119977, -179753, -6751);
        EXIT_LOCATION_3 = new Location(-109554, -180408, -6753);
        EXIT_LOCATION_4 = new Location(-109595, -177560, -6753);
    }
}
