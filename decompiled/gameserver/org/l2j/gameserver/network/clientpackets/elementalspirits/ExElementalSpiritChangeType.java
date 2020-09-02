// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExElementalSpiritChangeType extends ClientPacket
{
    private byte element;
    private byte type;
    
    @Override
    protected void readImpl() throws Exception {
        this.type = this.readByte();
        this.element = this.readByte();
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player.getElementalSpirit(ElementalType.of(this.element)))) {
            ((GameClient)this.client).sendPacket(SystemMessageId.NO_SPIRITS_ARE_AVAILABLE);
            return;
        }
        player.changeElementalSpirit(this.element);
        ((GameClient)this.client).sendPacket(new ElementalSpiritInfo(this.element, this.type));
        ((GameClient)this.client).sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_WILL_BE_YOUR_ATTRIBUTE_ATTACK_FROM_NOW_ON)).addElementalSpirit(this.element));
    }
}
