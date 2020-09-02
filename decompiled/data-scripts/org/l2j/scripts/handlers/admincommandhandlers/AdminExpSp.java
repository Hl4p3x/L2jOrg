// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.StringTokenizer;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminExpSp implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_add_exp_sp")) {
            try {
                final String val = command.substring(16);
                if (!this.adminAddExpSp(activeChar, val)) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //add_exp_sp exp sp");
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //add_exp_sp exp sp");
            }
        }
        else if (command.startsWith("admin_remove_exp_sp")) {
            try {
                final String val = command.substring(19);
                if (!this.adminRemoveExpSP(activeChar, val)) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //remove_exp_sp exp sp");
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //remove_exp_sp exp sp");
            }
        }
        this.addExpSp(activeChar);
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminExpSp.ADMIN_COMMANDS;
    }
    
    private void addExpSp(final Player activeChar) {
        final WorldObject target = activeChar.getTarget();
        Player player = null;
        if (GameUtils.isPlayer(target)) {
            player = (Player)target;
            final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
            adminReply.setFile(activeChar, "data/html/admin/expsp.htm");
            adminReply.replace("%name%", player.getName());
            adminReply.replace("%level%", String.valueOf(player.getLevel()));
            adminReply.replace("%xp%", String.valueOf(player.getExp()));
            adminReply.replace("%sp%", String.valueOf(player.getSp()));
            adminReply.replace("%class%", ClassListData.getInstance().getClass(player.getClassId()).getClientCode());
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
            return;
        }
        activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
    }
    
    private boolean adminAddExpSp(final Player activeChar, final String ExpSp) {
        final WorldObject target = activeChar.getTarget();
        Player player = null;
        if (!GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return false;
        }
        player = (Player)target;
        final StringTokenizer st = new StringTokenizer(ExpSp);
        if (st.countTokens() != 2) {
            return false;
        }
        final String exp = st.nextToken();
        final String sp = st.nextToken();
        long expval = 0L;
        long spval = 0L;
        try {
            expval = Long.parseLong(exp);
            spval = Long.parseLong(sp);
        }
        catch (Exception e) {
            return false;
        }
        if (expval != 0L || spval != 0L) {
            player.sendMessage(invokedynamic(makeConcatWithConstants:(JJ)Ljava/lang/String;, expval, spval));
            player.addExpAndSp((double)expval, (double)spval);
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(JJLjava/lang/String;)Ljava/lang/String;, expval, spval, player.getName()));
        }
        return true;
    }
    
    private boolean adminRemoveExpSP(final Player activeChar, final String ExpSp) {
        final WorldObject target = activeChar.getTarget();
        Player player = null;
        if (target == null || !GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return false;
        }
        player = (Player)target;
        final StringTokenizer st = new StringTokenizer(ExpSp);
        if (st.countTokens() != 2) {
            return false;
        }
        final String exp = st.nextToken();
        final String sp = st.nextToken();
        long expval = 0L;
        int spval = 0;
        try {
            expval = Long.parseLong(exp);
            spval = Integer.parseInt(sp);
        }
        catch (Exception e) {
            return false;
        }
        if (expval != 0L || spval != 0) {
            player.sendMessage(invokedynamic(makeConcatWithConstants:(JI)Ljava/lang/String;, expval, spval));
            player.removeExpAndSp(expval, (long)spval);
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(JILjava/lang/String;)Ljava/lang/String;, expval, spval, player.getName()));
        }
        return true;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_add_exp_sp_to_character", "admin_add_exp_sp", "admin_remove_exp_sp" };
    }
}
