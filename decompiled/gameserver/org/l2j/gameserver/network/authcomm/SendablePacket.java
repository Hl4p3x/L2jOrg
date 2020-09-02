// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm;

import org.slf4j.LoggerFactory;
import io.github.joealisson.mmocore.Client;
import org.slf4j.Logger;
import io.github.joealisson.mmocore.WritablePacket;

public abstract class SendablePacket extends WritablePacket<AuthServerClient>
{
    private static final Logger logger;
    
    public boolean write(final AuthServerClient client) {
        try {
            this.writeImpl(client);
        }
        catch (Exception e) {
            SendablePacket.logger.error(e.getLocalizedMessage(), (Throwable)e);
            return false;
        }
        return true;
    }
    
    protected abstract void writeImpl(final AuthServerClient client);
    
    static {
        logger = LoggerFactory.getLogger((Class)SendablePacket.class);
    }
}
