// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Objects;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.GameClient;

public abstract class UpdateElementalSpiritPacket extends AbstractElementalSpiritPacket
{
    private final byte type;
    private final boolean update;
    
    UpdateElementalSpiritPacket(final byte type, final boolean update) {
        this.type = type;
        this.update = update;
    }
    
    protected void writeUpdate(final GameClient client) {
        final Player player = client.getPlayer();
        this.writeByte(this.update);
        this.writeByte(this.type);
        if (this.update) {
            final ElementalSpirit spirit = player.getElementalSpirit(ElementalType.of(this.type));
            if (Objects.isNull(spirit)) {
                return;
            }
            this.writeByte(this.type);
            this.writeSpiritInfo(spirit);
        }
    }
}
