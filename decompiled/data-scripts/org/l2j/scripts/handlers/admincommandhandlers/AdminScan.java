// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.html.IHtmlStyle;
import org.l2j.gameserver.model.html.styles.ButtonsStyle;
import org.l2j.gameserver.model.html.IBypassFormatter;
import org.l2j.gameserver.model.html.formatters.BypassParserFormatter;
import org.l2j.gameserver.model.html.IPageHandler;
import org.l2j.gameserver.model.html.pagehandlers.NextPrevPageHandler;
import java.util.Collection;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BypassBuilder;
import java.util.function.Predicate;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.BypassParser;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminScan implements IAdminCommandHandler
{
    private static final String SPACE = " ";
    private static final String[] ADMIN_COMMANDS;
    private static final int DEFAULT_RADIUS = 1000;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        final String lowerCase = actualCommand.toLowerCase();
        switch (lowerCase) {
            case "admin_scan": {
                this.processBypass(activeChar, new BypassParser(command));
                break;
            }
            case "admin_deletenpcbyobjectid": {
                if (!st.hasMoreElements()) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //deletenpcbyobjectid objectId=<object_id>");
                    return false;
                }
                final BypassParser parser = new BypassParser(command);
                try {
                    final int objectId = parser.getInt("objectId", 0);
                    if (objectId == 0) {
                        BuilderUtil.sendSysMessage(activeChar, "objectId is not set!");
                    }
                    final WorldObject target = World.getInstance().findObject(objectId);
                    final Npc npc = (target instanceof Npc) ? target : null;
                    if (npc == null) {
                        BuilderUtil.sendSysMessage(activeChar, "NPC does not exist or object_id does not belong to an NPC");
                        return false;
                    }
                    npc.deleteMe();
                    final Spawn spawn = npc.getSpawn();
                    if (spawn != null) {
                        spawn.stopRespawn();
                        if (DBSpawnManager.getInstance().isDefined(spawn.getId())) {
                            DBSpawnManager.getInstance().deleteSpawn(spawn, true);
                        }
                        else {
                            SpawnTable.getInstance().deleteSpawn(spawn, true);
                        }
                    }
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, npc.getName()));
                }
                catch (NumberFormatException e) {
                    BuilderUtil.sendSysMessage(activeChar, "object_id must be a number.");
                    return false;
                }
                this.processBypass(activeChar, parser);
                break;
            }
        }
        return true;
    }
    
    private void processBypass(final Player activeChar, final BypassParser parser) {
        final int id = parser.getInt("id", 0);
        final String name = parser.getString("name", (String)null);
        final int radius = parser.getInt("radius", parser.getInt("range", 1000));
        final int page = parser.getInt("page", 0);
        Predicate<Npc> condition;
        if (id > 0) {
            condition = (npc -> npc.getId() == id);
        }
        else if (name != null) {
            condition = (npc -> npc.getName().toLowerCase().startsWith(name.toLowerCase()));
        }
        else {
            condition = (npc -> true);
        }
        this.sendNpcList(activeChar, radius, page, condition, parser);
    }
    
    private BypassBuilder createBypassBuilder(final BypassParser parser, final String bypass) {
        final int id = parser.getInt("id", 0);
        final String name = parser.getString("name", (String)null);
        final int radius = parser.getInt("radius", parser.getInt("range", 1000));
        final BypassBuilder builder = new BypassBuilder(bypass);
        if (id > 0) {
            builder.addParam("id", (Object)id);
        }
        else if (name != null) {
            builder.addParam("name", (Object)name);
        }
        if (radius > 1000) {
            builder.addParam("radius", (Object)radius);
        }
        return builder;
    }
    
    private void sendNpcList(final Player activeChar, final int radius, final int page, final Predicate<Npc> condition, final BypassParser parser) {
        final BypassBuilder bypassParser = this.createBypassBuilder(parser, "bypass -h admin_scan");
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(activeChar, "data/html/admin/scan.htm");
        final PageResult result = PageBuilder.newBuilder((Collection)World.getInstance().getVisibleObjectsInRange((WorldObject)activeChar, (Class)Npc.class, radius, (Predicate)condition), 15, bypassParser.toString()).currentPage(page).pageHandler((IPageHandler)NextPrevPageHandler.INSTANCE).formatter((IBypassFormatter)BypassParserFormatter.INSTANCE).style((IHtmlStyle)ButtonsStyle.INSTANCE).bodyHandler((pages, character, sb) -> {
            final BypassBuilder builder = this.createBypassBuilder(parser, "bypass -h admin_deleteNpcByObjectId");
            final String npcName = character.getName();
            builder.addParam("page", (Object)page);
            builder.addParam("objectId", (Object)character.getObjectId());
            sb.append("<tr>");
            sb.append("<td width=\"45\">").append(character.getId()).append("</td>");
            sb.append("<td><a action=\"bypass -h admin_move_to ").append(character.getX()).append(" ").append(character.getY()).append(" ").append(character.getZ()).append("\">").append(npcName.isEmpty() ? "No name NPC" : npcName).append("</a></td>");
            sb.append("<td width=\"60\">").append(GameUtils.formatAdena(Math.round(MathUtil.calculateDistance2D((ILocational)activeChar, (ILocational)character)))).append("</td>");
            sb.append("<td width=\"54\"><a action=\"").append((CharSequence)builder.toStringBuilder()).append("\"><font color=\"LEVEL\">Delete</font></a></td>");
            sb.append("</tr>");
        }).build();
        if (result.getPages() > 0) {
            html.replace("%pages%", invokedynamic(makeConcatWithConstants:(Ljava/lang/StringBuilder;)Ljava/lang/String;, result.getPagerTemplate()));
        }
        else {
            html.replace("%pages%", "");
        }
        html.replace("%data%", result.getBodyTemplate().toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    public String[] getAdminCommandList() {
        return AdminScan.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_scan", "admin_deleteNpcByObjectId" };
    }
}
