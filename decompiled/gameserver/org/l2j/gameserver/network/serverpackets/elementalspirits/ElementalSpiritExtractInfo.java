// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalSpirit;
import java.util.Objects;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ElementalSpiritExtractInfo extends ServerPacket
{
    private final byte type;
    
    public ElementalSpiritExtractInfo(final byte type) {
        this.type = type;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ELEMENTAL_SPIRIT_EXTRACT_INFO);
        final ElementalSpirit spirit = client.getPlayer().getElementalSpirit(ElementalType.of(this.type));
        if (Objects.isNull(spirit)) {
            this.writeByte(0);
            this.writeByte(0);
            return;
        }
        this.writeByte(this.type);
        this.writeByte(1);
        this.writeByte(1);
        this.writeInt(57);
        this.writeInt(1000000);
        this.writeInt(spirit.getExtractItem());
        this.writeInt(spirit.getExtractAmount());
    }
}
