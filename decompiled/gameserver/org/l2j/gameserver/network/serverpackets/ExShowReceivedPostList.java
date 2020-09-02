// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.MailType;
import java.util.function.Consumer;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.data.database.data.MailData;
import java.util.List;

public class ExShowReceivedPostList extends ServerPacket
{
    private final List<MailData> inbox;
    
    public ExShowReceivedPostList(final int objectId) {
        this.inbox = MailEngine.getInstance().getInbox(objectId);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_RECEIVED_POST_LIST);
        this.writeInt((int)(System.currentTimeMillis() / 1000L));
        this.writeInt(this.inbox.size());
        this.inbox.forEach(this::writeMail);
        this.writeInt(100);
        this.writeInt(1000);
    }
    
    private void writeMail(final MailData mail) {
        this.writeInt(mail.getType().ordinal());
        if (mail.getType() == MailType.COMMISSION_ITEM_SOLD) {
            this.writeInt(SystemMessageId.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD.getId());
        }
        else if (mail.getType() == MailType.COMMISSION_ITEM_RETURNED) {
            this.writeInt(SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
        }
        this.writeInt(mail.getId());
        this.writeString((CharSequence)mail.getSubject());
        this.writeString((CharSequence)mail.getSenderName());
        this.writeInt(mail.isLocked());
        this.writeInt((int)(mail.getExpiration() / 1000L));
        this.writeInt(mail.isUnread());
        this.writeInt((int)((mail.getType() != MailType.COMMISSION_ITEM_SOLD && mail.getType() != MailType.COMMISSION_ITEM_RETURNED) ? 1 : 0));
        this.writeInt(mail.hasAttachments());
        this.writeInt(mail.isReturned());
        this.writeInt(0);
    }
}
