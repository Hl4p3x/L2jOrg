// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.voicedcommandhandlers;

import java.util.Iterator;
import java.util.StringTokenizer;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IVoicedCommandHandler;

public class Lang implements IVoicedCommandHandler
{
    private static final String[] VOICED_COMMANDS;
    
    public boolean useVoicedCommand(final String command, final Player activeChar, final String params) {
        if (!Config.MULTILANG_ENABLE || !Config.MULTILANG_VOICED_ALLOW) {
            return false;
        }
        final NpcHtmlMessage msg = new NpcHtmlMessage();
        if (params == null) {
            final StringBuilder html = new StringBuilder(100);
            for (final String lang : Config.MULTILANG_ALLOWED) {
                html.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, lang.toUpperCase(), lang));
            }
            msg.setFile(activeChar, "data/html/mods/Lang/LanguageSelect.htm");
            msg.replace("%list%", html.toString());
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)msg });
            return true;
        }
        final StringTokenizer st = new StringTokenizer(params);
        if (!st.hasMoreTokens()) {
            return false;
        }
        final String lang2 = st.nextToken().trim();
        if (activeChar.setLang(lang2)) {
            msg.setFile(activeChar, "data/html/mods/Lang/Ok.htm");
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)msg });
            return true;
        }
        msg.setFile(activeChar, "data/html/mods/Lang/Error.htm");
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)msg });
        return true;
    }
    
    public String[] getVoicedCommandList() {
        return Lang.VOICED_COMMANDS;
    }
    
    static {
        VOICED_COMMANDS = new String[] { "lang" };
    }
}
