// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Clan;

public class PledgeInfo extends ServerPacket
{
    private final Clan _clan;
    
    public PledgeInfo(final Clan clan) {
        this._clan = clan;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PLEDGE_INFO);
        this.writeInt(((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).serverId());
        this.writeInt(this._clan.getId());
        this.writeString((CharSequence)this._clan.getName());
        this.writeString((CharSequence)this._clan.getAllyName());
    }
}
