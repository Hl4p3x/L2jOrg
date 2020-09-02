// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.HennaItemRemoveInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestHennaItemRemoveInfo extends ClientPacket
{
    private static final Logger LOGGER;
    private int _symbolId;
    
    public void readImpl() {
        this._symbolId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || this._symbolId == 0) {
            return;
        }
        final Henna henna = HennaData.getInstance().getHenna(this._symbolId);
        if (henna == null) {
            RequestHennaItemRemoveInfo.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this._symbolId, activeChar));
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        activeChar.sendPacket(new HennaItemRemoveInfo(henna, activeChar));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestHennaItemRemoveInfo.class);
    }
}
