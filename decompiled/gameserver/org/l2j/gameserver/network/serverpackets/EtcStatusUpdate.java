// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.instance.Player;

public class EtcStatusUpdate extends ServerPacket
{
    private final Player player;
    private int toggles;
    
    public EtcStatusUpdate(final Player activeChar) {
        this.player = activeChar;
        this.toggles = ((this.player.isMessageRefusing() || this.player.isChatBanned() || this.player.isSilenceMode()) ? 1 : 0);
        this.toggles |= (this.player.isInsideZone(ZoneType.DANGER_AREA) ? 2 : 0);
        this.toggles |= (this.player.hasCharmOfCourage() ? 4 : 0);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.ETC_STATUS_UPDATE);
        this.writeByte(this.player.getCharges());
        this.writeInt(this.player.getWeightPenalty());
        this.writeByte(0);
        this.writeByte(0);
        this.writeByte(0);
        this.writeByte(this.player.getChargedSouls());
        this.writeByte(this.toggles);
        this.writeByte(this.player.getShadowSouls());
        this.writeByte(this.player.getShineSouls());
    }
}
