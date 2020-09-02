// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.AttributeType;

public class ExAttributeEnchantResult extends ServerPacket
{
    private final int _result;
    private final int _isWeapon;
    private final int _type;
    private final int _before;
    private final int _after;
    private final int _successCount;
    private final int _failedCount;
    
    public ExAttributeEnchantResult(final int result, final boolean isWeapon, final AttributeType type, final int before, final int after, final int successCount, final int failedCount) {
        this._result = result;
        this._isWeapon = (isWeapon ? 1 : 0);
        this._type = type.getClientId();
        this._before = before;
        this._after = after;
        this._successCount = successCount;
        this._failedCount = failedCount;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ATTRIBUTE_ENCHANT_RESULT);
        this.writeInt(this._result);
        this.writeByte((byte)this._isWeapon);
        this.writeShort((short)this._type);
        this.writeShort((short)this._before);
        this.writeShort((short)this._after);
        this.writeShort((short)this._successCount);
        this.writeShort((short)this._failedCount);
    }
}
