// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.function.Predicate;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ExPCCafePointInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public final class AdminPcCafePoints implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        if (actualCommand.equals("admin_pccafepoints")) {
            if (st.hasMoreTokens()) {
                final String action = st.nextToken();
                final Player target = this.getTarget(activeChar);
                if (target == null || !st.hasMoreTokens()) {
                    return false;
                }
                int value;
                try {
                    value = Integer.parseInt(st.nextToken());
                }
                catch (Exception e) {
                    this.showMenuHtml(activeChar);
                    BuilderUtil.sendSysMessage(activeChar, "Invalid Value!");
                    return false;
                }
                final String s = action;
                switch (s) {
                    case "set": {
                        if (value > Config.PC_CAFE_MAX_POINTS) {
                            this.showMenuHtml(activeChar);
                            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Config.PC_CAFE_MAX_POINTS));
                            return false;
                        }
                        if (value < 0) {
                            value = 0;
                        }
                        target.setPcCafePoints(value);
                        target.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, value));
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, value, target.getName()));
                        target.sendPacket(new ServerPacket[] { (ServerPacket)new ExPCCafePointInfo(value, value, 1) });
                        break;
                    }
                    case "increase": {
                        if (target.getPcCafePoints() == Config.PC_CAFE_MAX_POINTS) {
                            this.showMenuHtml(activeChar);
                            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, target.getName()));
                            return false;
                        }
                        int pcCafeCount = Math.min(target.getPcCafePoints() + value, Config.PC_CAFE_MAX_POINTS);
                        if (pcCafeCount < 0) {
                            pcCafeCount = Config.PC_CAFE_MAX_POINTS;
                        }
                        target.setPcCafePoints(pcCafeCount);
                        target.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, value));
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, target.getName(), value));
                        target.sendPacket(new ServerPacket[] { (ServerPacket)new ExPCCafePointInfo(pcCafeCount, value, 1) });
                        break;
                    }
                    case "decrease": {
                        if (target.getPcCafePoints() == 0) {
                            this.showMenuHtml(activeChar);
                            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, target.getName()));
                            return false;
                        }
                        final int pcCafeCount = Math.max(target.getPcCafePoints() - value, 0);
                        target.setPcCafePoints(pcCafeCount);
                        target.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, value));
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, target.getName(), value));
                        target.sendPacket(new ServerPacket[] { (ServerPacket)new ExPCCafePointInfo(target.getPcCafePoints(), -value, 1) });
                        break;
                    }
                    case "rewardOnline": {
                        int range = 0;
                        try {
                            range = Integer.parseInt(st.nextToken());
                        }
                        catch (Exception ex) {}
                        final int points = value;
                        if (range <= 0) {
                            World.getInstance().getPlayers().forEach(player -> this.increasePoints(player, points));
                            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, value));
                            break;
                        }
                        World.getInstance().forEachPlayerInRange((WorldObject)activeChar, range, player -> this.increasePoints(player, points), (Predicate)this::canReceivePoints);
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, range, value));
                        break;
                    }
                }
            }
            this.showMenuHtml(activeChar);
        }
        return true;
    }
    
    private boolean canReceivePoints(final Player player) {
        return player.isOnlineInt() == 1 && player.getPcCafePoints() < Integer.MAX_VALUE;
    }
    
    private void increasePoints(final Player player, final int points) {
        int pcCafeCount = Math.min(player.getPcCafePoints() + points, Integer.MAX_VALUE);
        if (pcCafeCount < 0) {
            pcCafeCount = Integer.MAX_VALUE;
        }
        player.setPcCafePoints(pcCafeCount);
        player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, points));
        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExPCCafePointInfo(pcCafeCount, points, 1) });
    }
    
    private Player getTarget(final Player activeChar) {
        return (activeChar.getTarget() != null && activeChar.getTarget().getActingPlayer() != null) ? activeChar.getTarget().getActingPlayer() : activeChar;
    }
    
    private void showMenuHtml(final Player activeChar) {
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        final Player target = this.getTarget(activeChar);
        final int points = target.getPcCafePoints();
        html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/pccafe.htm"));
        html.replace("%points%", GameUtils.formatAdena((long)points));
        html.replace("%targetName%", target.getName());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    public String[] getAdminCommandList() {
        return AdminPcCafePoints.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_pccafepoints" };
    }
}
