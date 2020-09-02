// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.ServerType;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class KeyPacket extends ServerPacket
{
    private final byte[] _key;
    private final int _result;
    
    public KeyPacket(final byte[] key, final int result) {
        this._key = key;
        this._result = result;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.VERSION_CHECK);
        this.writeByte(this._result);
        for (int i = 0; i < 8; ++i) {
            this.writeByte(this._key[i]);
        }
        final ServerSettings serverSettings = (ServerSettings)Configurator.getSettings((Class)ServerSettings.class);
        this.writeInt(true);
        this.writeInt(serverSettings.serverId());
        this.writeByte(false);
        this.writeInt(0);
        this.writeByte((serverSettings.type() & ServerType.CLASSIC.getMask()) != 0x0);
        this.writeByte(0);
    }
}
