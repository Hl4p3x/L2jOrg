// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.html.IHtmlStyle;
import org.l2j.gameserver.model.html.styles.ButtonsStyle;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Fence;
import org.l2j.gameserver.world.World;
import java.util.NoSuchElementException;
import org.l2j.gameserver.enums.FenceState;
import org.l2j.gameserver.data.xml.FenceDataManager;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminFence implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String nextToken;
        final String cmd = nextToken = st.nextToken();
        switch (nextToken) {
            case "admin_addfence": {
                try {
                    final int width = Integer.parseInt(st.nextToken());
                    final int length = Integer.parseInt(st.nextToken());
                    final int height = Integer.parseInt(st.nextToken());
                    if (width < 1 || length < 1) {
                        BuilderUtil.sendSysMessage(activeChar, "Width and length values must be positive numbers.");
                        return false;
                    }
                    if (height < 1 || height > 3) {
                        BuilderUtil.sendSysMessage(activeChar, "The range for height can only be 1-3.");
                        return false;
                    }
                    FenceDataManager.getInstance().spawnFence(activeChar.getX(), activeChar.getY(), activeChar.getZ(), width, length, height, activeChar.getInstanceId(), FenceState.CLOSED);
                    BuilderUtil.sendSysMessage(activeChar, "Fence added succesfully.");
                }
                catch (NoSuchElementException | NumberFormatException ex3) {
                    final RuntimeException ex;
                    final RuntimeException e = ex;
                    BuilderUtil.sendSysMessage(activeChar, "Format must be: //addfence <width> <length> <height>");
                }
                break;
            }
            case "admin_setfencestate": {
                try {
                    final int objId = Integer.parseInt(st.nextToken());
                    final int fenceTypeOrdinal = Integer.parseInt(st.nextToken());
                    if (fenceTypeOrdinal < 0 || fenceTypeOrdinal >= FenceState.values().length) {
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, FenceState.values().length - 1));
                    }
                    else {
                        final WorldObject obj = World.getInstance().findObject(objId);
                        if (obj instanceof Fence) {
                            final Fence fence = (Fence)obj;
                            final FenceState state = FenceState.values()[fenceTypeOrdinal];
                            fence.setState(state);
                            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, fence.getName(), fence.getId(), state.toString()));
                        }
                        else {
                            BuilderUtil.sendSysMessage(activeChar, "Target is not a fence.");
                        }
                    }
                }
                catch (NoSuchElementException | NumberFormatException ex4) {
                    final RuntimeException ex2;
                    final RuntimeException e = ex2;
                    BuilderUtil.sendSysMessage(activeChar, "Format mustr be: //setfencestate <fenceObjectId> <fenceState>");
                }
                break;
            }
            case "admin_removefence": {
                try {
                    final int objId = Integer.parseInt(st.nextToken());
                    final WorldObject obj2 = World.getInstance().findObject(objId);
                    if (obj2 instanceof Fence) {
                        ((Fence)obj2).deleteMe();
                        BuilderUtil.sendSysMessage(activeChar, "Fence removed succesfully.");
                    }
                    else {
                        BuilderUtil.sendSysMessage(activeChar, "Target is not a fence.");
                    }
                }
                catch (Exception e2) {
                    BuilderUtil.sendSysMessage(activeChar, "Invalid object ID or target was not found.");
                }
                sendHtml(activeChar, 0);
                break;
            }
            case "admin_listfence": {
                int page = 0;
                if (st.hasMoreTokens()) {
                    page = Integer.parseInt(st.nextToken());
                }
                sendHtml(activeChar, page);
                break;
            }
            case "admin_gofence": {
                try {
                    final int objId = Integer.parseInt(st.nextToken());
                    final WorldObject obj2 = World.getInstance().findObject(objId);
                    if (obj2 != null) {
                        activeChar.teleToLocation((ILocational)obj2);
                    }
                }
                catch (Exception e2) {
                    BuilderUtil.sendSysMessage(activeChar, "Invalid object ID or target was not found.");
                }
                break;
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminFence.ADMIN_COMMANDS;
    }
    
    private static void sendHtml(final Player activeChar, final int page) {
        final PageResult result = PageBuilder.newBuilder(FenceDataManager.getInstance().getFences().values(), 10, "bypass -h admin_listfence").currentPage(page).style((IHtmlStyle)ButtonsStyle.INSTANCE).bodyHandler((pages, fence, sb) -> {
            sb.append("<tr><td>");
            sb.append((fence.getName() == null) ? Integer.valueOf(fence.getId()) : fence.getName());
            sb.append("</td><td>");
            sb.append("<button value=\"Go\" action=\"bypass -h admin_gofence ");
            sb.append(fence.getId());
            sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
            sb.append("</td><td>");
            sb.append("<button value=\"Hide\" action=\"bypass -h admin_setfencestate ");
            sb.append(fence.getId());
            sb.append(" 0");
            sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
            sb.append("</td><td>");
            sb.append("<button value=\"Off\" action=\"bypass -h admin_setfencestate ");
            sb.append(fence.getId());
            sb.append(" 1");
            sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
            sb.append("</td><td>");
            sb.append("<button value=\"On\" action=\"bypass -h admin_setfencestate ");
            sb.append(fence.getId());
            sb.append(" 2");
            sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
            sb.append("</td><td>");
            sb.append("<button value=\"X\" action=\"bypass -h admin_removefence ");
            sb.append(fence.getId());
            sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
            sb.append("</td></tr>");
        }).build();
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(activeChar, "data/html/admin/fences.htm");
        if (result.getPages() > 0) {
            html.replace("%pages%", invokedynamic(makeConcatWithConstants:(Ljava/lang/StringBuilder;)Ljava/lang/String;, result.getPagerTemplate()));
        }
        else {
            html.replace("%pages%", "");
        }
        html.replace("%fences%", result.getBodyTemplate().toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_addfence", "admin_setfencestate", "admin_removefence", "admin_listfence", "admin_gofence" };
    }
}
