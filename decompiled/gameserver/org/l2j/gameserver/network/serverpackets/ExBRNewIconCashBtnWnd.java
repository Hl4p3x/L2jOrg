// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExBRNewIconCashBtnWnd extends ServerPacket
{
    public static ExBRNewIconCashBtnWnd NOT_SHOW;
    public static ExBRNewIconCashBtnWnd SHOW;
    private final short active;
    
    private ExBRNewIconCashBtnWnd(final short active) {
        this.active = active;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BR_EXIST_NEW_PRODUCT_ACK);
        this.writeShort(this.active);
    }
    
    static {
        ExBRNewIconCashBtnWnd.NOT_SHOW = new ExBRNewIconCashBtnWnd((short)0);
        ExBRNewIconCashBtnWnd.SHOW = new ExBRNewIconCashBtnWnd((short)1);
    }
}
