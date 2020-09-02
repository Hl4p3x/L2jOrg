// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.SetupGauge;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.MobGroup;
import org.l2j.gameserver.model.MobGroupTable;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminMobGroup implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_mobmenu")) {
            this.showMainPage(activeChar, command);
            return true;
        }
        if (command.equals("admin_mobgroup_list")) {
            this.showGroupList(activeChar);
        }
        else if (command.startsWith("admin_mobgroup_create")) {
            this.createGroup(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_delete") || command.startsWith("admin_mobgroup_remove")) {
            this.removeGroup(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_spawn")) {
            this.spawnGroup(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_unspawn")) {
            this.unspawnGroup(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_kill")) {
            this.killGroup(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_attackgrp")) {
            this.attackGrp(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_attack")) {
            if (GameUtils.isCreature(activeChar.getTarget())) {
                final Creature target = (Creature)activeChar.getTarget();
                this.attack(command, activeChar, target);
            }
        }
        else if (command.startsWith("admin_mobgroup_rnd")) {
            this.setNormal(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_idle")) {
            this.idle(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_return")) {
            this.returnToChar(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_follow")) {
            this.follow(command, activeChar, (Creature)activeChar);
        }
        else if (command.startsWith("admin_mobgroup_casting")) {
            this.setCasting(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_nomove")) {
            this.noMove(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_invul")) {
            this.invul(command, activeChar);
        }
        else if (command.startsWith("admin_mobgroup_teleport")) {
            this.teleportGroup(command, activeChar);
        }
        this.showMainPage(activeChar, command);
        return true;
    }
    
    private void showMainPage(final Player activeChar, final String command) {
        final String filename = "mobgroup.htm";
        AdminHtml.showAdminHtml(activeChar, "mobgroup.htm");
    }
    
    private void returnToChar(final String command, final Player activeChar) {
        int groupId;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        group.returnGroup((Creature)activeChar);
    }
    
    private void idle(final String command, final Player activeChar) {
        int groupId;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        group.setIdleMode();
    }
    
    private void setNormal(final String command, final Player activeChar) {
        int groupId;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        group.setAttackRandom();
    }
    
    private void attack(final String command, final Player activeChar, final Creature target) {
        int groupId;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        group.setAttackTarget(target);
    }
    
    private void follow(final String command, final Player activeChar, final Creature target) {
        int groupId;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        group.setFollowMode(target);
    }
    
    private void createGroup(final String command, final Player activeChar) {
        int groupId;
        int templateId;
        int mobCount;
        try {
            final String[] cmdParams = command.split(" ");
            groupId = Integer.parseInt(cmdParams[1]);
            templateId = Integer.parseInt(cmdParams[2]);
            mobCount = Integer.parseInt(cmdParams[3]);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_create <group> <npcid> <count>");
            return;
        }
        if (MobGroupTable.getInstance().getGroup(groupId) != null) {
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, groupId));
            return;
        }
        final NpcTemplate template = NpcData.getInstance().getTemplate(templateId);
        if (template == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid NPC ID specified.");
            return;
        }
        final MobGroup group = new MobGroup(groupId, template, mobCount);
        MobGroupTable.getInstance().addGroup(groupId, group);
        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, groupId));
    }
    
    private void removeGroup(final String command, final Player activeChar) {
        int groupId;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_remove <groupId>");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        this.doAnimation(activeChar);
        group.unspawnGroup();
        if (MobGroupTable.getInstance().removeGroup(groupId)) {
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, groupId));
        }
    }
    
    private void spawnGroup(final String command, final Player activeChar) {
        boolean topos = false;
        int posx = 0;
        int posy = 0;
        int posz = 0;
        int groupId;
        try {
            final String[] cmdParams = command.split(" ");
            groupId = Integer.parseInt(cmdParams[1]);
            try {
                posx = Integer.parseInt(cmdParams[2]);
                posy = Integer.parseInt(cmdParams[3]);
                posz = Integer.parseInt(cmdParams[4]);
                topos = true;
            }
            catch (Exception ex) {}
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_spawn <group> [ x y z ]");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        this.doAnimation(activeChar);
        if (topos) {
            group.spawnGroup(posx, posy, posz);
        }
        else {
            group.spawnGroup(activeChar);
        }
        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, groupId));
    }
    
    private void unspawnGroup(final String command, final Player activeChar) {
        int groupId;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_unspawn <groupId>");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        this.doAnimation(activeChar);
        group.unspawnGroup();
        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, groupId));
    }
    
    private void killGroup(final String command, final Player activeChar) {
        int groupId;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_kill <groupId>");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        this.doAnimation(activeChar);
        group.killGroup(activeChar);
    }
    
    private void setCasting(final String command, final Player activeChar) {
        int groupId;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_casting <groupId>");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        group.setCastMode();
    }
    
    private void noMove(final String command, final Player activeChar) {
        int groupId;
        String enabled;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
            enabled = command.split(" ")[2];
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_nomove <groupId> <on|off>");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        if (enabled.equalsIgnoreCase("on") || enabled.equalsIgnoreCase("true")) {
            group.setNoMoveMode(true);
        }
        else if (enabled.equalsIgnoreCase("off") || enabled.equalsIgnoreCase("false")) {
            group.setNoMoveMode(false);
        }
        else {
            BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
        }
    }
    
    private void doAnimation(final Player activeChar) {
        Broadcast.toSelfAndKnownPlayersInRadius((Creature)activeChar, (ServerPacket)new MagicSkillUse((Creature)activeChar, 1008, 1, 4000, 0), 1500);
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new SetupGauge(activeChar.getObjectId(), 0, 4000) });
    }
    
    private void attackGrp(final String command, final Player activeChar) {
        int groupId;
        int othGroupId;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
            othGroupId = Integer.parseInt(command.split(" ")[2]);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_attackgrp <groupId> <TargetGroupId>");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        final MobGroup othGroup = MobGroupTable.getInstance().getGroup(othGroupId);
        if (othGroup == null) {
            BuilderUtil.sendSysMessage(activeChar, "Incorrect target group.");
            return;
        }
        group.setAttackGroup(othGroup);
    }
    
    private void invul(final String command, final Player activeChar) {
        int groupId;
        String enabled;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
            enabled = command.split(" ")[2];
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_invul <groupId> <on|off>");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        if (enabled.equalsIgnoreCase("on") || enabled.equalsIgnoreCase("true")) {
            group.setInvul(true);
        }
        else if (enabled.equalsIgnoreCase("off") || enabled.equalsIgnoreCase("false")) {
            group.setInvul(false);
        }
        else {
            BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
        }
    }
    
    private void teleportGroup(final String command, final Player activeChar) {
        String targetPlayerStr = null;
        Player targetPlayer = null;
        int groupId;
        try {
            groupId = Integer.parseInt(command.split(" ")[1]);
            targetPlayerStr = command.split(" ")[2];
            if (targetPlayerStr != null) {
                targetPlayer = World.getInstance().findPlayer(targetPlayerStr);
            }
            if (targetPlayer == null) {
                targetPlayer = activeChar;
            }
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_teleport <groupId> [playerName]");
            return;
        }
        final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
        if (group == null) {
            BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
            return;
        }
        group.teleportGroup(activeChar);
    }
    
    private void showGroupList(final Player activeChar) {
        final MobGroup[] mobGroupList = MobGroupTable.getInstance().getGroups();
        BuilderUtil.sendSysMessage(activeChar, "======= <Mob Groups> =======");
        for (final MobGroup mobGroup : mobGroupList) {
            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(IIIILjava/lang/String;)Ljava/lang/String;, mobGroup.getGroupId(), mobGroup.getActiveMobCount(), mobGroup.getMaxMobCount(), mobGroup.getTemplate().getId(), mobGroup.getStatus()));
        }
        activeChar.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
    }
    
    public String[] getAdminCommandList() {
        return AdminMobGroup.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_mobmenu", "admin_mobgroup_list", "admin_mobgroup_create", "admin_mobgroup_remove", "admin_mobgroup_delete", "admin_mobgroup_spawn", "admin_mobgroup_unspawn", "admin_mobgroup_kill", "admin_mobgroup_idle", "admin_mobgroup_attack", "admin_mobgroup_rnd", "admin_mobgroup_return", "admin_mobgroup_follow", "admin_mobgroup_casting", "admin_mobgroup_nomove", "admin_mobgroup_attackgrp", "admin_mobgroup_invul" };
    }
}
