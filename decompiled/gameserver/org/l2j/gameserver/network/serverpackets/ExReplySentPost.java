// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;
import org.l2j.gameserver.data.database.data.MailData;
import org.slf4j.Logger;

public class ExReplySentPost extends AbstractItemPacket
{
    private static final Logger LOGGER;
    private final MailData _msg;
    private Collection<Item> _items;
    
    public ExReplySentPost(final MailData msg) {
        this._items = null;
        this._msg = msg;
        if (msg.hasAttachments()) {
            final ItemContainer attachments = msg.getAttachment();
            if (attachments != null && attachments.getSize() > 0) {
                this._items = attachments.getItems();
            }
            else {
                ExReplySentPost.LOGGER.warn("Message {} has attachments but itemcontainer is empty.", (Object)msg.getId());
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_REPLY_SENT_POST);
        this.writeInt(0);
        this.writeInt(this._msg.getId());
        this.writeInt(this._msg.isLocked());
        this.writeString((CharSequence)this._msg.getReceiverName());
        this.writeString((CharSequence)this._msg.getSubject());
        this.writeString((CharSequence)this._msg.getContent());
        if (this._items != null && !this._items.isEmpty()) {
            this.writeInt(this._items.size());
            for (final Item item : this._items) {
                this.writeItem(item);
                this.writeInt(item.getObjectId());
            }
        }
        else {
            this.writeInt(0);
        }
        this.writeLong(this._msg.getFee());
        this.writeInt(this._msg.hasAttachments());
        this.writeInt(this._msg.isReturned());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ExReplySentPost.class);
    }
}
