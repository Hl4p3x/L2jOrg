// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pvpbook;

import org.l2j.gameserver.data.database.data.KillerData;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.pvpbook.PvpBookList;
import org.l2j.gameserver.network.GameClient;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestPvpBookList extends ClientPacket
{
    @Override
    protected void readImpl() {
    }
    
    @Override
    protected void runImpl() {
        final long since = Instant.now().minus(1L, (TemporalUnit)ChronoUnit.DAYS).getEpochSecond();
        final List<KillerData> killers = ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findKillersByPlayer(((GameClient)this.client).getPlayer().getObjectId(), since);
        ((GameClient)this.client).sendPacket(new PvpBookList(killers));
    }
}
