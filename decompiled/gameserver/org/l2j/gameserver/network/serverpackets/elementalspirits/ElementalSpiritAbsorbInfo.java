// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.elemental.AbsorbItem;
import java.util.Objects;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ElementalSpiritAbsorbInfo extends ServerPacket
{
    private final byte type;
    
    public ElementalSpiritAbsorbInfo(final byte type) {
        this.type = type;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ELEMENTAL_SPIRIT_ABSORB_INFO);
        final Player player = client.getPlayer();
        final ElementalSpirit spirit = player.getElementalSpirit(ElementalType.of(this.type));
        if (Objects.isNull(spirit)) {
            this.writeByte(0);
            this.writeByte(0);
            return;
        }
        this.writeByte(1);
        this.writeByte(this.type);
        this.writeByte(spirit.getStage());
        this.writeLong(spirit.getExperience());
        this.writeLong(spirit.getExperienceToNextLevel());
        this.writeLong(spirit.getExperienceToNextLevel());
        this.writeInt((int)spirit.getLevel());
        this.writeInt(spirit.getMaxLevel());
        final List<AbsorbItem> absorbItems = spirit.getAbsorbItems();
        this.writeInt(absorbItems.size());
        for (final AbsorbItem absorbItem : absorbItems) {
            this.writeInt(absorbItem.getId());
            this.writeInt(Util.zeroIfNullOrElse((Object)player.getInventory().getItemByItemId(absorbItem.getId()), item -> (int)item.getCount()));
            this.writeInt(absorbItem.getExperience());
        }
    }
}
