// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.voicedcommandhandlers;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IVoicedCommandHandler;

public class ChangePassword implements IVoicedCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] _voicedCommands;
    
    public boolean useVoicedCommand(final String command, final Player activeChar, final String target) {
        if (target != null) {
            final StringTokenizer st = new StringTokenizer(target);
            try {
                String curpass = null;
                String newpass = null;
                String repeatnewpass = null;
                if (st.hasMoreTokens()) {
                    curpass = st.nextToken();
                }
                if (st.hasMoreTokens()) {
                    newpass = st.nextToken();
                }
                if (st.hasMoreTokens()) {
                    repeatnewpass = st.nextToken();
                }
                if (curpass == null || newpass == null || repeatnewpass == null) {
                    activeChar.sendMessage("Invalid password data! You have to fill all boxes.");
                    return false;
                }
                if (!newpass.equals(repeatnewpass)) {
                    activeChar.sendMessage("The new password doesn't match with the repeated one!");
                    return false;
                }
                if (newpass.length() < 3) {
                    activeChar.sendMessage("The new password is shorter than 3 chars! Please try with a longer one.");
                    return false;
                }
                if (newpass.length() > 30) {
                    activeChar.sendMessage("The new password is longer than 30 chars! Please try with a shorter one.");
                    return false;
                }
                AuthServerCommunication.getInstance().sendChangePassword(activeChar.getAccountName(), curpass, newpass);
            }
            catch (Exception e) {
                activeChar.sendMessage("A problem occured while changing password!");
                ChangePassword.LOGGER.warn("", (Throwable)e);
            }
            return true;
        }
        String html = HtmCache.getInstance().getHtm((Player)null, "data/html/mods/ChangePassword.htm");
        if (html == null) {
            html = "<html><body><br><br><center><font color=LEVEL>404:</font> File Not Found</center></body></html>";
        }
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new NpcHtmlMessage(html) });
        return true;
    }
    
    public String[] getVoicedCommandList() {
        return ChangePassword._voicedCommands;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ChangePassword.class);
        _voicedCommands = new String[] { "changepassword" };
    }
}
