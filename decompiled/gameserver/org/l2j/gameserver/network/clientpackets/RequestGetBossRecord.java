// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public class RequestGetBossRecord extends ClientPacket
{
    private static final Logger LOGGER;
    private int _bossId;
    
    public void readImpl() {
        this._bossId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        RequestGetBossRecord.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;ILjava/lang/String;)Ljava/lang/String;, activeChar, this._bossId, RequestGetBossRecord.class.getSimpleName()));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestGetBossRecord.class);
    }
}
