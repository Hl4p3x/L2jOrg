// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExReplyPostItemList;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.Config;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;

public final class RequestPostItemList extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        if (!((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).allowMail() || !Config.ALLOW_ATTACHMENTS) {
            return;
        }
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (!activeChar.isInsideZone(ZoneType.PEACE)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }
        ((GameClient)this.client).sendPacket(new ExReplyPostItemList(1, activeChar));
        ((GameClient)this.client).sendPacket(new ExReplyPostItemList(2, activeChar));
    }
}
