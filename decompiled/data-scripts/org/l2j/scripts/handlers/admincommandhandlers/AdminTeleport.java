// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.actor.instance.RaidBoss;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.NoSuchElementException;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.ai.CtrlIntention;
import java.util.StringTokenizer;
import org.l2j.gameserver.enums.AdminTeleportType;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminTeleport implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_instant_move")) {
            BuilderUtil.sendSysMessage(activeChar, "Instant move ready. Click where you want to go.");
            activeChar.setTeleMode(AdminTeleportType.DEMONIC);
        }
        else if (command.equals("admin_teleto charge")) {
            BuilderUtil.sendSysMessage(activeChar, "Charge move ready. Click where you want to go.");
            activeChar.setTeleMode(AdminTeleportType.CHARGE);
        }
        else if (command.equals("admin_teleto end")) {
            activeChar.setTeleMode(AdminTeleportType.NORMAL);
        }
        else if (command.equals("admin_show_moves")) {
            AdminHtml.showAdminHtml(activeChar, "teleports.htm");
        }
        else if (command.equals("admin_show_moves_other")) {
            AdminHtml.showAdminHtml(activeChar, "tele/other.html");
        }
        else if (command.equals("admin_show_teleport")) {
            this.showTeleportCharWindow(activeChar);
        }
        else if (command.equals("admin_recall_npc")) {
            this.recallNPC(activeChar);
        }
        else if (command.equals("admin_teleport_to_character")) {
            this.teleportToCharacter(activeChar, activeChar.getTarget());
        }
        else if (command.startsWith("admin_walk")) {
            try {
                final String val = command.substring(11);
                final StringTokenizer st = new StringTokenizer(val);
                final int x = Integer.parseInt(st.nextToken());
                final int y = Integer.parseInt(st.nextToken());
                final int z = Integer.parseInt(st.nextToken());
                activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Object[] { new Location(x, y, z, 0) });
            }
            catch (Exception ex) {}
        }
        else if (command.startsWith("admin_move_to")) {
            try {
                final String val = command.substring(14);
                this.teleportTo(activeChar, val);
            }
            catch (StringIndexOutOfBoundsException e) {
                AdminHtml.showAdminHtml(activeChar, "teleports.htm");
            }
            catch (NumberFormatException nfe) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //move_to <x> <y> <z>");
                AdminHtml.showAdminHtml(activeChar, "teleports.htm");
            }
        }
        else if (command.startsWith("admin_teleport_character")) {
            try {
                final String val = command.substring(25);
                this.teleportCharacter(activeChar, val);
            }
            catch (StringIndexOutOfBoundsException e) {
                BuilderUtil.sendSysMessage(activeChar, "Wrong or no Coordinates given.");
                this.showTeleportCharWindow(activeChar);
            }
        }
        else if (command.startsWith("admin_teleportto ")) {
            try {
                final String targetName = command.substring(17);
                final Player player = World.getInstance().findPlayer(targetName);
                this.teleportToCharacter(activeChar, (WorldObject)player);
            }
            catch (StringIndexOutOfBoundsException ex2) {}
        }
        else if (command.startsWith("admin_teleport")) {
            try {
                final StringTokenizer st2 = new StringTokenizer(command, " ");
                st2.nextToken();
                final int x2 = (int)Float.parseFloat(st2.nextToken());
                final int y2 = (int)Float.parseFloat(st2.nextToken());
                final int z2 = st2.hasMoreTokens() ? ((int)Float.parseFloat(st2.nextToken())) : GeoEngine.getInstance().getHeight(x2, y2, 10000);
                activeChar.teleToLocation(x2, y2, z2);
            }
            catch (Exception e2) {
                BuilderUtil.sendSysMessage(activeChar, "Wrong coordinates!");
            }
        }
        else if (command.startsWith("admin_recall ")) {
            try {
                final String[] param = command.split(" ");
                if (param.length != 2) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //recall <playername>");
                    return false;
                }
                final String targetName2 = param[1];
                final Player player2 = World.getInstance().findPlayer(targetName2);
                if (player2 != null) {
                    this.teleportCharacter(player2, activeChar.getLocation(), activeChar);
                }
                else {
                    this.changeCharacterPosition(activeChar, targetName2);
                }
            }
            catch (StringIndexOutOfBoundsException ex3) {}
        }
        else if (command.equals("admin_tele")) {
            this.showTeleportWindow(activeChar);
        }
        else if (command.startsWith("admin_go")) {
            int intVal = 150;
            int x2 = activeChar.getX();
            int y2 = activeChar.getY();
            int z2 = activeChar.getZ();
            try {
                final String val2 = command.substring(8);
                final StringTokenizer st3 = new StringTokenizer(val2);
                final String dir = st3.nextToken();
                if (st3.hasMoreTokens()) {
                    intVal = Integer.parseInt(st3.nextToken());
                }
                if (dir.equals("east")) {
                    x2 += intVal;
                }
                else if (dir.equals("west")) {
                    x2 -= intVal;
                }
                else if (dir.equals("north")) {
                    y2 -= intVal;
                }
                else if (dir.equals("south")) {
                    y2 += intVal;
                }
                else if (dir.equals("up")) {
                    z2 += intVal;
                }
                else if (dir.equals("down")) {
                    z2 -= intVal;
                }
                activeChar.teleToLocation((ILocational)new Location(x2, y2, z2));
                this.showTeleportWindow(activeChar);
            }
            catch (Exception e3) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //go<north|south|east|west|up|down> [offset] (default 150)");
            }
        }
        else if (command.startsWith("admin_sendhome")) {
            final StringTokenizer st2 = new StringTokenizer(command, " ");
            st2.nextToken();
            if (st2.countTokens() > 1) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //sendhome <playername>");
            }
            else if (st2.countTokens() == 1) {
                final String name = st2.nextToken();
                final Player player2 = World.getInstance().findPlayer(name);
                if (player2 == null) {
                    activeChar.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
                    return false;
                }
                this.teleportHome(player2);
            }
            else {
                final WorldObject target = activeChar.getTarget();
                if (GameUtils.isPlayer(target)) {
                    this.teleportHome(target.getActingPlayer());
                }
                else {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                }
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminTeleport.ADMIN_COMMANDS;
    }
    
    private void teleportHome(final Player player) {
        String s = null;
        switch (player.getRace()) {
            case ELF: {
                s = "elf_town";
                break;
            }
            case DARK_ELF: {
                s = "darkelf_town";
                break;
            }
            case ORC: {
                s = "orc_town";
                break;
            }
            case DWARF: {
                s = "dwarf_town";
                break;
            }
            case JIN_KAMAEL: {
                s = "kamael_town";
                break;
            }
            default: {
                s = "talking_island_town";
                break;
            }
        }
        final String regionName = s;
        player.teleToLocation((ILocational)MapRegionManager.getInstance().getMapRegionByName(regionName).getSpawnLoc(), true, (Instance)null);
    }
    
    private void teleportTo(final Player activeChar, final String Coords) {
        try {
            final StringTokenizer st = new StringTokenizer(Coords);
            final int x = Integer.parseInt(st.nextToken());
            final int y = Integer.parseInt(st.nextToken());
            final int z = Integer.parseInt(st.nextToken());
            activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
            activeChar.teleToLocation(x, y, z, (Instance)null);
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Coords));
        }
        catch (NoSuchElementException nsee) {
            BuilderUtil.sendSysMessage(activeChar, "Wrong or no Coordinates given.");
        }
    }
    
    private void showTeleportWindow(final Player activeChar) {
        AdminHtml.showAdminHtml(activeChar, "move.htm");
    }
    
    private void showTeleportCharWindow(final Player activeChar) {
        final WorldObject target = activeChar.getTarget();
        Player player = null;
        if (target != null && GameUtils.isPlayer(target)) {
            player = (Player)target;
            final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
            final String replyMSG = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, player.getName(), activeChar.getX(), activeChar.getY(), activeChar.getZ());
            adminReply.setHtml(replyMSG);
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
            return;
        }
        activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
    }
    
    private void teleportCharacter(final Player activeChar, final String Cords) {
        final WorldObject target = activeChar.getTarget();
        Player player = null;
        if (target != null && GameUtils.isPlayer(target)) {
            player = (Player)target;
            if (player.getObjectId() == activeChar.getObjectId()) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
            }
            else {
                try {
                    final StringTokenizer st = new StringTokenizer(Cords);
                    final String x1 = st.nextToken();
                    final int x2 = Integer.parseInt(x1);
                    final String y1 = st.nextToken();
                    final int y2 = Integer.parseInt(y1);
                    final String z1 = st.nextToken();
                    final int z2 = Integer.parseInt(z1);
                    this.teleportCharacter(player, new Location(x2, y2, z2), null);
                }
                catch (NoSuchElementException ex) {}
            }
            return;
        }
        activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
    }
    
    private void teleportCharacter(final Player player, final Location loc, final Player activeChar) {
        if (player != null) {
            if (player.isJailed()) {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                player.sendMessage("Admin is teleporting you.");
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                player.teleToLocation((ILocational)loc, true, activeChar.getInstanceWorld());
            }
        }
    }
    
    private void teleportToCharacter(final Player activeChar, final WorldObject target) {
        if (target == null || !GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        final Player player = target.getActingPlayer();
        if (player.getObjectId() == activeChar.getObjectId()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
        }
        else {
            activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
            activeChar.teleToLocation((ILocational)player, true, player.getInstanceWorld());
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
        }
    }
    
    private void changeCharacterPosition(final Player activeChar, final String name) {
        final int x = activeChar.getX();
        final int y = activeChar.getY();
        final int z = activeChar.getZ();
        if (((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).updateLocationByName(name, x, y, z)) {
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, name, x, y, z));
        }
    }
    
    private void recallNPC(final Player activeChar) {
        final WorldObject obj = activeChar.getTarget();
        if (obj instanceof Npc && !((Npc)obj).isMinion() && !(obj instanceof RaidBoss) && !(obj instanceof GrandBoss)) {
            final Npc target = (Npc)obj;
            final int monsterTemplate = target.getTemplate().getId();
            final NpcTemplate template1 = NpcData.getInstance().getTemplate(monsterTemplate);
            if (template1 == null) {
                BuilderUtil.sendSysMessage(activeChar, "Incorrect monster template.");
                AdminTeleport.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, target.getObjectId()));
                return;
            }
            Spawn spawn = target.getSpawn();
            if (spawn == null) {
                BuilderUtil.sendSysMessage(activeChar, "Incorrect monster spawn.");
                AdminTeleport.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, target.getObjectId()));
                return;
            }
            final int respawnTime = spawn.getRespawnDelay() / 1000;
            target.deleteMe();
            spawn.stopRespawn();
            SpawnTable.getInstance().deleteSpawn(spawn, true);
            try {
                spawn = new Spawn(template1);
                spawn.setXYZ((ILocational)activeChar);
                spawn.setAmount(1);
                spawn.setHeading(activeChar.getHeading());
                spawn.setRespawnDelay(respawnTime);
                if (activeChar.isInInstance()) {
                    spawn.setInstanceId(activeChar.getInstanceId());
                }
                SpawnTable.getInstance().addNewSpawn(spawn, true);
                spawn.init();
                if (respawnTime <= 0) {
                    spawn.stopRespawn();
                }
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, template1.getName(), target.getObjectId()));
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Target is not in game.");
            }
        }
        else if (obj instanceof RaidBoss) {
            final RaidBoss target2 = (RaidBoss)obj;
            final Spawn spawn2 = target2.getSpawn();
            final double curHP = target2.getCurrentHp();
            final double curMP = target2.getCurrentMp();
            if (spawn2 == null) {
                BuilderUtil.sendSysMessage(activeChar, "Incorrect raid spawn.");
                AdminTeleport.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, target2.getId()));
                return;
            }
            DBSpawnManager.getInstance().deleteSpawn(spawn2, true);
            try {
                final Spawn spawnDat = new Spawn(target2.getId());
                spawnDat.setXYZ((ILocational)activeChar);
                spawnDat.setAmount(1);
                spawnDat.setHeading(activeChar.getHeading());
                spawnDat.setRespawnMinDelay(43200);
                spawnDat.setRespawnMaxDelay(129600);
                DBSpawnManager.getInstance().addNewSpawn(spawnDat, 0L, curHP, curMP, true);
            }
            catch (Exception e2) {
                activeChar.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
            }
        }
        else {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminTeleport.class);
        ADMIN_COMMANDS = new String[] { "admin_show_moves", "admin_show_moves_other", "admin_show_teleport", "admin_teleport_to_character", "admin_teleportto", "admin_teleport", "admin_move_to", "admin_teleport_character", "admin_recall", "admin_walk", "teleportto", "recall", "admin_recall_npc", "admin_gonorth", "admin_gosouth", "admin_goeast", "admin_gowest", "admin_goup", "admin_godown", "admin_tele", "admin_teleto", "admin_instant_move", "admin_sendhome" };
    }
}
