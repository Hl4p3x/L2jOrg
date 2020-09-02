// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.autoplay;

import org.l2j.gameserver.engine.autoplay.AutoPlaySettings;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExAutoPlaySettingResponse extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_AUTOPLAY_SETTING);
        final AutoPlaySettings settings = client.getPlayer().getAutoPlaySettings();
        this.writeShort(settings.getSize());
        this.writeByte(settings.isActive());
        this.writeByte(settings.isAutoPickUpOn());
        this.writeShort(settings.getNextTargetMode());
        this.writeByte(settings.isNearTarget());
        this.writeInt(settings.getUsableHpPotionPercent());
        this.writeInt(settings.getUsableHpPetPotionPercent());
        this.writeByte(settings.isRespectfulMode());
    }
}
