// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class Bypass implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            return false;
        }
        final Player player = (Player)playable;
        final int itemId = item.getId();
        final String filename = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, itemId);
        final String content = HtmCache.getInstance().getHtm(player, filename);
        final NpcHtmlMessage html = new NpcHtmlMessage(0, item.getId());
        if (content == null) {
            html.setHtml(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, filename));
            player.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        else {
            html.setHtml(content);
            html.replace("%itemId%", String.valueOf(item.getObjectId()));
            player.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        return true;
    }
}
