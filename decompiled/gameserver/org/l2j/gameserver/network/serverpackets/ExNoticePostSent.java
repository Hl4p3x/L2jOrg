// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExNoticePostSent extends ServerPacket
{
    private static final ExNoticePostSent STATIC_PACKET_TRUE;
    private static final ExNoticePostSent STATIC_PACKET_FALSE;
    private final boolean _showAnim;
    
    public ExNoticePostSent(final boolean showAnimation) {
        this._showAnim = showAnimation;
    }
    
    public static ExNoticePostSent valueOf(final boolean result) {
        return result ? ExNoticePostSent.STATIC_PACKET_TRUE : ExNoticePostSent.STATIC_PACKET_FALSE;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_REPLY_WRITE_POST);
        this.writeInt((int)(this._showAnim ? 1 : 0));
    }
    
    static {
        STATIC_PACKET_TRUE = new ExNoticePostSent(true);
        STATIC_PACKET_FALSE = new ExNoticePostSent(false);
    }
}
