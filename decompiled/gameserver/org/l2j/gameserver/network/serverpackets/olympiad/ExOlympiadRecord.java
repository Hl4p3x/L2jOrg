// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.engine.olympiad.OlympiadRuleType;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadRecord extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_OLYMPIAD_RECORD);
        final Olympiad olympiad = Olympiad.getInstance();
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(5);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(olympiad.getSeasonYear());
        this.writeInt(olympiad.getSeasonMonth());
        this.writeByte(olympiad.isMatchesInProgress());
        this.writeInt(olympiad.getCurrentSeason());
        this.writeByte(olympiad.isRegistered(client.getPlayer()));
        this.writeByte(OlympiadRuleType.CLASSLESS.ordinal());
    }
}
