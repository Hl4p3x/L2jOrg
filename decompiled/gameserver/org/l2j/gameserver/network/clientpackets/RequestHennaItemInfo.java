// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.HennaItemDrawInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestHennaItemInfo extends ClientPacket
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
        final Henna henna = HennaData.getInstance().getHenna(this._symbolId);
        if (henna == null) {
            if (this._symbolId != 0) {
                RequestHennaItemInfo.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this.getClass().getSimpleName(), this._symbolId, activeChar));
            }
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        ((GameClient)this.client).sendPacket(new HennaItemDrawInfo(henna, activeChar));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestHennaItemInfo.class);
    }
}
