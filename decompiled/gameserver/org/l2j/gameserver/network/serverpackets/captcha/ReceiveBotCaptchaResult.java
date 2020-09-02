// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.captcha;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ReceiveBotCaptchaResult extends ServerPacket
{
    public static final ReceiveBotCaptchaResult SUCCESS;
    public static final ReceiveBotCaptchaResult FAILED;
    private final int answer;
    
    private ReceiveBotCaptchaResult(final int answer) {
        this.answer = answer;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CAPTCHA_ANSWER_RESULT);
        this.writeInt(this.answer);
    }
    
    static {
        SUCCESS = new ReceiveBotCaptchaResult(1);
        FAILED = new ReceiveBotCaptchaResult(0);
    }
}
