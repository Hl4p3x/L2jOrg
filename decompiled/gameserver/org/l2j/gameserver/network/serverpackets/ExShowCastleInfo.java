// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Collection;
import java.time.ZoneId;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public class ExShowCastleInfo extends ServerPacket
{
    private static final Logger LOGGER;
    public static final ExShowCastleInfo STATIC_PACKET;
    
    private ExShowCastleInfo() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_CASTLE_INFO);
        final Collection<Castle> castles = CastleManager.getInstance().getCastles();
        this.writeInt(castles.size());
        for (final Castle castle : castles) {
            this.writeInt(castle.getId());
            if (castle.getOwnerId() > 0) {
                if (ClanTable.getInstance().getClan(castle.getOwnerId()) != null) {
                    this.writeString((CharSequence)ClanTable.getInstance().getClan(castle.getOwnerId()).getName());
                }
                else {
                    ExShowCastleInfo.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, castle.getName(), castle.getOwnerId()));
                    this.writeString((CharSequence)"");
                }
            }
            else {
                this.writeString((CharSequence)"");
            }
            this.writeInt(castle.getTaxPercent(TaxType.BUY));
            this.writeInt((int)castle.getSiege().getSiegeDate().atZone(ZoneId.systemDefault()).toEpochSecond());
            this.writeByte((byte)(byte)(castle.getSiege().isInProgress() ? 1 : 0));
            this.writeByte((byte)castle.getSide().ordinal());
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ExShowCastleInfo.class);
        STATIC_PACKET = new ExShowCastleInfo();
    }
}
