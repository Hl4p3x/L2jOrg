// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Objects;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ElementalSpiritInfo extends AbstractElementalSpiritPacket
{
    private final byte spiritType;
    private final byte type;
    
    public ElementalSpiritInfo(final byte spiritType, final byte packetType) {
        this.spiritType = spiritType;
        this.type = packetType;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ELEMENTAL_SPIRIT_INFO);
        final Player player = client.getPlayer();
        final ElementalSpirit[] spirits = player.getSpirits();
        if (Objects.isNull(spirits)) {
            this.writeByte(0);
            this.writeByte(0);
            this.writeByte(0);
            return;
        }
        this.writeByte(this.type);
        this.writeByte(this.spiritType);
        this.writeByte(spirits.length);
        for (final ElementalSpirit spirit : spirits) {
            this.writeByte(spirit.getType());
            this.writeByte(1);
            this.writeSpiritInfo(spirit);
        }
        this.writeInt(1);
        for (int j = 0; j < 1; ++j) {
            this.writeInt(57);
            this.writeLong(50000L);
        }
    }
}
