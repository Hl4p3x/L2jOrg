// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.Config;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class AuthRequest extends SendablePacket
{
    @Override
    protected void writeImpl(final AuthServerClient client) {
        final ServerSettings serverSettings = (ServerSettings)Configurator.getSettings((Class)ServerSettings.class);
        this.writeByte(0);
        this.writeByte(serverSettings.serverId());
        this.writeByte(serverSettings.acceptAlternativeId());
        this.writeInt(serverSettings.type());
        this.writeInt(serverSettings.maximumOnlineUsers());
        this.writeByte(serverSettings.ageLimit());
        this.writeByte(serverSettings.isShowingBrackets());
        this.writeByte(serverSettings.isPvP());
        final int hosts = Config.GAME_SERVER_HOSTS.size();
        this.writeShort(hosts);
        for (int i = 0; i < Config.GAME_SERVER_HOSTS.size(); ++i) {
            this.writeString((CharSequence)Config.GAME_SERVER_HOSTS.get(i));
            this.writeString((CharSequence)Config.GAME_SERVER_SUBNETS.get(i));
        }
        this.writeShort(serverSettings.port());
    }
}
