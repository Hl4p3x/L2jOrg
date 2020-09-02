// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.time.zone.ZoneRules;
import java.time.ZoneId;
import java.time.Instant;

public class ExEnterWorld extends ServerPacket
{
    private final int zoneIdOffsetSeconds;
    private final int epochInSeconds;
    private final int daylight;
    
    public ExEnterWorld() {
        final Instant now = Instant.now().plusSeconds(2L);
        this.epochInSeconds = (int)now.getEpochSecond();
        final ZoneRules rules = ZoneId.systemDefault().getRules();
        this.zoneIdOffsetSeconds = rules.getStandardOffset(now).getTotalSeconds();
        this.daylight = (int)rules.getDaylightSavings(now).toSeconds();
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ENTER_WORLD);
        this.writeInt(this.epochInSeconds);
        this.writeInt(-this.zoneIdOffsetSeconds);
        this.writeInt(this.daylight);
        this.writeInt(40);
    }
}
