// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritSetTalent;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExElementalInitTalent extends ClientPacket
{
    private byte type;
    
    @Override
    protected void readImpl() throws Exception {
        this.type = this.readByte();
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        final ElementalSpirit spirit = player.getElementalSpirit(ElementalType.of(this.type));
        if (Objects.isNull(spirit)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.NO_SPIRITS_ARE_AVAILABLE);
            return;
        }
        if (player.isInBattle()) {
            ((GameClient)this.client).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_RESET_SPIRIT_CHARACTERISTICS_DURING_BATTLE));
            ((GameClient)this.client).sendPacket(new ElementalSpiritSetTalent(this.type, false));
            return;
        }
        if (player.reduceAdena("Talent", 50000L, player, true)) {
            spirit.resetCharacteristics();
            ((GameClient)this.client).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.RESET_THE_SELECTED_SPIRIT_S_CHARACTERISTICS_SUCCESSFULLY));
            ((GameClient)this.client).sendPacket(new ElementalSpiritSetTalent(this.type, true));
        }
        else {
            ((GameClient)this.client).sendPacket(new ElementalSpiritSetTalent(this.type, false));
        }
    }
}
