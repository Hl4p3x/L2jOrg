// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.Config;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;

public final class RequestRejectPostAttachment extends ClientPacket
{
    private int mailId;
    
    public void readImpl() {
        this.mailId = this.readInt();
    }
    
    public void runImpl() {
        if (!((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).allowMail() || !Config.ALLOW_ATTACHMENTS) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("rejectattach")) {
            return;
        }
        if (!player.isInsideZone(ZoneType.PEACE)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }
        final MailData mail = MailEngine.getInstance().getMail(this.mailId);
        if (mail == null) {
            return;
        }
        if (mail.getReceiver() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return;
        }
        if (!mail.hasAttachments() || mail.getType() != MailType.REGULAR) {
            return;
        }
        MailEngine.getInstance().sendMail(mail.asReturned());
        ((GameClient)this.client).sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_RETURNED);
        ((GameClient)this.client).sendPacket(ExChangePostState.rejected(true, this.mailId));
        final Player sender = World.getInstance().findPlayer(mail.getSender());
        if (sender != null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_RETURNED_THE_MAIL);
            sm.addString(player.getName());
            sender.sendPacket(sm);
        }
    }
}
