// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public class RequestMagicSkillList extends ClientPacket
{
    private static final Logger LOGGER;
    private int _objectId;
    private int _charId;
    private int _unk;
    
    public void readImpl() {
        this._objectId = this.readInt();
        this._charId = this.readInt();
        this._unk = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.getObjectId() != this._objectId) {
            RequestMagicSkillList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Ljava/lang/String;I)Ljava/lang/String;, activeChar, this.getClass().getSimpleName(), this._objectId));
            return;
        }
        activeChar.sendSkillList();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestMagicSkillList.class);
    }
}
