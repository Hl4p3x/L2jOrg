// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.GameServer;
import org.slf4j.Logger;
import org.l2j.gameserver.network.GameClient;
import io.github.joealisson.mmocore.ReadablePacket;

public abstract class ClientPacket extends ReadablePacket<GameClient>
{
    private static final Logger LOGGER;
    
    protected boolean read() {
        try {
            this.readImpl();
            return true;
        }
        catch (InvalidDataPacketException e2) {
            ClientPacket.LOGGER.warn("[{}] Invalid data packet {} from client {}", new Object[] { GameServer.fullVersion, this, this.client });
        }
        catch (Exception e) {
            ClientPacket.LOGGER.error("[{}] Error while reading packet {} from client {}", new Object[] { GameServer.fullVersion, this, this.client });
            ClientPacket.LOGGER.error(e.getMessage(), (Throwable)e);
        }
        return false;
    }
    
    public void run() {
        try {
            this.runImpl();
        }
        catch (Exception e) {
            ClientPacket.LOGGER.error("[{}] Error while running packet {} from client {}", new Object[] { GameServer.fullVersion, this, this.client });
            ClientPacket.LOGGER.error(e.getMessage(), (Throwable)e);
        }
    }
    
    protected abstract void runImpl() throws Exception;
    
    protected abstract void readImpl() throws Exception;
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClientPacket.class);
    }
}
