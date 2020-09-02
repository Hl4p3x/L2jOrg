// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class Link implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        final String htmlPath = command.substring(4).trim();
        if (htmlPath.isEmpty()) {
            Link.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            return false;
        }
        if (htmlPath.contains("..")) {
            Link.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), htmlPath));
            return false;
        }
        final String content = HtmCache.getInstance().getHtm(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmlPath));
        final NpcHtmlMessage html = new NpcHtmlMessage((target != null) ? target.getObjectId() : 0);
        html.setHtml(content.replace("%objectId%", String.valueOf((target != null) ? target.getObjectId() : 0)));
        player.sendPacket(new ServerPacket[] { (ServerPacket)html });
        return true;
    }
    
    public String[] getBypassList() {
        return Link.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "Link" };
    }
}
