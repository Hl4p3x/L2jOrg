// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.ChatHandler;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerChat;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.returns.ChatFilterReturn;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.Disconnection;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.ChatType;
import org.slf4j.Logger;

public final class Say2 extends ClientPacket
{
    private static final Logger LOGGER;
    private static final Logger LOGGER_CHAT;
    private String text;
    private int type;
    private String target;
    
    public void readImpl() {
        this.text = this.readString();
        this.type = this.readInt();
        if (this.type == ChatType.WHISPER.getClientId()) {
            this.readByte();
            this.target = this.readString();
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        ChatType chatType = ChatType.findByClientId(this.type);
        if (Objects.isNull(chatType)) {
            Say2.LOGGER.warn("player {} send invalid type {} with text {}", new Object[] { player, this.type, this.text });
            Disconnection.of(player).defaultSequence(false);
            return;
        }
        if (this.text.isEmpty()) {
            Say2.LOGGER.warn("{} sending empty text. Possible packet hack!", (Object)player);
            Disconnection.of(player).defaultSequence(false);
            return;
        }
        if (!player.isGM() && ((this.text.indexOf(8) >= 0 && this.text.length() > 500) || (this.text.indexOf(8) < 0 && this.text.length() > 105))) {
            player.sendPacket(SystemMessageId.WHEN_A_USER_S_KEYBOARD_INPUT_EXCEEDS_A_CERTAIN_CUMULATIVE_SCORE_A_CHAT_BAN_WILL_BE_APPLIED_THIS_IS_DONE_TO_DISCOURAGE_SPAMMING_PLEASE_AVOID_POSTING_THE_SAME_MESSAGE_MULTIPLE_TIMES_DURING_A_SHORT_PERIOD);
            return;
        }
        final ChatSettings chatSettings = (ChatSettings)Configurator.getSettings((Class)ChatSettings.class);
        if (chatType == ChatType.WHISPER && chatSettings.l2WalkerProtectionEnabled() && chatSettings.isL2WalkerCommand(this.text)) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return;
        }
        if (player.isChatBanned() && this.text.charAt(0) != '.') {
            if (player.isAffected(EffectFlag.CHAT_BLOCK)) {
                player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_CHATTING_IS_NOT_ALLOWED);
            }
            else if (chatSettings.bannableChannels().contains(chatType)) {
                player.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER);
            }
            return;
        }
        if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CHAT_WHILE_PARTICIPATING_IN_THE_OLYMPIAD);
            return;
        }
        if (player.isJailed() && ((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).disableChatInJail() && !player.canOverrideCond(PcCondOverride.CHAT_CONDITIONS)) {
            player.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
            return;
        }
        if (chatType == ChatType.PETITION_PLAYER && player.isGM()) {
            chatType = ChatType.PETITION_GM;
        }
        if (chatSettings.logChat()) {
            if (chatType == ChatType.WHISPER) {
                Say2.LOGGER_CHAT.info("{} [ {} to {} ] {}", new Object[] { chatType, player, this.target, this.text });
            }
            else {
                Say2.LOGGER_CHAT.info("{} [ {} ] {}", new Object[] { chatType, player, this.text });
            }
        }
        if (this.text.indexOf(8) >= 0 && !this.parseAndPublishItem(player)) {
            return;
        }
        final ChatFilterReturn filter = EventDispatcher.getInstance().notifyEvent(new OnPlayerChat(player, this.target, this.text, chatType), player, ChatFilterReturn.class);
        if (Objects.nonNull(filter)) {
            this.text = filter.getFilteredText();
            chatType = filter.getChatType();
        }
        if (chatSettings.enableChatFilter()) {
            this.text = chatSettings.filterText(this.text);
        }
        final IChatHandler handler = ChatHandler.getInstance().getHandler(chatType);
        if (Objects.nonNull(handler)) {
            handler.handleChat(chatType, player, this.target, this.text);
        }
        else {
            Say2.LOGGER.info(invokedynamic(makeConcatWithConstants:(ILio/github/joealisson/mmocore/Client;)Ljava/lang/String;, this.type, this.client));
        }
    }
    
    private boolean parseAndPublishItem(final Player owner) {
        int pos1 = -1;
        while ((pos1 = this.text.indexOf(8, pos1)) > -1) {
            int pos2 = this.text.indexOf("ID=", pos1);
            if (pos2 == -1) {
                return false;
            }
            final StringBuilder result = new StringBuilder(9);
            pos2 += 3;
            while (Character.isDigit(this.text.charAt(pos2))) {
                result.append(this.text.charAt(pos2++));
            }
            final int id = Integer.parseInt(result.toString());
            final Item item = owner.getInventory().getItemByObjectId(id);
            if (Objects.isNull(item)) {
                Say2.LOGGER.warn("{} trying publish item which doesnt own! ID: {}", (Object)owner, (Object)id);
                return false;
            }
            item.publish();
            pos1 = this.text.indexOf(8, pos2) + 1;
            if (pos1 == 0) {
                Say2.LOGGER.warn("{} sent invalid publish item msg! ID:{}", (Object)owner, (Object)id);
                return false;
            }
        }
        return true;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Say2.class);
        LOGGER_CHAT = LoggerFactory.getLogger("chat");
    }
}
