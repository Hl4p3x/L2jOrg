// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.model.html.IHtmlStyle;
import org.l2j.gameserver.model.html.styles.ButtonsStyle;
import org.l2j.gameserver.model.html.IBypassFormatter;
import org.l2j.gameserver.model.html.formatters.BypassParserFormatter;
import org.l2j.gameserver.model.html.IPageHandler;
import org.l2j.gameserver.model.html.pagehandlers.NextPrevPageHandler;
import org.l2j.gameserver.model.html.PageBuilder;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.List;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.Collection;
import java.util.ArrayList;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.util.BypassParser;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public final class AdminInstance implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    private static final int[] IGNORED_TEMPLATES;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        final String lowerCase = actualCommand.toLowerCase();
        switch (lowerCase) {
            case "admin_instance":
            case "admin_instances": {
                final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
                html.setFile(activeChar, "data/html/admin/instances.htm");
                html.replace("%instCount%", InstanceManager.getInstance().getInstances().size());
                html.replace("%tempCount%", InstanceManager.getInstance().getInstanceTemplates().size());
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
                break;
            }
            case "admin_instancelist": {
                this.processBypass(activeChar, new BypassParser(command));
                break;
            }
            case "admin_instancecreate": {
                final int templateId = Util.parseNextInt(st, 0);
                final InstanceTemplate template = InstanceManager.getInstance().getInstanceTemplate(templateId);
                if (template != null) {
                    final String enterGroup = st.hasMoreTokens() ? st.nextToken() : "Alone";
                    final List<Player> members = new ArrayList<Player>();
                    final String s = enterGroup;
                    switch (s) {
                        case "Alone": {
                            members.add(activeChar);
                            break;
                        }
                        case "Party": {
                            if (activeChar.isInParty()) {
                                members.addAll(activeChar.getParty().getMembers());
                                break;
                            }
                            members.add(activeChar);
                            break;
                        }
                        case "CommandChannel": {
                            if (activeChar.isInCommandChannel()) {
                                members.addAll(activeChar.getParty().getCommandChannel().getMembers());
                                break;
                            }
                            if (activeChar.isInParty()) {
                                members.addAll(activeChar.getParty().getMembers());
                                break;
                            }
                            members.add(activeChar);
                            break;
                        }
                        default: {
                            BuilderUtil.sendSysMessage(activeChar, "Wrong enter group usage! Please use those values: Alone, Party or CommandChannel.");
                            return true;
                        }
                    }
                    final Instance instance = InstanceManager.getInstance().createInstance(template, activeChar);
                    final Location loc = instance.getEnterLocation();
                    if (loc != null) {
                        for (final Player players : members) {
                            instance.addAllowed(players);
                            players.teleToLocation((ILocational)loc, instance);
                        }
                    }
                    this.sendTemplateDetails(activeChar, instance.getTemplateId());
                    break;
                }
                BuilderUtil.sendSysMessage(activeChar, "Wrong parameters! Please try again.");
                return true;
            }
            case "admin_instanceteleport": {
                final Instance instance2 = InstanceManager.getInstance().getInstance(Util.parseNextInt(st, -1));
                if (instance2 != null) {
                    final Location loc2 = instance2.getEnterLocation();
                    if (loc2 != null) {
                        if (!instance2.isAllowed(activeChar)) {
                            instance2.addAllowed(activeChar);
                        }
                        activeChar.teleToLocation((ILocational)loc2, false);
                        activeChar.setInstance(instance2);
                        this.sendTemplateDetails(activeChar, instance2.getTemplateId());
                    }
                    break;
                }
                break;
            }
            case "admin_instancedestroy": {
                final Instance instance2 = InstanceManager.getInstance().getInstance(Util.parseNextInt(st, -1));
                if (instance2 != null) {
                    instance2.getPlayers().forEach(player -> player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage("Your instance has been destroyed by Game Master!", 10000) }));
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, instance2.getId(), instance2.getPlayersCount()));
                    instance2.destroy();
                    this.sendTemplateDetails(activeChar, instance2.getTemplateId());
                    break;
                }
                break;
            }
        }
        return true;
    }
    
    private void sendTemplateDetails(final Player player, final int templateId) {
        if (InstanceManager.getInstance().getInstanceTemplate(templateId) != null) {
            final InstanceTemplate template = InstanceManager.getInstance().getInstanceTemplate(templateId);
            final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
            final StringBuilder sb = new StringBuilder();
            html.setFile(player, "data/html/admin/instances_detail.htm");
            html.replace("%templateId%", template.getId());
            html.replace("%templateName%", template.getName());
            html.replace("%activeWorlds%", invokedynamic(makeConcatWithConstants:(JLjava/io/Serializable;)Ljava/lang/String;, template.getWorldCount(), (template.getMaxWorlds() == -1) ? "Unlimited" : Integer.valueOf(template.getMaxWorlds())));
            html.replace("%duration%", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, template.getDuration()));
            html.replace("%emptyDuration%", invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, TimeUnit.MILLISECONDS.toMinutes(template.getEmptyDestroyTime())));
            html.replace("%ejectDuration%", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, template.getEjectTime()));
            html.replace("%removeBuff%", template.isRemoveBuffEnabled());
            sb.append("<table border=0 cellpadding=2 cellspacing=0 bgcolor=\"363636\">");
            sb.append("<tr>");
            sb.append("<td fixwidth=\"83\"><font color=\"LEVEL\">Instance ID</font></td>");
            sb.append("<td fixwidth=\"83\"><font color=\"LEVEL\">Teleport</font></td>");
            sb.append("<td fixwidth=\"83\"><font color=\"LEVEL\">Destroy</font></td>");
            sb.append("</tr>");
            sb.append("</table>");
            final StringBuilder sb2;
            InstanceManager.getInstance().getInstances().stream().filter(inst -> inst.getTemplateId() == templateId).sorted(Comparator.comparingInt(Instance::getPlayersCount)).forEach(instance -> {
                sb2.append("<table border=0 cellpadding=2 cellspacing=0 bgcolor=\"363636\">");
                sb2.append("<tr>");
                sb2.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, instance.getId()));
                sb2.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, instance.getId()));
                sb2.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, instance.getId()));
                sb2.append("</tr>");
                sb2.append("</table>");
                return;
            });
            html.replace("%instanceList%", sb.toString());
            player.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        else {
            player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, templateId));
            this.useAdminCommand("admin_instance", player);
        }
    }
    
    private void sendTemplateList(final Player player, final int page, final BypassParser parser) {
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(player, "data/html/admin/instances_list.htm");
        final InstanceManager instManager = InstanceManager.getInstance();
        final List<InstanceTemplate> templateList = instManager.getInstanceTemplates().stream().sorted(Comparator.comparingLong(InstanceTemplate::getWorldCount).reversed()).filter(template -> !Util.contains(AdminInstance.IGNORED_TEMPLATES, template.getId())).collect((Collector<? super Object, ?, List<InstanceTemplate>>)Collectors.toList());
        final PageResult result = PageBuilder.newBuilder((Collection)templateList, 4, "bypass -h admin_instancelist").currentPage(page).pageHandler((IPageHandler)NextPrevPageHandler.INSTANCE).formatter((IBypassFormatter)BypassParserFormatter.INSTANCE).style((IHtmlStyle)ButtonsStyle.INSTANCE).bodyHandler((pages, template, sb) -> {
            sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, template.getName(), template.getId()));
            sb.append("</table>");
            sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
            sb.append("<tr>");
            sb.append("<td align=center fixwidth=\"83\">Active worlds:</td>");
            sb.append("<td align=center fixwidth=\"83\"></td>");
            sb.append(invokedynamic(makeConcatWithConstants:(JLjava/io/Serializable;)Ljava/lang/String;, template.getWorldCount(), (template.getMaxWorlds() == -1) ? "Unlimited" : Integer.valueOf(template.getMaxWorlds())));
            sb.append("</tr>");
            sb.append("<tr>");
            sb.append("<td align=center fixwidth=\"83\">Detailed info:</td>");
            sb.append("<td align=center fixwidth=\"83\"></td>");
            sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, template.getId()));
            sb.append("</tr>");
            sb.append("</table>");
            sb.append("<br>");
        }).build();
        html.replace("%pages%", (result.getPages() > 0) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/StringBuilder;)Ljava/lang/String;, result.getPagerTemplate()) : "");
        html.replace("%data%", result.getBodyTemplate().toString());
        player.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    private void processBypass(final Player player, final BypassParser parser) {
        final int page = parser.getInt("page", 0);
        final int templateId = parser.getInt("id", 0);
        if (templateId > 0) {
            this.sendTemplateDetails(player, templateId);
        }
        else {
            this.sendTemplateList(player, page, parser);
        }
    }
    
    public String[] getAdminCommandList() {
        return AdminInstance.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_instance", "admin_instances", "admin_instancelist", "admin_instancecreate", "admin_instanceteleport", "admin_instancedestroy" };
        IGNORED_TEMPLATES = new int[] { 127, 128, 129, 130, 131, 132, 147, 149, 150, 148 };
    }
}
