// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.AccessLevel;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExNoticePostSent;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import java.util.ArrayList;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import java.util.Collections;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;

public final class RequestSendPost extends ClientPacket
{
    private static final int BATCH_LENGTH = 12;
    private static final int MAX_RECV_LENGTH = 16;
    private static final int MAX_SUBJ_LENGTH = 128;
    private static final int MAX_TEXT_LENGTH = 512;
    private static final int MAX_ATTACHMENTS = 8;
    private static final int INBOX_SIZE = 240;
    private static final int OUTBOX_SIZE = 240;
    private String _receiver;
    private boolean _isCod;
    private String _subject;
    private String _text;
    private List<ItemHolder> items;
    private long _reqAdena;
    
    public RequestSendPost() {
        this.items = Collections.emptyList();
    }
    
    public void readImpl() throws InvalidDataPacketException {
        this._receiver = this.readString();
        this._isCod = (this.readInt() != 0);
        this._subject = this.readString();
        this._text = this.readString();
        final int attachCount = this.readInt();
        if (attachCount < 0 || attachCount > Config.MAX_ITEM_IN_PACKET || attachCount * 12 + 8 != this.available()) {
            throw new InvalidDataPacketException();
        }
        if (attachCount > 0) {
            this.items = new ArrayList<ItemHolder>(attachCount);
            for (int i = 0; i < attachCount; ++i) {
                final int objectId = this.readInt();
                final long count = this.readLong();
                if (objectId < 1 || count < 0L) {
                    this.items = null;
                    throw new InvalidDataPacketException();
                }
                this.items.add(new ItemHolder(objectId, count));
            }
        }
        this._reqAdena = this.readLong();
    }
    
    public void runImpl() {
        if (!((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).allowMail()) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        if (!Config.ALLOW_ATTACHMENTS) {
            this.items = Collections.emptyList();
            this._isCod = false;
            this._reqAdena = 0L;
        }
        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            return;
        }
        if (!player.isInsideZone(ZoneType.PEACE) && !this.items.isEmpty()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }
        if (player.getActiveTradeList() != null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE);
            return;
        }
        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_SEND_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE_COMBINING_JEWELS_OR_SEALING_UNSEALING_OR_COMBINING);
            return;
        }
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }
        if (this._receiver.length() > 16) {
            player.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_RECIPIENT_EXCEEDED);
            return;
        }
        if (this._subject.length() > 128) {
            player.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED);
            return;
        }
        if (this._text.length() > 512) {
            player.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED);
            return;
        }
        if (this.items.size() > 8) {
            player.sendPacket(SystemMessageId.ITEM_SELECTION_IS_POSSIBLE_UP_TO_8);
            return;
        }
        if (this._reqAdena < 0L || this._reqAdena > Inventory.MAX_ADENA) {
            return;
        }
        if (this._isCod) {
            if (this._reqAdena == 0L) {
                player.sendPacket(SystemMessageId.WHEN_NOT_ENTERING_THE_AMOUNT_FOR_THE_PAYMENT_REQUEST_YOU_CANNOT_SEND_ANY_MAIL);
                return;
            }
            if (this.items.isEmpty()) {
                player.sendPacket(SystemMessageId.IT_S_A_PAYMENT_REQUEST_TRANSACTION_PLEASE_ATTACH_THE_ITEM);
                return;
            }
        }
        final int receiverId = PlayerNameTable.getInstance().getIdByName(this._receiver);
        if (receiverId <= 0) {
            player.sendPacket(SystemMessageId.WHEN_THE_RECIPIENT_DOESN_T_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
            return;
        }
        if (receiverId == player.getObjectId()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF);
            return;
        }
        final int level = PlayerNameTable.getInstance().getAccessLevelById(receiverId);
        final AccessLevel accessLevel = AdminData.getInstance().getAccessLevel(level);
        if (accessLevel != null && accessLevel.isGm() && !player.getAccessLevel().isGm()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_MESSAGE_TO_C1_DID_NOT_REACH_ITS_RECIPIENT_YOU_CANNOT_SEND_MAIL_TO_THE_GM_STAFF);
            sm.addString(this._receiver);
            player.sendPacket(sm);
            return;
        }
        if (player.isJailed() && ((Config.JAIL_DISABLE_TRANSACTION && !this.items.isEmpty()) || ((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).disableChatInJail())) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }
        if (BlockList.isInBlockList(receiverId, player.getObjectId())) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1);
            sm.addString(this._receiver);
            player.sendPacket(sm);
            return;
        }
        if (MailEngine.getInstance().getOutboxSize(player.getObjectId()) >= 240) {
            player.sendPacket(SystemMessageId.THE_MAIL_LIMIT_240_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED);
            return;
        }
        if (MailEngine.getInstance().getInboxSize(receiverId) >= 240) {
            player.sendPacket(SystemMessageId.THE_MAIL_LIMIT_240_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED);
            return;
        }
        if (!((GameClient)this.client).getFloodProtectors().getSendMail().tryPerformAction("sendmail")) {
            player.sendPacket(SystemMessageId.THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED);
            return;
        }
        if (MailEngine.getInstance().sendMail(player, receiverId, this._isCod, this._subject, this._text, this._reqAdena, this.items)) {
            player.sendPacket(ExNoticePostSent.valueOf(true));
            player.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_SENT);
        }
    }
}
