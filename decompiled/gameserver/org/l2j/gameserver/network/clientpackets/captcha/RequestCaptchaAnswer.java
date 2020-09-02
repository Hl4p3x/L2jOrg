// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.captcha;

import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.engine.captcha.Captcha;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.captcha.ReceiveBotCaptchaImage;
import org.l2j.gameserver.engine.captcha.CaptchaEngine;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.captcha.ReceiveBotCaptchaResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.model.actor.request.impl.CaptchaRequest;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestCaptchaAnswer extends ClientPacket
{
    private int answer;
    
    @Override
    protected void readImpl() throws Exception {
        this.readLong();
        this.answer = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        CaptchaRequest request = player.getRequest(CaptchaRequest.class);
        if (Objects.nonNull(request)) {
            if (this.answer == request.getCaptcha().getCode()) {
                player.sendPacket(ReceiveBotCaptchaResult.SUCCESS);
                player.sendPacket(SystemMessageId.IDENTIFICATION_COMPLETED_HAVE_A_GOOD_TIME_WITH_THANK_YOU);
                request.cancelTimeout();
                player.removeRequest(CaptchaRequest.class);
            }
            else {
                this.onWrongCode(player, request);
            }
        }
        else {
            final Captcha captcha = CaptchaEngine.getInstance().next();
            request = new CaptchaRequest(player, captcha);
            player.addRequest(request);
            player.sendPacket(new ReceiveBotCaptchaImage(captcha, request.getRemainingTime()));
        }
    }
    
    private void onWrongCode(final Player player, final CaptchaRequest request) {
        if (request.isLimitReached()) {
            request.cancelTimeout();
            ReportTable.getInstance().punishBotDueUnsolvedCaptcha(player);
        }
        else {
            final Captcha captcha = CaptchaEngine.getInstance().next();
            request.newRequest(captcha);
            player.sendPacket(new ReceiveBotCaptchaImage(captcha, request.getRemainingTime()));
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.WRONG_AUTHENTICATION_CODE_IF_YOU_ENTER_THE_WRONG_CODE_S1_TIME_S_THE_SYSTEM_WILL_QUALIFY_YOU_AS_A_PROHIBITED_SOFTWARE_USER_AND_CHARGE_A_PENALTY_ATTEMPTS_LEFT_S2);
            msg.addInt(request.maxAttemps());
            msg.addInt(request.remainingAttemps());
            player.sendPacket(msg);
        }
    }
}
