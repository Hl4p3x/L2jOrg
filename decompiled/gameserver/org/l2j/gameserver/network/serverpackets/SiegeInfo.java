// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.slf4j.LoggerFactory;
import java.time.ZonedDateTime;
import org.l2j.gameserver.model.Clan;
import java.time.ZoneId;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import java.util.Objects;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.slf4j.Logger;

public class SiegeInfo extends ServerPacket
{
    private static final Logger LOGGER;
    private final Castle castle;
    private final Player player;
    
    public SiegeInfo(final Castle castle, final Player player) {
        this.castle = castle;
        this.player = player;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CASTLE_SIEGE_INFO);
        if (Objects.nonNull(this.castle)) {
            this.writeInt(this.castle.getId());
            final int ownerId = this.castle.getOwnerId();
            this.writeInt((int)((ownerId == this.player.getClanId() && this.player.isClanLeader()) ? 1 : 0));
            this.writeInt(ownerId);
            if (ownerId > 0) {
                final Clan owner = ClanTable.getInstance().getClan(ownerId);
                if (owner != null) {
                    this.writeString((CharSequence)owner.getName());
                    this.writeString((CharSequence)owner.getLeaderName());
                    this.writeInt(owner.getAllyId());
                    this.writeString((CharSequence)owner.getAllyName());
                }
                else {
                    SiegeInfo.LOGGER.warn("Null owner for castle: {}", (Object)this.castle.getName());
                }
            }
            else {
                this.writeString((CharSequence)"");
                this.writeString((CharSequence)"");
                this.writeInt(0);
                this.writeString((CharSequence)"");
            }
            this.writeInt((int)(System.currentTimeMillis() / 1000L));
            final ZonedDateTime siegeDate = this.castle.getSiegeDate().atZone(ZoneId.systemDefault());
            this.writeInt((int)siegeDate.toEpochSecond());
            this.writeInt(0);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SiegeInfo.class);
    }
}
