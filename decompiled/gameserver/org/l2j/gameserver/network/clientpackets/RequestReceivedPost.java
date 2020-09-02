// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExReplyReceivedPost;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.network.GameClient;

public final class RequestReceivedPost extends ClientPacket
{
    private int mailId;
    
    public void readImpl() {
        this.mailId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (!((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).allowMail()) {
            return;
        }
        final MailData mail = MailEngine.getInstance().getMail(this.mailId);
        if (mail == null) {
            return;
        }
        if (!player.isInsideZone(ZoneType.PEACE) && mail.hasAttachments()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }
        if (mail.getReceiver() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            return;
        }
        if (mail.isDeletedByReceiver()) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExReplyReceivedPost(mail));
        ((GameClient)this.client).sendPacket(ExChangePostState.reAdded(true, this.mailId));
        MailEngine.getInstance().markAsRead(player, mail);
    }
}
