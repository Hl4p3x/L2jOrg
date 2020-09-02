// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.captcha;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.captcha.Captcha;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ReceiveBotCaptchaImage extends ServerPacket
{
    private final Captcha captcha;
    private final int time;
    
    public ReceiveBotCaptchaImage(final Captcha captcha, final int time) {
        this.captcha = captcha;
        this.time = time;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CAPTCHA_IMAGE);
        this.writeLong((long)this.captcha.getId());
        this.writeByte((byte)2);
        this.writeInt(this.time);
        this.writeBytes(this.captcha.getData());
    }
}
