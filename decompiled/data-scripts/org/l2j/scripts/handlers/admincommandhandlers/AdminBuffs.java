// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.effects.AbstractEffect;
import java.util.Set;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.html.PageResult;
import java.util.List;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.html.IHtmlStyle;
import org.l2j.gameserver.model.html.styles.ButtonsStyle;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminBuffs implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    private static final String FONT_RED1 = "<font color=\"FF0000\">";
    private static final String FONT_RED2 = "</font>";
    
    public boolean useAdminCommand(String command, final Player activeChar) {
        if (command.startsWith("admin_buff")) {
            if (!GameUtils.isCreature(activeChar.getTarget())) {
                activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                return false;
            }
            final StringTokenizer st = new StringTokenizer(command, " ");
            command = st.nextToken();
            if (!st.hasMoreTokens()) {
                BuilderUtil.sendSysMessage(activeChar, "Skill Id and level are not specified.");
                BuilderUtil.sendSysMessage(activeChar, "Usage: //buff <skillId> <skillLevel>");
                return false;
            }
            try {
                final int skillId = Integer.parseInt(st.nextToken());
                final int skillLevel = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : SkillEngine.getInstance().getMaxLevel(skillId);
                final Creature target = (Creature)activeChar.getTarget();
                final Skill skill = SkillEngine.getInstance().getSkill(skillId, skillLevel);
                if (skill == null) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, skillId, skillLevel));
                    return false;
                }
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, skill.getName(), skillId, skillLevel));
                skill.applyEffects((Creature)activeChar, target);
                return true;
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
                BuilderUtil.sendSysMessage(activeChar, "Usage: //buff <skillId> <skillLevel>");
                return false;
            }
        }
        if (command.startsWith("admin_getbuffs")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            command = st.nextToken();
            if (st.hasMoreTokens()) {
                final String playername = st.nextToken();
                final Player player = World.getInstance().findPlayer(playername);
                if (player != null) {
                    int page = 0;
                    if (st.hasMoreTokens()) {
                        page = Integer.parseInt(st.nextToken());
                    }
                    showBuffs(activeChar, (Creature)player, page, command.endsWith("_ps"));
                    return true;
                }
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, playername));
                return false;
            }
            else {
                if (GameUtils.isCreature(activeChar.getTarget())) {
                    showBuffs(activeChar, (Creature)activeChar.getTarget(), 0, command.endsWith("_ps"));
                    return true;
                }
                activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                return false;
            }
        }
        else {
            if (command.startsWith("admin_stopbuff")) {
                try {
                    final StringTokenizer st = new StringTokenizer(command, " ");
                    st.nextToken();
                    final int objectId = Integer.parseInt(st.nextToken());
                    final int skillId2 = Integer.parseInt(st.nextToken());
                    removeBuff(activeChar, objectId, skillId2);
                    return true;
                }
                catch (Exception e2) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e2.getMessage()));
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //stopbuff <objectId> <skillId>");
                    return false;
                }
            }
            if (command.startsWith("admin_stopallbuffs")) {
                try {
                    final StringTokenizer st = new StringTokenizer(command, " ");
                    st.nextToken();
                    final int objectId = Integer.parseInt(st.nextToken());
                    removeAllBuffs(activeChar, objectId);
                    return true;
                }
                catch (Exception e2) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e2.getMessage()));
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //stopallbuffs <objectId>");
                    return false;
                }
            }
            if (command.startsWith("admin_viewblockedeffects")) {
                try {
                    final StringTokenizer st = new StringTokenizer(command, " ");
                    st.nextToken();
                    final int objectId = Integer.parseInt(st.nextToken());
                    viewBlockedEffects(activeChar, objectId);
                    return true;
                }
                catch (Exception e2) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e2.getMessage()));
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //viewblockedeffects <objectId>");
                    return false;
                }
            }
            if (command.startsWith("admin_areacancel")) {
                final StringTokenizer st = new StringTokenizer(command, " ");
                st.nextToken();
                final String val = st.nextToken();
                try {
                    final int radius = Integer.parseInt(val);
                    World.getInstance().forEachVisibleObjectInRange((WorldObject)activeChar, (Class)Player.class, radius, Creature::stopAllEffects);
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, radius));
                    return true;
                }
                catch (NumberFormatException e3) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //areacancel <radius>");
                    return false;
                }
            }
            if (command.startsWith("admin_removereuse")) {
                final StringTokenizer st = new StringTokenizer(command, " ");
                command = st.nextToken();
                Player player2 = null;
                if (st.hasMoreTokens()) {
                    final String playername2 = st.nextToken();
                    try {
                        player2 = World.getInstance().findPlayer(playername2);
                    }
                    catch (Exception ex) {}
                    if (player2 == null) {
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, playername2));
                        return false;
                    }
                }
                else {
                    if (activeChar.getTarget() == null || !GameUtils.isPlayer(activeChar.getTarget())) {
                        activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                        return false;
                    }
                    player2 = activeChar.getTarget().getActingPlayer();
                }
                try {
                    player2.resetTimeStamps();
                    player2.resetDisabledSkills();
                    player2.sendPacket(new ServerPacket[] { (ServerPacket)new SkillCoolTime(player2) });
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player2.getName()));
                    return true;
                }
                catch (NullPointerException e4) {
                    return false;
                }
            }
            if (!command.startsWith("admin_switch_gm_buffs")) {
                return true;
            }
            if (Config.GM_GIVE_SPECIAL_SKILLS != Config.GM_GIVE_SPECIAL_AURA_SKILLS) {
                final boolean toAuraSkills = activeChar.getKnownSkill(7041) != null;
                switchSkills(activeChar, toAuraSkills);
                activeChar.sendSkillList();
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, toAuraSkills ? "aura" : "one"));
                return true;
            }
            BuilderUtil.sendSysMessage(activeChar, "There is nothing to switch.");
            return false;
        }
    }
    
    private static void switchSkills(final Player gmchar, final boolean toAuraSkills) {
        final Collection<Skill> skills = (Collection<Skill>)(toAuraSkills ? SkillTreesData.getInstance().getGMSkillTree() : SkillTreesData.getInstance().getGMAuraSkillTree());
        for (final Skill skill : skills) {
            gmchar.removeSkill(skill, false);
        }
        SkillTreesData.getInstance().addSkills(gmchar, toAuraSkills);
    }
    
    public String[] getAdminCommandList() {
        return AdminBuffs.ADMIN_COMMANDS;
    }
    
    private static void showBuffs(final Player activeChar, final Creature target, final int page, final boolean passive) {
        final List<BuffInfo> effects = new ArrayList<BuffInfo>();
        if (!passive) {
            effects.addAll(target.getEffectList().getEffects());
        }
        else {
            effects.addAll(target.getEffectList().getPassives());
        }
        final String pageLink = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, passive ? "_ps " : " ", target.getName());
        final PageResult result = PageBuilder.newBuilder((Collection)effects, 3, pageLink).currentPage(page).style((IHtmlStyle)ButtonsStyle.INSTANCE).bodyHandler((pages, info, sb) -> {
            for (final AbstractEffect effect : info.getEffects()) {
                sb.append("<tr><td>");
                sb.append(info.isInUse() ? "" : "<font color=\"FF0000\">");
                sb.append(info.getSkill().getName());
                sb.append(" Lv ");
                sb.append(info.getSkill().getLevel());
                sb.append(" (");
                sb.append(effect.getClass().getSimpleName());
                sb.append(")");
                sb.append(info.isInUse() ? "" : "</font>");
                sb.append("</td><td>");
                sb.append(info.getSkill().isToggle() ? "T" : (info.getSkill().isPassive() ? "P" : invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, info.getTime())));
                sb.append("</td><td><button value=\"X\" action=\"bypass -h admin_stopbuff ");
                sb.append(target.getObjectId());
                sb.append(" ");
                sb.append(info.getSkill().getId());
                sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
            }
        }).build();
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(activeChar, "data/html/admin/getbuffs.htm");
        if (result.getPages() > 0) {
            html.replace("%pages%", invokedynamic(makeConcatWithConstants:(Ljava/lang/StringBuilder;)Ljava/lang/String;, result.getPagerTemplate()));
        }
        else {
            html.replace("%pages%", "");
        }
        html.replace("%targetName%", target.getName());
        html.replace("%targetObjId%", target.getObjectId());
        html.replace("%buffs%", result.getBodyTemplate().toString());
        html.replace("%effectSize%", effects.size());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).auditGM()) {
            GMAudit.auditGMAction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, activeChar.getName(), activeChar.getObjectId()), "getbuffs", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, target.getName(), target.getObjectId()), "");
        }
    }
    
    private static void removeBuff(final Player activeChar, final int objId, final int skillId) {
        Creature target = null;
        try {
            target = (Creature)World.getInstance().findObject(objId);
        }
        catch (Exception ex) {}
        if (target != null && skillId > 0) {
            if (target.isAffectedBySkill(skillId)) {
                target.stopSkillEffects(true, skillId);
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;I)Ljava/lang/String;, skillId, target.getName(), objId));
            }
            showBuffs(activeChar, target, 0, false);
            if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).auditGM()) {
                GMAudit.auditGMAction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, activeChar.getName(), activeChar.getObjectId()), "stopbuff", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, target.getName(), objId), Integer.toString(skillId));
            }
        }
    }
    
    private static void removeAllBuffs(final Player activeChar, final int objId) {
        Creature target = null;
        try {
            target = (Creature)World.getInstance().findObject(objId);
        }
        catch (Exception ex) {}
        if (target != null) {
            target.stopAllEffects();
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, target.getName(), objId));
            showBuffs(activeChar, target, 0, false);
            if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).auditGM()) {
                GMAudit.auditGMAction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, activeChar.getName(), activeChar.getObjectId()), "stopallbuffs", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, target.getName(), objId), "");
            }
        }
    }
    
    private static void viewBlockedEffects(final Player activeChar, final int objId) {
        Creature target = null;
        try {
            target = (Creature)World.getInstance().findObject(objId);
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, objId));
            return;
        }
        if (target != null) {
            final Set<AbnormalType> blockedAbnormals = (Set<AbnormalType>)target.getEffectList().getBlockedAbnormalTypes();
            final int blockedAbnormalsSize = (blockedAbnormals != null) ? blockedAbnormals.size() : 0;
            final StringBuilder html = new StringBuilder(500 + blockedAbnormalsSize * 50);
            html.append("<html><table width=\"100%\"><tr><td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=180><center><font color=\"LEVEL\">Blocked effects of ");
            html.append(target.getName());
            html.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, GameUtils.isPlayer((WorldObject)target) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, target.getName()) : ""));
            if (blockedAbnormals != null && !blockedAbnormals.isEmpty()) {
                html.append("<br>Blocked buff slots: ");
                for (final AbnormalType slot : blockedAbnormals) {
                    html.append("<br>").append(slot.toString());
                }
            }
            html.append("</html>");
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new NpcHtmlMessage(0, 1, html.toString()) });
            if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).auditGM()) {
                GMAudit.auditGMAction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, activeChar.getName(), activeChar.getObjectId()), "viewblockedeffects", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, target.getName(), Integer.toString(target.getObjectId())), "");
            }
        }
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_buff", "admin_getbuffs", "admin_getbuffs_ps", "admin_stopbuff", "admin_stopallbuffs", "admin_viewblockedeffects", "admin_areacancel", "admin_removereuse", "admin_switch_gm_buffs" };
    }
}
