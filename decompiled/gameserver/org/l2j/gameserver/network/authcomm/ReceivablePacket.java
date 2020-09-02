// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import io.github.joealisson.mmocore.ReadablePacket;

public abstract class ReceivablePacket extends ReadablePacket<AuthServerClient>
{
    private static final Logger logger;
    
    public final boolean read() {
        try {
            this.readImpl();
        }
        catch (Exception e) {
            ReceivablePacket.logger.error(e.getLocalizedMessage(), (Throwable)e);
            return false;
        }
        return true;
    }
    
    public final void run() {
        try {
            this.runImpl();
        }
        catch (Exception e) {
            ReceivablePacket.logger.error(e.getLocalizedMessage(), (Throwable)e);
        }
    }
    
    protected abstract void readImpl();
    
    protected abstract void runImpl();
    
    protected void sendPacket(final SendablePacket sp) {
        ((AuthServerClient)this.client).sendPacket(sp);
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)ReceivablePacket.class);
    }
}
