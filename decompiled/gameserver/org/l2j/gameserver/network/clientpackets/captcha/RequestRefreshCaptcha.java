// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.captcha;

import org.l2j.gameserver.engine.captcha.Captcha;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.captcha.ReceiveBotCaptchaImage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import java.util.Objects;
import org.l2j.gameserver.engine.captcha.CaptchaEngine;
import org.l2j.gameserver.model.actor.request.impl.CaptchaRequest;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestRefreshCaptcha extends ClientPacket
{
    private long captchaId;
    
    @Override
    protected void readImpl() throws Exception {
        this.captchaId = this.readLong();
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        CaptchaRequest request = player.getRequest(CaptchaRequest.class);
        final Captcha captcha = CaptchaEngine.getInstance().next((int)this.captchaId);
        if (Objects.nonNull(request)) {
            request.refresh(captcha);
        }
        else {
            request = new CaptchaRequest(player, captcha);
            player.addRequest(request);
        }
        player.sendPacket(new ReceiveBotCaptchaImage(captcha, request.getRemainingTime()));
    }
}
