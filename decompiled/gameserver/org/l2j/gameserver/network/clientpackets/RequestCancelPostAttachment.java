// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.Iterator;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.util.GameUtils;
import java.util.Objects;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.Config;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.network.GameClient;

public final class RequestCancelPostAttachment extends ClientPacket
{
    private int mailId;
    
    public void readImpl() {
        this.mailId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (!((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).allowMail() || !Config.ALLOW_ATTACHMENTS) {
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getTransaction().tryPerformAction("cancelpost")) {
            return;
        }
        final MailData mail = MailEngine.getInstance().getMail(this.mailId);
        if (Objects.isNull(mail)) {
            return;
        }
        if (mail.getSender() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return;
        }
        if (!player.isInsideZone(ZoneType.PEACE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }
        if (player.getActiveTradeList() != null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
            return;
        }
        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_CANCEL_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE);
            return;
        }
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }
        if (!mail.hasAttachments()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT);
            return;
        }
        final ItemContainer attachments = mail.getAttachment();
        if (attachments == null || attachments.getSize() == 0) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT);
            return;
        }
        int weight = 0;
        int slots = 0;
        for (final Item item : attachments.getItems()) {
            if (item == null) {
                continue;
            }
            if (item.getOwnerId() != player.getObjectId()) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                return;
            }
            if (item.getItemLocation() != ItemLocation.MAIL) {
                GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
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
            player.sendPacket(SystemMessageId.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }
        if (!player.getInventory().validateWeight(weight)) {
            player.sendPacket(SystemMessageId.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }
        final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
        for (final Item item2 : attachments.getItems()) {
            if (item2 == null) {
                continue;
            }
            final long count = item2.getCount();
            final Item newItem = attachments.transferItem(attachments.getName(), item2.getObjectId(), count, player.getInventory(), player, null);
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
            player.sendPacket(sm);
        }
        mail.removeAttachments();
        if (playerIU != null) {
            player.sendInventoryUpdate(playerIU);
        }
        else {
            player.sendItemList();
        }
        final Player receiver = World.getInstance().findPlayer(mail.getReceiver());
        if (receiver != null) {
            final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.S1_CANCELED_THE_SENT_MAIL);
            sm2.addString(player.getName());
            receiver.sendPacket(sm2);
            receiver.sendPacket(ExChangePostState.deleted(true, this.mailId));
        }
        MailEngine.getInstance().deleteMailInDb(this.mailId);
        player.sendPacket(ExChangePostState.deleted(false, this.mailId));
        player.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_CANCELLED);
    }
}
