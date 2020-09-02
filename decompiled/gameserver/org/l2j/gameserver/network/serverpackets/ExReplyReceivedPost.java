// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;
import org.l2j.gameserver.data.database.data.MailData;
import org.slf4j.Logger;

public class ExReplyReceivedPost extends AbstractItemPacket
{
    private static final Logger LOGGER;
    private final MailData _msg;
    private Collection<Item> _items;
    
    public ExReplyReceivedPost(final MailData msg) {
        this._items = null;
        this._msg = msg;
        if (msg.hasAttachments()) {
            final ItemContainer attachments = msg.getAttachment();
            if (attachments != null && attachments.getSize() > 0) {
                this._items = attachments.getItems();
            }
            else {
                ExReplyReceivedPost.LOGGER.warn("Message {} has attachments but itemcontainer is empty.", (Object)msg.getId());
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_REPLY_RECEIVED_POST);
        this.writeInt(this._msg.getType().ordinal());
        if (this._msg.getType() == MailType.COMMISSION_ITEM_RETURNED) {
            this.writeInt(SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
            this.writeInt(SystemMessageId.THE_AUCTION_HOUSE_REGISTRATION_PERIOD_HAS_EXPIRED_AND_THE_CORRESPONDING_ITEM_IS_BEING_FORWARDED.getId());
        }
        else if (this._msg.getType() == MailType.COMMISSION_ITEM_SOLD) {
            this.writeInt(this._msg.getItem());
            this.writeInt(this._msg.getEnchant());
            for (int i = 0; i < AttributeType.ATTRIBUTE_TYPES.length; ++i) {
                this.writeInt(0);
            }
            this.writeInt(SystemMessageId.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD.getId());
            this.writeInt(SystemMessageId.S1_HAS_BEEN_SOLD.getId());
        }
        this.writeInt(this._msg.getId());
        this.writeInt((int)(this._msg.isLocked() ? 1 : 0));
        this.writeInt(0);
        this.writeString((CharSequence)this._msg.getSenderName());
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
        this.writeInt((int)(this._msg.hasAttachments() ? 1 : 0));
        this.writeInt((int)(this._msg.isReturned() ? 1 : 0));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ExReplyReceivedPost.class);
    }
}
