// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowUsm extends ServerPacket
{
    public static final ExShowUsm GOD_INTRO;
    public static final ExShowUsm SECOND_TRANSFER_QUEST;
    public static final ExShowUsm OCTAVIS_INSTANCE_END;
    public static final ExShowUsm AWAKENING_END;
    public static final ExShowUsm ERTHEIA_FIRST_QUEST;
    public static final ExShowUsm USM_Q015_E;
    public static final ExShowUsm ERTHEIA_INTRO_FOR_ERTHEIA;
    public static final ExShowUsm ERTHEIA_INTRO_FOR_OTHERS;
    private final int _videoId;
    
    private ExShowUsm(final int videoId) {
        this._videoId = videoId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SEND_USM_EVENT);
        this.writeInt(this._videoId);
    }
    
    static {
        GOD_INTRO = new ExShowUsm(2);
        SECOND_TRANSFER_QUEST = new ExShowUsm(4);
        OCTAVIS_INSTANCE_END = new ExShowUsm(6);
        AWAKENING_END = new ExShowUsm(10);
        ERTHEIA_FIRST_QUEST = new ExShowUsm(14);
        USM_Q015_E = new ExShowUsm(15);
        ERTHEIA_INTRO_FOR_ERTHEIA = new ExShowUsm(147);
        ERTHEIA_INTRO_FOR_OTHERS = new ExShowUsm(148);
    }
}
