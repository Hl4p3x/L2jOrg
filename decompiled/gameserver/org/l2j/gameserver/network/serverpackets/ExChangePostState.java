// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.mail.MailState;

public class ExChangePostState extends ServerPacket
{
    private final boolean receivedBoard;
    private final int[] changedMailsId;
    private final MailState state;
    
    private ExChangePostState(final boolean receivedBoard, final MailState state, final int... mailIds) {
        this.receivedBoard = receivedBoard;
        this.changedMailsId = mailIds;
        this.state = state;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHANGE_POST_STATE);
        this.writeInt(this.receivedBoard);
        this.writeInt(this.changedMailsId.length);
        for (final int mailId : this.changedMailsId) {
            this.writeInt(mailId);
            this.writeInt(this.state.ordinal());
        }
    }
    
    public static ExChangePostState deleted(final boolean receivedBoard, final int... mailsId) {
        return new ExChangePostState(receivedBoard, MailState.DELETED, mailsId);
    }
    
    public static ExChangePostState rejected(final boolean receiveBoard, final int mailId) {
        return new ExChangePostState(receiveBoard, MailState.REJECTED, new int[] { mailId });
    }
    
    public static ExChangePostState reAdded(final boolean receiveBoard, final int mailId) {
        return new ExChangePostState(receiveBoard, MailState.RE_ADDED, new int[] { mailId });
    }
}
