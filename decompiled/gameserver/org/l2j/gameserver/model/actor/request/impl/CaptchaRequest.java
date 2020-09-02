// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.request.impl;

import org.l2j.gameserver.datatables.ReportTable;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import java.time.Duration;
import org.l2j.gameserver.model.actor.instance.Player;
import java.time.Instant;
import org.l2j.gameserver.engine.captcha.Captcha;
import org.l2j.gameserver.model.actor.request.AbstractRequest;

public class CaptchaRequest extends AbstractRequest
{
    private static final byte MAX_ATTEMPTS = 3;
    private static final int DURATION = 20;
    private Captcha captcha;
    private byte count;
    private final Instant timeout;
    
    public CaptchaRequest(final Player activeChar, final Captcha captcha) {
        super(activeChar);
        this.count = 0;
        this.captcha = captcha;
        final long currentTime = System.currentTimeMillis();
        this.setTimestamp(currentTime);
        this.scheduleTimeout(Duration.ofMinutes(20L).toMillis());
        this.timeout = Instant.ofEpochMilli(currentTime).plus(20L, (TemporalUnit)ChronoUnit.MINUTES);
    }
    
    @Override
    public boolean isUsingItem(final int objectId) {
        return false;
    }
    
    public int getRemainingTime() {
        return (int)this.timeout.minusMillis(System.currentTimeMillis()).getEpochSecond();
    }
    
    public void refresh(final Captcha captcha) {
        this.captcha = captcha;
    }
    
    public void newRequest(final Captcha captcha) {
        ++this.count;
        this.captcha = captcha;
    }
    
    public boolean isLimitReached() {
        return this.count >= 2;
    }
    
    public Captcha getCaptcha() {
        return this.captcha;
    }
    
    @Override
    public void onTimeout() {
        ReportTable.getInstance().punishBotDueUnsolvedCaptcha(this.getPlayer());
    }
    
    public int maxAttemps() {
        return 3;
    }
    
    public int remainingAttemps() {
        return Math.max(3 - this.count, 0);
    }
}
