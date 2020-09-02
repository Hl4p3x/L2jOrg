// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.Objects;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class Book implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        final Player activeChar = (Player)playable;
        final int itemId = item.getId();
        final String filename = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, itemId);
        final String content = HtmCache.getInstance().getHtm(activeChar, filename);
        if (Objects.isNull(content)) {
            final NpcHtmlMessage html = new NpcHtmlMessage(0, item.getId());
            html.setHtml(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, filename));
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        else {
            final NpcHtmlMessage itemReply = new NpcHtmlMessage();
            itemReply.setHtml(content);
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)itemReply });
        }
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
        return true;
    }
}
