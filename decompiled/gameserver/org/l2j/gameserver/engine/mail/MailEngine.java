// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.mail;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.item.container.Attachment;
import java.util.Collection;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.enums.MailType;
import java.util.stream.Stream;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ExUnReadMailCount;
import org.l2j.gameserver.network.serverpackets.ExNoticePostArrived;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.instancemanager.tasks.MessageDeletionTask;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.MailDAO;
import io.github.joealisson.primitive.Containers;
import org.l2j.gameserver.data.database.data.MailData;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public final class MailEngine
{
    private static final Logger LOGGER;
    public static final int MAIL_FEE = 100;
    public static final int MAIL_FEE_PER_SLOT = 1000;
    private IntMap<MailData> mails;
    
    private MailEngine() {
        this.mails = (IntMap<MailData>)Containers.emptyIntMap();
    }
    
    private void load() {
        this.mails = (IntMap<MailData>)((MailDAO)DatabaseAccess.getDAO((Class)MailDAO.class)).findAll();
        final long currentTime = System.currentTimeMillis();
        this.mails.values().forEach(m -> {
            if (m.getExpiration() < currentTime) {
                ThreadPool.schedule((Runnable)new MessageDeletionTask(m.getId()), 10L, TimeUnit.SECONDS);
            }
            else {
                ThreadPool.schedule((Runnable)new MessageDeletionTask(m.getId()), m.getExpiration() - System.currentTimeMillis());
            }
        });
    }
    
    public final MailData getMail(final int mailId) {
        return (MailData)this.mails.get(mailId);
    }
    
    public final void sendUnreadCount(final Player player) {
        final long unread = this.getUnreadCount(player);
        if (unread > 0L) {
            player.sendPacket(ExNoticePostArrived.valueOf(false));
        }
        player.sendPacket(new ExUnReadMailCount((int)unread));
    }
    
    public final int getInboxSize(final int objectId) {
        return (int)this.inboxStream(objectId).count();
    }
    
    public final int getOutboxSize(final int objectId) {
        int size = 0;
        for (final MailData mail : this.mails.values()) {
            if (mail.getSender() == objectId && !mail.isDeletedBySender()) {
                ++size;
            }
        }
        return size;
    }
    
    public final List<MailData> getInbox(final int objectId) {
        return this.inboxStream(objectId).collect((Collector<? super MailData, ?, List<MailData>>)Collectors.toList());
    }
    
    public Stream<MailData> inboxStream(final int objectId) {
        return this.mails.values().stream().filter(m -> m.getReceiver() == objectId && !m.isDeletedByReceiver());
    }
    
    public final long getUnreadCount(final Player player) {
        return this.inboxStream(player.getObjectId()).filter(MailData::isUnread).count();
    }
    
    public boolean hasMailInProgress(final int objectId) {
        for (final MailData mail : this.mails.values()) {
            if (mail.getType() == MailType.REGULAR) {
                if (mail.getReceiver() == objectId && !mail.isDeletedByReceiver() && !mail.isReturned() && mail.hasAttachments()) {
                    return true;
                }
                if (mail.getSender() == objectId && !mail.isDeletedBySender() && !mail.isReturned() && mail.hasAttachments()) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public final List<MailData> getOutbox(final int objectId) {
        return this.mails.values().stream().filter(m -> m.getSender() == objectId && !m.isDeletedBySender()).collect((Collector<? super Object, ?, List<MailData>>)Collectors.toList());
    }
    
    public void sendMail(final MailData mail) {
        ((MailDAO)DatabaseAccess.getDAO((Class)MailDAO.class)).save((Object)mail);
        this.mails.put(mail.getId(), (Object)mail);
        Util.doIfNonNull((Object)World.getInstance().findPlayer(mail.getReceiver()), receiver -> {
            receiver.sendPacket(ExNoticePostArrived.valueOf(true), new ExUnReadMailCount((int)this.getUnreadCount(receiver)));
            receiver.sendPacket(new ServerPacket[0]);
            return;
        });
        ThreadPool.schedule((Runnable)new MessageDeletionTask(mail.getId()), mail.getExpiration() - System.currentTimeMillis());
    }
    
    public final void markAsRead(final Player player, final MailData mail) {
        if (mail.isUnread()) {
            mail.markAsRead();
            ((MailDAO)DatabaseAccess.getDAO((Class)MailDAO.class)).markAsRead(mail.getId());
            player.sendPacket(new ExUnReadMailCount((int)this.getUnreadCount(player)));
        }
    }
    
    public final void markAsDeletedBySenderInDb(final int mailId) {
        ((MailDAO)DatabaseAccess.getDAO((Class)MailDAO.class)).markAsDeletedBySender(mailId);
    }
    
    public final void markAsDeletedByReceiverInDb(final int mailId) {
        ((MailDAO)DatabaseAccess.getDAO((Class)MailDAO.class)).markAsDeletedByReceiver(mailId);
    }
    
    public final void removeAttachmentsInDb(final int mailId) {
        ((MailDAO)DatabaseAccess.getDAO((Class)MailDAO.class)).deleteAttachment(mailId);
    }
    
    public final void deleteMailInDb(final int mailId) {
        ((MailDAO)DatabaseAccess.getDAO((Class)MailDAO.class)).deleteById(mailId);
        this.mails.remove(mailId);
        IdFactory.getInstance().releaseId(mailId);
    }
    
    public boolean sendMail(final Player sender, final int receiverId, final boolean isCod, final String subject, final String content, final long reqAdena, final List<ItemHolder> items) {
        if (this.chargeFee(sender, items)) {
            final MailData msg = MailData.of(sender.getObjectId(), receiverId, isCod, subject, content, reqAdena, MailType.REGULAR);
            if (this.attach(sender, msg, items)) {
                this.sendMail(msg);
            }
            return true;
        }
        return false;
    }
    
    private boolean attach(final Player sender, final MailData mail, final List<ItemHolder> items) {
        if (Util.isNullOrEmpty((Collection)items)) {
            return true;
        }
        final Attachment attachment = new Attachment(mail.getSender(), mail.getId());
        mail.attach(attachment);
        final InventoryUpdate playerIU = new InventoryUpdate();
        for (final ItemHolder item : items) {
            final Item oldItem = sender.checkItemManipulation(item.getId(), item.getCount(), "attach");
            if (oldItem == null || !oldItem.isTradeable() || oldItem.isEquipped()) {
                MailEngine.LOGGER.warn("Error adding attachment for player {} (olditem == null)", (Object)sender);
                return false;
            }
            final Item newItem = sender.getInventory().transferItem("SendMail", item.getId(), item.getCount(), (ItemContainer)attachment, sender, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, mail.getReceiverName(), mail.getReceiver()));
            if (newItem == null) {
                MailEngine.LOGGER.warn("Error adding attachment for player {} (newitem == null)", (Object)sender);
            }
            else {
                newItem.setItemLocation(newItem.getItemLocation(), mail.getId());
                if (oldItem.getCount() > 0L && oldItem != newItem) {
                    playerIU.addModifiedItem(oldItem);
                }
                else {
                    playerIU.addRemovedItem(oldItem);
                }
            }
        }
        sender.sendInventoryUpdate(playerIU);
        return true;
    }
    
    private boolean chargeFee(final Player player, final List<ItemHolder> items) {
        long currentAdena = player.getAdena();
        long fee = 100L;
        for (final ItemHolder i : items) {
            final Item item = player.checkItemManipulation(i.getId(), i.getCount(), "attach");
            if (item == null || !item.isTradeable() || item.isEquipped()) {
                player.sendPacket(SystemMessageId.THE_ITEM_THAT_YOU_RE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISN_T_PROPER);
                return false;
            }
            fee += 1000L;
            if (item.getId() != 57) {
                continue;
            }
            currentAdena -= i.getCount();
        }
        if (currentAdena < fee || !player.reduceAdena("MailFee", fee, null, false)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
            return false;
        }
        return true;
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static MailEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)MailEngine.class);
    }
    
    private static class Singleton
    {
        private static final MailEngine INSTANCE;
        
        static {
            INSTANCE = new MailEngine();
        }
    }
}
