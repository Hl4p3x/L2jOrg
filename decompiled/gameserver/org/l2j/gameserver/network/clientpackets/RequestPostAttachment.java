// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.Iterator;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.Config;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;

public final class RequestPostAttachment extends ClientPacket
{
    private int mailId;
    
    public void readImpl() {
        this.mailId = this.readInt();
    }
    
    public void runImpl() {
        if (!((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).allowMail() || !Config.ALLOW_ATTACHMENTS) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("getattach")) {
            return;
        }
        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level");
            return;
        }
        if (!player.isInsideZone(ZoneType.PEACE)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }
        if (player.getActiveTradeList() != null) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE);
            return;
        }
        if (player.hasItemRequest()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CAN_T_RECEIVE_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE_COMBINING_JEWELS_OR_SEALING_UNSEALING_OR_COMBINING);
            return;
        }
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }
        final MailData mail = MailEngine.getInstance().getMail(this.mailId);
        if (mail == null) {
            return;
        }
        if (mail.getReceiver() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return;
        }
        if (!mail.hasAttachments()) {
            return;
        }
        final ItemContainer attachments = mail.getAttachment();
        if (attachments == null) {
            return;
        }
        int weight = 0;
        int slots = 0;
        for (final Item item : attachments.getItems()) {
            if (item == null) {
                continue;
            }
            if (item.getOwnerId() != mail.getSender()) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                return;
            }
            if (item.getItemLocation() != ItemLocation.MAIL) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
                return;
            }
            if (item.getLocationSlot() != mail.getId()) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
                return;
            }
            weight += (int)(item.getCount() * item.getTemplate().getWeight());
            if (!item.isStackable()) {
                slots += (int)item.getCount();
            }
            else {
                if (player.getInventory().getItemByItemId(item.getId()) != null) {
                    continue;
                }
                ++slots;
            }
        }
        if (!player.getInventory().validateCapacity(slots)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }
        if (!player.getInventory().validateWeight(weight)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }
        final long adena = mail.getFee();
        if (adena > 0L && !player.reduceAdena("PayMail", adena, null, true)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
            return;
        }
        final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
        for (final Item item2 : attachments.getItems()) {
            if (item2 == null) {
                continue;
            }
            if (item2.getOwnerId() != mail.getSender()) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                return;
            }
            final long count = item2.getCount();
            final Item newItem = attachments.transferItem(attachments.getName(), item2.getObjectId(), item2.getCount(), player.getInventory(), player, null);
            if (newItem == null) {
                return;
            }
            if (playerIU != null) {
                if (newItem.getCount() > count) {
                    playerIU.addModifiedItem(newItem);
                }
                else {
                    playerIU.addNewItem(newItem);
                }
            }
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S2_S1);
            sm.addItemName(item2.getId());
            sm.addLong(count);
            ((GameClient)this.client).sendPacket(sm);
        }
        if (playerIU != null) {
            player.sendInventoryUpdate(playerIU);
        }
        else {
            player.sendItemList();
        }
        mail.removeAttachments();
        final Player sender = World.getInstance().findPlayer(mail.getSender());
        if (adena > 0L) {
            if (sender != null) {
                sender.addAdena("PayMail", adena, player, false);
                final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.S2_HAS_MADE_A_PAYMENT_OF_S1_ADENA_PER_YOUR_PAYMENT_REQUEST_MAIL);
                sm2.addLong(adena);
                sm2.addString(player.getName());
                sender.sendPacket(sm2);
            }
            else {
                final Item paidAdena = ItemEngine.getInstance().createItem("PayMail", 57, adena, player, null);
                paidAdena.setOwnerId(mail.getSender());
                paidAdena.setItemLocation(ItemLocation.INVENTORY);
                paidAdena.updateDatabase(true);
                World.getInstance().removeObject(paidAdena);
            }
        }
        else if (sender != null) {
            final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.S1_ACQUIRED_THE_ATTACHED_ITEM_TO_YOUR_MAIL);
            sm2.addString(player.getName());
            sender.sendPacket(sm2);
        }
        ((GameClient)this.client).sendPacket(ExChangePostState.reAdded(true, this.mailId));
        ((GameClient)this.client).sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_RECEIVED);
    }
}
