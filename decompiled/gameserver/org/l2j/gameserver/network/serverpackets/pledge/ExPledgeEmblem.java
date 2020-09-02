// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeEmblem extends ServerPacket
{
    private static final int TOTAL_SIZE = 65664;
    private final int _crestId;
    private final int _clanId;
    private final byte[] _data;
    private final int _chunkId;
    
    public ExPledgeEmblem(final int crestId, final byte[] chunkedData, final int clanId, final int chunkId) {
        this._crestId = crestId;
        this._data = chunkedData;
        this._clanId = clanId;
        this._chunkId = chunkId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_EMBLEM);
        this.writeInt(((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).serverId());
        this.writeInt(this._clanId);
        this.writeInt(this._crestId);
        this.writeInt(this._chunkId);
        this.writeInt(65664);
        if (this._data != null) {
            this.writeInt(this._data.length);
            this.writeBytes(this._data);
        }
        else {
            this.writeInt(0);
        }
    }
}
