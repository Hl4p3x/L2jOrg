// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Clan;

public class PledgeShowInfoUpdate extends PledgeAbstractPacket
{
    public PledgeShowInfoUpdate(final Clan clan) {
        super(clan);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PLEDGE_SHOW_INFO_UPDATE);
        this.writeInt(this.clan.getId());
        this.writeInt(((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).serverId());
        this.writeClanInfo(0);
    }
}
