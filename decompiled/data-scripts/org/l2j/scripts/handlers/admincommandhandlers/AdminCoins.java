// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.function.Predicate;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public final class AdminCoins implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        if (actualCommand.equals("admin_coins")) {
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
                        target.setNCoins(value);
                        target.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, value));
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, value, target.getName()));
                        break;
                    }
                    case "increase": {
                        if (target.getNCoins() == Integer.MAX_VALUE) {
                            this.showMenuHtml(activeChar);
                            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, target.getName()));
                            return false;
                        }
                        this.updateCoin(target, value);
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, target.getName(), value));
                        break;
                    }
                    case "decrease": {
                        if (target.getNCoins() == 0) {
                            this.showMenuHtml(activeChar);
                            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, target.getName()));
                            return false;
                        }
                        target.updateNCoins(-value);
                        target.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, value));
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, target.getName(), value));
                        break;
                    }
                    case "rewardOnline": {
                        int range = 0;
                        try {
                            range = Integer.parseInt(st.nextToken());
                        }
                        catch (Exception ex) {}
                        final int coinCount = value;
                        if (range <= 0) {
                            World.getInstance().getPlayers().forEach(player -> this.updateCoin(player, coinCount));
                            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, value));
                            break;
                        }
                        World.getInstance().forEachPlayerInRange((WorldObject)activeChar, range, player -> this.updateCoin(player, coinCount), (Predicate)this::canReceiveCoin);
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, range, value));
                        break;
                    }
                }
            }
            this.showMenuHtml(activeChar);
        }
        return true;
    }
    
    private boolean canReceiveCoin(final Player player) {
        return player.isOnline() && player.getNCoins() < Integer.MAX_VALUE;
    }
    
    private void updateCoin(final Player player, final int coinCount) {
        player.updateNCoins(coinCount);
        player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, coinCount));
    }
    
    private Player getTarget(final Player activeChar) {
        return (activeChar.getTarget() != null && activeChar.getTarget().getActingPlayer() != null) ? activeChar.getTarget().getActingPlayer() : activeChar;
    }
    
    private void showMenuHtml(final Player activeChar) {
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        final Player target = this.getTarget(activeChar);
        html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/coins.htm"));
        html.replace("%coins%", GameUtils.formatAdena((long)target.getNCoins()));
        html.replace("%targetName%", target.getName());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    public String[] getAdminCommandList() {
        return AdminCoins.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_coins" };
    }
}
