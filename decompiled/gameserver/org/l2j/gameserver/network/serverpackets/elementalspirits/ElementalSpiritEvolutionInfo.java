// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.elementalspirits;

import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Objects;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ElementalSpiritEvolutionInfo extends ServerPacket
{
    private final byte type;
    
    public ElementalSpiritEvolutionInfo(final byte type) {
        this.type = type;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ELEMENTAL_SPIRIT_EVOLUTION_INFO);
        final Player player = client.getPlayer();
        final ElementalSpirit spirit = player.getElementalSpirit(ElementalType.of(this.type));
        if (Objects.isNull(spirit)) {
            this.writeByte(0);
            this.writeInt(0);
            return;
        }
        this.writeByte(this.type);
        this.writeInt(spirit.getNpcId());
        this.writeInt(1);
        this.writeInt((int)spirit.getStage());
        this.writeDouble(100.0);
        final List<ItemHolder> items = spirit.getItemsToEvolve();
        this.writeInt(items.size());
        for (final ItemHolder item : items) {
            this.writeInt(item.getId());
            this.writeLong(item.getCount());
        }
    }
}
