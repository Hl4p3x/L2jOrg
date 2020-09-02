// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestGetItemFromPet extends ClientPacket
{
    private static final Logger LOGGER;
    private int _objectId;
    private long _amount;
    private int _unknown;
    
    public void readImpl() {
        this._objectId = this.readInt();
        this._amount = this.readLong();
        this._unknown = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (this._amount <= 0L || player == null || !player.hasPet()) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("getfrompet")) {
            player.sendMessage("You get items from pet too fast.");
            return;
        }
        if (player.hasItemRequest()) {
            return;
        }
        final Pet pet = player.getPet();
        final Item item = pet.getInventory().getItemByObjectId(this._objectId);
        if (item == null) {
            return;
        }
        if (this._amount > item.getCount()) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IJJ)Ljava/lang/String;, this.getClass().getSimpleName(), player.getName(), player.getAccountName(), this._objectId, this._amount, item.getCount()));
            return;
        }
        final Item transferedItem = pet.transferItem("Transfer", this._objectId, this._amount, player.getInventory(), player, pet);
        if (transferedItem != null) {
            player.sendPacket(new PetItemList(pet.getInventory().getItems()));
        }
        else {
            RequestGetItemFromPet.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, pet.getName(), player.getName()));
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestGetItemFromPet.class);
    }
}
