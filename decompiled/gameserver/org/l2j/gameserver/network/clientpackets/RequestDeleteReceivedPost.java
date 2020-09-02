// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;

public final class RequestDeleteReceivedPost extends ClientPacket
{
    private static final int BATCH_LENGTH = 4;
    int[] mailIds;
    
    public RequestDeleteReceivedPost() {
        this.mailIds = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        final int count = this.readInt();
        if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * 4 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this.mailIds = new int[count];
        for (int i = 0; i < count; ++i) {
            this.mailIds[i] = this.readInt();
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player) || Objects.isNull(this.mailIds) || !((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).allowMail()) {
            return;
        }
        if (!player.isInsideZone(ZoneType.PEACE)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }
        for (final int mailId : this.mailIds) {
            final MailData mail = MailEngine.getInstance().getMail(mailId);
            if (mail != null) {
                if (mail.getReceiver() != player.getObjectId()) {
                    GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
                    return;
                }
                if (mail.hasAttachments() || mail.isDeletedByReceiver()) {
                    return;
                }
                mail.setDeletedByReceiver();
            }
        }
        ((GameClient)this.client).sendPacket(ExChangePostState.deleted(true, this.mailIds));
    }
}
