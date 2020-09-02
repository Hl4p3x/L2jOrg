// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestLinkHtml extends ClientPacket
{
    private static final Logger LOGGER;
    private String _link;
    
    public void readImpl() {
        this._link = this.readString();
    }
    
    public void runImpl() {
        final Player actor = ((GameClient)this.client).getPlayer();
        if (actor == null) {
            return;
        }
        if (this._link.isEmpty()) {
            RequestLinkHtml.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, actor.getName()));
            return;
        }
        if (this._link.contains("..")) {
            RequestLinkHtml.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, actor.getName(), this._link));
            return;
        }
        final int htmlObjectId = actor.validateHtmlAction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._link));
        if (htmlObjectId == -1) {
            RequestLinkHtml.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, actor.getName(), this._link));
            return;
        }
        if (htmlObjectId > 0 && !GameUtils.isInsideRangeOfObjectId(actor, htmlObjectId, 250)) {
            return;
        }
        final String filename = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._link);
        final NpcHtmlMessage msg = new NpcHtmlMessage(htmlObjectId);
        msg.setFile(actor, filename);
        actor.sendPacket(msg);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestLinkHtml.class);
    }
}
