// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritExtract;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExElementalSpiritExtract extends ClientPacket
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
        final boolean canExtract = this.checkConditions(player, spirit);
        if (canExtract) {
            final int amount = spirit.getExtractAmount();
            ((GameClient)this.client).sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.EXTRACTED_S1_S2_SUCCESSFULLY).addItemName(spirit.getExtractItem())).addInt(amount));
            spirit.resetLevel();
            player.addItem("Extract", spirit.getExtractItem(), amount, player, true);
            final UserInfo userInfo = new UserInfo(player);
            userInfo.addComponentType(UserInfoType.SPIRITS);
            ((GameClient)this.client).sendPacket(userInfo);
        }
        ((GameClient)this.client).sendPacket(new ElementalSpiritExtract(this.type, canExtract));
    }
    
    private boolean checkConditions(final Player player, final ElementalSpirit spirit) {
        boolean noMeetConditions = false;
        if (noMeetConditions = (spirit.getExtractAmount() < 1)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SKILL_XP_TO_EXTRACT);
        }
        else if (noMeetConditions = !player.getInventory().validateCapacity(1L)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.INVENTORY_IS_FULL_CANNOT_EXTRACT);
        }
        else if (noMeetConditions = (player.getPrivateStoreType() != PrivateStoreType.NONE)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP);
        }
        else if (noMeetConditions = player.isInBattle()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.CANNOT_EVOLVE_DURING_BATTLE);
        }
        else if (noMeetConditions = !player.reduceAdena("Extract", 1000000L, player, true)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_MATERIALS_REQUIRED_TO_EXTRACT);
        }
        return !noMeetConditions;
    }
}
