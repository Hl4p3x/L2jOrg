// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExReplySentPost;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;

public final class RequestSentPost extends ClientPacket
{
    private int mailId;
    
    public void readImpl() {
        this.mailId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player) || !((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).allowMail()) {
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
        if (mail.getSender() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return;
        }
        if (mail.isDeletedBySender()) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExReplySentPost(mail));
    }
}
