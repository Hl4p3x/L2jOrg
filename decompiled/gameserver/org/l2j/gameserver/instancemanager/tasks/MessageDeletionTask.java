// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager.tasks;

import org.l2j.gameserver.model.item.container.Attachment;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.world.World;
import java.util.Objects;
import org.l2j.gameserver.engine.mail.MailEngine;

public final class MessageDeletionTask implements Runnable
{
    final int mailId;
    
    public MessageDeletionTask(final int msgId) {
        this.mailId = msgId;
    }
    
    @Override
    public void run() {
        final MailData mail = MailEngine.getInstance().getMail(this.mailId);
        if (Objects.isNull(mail)) {
            return;
        }
        if (mail.hasAttachments()) {
            final Player sender;
            Util.doIfNonNull((Object)mail.getAttachment(), attachment -> {
                sender = World.getInstance().findPlayer(mail.getSender());
                if (Objects.nonNull(sender)) {
                    attachment.returnToWh(sender.getWarehouse());
                    sender.sendPacket(SystemMessageId.THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME);
                }
                else {
                    attachment.returnToWh(null);
                }
                attachment.deleteMe();
                return;
            });
            mail.removeAttachments();
            Util.doIfNonNull((Object)World.getInstance().findPlayer(mail.getReceiver()), receiver -> receiver.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME)));
        }
        MailEngine.getInstance().deleteMailInDb(mail.getId());
    }
}
