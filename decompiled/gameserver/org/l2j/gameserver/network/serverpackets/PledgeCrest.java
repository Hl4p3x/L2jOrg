// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.database.data.CrestData;
import org.l2j.gameserver.data.sql.impl.CrestTable;

public final class PledgeCrest extends ServerPacket
{
    private final int _crestId;
    private final byte[] _data;
    
    public PledgeCrest(final int crestId) {
        this._crestId = crestId;
        final CrestData crest = CrestTable.getInstance().getCrest(crestId);
        this._data = (byte[])((crest != null) ? crest.getData() : null);
    }
    
    public PledgeCrest(final int crestId, final byte[] data) {
        this._crestId = crestId;
        this._data = data;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PLEDGE_CREST);
        this.writeInt(((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).serverId());
        this.writeInt(this._crestId);
        if (this._data != null) {
            this.writeInt(this._data.length);
            this.writeBytes(this._data);
        }
        else {
            this.writeInt(0);
        }
    }
}
