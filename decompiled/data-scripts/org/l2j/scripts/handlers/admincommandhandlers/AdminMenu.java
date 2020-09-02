// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.Clan;
import java.util.Iterator;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.StringTokenizer;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminMenu implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player playerGM) {
        if (command.equals("admin_char_manage")) {
            this.showMainPage(playerGM);
        }
        else if (command.startsWith("admin_teleport_character_to_menu")) {
            final String[] data = command.split(" ");
            if (data.length == 5) {
                final String playerName = data[1];
                final Player player = World.getInstance().findPlayer(playerName);
                if (player != null) {
                    this.teleportCharacter(player, new Location(Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4])), playerGM, "Admin is teleporting you.");
                }
            }
            this.showMainPage(playerGM);
        }
        else if (command.startsWith("admin_recall_char_menu")) {
            try {
                final String targetName = command.substring(23);
                final Player player2 = World.getInstance().findPlayer(targetName);
                this.teleportCharacter(player2, playerGM.getLocation(), playerGM, "Admin is teleporting you.");
            }
            catch (StringIndexOutOfBoundsException ex) {}
        }
        else if (command.startsWith("admin_recall_party_menu")) {
            try {
                final String targetName = command.substring(24);
                final Player player2 = World.getInstance().findPlayer(targetName);
                if (player2 == null) {
                    playerGM.sendPacket(SystemMessageId.INVALID_TARGET);
                    return true;
                }
                if (!player2.isInParty()) {
                    BuilderUtil.sendSysMessage(playerGM, "Player is not in party.");
                    this.teleportCharacter(player2, playerGM.getLocation(), playerGM, "Admin is teleporting you.");
                    return true;
                }
                for (final Player pm : player2.getParty().getMembers()) {
                    this.teleportCharacter(pm, playerGM.getLocation(), playerGM, "Your party is being teleported by an Admin.");
                }
            }
            catch (Exception e) {
                AdminMenu.LOGGER.warn("", (Throwable)e);
            }
        }
        else if (command.startsWith("admin_recall_clan_menu")) {
            try {
                final String targetName = command.substring(23);
                final Player player2 = World.getInstance().findPlayer(targetName);
                if (player2 == null) {
                    playerGM.sendPacket(SystemMessageId.INVALID_TARGET);
                    return true;
                }
                final Clan clan = player2.getClan();
                if (clan == null) {
                    BuilderUtil.sendSysMessage(playerGM, "Player is not in a clan.");
                    this.teleportCharacter(player2, playerGM.getLocation(), playerGM, "Admin is teleporting you.");
                    return true;
                }
                for (final Player member : clan.getOnlineMembers(0)) {
                    this.teleportCharacter(member, playerGM.getLocation(), playerGM, "Your clan is being teleported by an Admin.");
                }
            }
            catch (Exception e) {
                AdminMenu.LOGGER.warn("", (Throwable)e);
            }
        }
        else if (command.startsWith("admin_recall_all")) {
            final StringTokenizer tokens = new StringTokenizer(command);
            tokens.nextToken();
            if (tokens.hasMoreTokens()) {
                final int radius = Integer.parseInt(tokens.nextToken());
                final int n;
                World.getInstance().forEachPlayer(p -> {
                    if (!p.isGM()) {
                        p.teleToLocation((ILocational)playerGM, n);
                    }
                    return;
                });
                if (tokens.hasMoreTokens()) {
                    final int monsterId = Integer.parseInt(tokens.nextToken());
                    final int monsterCount = tokens.hasMoreTokens() ? Integer.parseInt(tokens.nextToken()) : 10;
                    final int monsterId2;
                    final int mobCount;
                    World.getInstance().forEachPlayer(p -> {
                        if (!p.isGM()) {
                            this.spawnMonster(playerGM, p, monsterId2, 60, mobCount);
                        }
                        return;
                    });
                }
            }
            else {
                World.getInstance().forEachPlayer(p -> {
                    if (!p.isGM()) {
                        p.teleToLocation((ILocational)playerGM, true);
                    }
                    return;
                });
            }
        }
        else if (command.startsWith("admin_goto_char_menu")) {
            try {
                final Player player3 = World.getInstance().findPlayer(command.substring(21));
                this.teleportToCharacter(playerGM, (WorldObject)player3);
            }
            catch (StringIndexOutOfBoundsException ex2) {}
        }
        else if (command.equals("admin_kill_menu")) {
            this.handleKill(playerGM);
        }
        else if (command.startsWith("admin_kick_menu")) {
            final StringTokenizer st = new StringTokenizer(command);
            if (st.countTokens() > 1) {
                st.nextToken();
                final String player4 = st.nextToken();
                final Player plyr = World.getInstance().findPlayer(player4);
                String text;
                if (plyr != null) {
                    Disconnection.of(plyr).defaultSequence(false);
                    text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, plyr.getName());
                }
                else {
                    text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player4);
                }
                playerGM.sendMessage(text);
            }
            this.showMainPage(playerGM);
        }
        else if (command.startsWith("admin_ban_menu")) {
            final StringTokenizer st = new StringTokenizer(command);
            if (st.countTokens() > 1) {
                final String subCommand = "admin_ban_char";
                AdminCommandHandler.getInstance().useAdminCommand(playerGM, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, command.substring(14)), true);
            }
            this.showMainPage(playerGM);
        }
        else if (command.startsWith("admin_unban_menu")) {
            final StringTokenizer st = new StringTokenizer(command);
            if (st.countTokens() > 1) {
                final String subCommand = "admin_unban_char";
                AdminCommandHandler.getInstance().useAdminCommand(playerGM, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, command.substring(16)), true);
            }
            this.showMainPage(playerGM);
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminMenu.ADMIN_COMMANDS;
    }
    
    private void handleKill(final Player activeChar) {
        this.handleKill(activeChar, null);
    }
    
    private void handleKill(final Player activeChar, final String player) {
        final WorldObject obj = activeChar.getTarget();
        Creature target = (Creature)obj;
        String filename = "main_menu.htm";
        if (player != null) {
            final Player plyr = World.getInstance().findPlayer(player);
            if (plyr != null) {
                target = (Creature)plyr;
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, plyr.getName()));
            }
        }
        if (target != null) {
            if (GameUtils.isPlayer((WorldObject)target)) {
                target.reduceCurrentHp((double)(target.getMaxHp() + target.getMaxCp() + 1), (Creature)activeChar, (Skill)null, DamageInfo.DamageType.OTHER);
                filename = "charmanage.htm";
            }
            else if (Config.CHAMPION_ENABLE && target.isChampion()) {
                target.reduceCurrentHp((double)(target.getMaxHp() * Config.CHAMPION_HP + 1), (Creature)activeChar, (Skill)null, DamageInfo.DamageType.OTHER);
            }
            else {
                target.reduceCurrentHp((double)(target.getMaxHp() + 1), (Creature)activeChar, (Skill)null, DamageInfo.DamageType.OTHER);
            }
        }
        else {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
        }
        AdminHtml.showAdminHtml(activeChar, filename);
    }
    
    private void teleportCharacter(final Player player, final Location loc, final Player activeChar, final String message) {
        if (player != null) {
            player.sendMessage(message);
            player.teleToLocation((ILocational)loc, true);
        }
        this.showMainPage(activeChar);
    }
    
    private void teleportToCharacter(final Player activeChar, final WorldObject target) {
        if (!GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        final Player player = target.getActingPlayer();
        if (player.getObjectId() == activeChar.getObjectId()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
        }
        else {
            activeChar.teleToLocation((ILocational)player.getLocation(), true, player.getInstanceWorld());
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
        }
        this.showMainPage(activeChar);
    }
    
    private void spawnMonster(final Player gm, final Player target, final int monsterId, final int respawnTime, final int mobCount) {
        final int monsterTemplate = monsterId;
        final NpcTemplate template1 = NpcData.getInstance().getTemplate(monsterTemplate);
        try {
            final Spawn spawn = new Spawn(template1);
            spawn.setXYZ((ILocational)target);
            spawn.setAmount(mobCount);
            spawn.setHeading(target.getHeading());
            spawn.setRespawnDelay(respawnTime);
            if (target.isInInstance()) {
                spawn.setInstanceId(target.getInstanceId());
            }
            SpawnTable.getInstance().addNewSpawn(spawn, false);
            spawn.init();
            if (respawnTime <= 0) {
                spawn.stopRespawn();
            }
            spawn.getLastSpawn().broadcastInfo();
            BuilderUtil.sendSysMessage(gm, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, template1.getName(), target.getObjectId()));
        }
        catch (Exception e) {
            gm.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
        }
    }
    
    private void showMainPage(final Player activeChar) {
        AdminHtml.showAdminHtml(activeChar, "charmanage.htm");
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminMenu.class);
        ADMIN_COMMANDS = new String[] { "admin_char_manage", "admin_teleport_character_to_menu", "admin_recall_char_menu", "admin_recall_party_menu", "admin_recall_clan_menu", "admin_recall_all", "admin_goto_char_menu", "admin_kick_menu", "admin_kill_menu", "admin_ban_menu", "admin_unban_menu" };
    }
}
