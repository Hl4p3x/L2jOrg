// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class TutorialClose implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        player.sendPacket(new ServerPacket[] { (ServerPacket)TutorialCloseHtml.STATIC_PACKET });
        player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
        return false;
    }
    
    public String[] getBypassList() {
        return TutorialClose.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "tutorial_close" };
    }
}
