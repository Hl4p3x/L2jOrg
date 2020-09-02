// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.function.Consumer;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.data.database.data.MailData;
import java.util.List;

public class ExShowSentPostList extends ServerPacket
{
    private final List<MailData> outbox;
    
    public ExShowSentPostList(final int objectId) {
        this.outbox = MailEngine.getInstance().getOutbox(objectId);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_SENT_POST_LIST);
        this.writeInt((int)(System.currentTimeMillis() / 1000L));
        this.writeInt(this.outbox.size());
        this.outbox.forEach(this::writeMail);
    }
    
    private void writeMail(final MailData mail) {
        this.writeInt(mail.getId());
        this.writeString((CharSequence)mail.getSubject());
        this.writeString((CharSequence)mail.getReceiverName());
        this.writeInt(mail.isLocked());
        this.writeInt((int)(mail.getExpiration() / 1000L));
        this.writeInt(mail.isUnread());
        this.writeInt(1);
        this.writeInt(mail.hasAttachments());
        this.writeInt(0);
    }
}
