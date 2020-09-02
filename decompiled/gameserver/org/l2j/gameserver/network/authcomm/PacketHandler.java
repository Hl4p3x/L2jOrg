// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm;

import org.slf4j.LoggerFactory;
import io.github.joealisson.mmocore.Client;
import org.l2j.gameserver.network.authcomm.as2gs.PingRequest;
import org.l2j.gameserver.network.authcomm.as2gs.GetAccountInfo;
import org.l2j.gameserver.network.authcomm.as2gs.KickPlayer;
import org.l2j.gameserver.network.authcomm.as2gs.PlayerAuthResponse;
import org.l2j.gameserver.network.authcomm.as2gs.LoginServerFail;
import org.l2j.gameserver.network.authcomm.as2gs.AuthResponse;
import io.github.joealisson.mmocore.ReadablePacket;
import io.github.joealisson.mmocore.ReadableBuffer;
import org.slf4j.Logger;

public class PacketHandler implements io.github.joealisson.mmocore.PacketHandler<AuthServerClient>
{
    private static final Logger LOGGER;
    
    public ReadablePacket<AuthServerClient> handlePacket(final ReadableBuffer buf, final AuthServerClient client) {
        final int id = buf.readByte() & 0xFF;
        ReadablePacket<AuthServerClient> readablePacket = null;
        switch (id) {
            case 0: {
                readablePacket = new AuthResponse();
                break;
            }
            case 1: {
                readablePacket = new LoginServerFail();
                break;
            }
            case 2: {
                readablePacket = new PlayerAuthResponse();
                break;
            }
            case 3: {
                readablePacket = new KickPlayer();
                break;
            }
            case 4: {
                readablePacket = new GetAccountInfo();
                break;
            }
            case 6: {
                readablePacket = null;
                break;
            }
            case 255: {
                readablePacket = new PingRequest();
                break;
            }
            default: {
                PacketHandler.LOGGER.error("Received unknown packet: {}", (Object)Integer.toHexString(id));
                readablePacket = null;
                break;
            }
        }
        return readablePacket;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PacketHandler.class);
    }
}
