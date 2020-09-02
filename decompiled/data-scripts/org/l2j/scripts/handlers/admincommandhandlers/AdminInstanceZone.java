// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import io.github.joealisson.primitive.maps.IntLongMap;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.world.World;
import java.util.StringTokenizer;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminInstanceZone implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(String command, final Player activeChar) {
        final String target = (activeChar.getTarget() != null) ? activeChar.getTarget().getName() : "no-target";
        GMAudit.auditGMAction(activeChar.getName(), command, target, "");
        if (command.startsWith("admin_instancezone_clear")) {
            try {
                final StringTokenizer st = new StringTokenizer(command, " ");
                st.nextToken();
                final Player player = World.getInstance().findPlayer(st.nextToken());
                final int instanceId = Integer.parseInt(st.nextToken());
                final String name = InstanceManager.getInstance().getInstanceName(instanceId);
                InstanceManager.getInstance().deleteInstanceTime(player, instanceId);
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, player.getName()));
                player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
                this.display(activeChar, activeChar);
                return true;
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
                BuilderUtil.sendSysMessage(activeChar, "Usage: //instancezone_clear <playername> [instanceId]");
                return false;
            }
        }
        if (command.startsWith("admin_instancezone")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            command = st.nextToken();
            if (st.hasMoreTokens()) {
                Player player = null;
                final String playername = st.nextToken();
                try {
                    player = World.getInstance().findPlayer(playername);
                }
                catch (Exception ex) {}
                if (player == null) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, playername));
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //instancezone [playername]");
                    return false;
                }
                this.display(player, activeChar);
            }
            else if (activeChar.getTarget() != null) {
                if (GameUtils.isPlayer(activeChar.getTarget())) {
                    this.display((Player)activeChar.getTarget(), activeChar);
                }
            }
            else {
                this.display(activeChar, activeChar);
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminInstanceZone.ADMIN_COMMANDS;
    }
    
    private void display(final Player player, final Player activeChar) {
        final IntLongMap instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(player);
        final StringBuilder html = new StringBuilder(500 + instanceTimes.size() * 200);
        html.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
        int hours;
        int minutes;
        final IntLongMap intLongMap;
        final long remainingTime;
        final StringBuilder sb;
        instanceTimes.keySet().forEach(id -> {
            hours = 0;
            minutes = 0;
            remainingTime = (intLongMap.get(id) - System.currentTimeMillis()) / 1000L;
            if (remainingTime > 0L) {
                hours = (int)(remainingTime / 3600L);
                minutes = (int)(remainingTime % 3600L / 60L);
            }
            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IILjava/lang/String;I)Ljava/lang/String;, InstanceManager.getInstance().getInstanceName(id), hours, minutes, player.getName(), id));
            return;
        });
        html.append("</table></html>");
        final NpcHtmlMessage ms = new NpcHtmlMessage(0, 1);
        ms.setHtml(html.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)ms });
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_instancezone", "admin_instancezone_clear" };
    }
}
