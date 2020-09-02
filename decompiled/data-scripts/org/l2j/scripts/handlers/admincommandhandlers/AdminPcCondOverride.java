// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminPcCondOverride implements IAdminCommandHandler
{
    private static final String[] COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command);
        if (st.hasMoreTokens()) {
            final String nextToken = st.nextToken();
            switch (nextToken) {
                case "admin_exceptions": {
                    final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
                    msg.setFile(activeChar, "data/html/admin/cond_override.htm");
                    final StringBuilder sb = new StringBuilder();
                    for (final PcCondOverride ex : PcCondOverride.values()) {
                        sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, ex.getDescription(), ex.ordinal(), activeChar.canOverrideCond(ex) ? "Disable" : "Enable"));
                    }
                    msg.replace("%cond_table%", sb.toString());
                    activeChar.sendPacket(new ServerPacket[] { (ServerPacket)msg });
                    break;
                }
                case "admin_set_exception": {
                    if (st.hasMoreTokens()) {
                        final String token = st.nextToken();
                        if (Util.isDigit(token)) {
                            final PcCondOverride ex2 = PcCondOverride.getCondOverride((int)Integer.valueOf(token));
                            if (ex2 != null) {
                                if (activeChar.canOverrideCond(ex2)) {
                                    activeChar.removeOverridedCond(new PcCondOverride[] { ex2 });
                                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ex2.getDescription()));
                                }
                                else {
                                    activeChar.addOverrideCond(new PcCondOverride[] { ex2 });
                                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ex2.getDescription()));
                                }
                            }
                        }
                        else {
                            final String s = token;
                            switch (s) {
                                case "enable_all": {
                                    for (final PcCondOverride ex3 : PcCondOverride.values()) {
                                        if (!activeChar.canOverrideCond(ex3)) {
                                            activeChar.addOverrideCond(new PcCondOverride[] { ex3 });
                                        }
                                    }
                                    BuilderUtil.sendSysMessage(activeChar, "All condition exceptions have been enabled.");
                                    break;
                                }
                                case "disable_all": {
                                    for (final PcCondOverride ex3 : PcCondOverride.values()) {
                                        if (activeChar.canOverrideCond(ex3)) {
                                            activeChar.removeOverridedCond(new PcCondOverride[] { ex3 });
                                        }
                                    }
                                    BuilderUtil.sendSysMessage(activeChar, "All condition exceptions have been disabled.");
                                    break;
                                }
                            }
                        }
                        this.useAdminCommand(AdminPcCondOverride.COMMANDS[0], activeChar);
                        break;
                    }
                    break;
                }
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminPcCondOverride.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "admin_exceptions", "admin_set_exception" };
    }
}
