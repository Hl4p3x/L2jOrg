// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestHennaRemove extends ClientPacket
{
    private static final Logger LOGGER;
    private int _symbolId;
    
    public void readImpl() {
        this._symbolId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("HennaRemove")) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        boolean found = false;
        for (int i = 1; i <= 3; ++i) {
            final Henna henna = activeChar.getHenna(i);
            if (henna != null && henna.getDyeId() == this._symbolId) {
                if (activeChar.getAdena() >= henna.getCancelFee()) {
                    activeChar.removeHenna(i);
                }
                else {
                    activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
                    ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                }
                found = true;
                break;
            }
        }
        if (!found) {
            RequestHennaRemove.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this.getClass().getSimpleName(), activeChar));
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestHennaRemove.class);
    }
}
