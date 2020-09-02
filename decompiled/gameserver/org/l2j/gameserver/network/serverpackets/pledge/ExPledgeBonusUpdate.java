// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeBonusUpdate extends ServerPacket
{
    private final ClanRewardType _type;
    private final int _value;
    
    public ExPledgeBonusUpdate(final ClanRewardType type, final int value) {
        this._type = type;
        this._value = value;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_BONUS_UPDATE);
        this.writeByte(this._type.getClientId());
        this.writeInt(this._value);
    }
}
