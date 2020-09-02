// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.punishment.PunishmentType;

public class ExUserBanInfo extends ServerPacket
{
    private final PunishmentType type;
    private final long expiration;
    private final String reason;
    
    public ExUserBanInfo(final PunishmentType type, final long expiration, final String reason) {
        this.type = type;
        this.expiration = expiration;
        this.reason = reason;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_USER_BAN_INFO);
        this.writeInt(this.type.ordinal());
        this.writeLong((this.expiration > 0L) ? System.currentTimeMillis() : 0L);
        this.writeLong(this.expiration);
        this.writeString((CharSequence)this.reason);
    }
}
