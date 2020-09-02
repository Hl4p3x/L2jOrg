// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import java.util.Iterator;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.commons.util.StreamUtil;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritEvolution;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExElementalSpiritEvolution extends ClientPacket
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
        final boolean canEvolve = this.checkConditions(player, spirit);
        if (canEvolve) {
            spirit.upgrade();
            ((GameClient)this.client).sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_EVOLVED_TO_S2_STAR).addElementalSpirit(this.type)).addInt(spirit.getStage()));
            final UserInfo userInfo = new UserInfo(player);
            userInfo.addComponentType(UserInfoType.SPIRITS);
            ((GameClient)this.client).sendPacket(userInfo);
        }
        ((GameClient)this.client).sendPacket(new ElementalSpiritEvolution(this.type, canEvolve));
    }
    
    private boolean checkConditions(final Player player, final ElementalSpirit spirit) {
        boolean noMeetConditions = false;
        if (noMeetConditions = (player.getPrivateStoreType() != PrivateStoreType.NONE)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP);
        }
        else if (noMeetConditions = player.isInBattle()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.CANNOT_EVOLVE_DURING_BATTLE);
        }
        else if (noMeetConditions = !spirit.canEvolve()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.SPIRIT_CANNOT_BE_EVOLVED);
        }
        else if (noMeetConditions = !this.consumeEvolveItems(player, spirit)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_MATERIALS_REQUIRED_TO_EVOLVE);
        }
        return !noMeetConditions;
    }
    
    private boolean consumeEvolveItems(final Player player, final ElementalSpirit spirit) {
        final PlayerInventory inventory = player.getInventory();
        try {
            final IntCollection blocked = (IntCollection)StreamUtil.collectToSet(spirit.getItemsToEvolve().stream().mapToInt(ItemHolder::getId));
            inventory.setInventoryBlock(blocked, InventoryBlockType.BLACKLIST);
            for (final ItemHolder itemHolder : spirit.getItemsToEvolve()) {
                if (inventory.getInventoryItemCount(itemHolder.getId(), -1) < itemHolder.getCount()) {
                    return false;
                }
            }
            for (final ItemHolder itemHolder : spirit.getItemsToEvolve()) {
                player.destroyItemByItemId("Evolve", itemHolder.getId(), itemHolder.getCount(), player, true);
            }
            return true;
        }
        finally {
            inventory.unblock();
        }
    }
}
