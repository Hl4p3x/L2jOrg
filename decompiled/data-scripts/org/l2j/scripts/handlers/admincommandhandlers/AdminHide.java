// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public final class AdminHide implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player player) {
        final StringTokenizer st = new StringTokenizer(command);
        st.nextToken();
        try {
            final String nextToken;
            final String param = nextToken = st.nextToken();
            int n = -1;
            switch (nextToken.hashCode()) {
                case 3551: {
                    if (nextToken.equals("on")) {
                        n = 0;
                        break;
                    }
                    break;
                }
                case 109935: {
                    if (nextToken.equals("off")) {
                        n = 1;
                        break;
                    }
                    break;
                }
            }
            Label_0139: {
                switch (n) {
                    case 0: {
                        if (!BuilderUtil.setHiding(player, true)) {
                            BuilderUtil.sendSysMessage(player, "Currently, you cannot be seen.");
                            break Label_0139;
                        }
                        BuilderUtil.sendSysMessage(player, "Now, you cannot be seen.");
                        break Label_0139;
                    }
                    case 1: {
                        if (!BuilderUtil.setHiding(player, false)) {
                            BuilderUtil.sendSysMessage(player, "Currently, you can be seen.");
                            break;
                        }
                        BuilderUtil.sendSysMessage(player, "Now, you can be seen.");
                        break;
                    }
                }
            }
            BuilderUtil.sendSysMessage(player, "//hide [on|off]");
            return true;
        }
        catch (Exception e) {
            BuilderUtil.sendSysMessage(player, "//hide [on|off]");
            return false;
        }
    }
    
    public String[] getAdminCommandList() {
        return AdminHide.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_hide" };
    }
}
