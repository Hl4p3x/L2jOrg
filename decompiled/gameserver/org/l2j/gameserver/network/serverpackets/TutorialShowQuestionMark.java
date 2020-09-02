// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class TutorialShowQuestionMark extends ServerPacket
{
    private final int _markId;
    private final int _markType;
    
    public TutorialShowQuestionMark(final int markId, final int markType) {
        this._markId = markId;
        this._markType = markType;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SHOW_TUTORIAL_MARK);
        this.writeByte(this._markType);
        this.writeInt(this._markId);
    }
}
