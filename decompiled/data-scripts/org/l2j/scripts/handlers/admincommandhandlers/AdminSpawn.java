// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.slf4j.LoggerFactory;
import java.util.List;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.regex.Matcher;
import org.l2j.gameserver.model.Spawn;
import java.util.Iterator;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.Map;
import java.util.HashMap;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.impl.NpcData;
import java.util.regex.Pattern;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.instancemanager.InstanceManager;
import java.util.NoSuchElementException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Npc;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminSpawn implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_show_spawns")) {
            AdminHtml.showAdminHtml(activeChar, "spawns.htm");
        }
        else if (command.equalsIgnoreCase("admin_spawn_debug_menu")) {
            AdminHtml.showAdminHtml(activeChar, "spawns_debug.htm");
        }
        else if (command.startsWith("admin_spawn_debug_print")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            final WorldObject target = activeChar.getTarget();
            if (target instanceof Npc) {
                try {
                    st.nextToken();
                    final int type = Integer.parseInt(st.nextToken());
                    this.printSpawn((Npc)target, type);
                    if (command.contains("_menu")) {
                        AdminHtml.showAdminHtml(activeChar, "spawns_debug.htm");
                    }
                }
                catch (Exception ex) {}
            }
            else {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            }
        }
        else if (command.startsWith("admin_spawn_index")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            try {
                st.nextToken();
                final int level = Integer.parseInt(st.nextToken());
                int from = 0;
                try {
                    from = Integer.parseInt(st.nextToken());
                }
                catch (NoSuchElementException ex2) {}
                this.showMonsters(activeChar, level, from);
            }
            catch (Exception e) {
                AdminHtml.showAdminHtml(activeChar, "spawns.htm");
            }
        }
        else if (command.equals("admin_show_npcs")) {
            AdminHtml.showAdminHtml(activeChar, "npcs.htm");
        }
        else if (command.startsWith("admin_npc_index")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            try {
                st.nextToken();
                final String letter = st.nextToken();
                int from = 0;
                try {
                    from = Integer.parseInt(st.nextToken());
                }
                catch (NoSuchElementException ex3) {}
                this.showNpcs(activeChar, letter, from);
            }
            catch (Exception e) {
                AdminHtml.showAdminHtml(activeChar, "npcs.htm");
            }
        }
        else if (command.startsWith("admin_instance_spawns")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            try {
                st.nextToken();
                final int instance = Integer.parseInt(st.nextToken());
                if (instance >= 300000) {
                    final StringBuilder html = new StringBuilder(1500);
                    html.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, instance));
                    int counter = 0;
                    int skiped = 0;
                    final Instance inst = InstanceManager.getInstance().getInstance(instance);
                    if (inst != null) {
                        for (final Npc npc : inst.getNpcs()) {
                            if (!npc.isDead()) {
                                if (counter < 50) {
                                    html.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, npc.getName(), npc.getX(), npc.getY(), npc.getZ()));
                                    ++counter;
                                }
                                else {
                                    ++skiped;
                                }
                            }
                        }
                        html.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, skiped));
                        final NpcHtmlMessage ms = new NpcHtmlMessage(0, 1);
                        ms.setHtml(html.toString());
                        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)ms });
                    }
                    else {
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, instance));
                    }
                }
                else {
                    BuilderUtil.sendSysMessage(activeChar, "Invalid instance number.");
                }
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage //instance_spawns <instance_number>");
            }
        }
        else if (command.startsWith("admin_unspawnall")) {
            Broadcast.toAllOnlinePlayers(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.THE_NPC_SERVER_IS_NOT_OPERATING_AT_THIS_TIME) });
            QuestManager.getInstance().unloadAllScripts();
            ZoneManager.getInstance().unload();
            for (final Npc npc2 : DBSpawnManager.getInstance().getNpcs().values()) {
                if (npc2 != null) {
                    DBSpawnManager.getInstance().deleteSpawn(npc2.getSpawn(), true);
                    npc2.deleteMe();
                }
            }
            DBSpawnManager.getInstance().cleanUp();
            for (final WorldObject obj : World.getInstance().getVisibleObjects()) {
                if (GameUtils.isNpc(obj)) {
                    final Npc target2 = (Npc)obj;
                    target2.deleteMe();
                    final Spawn spawn = target2.getSpawn();
                    if (spawn == null) {
                        continue;
                    }
                    spawn.stopRespawn();
                    SpawnTable.getInstance().deleteSpawn(spawn, false);
                }
            }
            ZoneManager.getInstance().reload();
            QuestManager.getInstance().reloadAllScripts();
            AdminData.getInstance().broadcastMessageToGMs("NPC unspawn completed!");
        }
        else if (command.startsWith("admin_respawnall") || command.startsWith("admin_spawn_reload")) {
            QuestManager.getInstance().unloadAllScripts();
            ZoneManager.getInstance().unload();
            for (final Npc npc2 : DBSpawnManager.getInstance().getNpcs().values()) {
                if (npc2 != null) {
                    DBSpawnManager.getInstance().deleteSpawn(npc2.getSpawn(), true);
                    npc2.deleteMe();
                }
            }
            DBSpawnManager.getInstance().cleanUp();
            for (final WorldObject obj : World.getInstance().getVisibleObjects()) {
                if (GameUtils.isNpc(obj)) {
                    final Npc target2 = (Npc)obj;
                    target2.deleteMe();
                    final Spawn spawn = target2.getSpawn();
                    if (spawn == null) {
                        continue;
                    }
                    spawn.stopRespawn();
                    SpawnTable.getInstance().deleteSpawn(spawn, false);
                }
            }
            SpawnsData.getInstance();
            SpawnsData.init();
            DBSpawnManager.init();
            ZoneManager.getInstance().reload();
            QuestManager.getInstance().reloadAllScripts();
            AdminData.getInstance().broadcastMessageToGMs("NPC respawn completed!");
        }
        else if (command.startsWith("admin_spawnat")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            try {
                final String cmd = st.nextToken();
                final String id = st.nextToken();
                final String x = st.nextToken();
                final String y = st.nextToken();
                final String z = st.nextToken();
                int h = activeChar.getHeading();
                if (st.hasMoreTokens()) {
                    h = Integer.parseInt(st.nextToken());
                }
                this.spawnMonster(activeChar, Integer.parseInt(id), Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z), h);
            }
            catch (Exception e) {
                AdminHtml.showAdminHtml(activeChar, "spawns.htm");
            }
        }
        else if (command.startsWith("admin_spawn_monster") || command.startsWith("admin_spawn")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            try {
                final String cmd = st.nextToken();
                final String id = st.nextToken();
                int respawnTime = 60;
                int mobCount = 1;
                if (st.hasMoreTokens()) {
                    mobCount = Integer.parseInt(st.nextToken());
                }
                if (st.hasMoreTokens()) {
                    respawnTime = Integer.parseInt(st.nextToken());
                }
                this.spawnMonster(activeChar, id, respawnTime, mobCount, !cmd.equalsIgnoreCase("admin_spawn_once"));
            }
            catch (Exception e) {
                AdminHtml.showAdminHtml(activeChar, "spawns.htm");
            }
        }
        else if (command.startsWith("admin_list_spawns") || command.startsWith("admin_list_positions")) {
            int npcId = 0;
            int teleportIndex = -1;
            try {
                final String[] params = command.split(" ");
                final Pattern pattern = Pattern.compile("[0-9]*");
                final Matcher regexp = pattern.matcher(params[1]);
                if (regexp.matches()) {
                    npcId = Integer.parseInt(params[1]);
                }
                else {
                    params[1] = params[1].replace('_', ' ');
                    npcId = NpcData.getInstance().getTemplateByName(params[1]).getId();
                }
                if (params.length > 2) {
                    teleportIndex = Integer.parseInt(params[2]);
                }
            }
            catch (Exception e2) {
                BuilderUtil.sendSysMessage(activeChar, "Command format is //list_spawns <npcId|npc_name> [tele_index]");
            }
            if (command.startsWith("admin_list_positions")) {
                this.findNPCInstances(activeChar, npcId, teleportIndex, true);
            }
            else {
                this.findNPCInstances(activeChar, npcId, teleportIndex, false);
            }
        }
        else if (command.startsWith("admin_topspawncount") || command.startsWith("admin_top_spawn_count")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken();
            int count = 5;
            if (st.hasMoreTokens()) {
                final String nextToken = st.nextToken();
                if (Util.isDigit(nextToken)) {
                    count = Integer.parseInt(nextToken);
                }
                if (count <= 0) {
                    return true;
                }
            }
            final Map<Integer, Integer> npcsFound = new HashMap<Integer, Integer>();
            for (final WorldObject obj2 : World.getInstance().getVisibleObjects()) {
                if (!GameUtils.isNpc(obj2)) {
                    continue;
                }
                final int npcId2 = obj2.getId();
                if (npcsFound.containsKey(npcId2)) {
                    npcsFound.put(npcId2, npcsFound.get(npcId2) + 1);
                }
                else {
                    npcsFound.put(npcId2, 1);
                }
            }
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, count));
            for (final Map.Entry<Integer, Integer> entry : GameUtils.sortByValue((Map)npcsFound, true).entrySet()) {
                if (--count < 0) {
                    break;
                }
                final int npcId2 = entry.getKey();
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/Object;)Ljava/lang/String;, NpcData.getInstance().getTemplate(npcId2).getName(), npcId2, entry.getValue()));
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminSpawn.ADMIN_COMMANDS;
    }
    
    private void findNPCInstances(final Player activeChar, final int npcId, final int teleportIndex, final boolean showposition) {
        int index = 0;
        for (final Spawn spawn : SpawnTable.getInstance().getSpawns(npcId)) {
            ++index;
            final Npc npc = spawn.getLastSpawn();
            if (teleportIndex > -1) {
                if (teleportIndex != index) {
                    continue;
                }
                if (showposition && npc != null) {
                    activeChar.teleToLocation((ILocational)npc.getLocation(), true);
                }
                else {
                    activeChar.teleToLocation((ILocational)spawn.getLocation(), true);
                }
            }
            else if (showposition && npc != null) {
                activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Lorg/l2j/gameserver/model/Spawn;III)Ljava/lang/String;, index, spawn.getTemplate().getName(), spawn, npc.getX(), npc.getY(), npc.getZ()));
            }
            else {
                activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Lorg/l2j/gameserver/model/Spawn;III)Ljava/lang/String;, index, spawn.getTemplate().getName(), spawn, spawn.getX(), spawn.getY(), spawn.getZ()));
            }
        }
        if (index == 0) {
            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
        }
    }
    
    private void printSpawn(final Npc target, final int type) {
        final int i = target.getId();
        final int x = target.getSpawn().getX();
        final int y = target.getSpawn().getY();
        final int z = target.getSpawn().getZ();
        final int h = target.getSpawn().getHeading();
        switch (type) {
            default: {
                AdminSpawn.LOGGER.info(invokedynamic(makeConcatWithConstants:(IIIII)Ljava/lang/String;, i, x, y, z, h));
                break;
            }
            case 1: {
                AdminSpawn.LOGGER.info(invokedynamic(makeConcatWithConstants:(IIIII)Ljava/lang/String;, i, x, y, z, h));
                break;
            }
            case 2: {
                AdminSpawn.LOGGER.info(invokedynamic(makeConcatWithConstants:(IIIII)Ljava/lang/String;, i, x, y, z, h));
                break;
            }
        }
    }
    
    private void spawnMonster(final Player activeChar, String monsterId, final int respawnTime, final int mobCount, boolean permanent) {
        WorldObject target = activeChar.getTarget();
        if (target == null) {
            target = (WorldObject)activeChar;
        }
        NpcTemplate template1;
        if (monsterId.matches("[0-9]*")) {
            final int monsterTemplate = Integer.parseInt(monsterId);
            template1 = NpcData.getInstance().getTemplate(monsterTemplate);
        }
        else {
            monsterId = monsterId.replace('_', ' ');
            template1 = NpcData.getInstance().getTemplateByName(monsterId);
        }
        try {
            final Spawn spawn = new Spawn(template1);
            spawn.setXYZ((ILocational)target);
            spawn.setAmount(mobCount);
            spawn.setHeading(activeChar.getHeading());
            spawn.setRespawnDelay(respawnTime);
            if (activeChar.isInInstance()) {
                spawn.setInstanceId(activeChar.getInstanceId());
                permanent = false;
            }
            SpawnTable.getInstance().addNewSpawn(spawn, permanent);
            spawn.init();
            if (!permanent || respawnTime <= 0) {
                spawn.stopRespawn();
            }
            spawn.getLastSpawn().broadcastInfo();
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, template1.getName(), target.getObjectId()));
        }
        catch (Exception e) {
            activeChar.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
        }
    }
    
    private void spawnMonster(final Player activeChar, final int id, final int x, final int y, final int z, final int h) {
        WorldObject target = activeChar.getTarget();
        if (target == null) {
            target = (WorldObject)activeChar;
        }
        final NpcTemplate template1 = NpcData.getInstance().getTemplate(id);
        try {
            final Spawn spawn = new Spawn(template1);
            spawn.setXYZ(x, y, z);
            spawn.setAmount(1);
            spawn.setHeading(h);
            spawn.setRespawnDelay(60);
            if (activeChar.isInInstance()) {
                spawn.setInstanceId(activeChar.getInstanceId());
            }
            SpawnTable.getInstance().addNewSpawn(spawn, true);
            spawn.init();
            if (activeChar.isInInstance()) {
                spawn.stopRespawn();
            }
            spawn.getLastSpawn().broadcastInfo();
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, template1.getName(), target.getObjectId()));
        }
        catch (Exception e) {
            activeChar.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
        }
    }
    
    private void showMonsters(final Player activeChar, final int level, final int from) {
        final List<NpcTemplate> mobs = (List<NpcTemplate>)NpcData.getInstance().getAllMonstersOfLevel(new int[] { level });
        final int mobsCount = mobs.size();
        final StringBuilder tb = new StringBuilder(500 + mobsCount * 80);
        tb.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, level, mobsCount));
        int i = from;
        for (int j = 0; i < mobsCount && j < 50; ++i, ++j) {
            tb.append(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, mobs.get(i).getId(), mobs.get(i).getName()));
        }
        if (i == mobsCount) {
            tb.append("<br><center><button value=\"Back\" action=\"bypass -h admin_show_spawns\" width=40 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></body></html>");
        }
        else {
            tb.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, level, i));
        }
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new NpcHtmlMessage(0, 1, tb.toString()) });
    }
    
    private void showNpcs(final Player activeChar, final String starting, final int from) {
        final List<NpcTemplate> mobs = (List<NpcTemplate>)NpcData.getInstance().getAllNpcStartingWith(starting);
        final int mobsCount = mobs.size();
        final StringBuilder tb = new StringBuilder(500 + mobsCount * 80);
        tb.append(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, mobsCount, starting));
        int i = from;
        for (int j = 0; i < mobsCount && j < 50; ++i, ++j) {
            tb.append(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, mobs.get(i).getId(), mobs.get(i).getName()));
        }
        if (i == mobsCount) {
            tb.append("<br><center><button value=\"Back\" action=\"bypass -h admin_show_npcs\" width=40 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></body></html>");
        }
        else {
            tb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, starting, i));
        }
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new NpcHtmlMessage(0, 1, tb.toString()) });
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminSpawn.class);
        ADMIN_COMMANDS = new String[] { "admin_show_spawns", "admin_spawnat", "admin_spawn", "admin_spawn_monster", "admin_spawn_index", "admin_unspawnall", "admin_respawnall", "admin_spawn_reload", "admin_npc_index", "admin_spawn_once", "admin_show_npcs", "admin_instance_spawns", "admin_list_spawns", "admin_list_positions", "admin_spawn_debug_menu", "admin_spawn_debug_print", "admin_spawn_debug_print_menu", "admin_topspawncount", "admin_top_spawn_count" };
    }
}
