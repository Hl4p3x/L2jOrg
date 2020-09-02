// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.container.PetInventory;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestGiveItemToPet extends ClientPacket
{
    private static final Logger LOGGER;
    private int _objectId;
    private long _amount;
    
    public void readImpl() {
        this._objectId = this.readInt();
        this._amount = this.readLong();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (this._amount <= 0L || player == null || !player.hasPet()) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("giveitemtopet")) {
            player.sendMessage("You are giving items to pet too fast.");
            return;
        }
        if (player.hasItemRequest()) {
            return;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && player.getReputation() < 0) {
            return;
        }
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendMessage("You cannot exchange items while trading.");
            return;
        }
        final Item item = player.getInventory().getItemByObjectId(this._objectId);
        if (item == null) {
            return;
        }
        if (this._amount > item.getCount()) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IJJ)Ljava/lang/String;, this.getClass().getSimpleName(), player.getName(), player.getAccountName(), this._objectId, this._amount, item.getCount()));
            return;
        }
        if (item.isAugmented()) {
            return;
        }
        if (item.isHeroItem() || !item.isDropable() || !item.isDestroyable() || !item.isTradeable()) {
            player.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return;
        }
        final Pet pet = player.getPet();
        if (pet.isDead()) {
            player.sendPacket(SystemMessageId.YOUR_PET_IS_DEAD_AND_ANY_ATTEMPT_YOU_MAKE_TO_GIVE_IT_SOMETHING_GOES_UNRECOGNIZED);
            return;
        }
        final PetInventory inventory = pet.getInventory();
        if (!inventory.validateCapacity(item) || !inventory.validateWeight(item, this._amount)) {
            player.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
            return;
        }
        final Item transferedItem = player.transferItem("Transfer", this._objectId, this._amount, pet.getInventory(), pet);
        if (transferedItem != null) {
            player.sendPacket(new PetItemList(inventory.getItems()));
        }
        else {
            RequestGiveItemToPet.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, pet.getName(), player.getName()));
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestGiveItemToPet.class);
    }
}
