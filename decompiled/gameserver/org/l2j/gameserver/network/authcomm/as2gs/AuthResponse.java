// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.as2gs;

import org.slf4j.LoggerFactory;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerInGame;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.slf4j.Logger;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;

public class AuthResponse extends ReceivablePacket
{
    private static final Logger LOGGER;
    private int serverId;
    private String serverName;
    
    @Override
    protected void readImpl() {
        this.serverId = this.readByte();
        this.serverName = this.readString();
    }
    
    @Override
    protected void runImpl() {
        final String[] accounts = AuthServerCommunication.getInstance().getAccounts();
        this.sendPacket(new PlayerInGame(accounts));
        ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).setServerId(this.serverId);
        AuthResponse.LOGGER.info("Registered on authserver as {} [{}]", (Object)this.serverId, (Object)this.serverName);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AuthResponse.class);
    }
}
