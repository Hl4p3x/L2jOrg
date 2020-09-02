// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.gameserver.network.serverpackets.KeyPacket;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerLogout;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.commons.util.Util;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class ProtocolVersion extends ClientPacket
{
    private static final Logger LOGGER_ACCOUNTING;
    private int version;
    
    public void readImpl() {
        this.version = this.readInt();
    }
    
    public void runImpl() {
        if (this.version == -2) {
            ((GameClient)this.client).close();
        }
        else if (!Util.contains(((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).acceptedProtocols(), this.version)) {
            ProtocolVersion.LOGGER_ACCOUNTING.warn("Wrong protocol version {}, {}", (Object)this.version, (Object)this.client);
            AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(((GameClient)this.client).getAccountName()));
            ((GameClient)this.client).setProtocolOk(false);
            ((GameClient)this.client).close((WritablePacket)new KeyPacket(((GameClient)this.client).enableCrypt(), 0));
        }
        else {
            ((GameClient)this.client).setProtocolOk(true);
            ((GameClient)this.client).sendPacket(new KeyPacket(((GameClient)this.client).enableCrypt(), 1));
        }
    }
    
    static {
        LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
    }
}
