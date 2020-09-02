// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.engine.mail.MailEngine;
import java.util.Objects;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.gameserver.model.item.container.Attachment;
import org.l2j.gameserver.enums.MailType;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("mail")
public class MailData
{
    private int id;
    private int sender;
    private int receiver;
    private String subject;
    private String content;
    private long expiration;
    private long fee;
    @Column("has_attachment")
    private boolean hasAttachment;
    private boolean unread;
    @Column("sender_deleted")
    private boolean deletedBySender;
    @Column("receiver_deleted")
    private boolean deletedByReceiver;
    private boolean locked;
    private MailType type;
    private boolean returned;
    private int item;
    private int enchant;
    @NonUpdatable
    private Attachment attachment;
    @NonUpdatable
    private String receiverName;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getSender() {
        return this.sender;
    }
    
    public void setSender(final int sender) {
        this.sender = sender;
    }
    
    public int getReceiver() {
        return this.receiver;
    }
    
    public void setReceiver(final int receiver) {
        this.receiver = receiver;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public void setSubject(final String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void setContent(final String content) {
        this.content = content;
    }
    
    public long getExpiration() {
        return this.expiration;
    }
    
    public void setExpiration(final long expiration) {
        this.expiration = expiration;
    }
    
    public long getFee() {
        return this.fee;
    }
    
    public void setFee(final long fee) {
        this.fee = fee;
    }
    
    public boolean hasAttachments() {
        return this.hasAttachment;
    }
    
    public boolean isUnread() {
        return this.unread;
    }
    
    public void setUnread(final boolean unread) {
        this.unread = unread;
    }
    
    public boolean isDeletedBySender() {
        return this.deletedBySender;
    }
    
    public void setDeletedBySender(final boolean deletedBySender) {
        this.deletedBySender = deletedBySender;
    }
    
    public boolean isDeletedByReceiver() {
        return this.deletedByReceiver;
    }
    
    public void setDeletedByReceiver(final boolean deletedByReceiver) {
        this.deletedByReceiver = deletedByReceiver;
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }
    
    public MailType getType() {
        return this.type;
    }
    
    public void setType(final MailType type) {
        this.type = type;
    }
    
    public boolean isReturned() {
        return this.returned;
    }
    
    public void setReturned(final boolean returned) {
        this.returned = returned;
    }
    
    public int getItem() {
        return this.item;
    }
    
    public void setItem(final int item) {
        this.item = item;
    }
    
    public int getEnchant() {
        return this.enchant;
    }
    
    public void setEnchant(final int enchant) {
        this.enchant = enchant;
    }
    
    public final synchronized Attachment getAttachment() {
        if (!this.hasAttachment) {
            return null;
        }
        if (this.attachment == null) {
            (this.attachment = new Attachment(this.sender, this.id)).restore();
        }
        return this.attachment;
    }
    
    public final void removeAttachments() {
        if (Objects.nonNull(this.attachment)) {
            this.attachment = null;
            this.hasAttachment = false;
            MailEngine.getInstance().removeAttachmentsInDb(this.id);
        }
    }
    
    public final void setDeletedByReceiver() {
        if (!this.deletedByReceiver) {
            this.deletedByReceiver = true;
            if (this.deletedBySender) {
                MailEngine.getInstance().deleteMailInDb(this.id);
            }
            else {
                MailEngine.getInstance().markAsDeletedByReceiverInDb(this.id);
            }
        }
    }
    
    public final void setDeletedBySender() {
        if (!this.deletedBySender) {
            this.deletedBySender = true;
            if (this.deletedByReceiver) {
                MailEngine.getInstance().deleteMailInDb(this.id);
            }
            else {
                MailEngine.getInstance().markAsDeletedBySenderInDb(this.id);
            }
        }
    }
    
    public final String getSenderName() {
        if (this.type == MailType.REGULAR) {
            return PlayerNameTable.getInstance().getNameById(this.sender);
        }
        return "";
    }
    
    public String getReceiverName() {
        if (this.receiverName == null) {
            this.receiverName = PlayerNameTable.getInstance().getNameById(this.receiver);
            if (this.receiverName == null) {
                this.receiverName = "";
            }
        }
        return this.receiverName;
    }
    
    public final void markAsRead() {
        this.unread = false;
    }
    
    public void attach(final Attachment attachment) {
        this.attachment = attachment;
        this.hasAttachment = true;
    }
    
    public MailData asReturned() {
        final MailData returned = new MailData();
        returned.id = IdFactory.getInstance().getNextId();
        returned.sender = this.sender;
        returned.receiver = this.sender;
        returned.subject = "";
        returned.content = "";
        returned.expiration = Instant.now().plus(15L, (TemporalUnit)ChronoUnit.DAYS).toEpochMilli();
        returned.unread = true;
        returned.deletedBySender = true;
        returned.type = MailType.REGULAR;
        returned.returned = true;
        returned.hasAttachment = true;
        returned.attachment = this.getAttachment();
        this.removeAttachments();
        return returned;
    }
    
    public static MailData of(final int senderId, final int receiverId, final boolean isCod, final String subject, final String content, final long reqAdena, final MailType type) {
        final MailData mail = new MailData();
        mail.id = IdFactory.getInstance().getNextId();
        mail.sender = senderId;
        mail.receiver = receiverId;
        mail.subject = subject;
        mail.content = content;
        mail.fee = reqAdena;
        mail.type = MailType.REGULAR;
        mail.unread = true;
        if (isCod) {
            mail.expiration = Instant.now().plus(12L, (TemporalUnit)ChronoUnit.HOURS).toEpochMilli();
        }
        else {
            mail.expiration = Instant.now().plus(15L, (TemporalUnit)ChronoUnit.DAYS).toEpochMilli();
        }
        return mail;
    }
    
    public static MailData of(final int receiver, final Item item, final MailType type) {
        final MailData mail = of(-1, receiver, false, "", item.getName(), 0L, type);
        mail.deletedBySender = true;
        if (type == MailType.COMMISSION_ITEM_SOLD) {
            mail.item = item.getId();
            mail.enchant = item.getEnchantLevel();
        }
        else if (type == MailType.COMMISSION_ITEM_RETURNED) {
            (mail.attachment = new Attachment(mail.sender, mail.id)).addItem("CommissionReturnItem", item, null, null);
            mail.hasAttachment = true;
        }
        return mail;
    }
    
    public static MailData of(final int receiverId, final String subject, final String content, final MailType type) {
        final MailData mail = of(-1, receiverId, false, subject, content, 0L, type);
        mail.deletedBySender = true;
        return mail;
    }
}
