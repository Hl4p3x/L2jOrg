// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.chathandlers;

import io.github.joealisson.primitive.CHashIntMap;
import java.util.stream.Stream;
import java.util.Collection;
import java.time.temporal.TemporalAmount;
import org.l2j.gameserver.network.serverpackets.ExWorldChatCnt;
import java.util.function.Consumer;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import java.time.temporal.Temporal;
import java.time.Duration;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ChatType;
import java.time.Instant;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.handler.IChatHandler;

public final class ChatWorld implements IChatHandler
{
    private static final IntMap<Instant> REUSE;
    private static final ChatType[] CHAT_TYPES;
    
    public void handleChat(final ChatType type, final Player player, final String target, final String text) {
        final ChatSettings chatSettings = (ChatSettings)Configurator.getSettings((Class)ChatSettings.class);
        if (!chatSettings.worldChatEnabled()) {
            return;
        }
        final Instant now = Instant.now();
        if (!ChatWorld.REUSE.isEmpty()) {
            final Collection values = ChatWorld.REUSE.values();
            final Instant obj = now;
            Objects.requireNonNull(obj);
            values.removeIf(obj::isAfter);
        }
        if (player.getLevel() < chatSettings.worldChatMinLevel() && player.getVipTier() < 1) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.YOU_MUST_BE_LV_S1_OR_HIGHER_TO_USE_WORLD_CHAT_YOU_CAN_ALSO_USE_IT_WITH_VIP_BENEFITS).addInt(chatSettings.worldChatMinLevel()) });
        }
        else if (player.getWorldChatUsed() >= player.getWorldChatPoints()) {
            player.sendPacket(SystemMessageId.YOU_USED_WORLD_CHAT_UP_TO_TODAY_S_LIMIT_THE_USAGE_COUNT_OF_WORLD_CHAT_IS_RESET_EVERY_DAY_AT_6_30);
        }
        else {
            if (chatSettings.worldChatInterval().getSeconds() > 0L) {
                final Instant instant = (Instant)ChatWorld.REUSE.getOrDefault(player.getObjectId(), (Object)null);
                if (Objects.nonNull(instant) && instant.isAfter(now)) {
                    final Duration timeDiff = Duration.between(now, instant);
                    player.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_S1_SEC_UNTIL_YOU_ARE_ABLE_TO_USE_WORLD_CHAT).addInt((int)timeDiff.getSeconds()) });
                    return;
                }
            }
            final CreatureSay cs = new CreatureSay(player, type, text);
            final Stream stream = World.getInstance().getPlayers().stream();
            Objects.requireNonNull(player);
            final Stream filter = stream.filter(player::isNotBlocked);
            final CreatureSay obj2 = cs;
            Objects.requireNonNull(obj2);
            filter.forEach(obj2::sendTo);
            player.setWorldChatUsed(player.getWorldChatUsed() + 1);
            player.sendPacket(new ServerPacket[] { (ServerPacket)new ExWorldChatCnt(player) });
            if (chatSettings.worldChatInterval().getSeconds() > 0L) {
                ChatWorld.REUSE.put(player.getObjectId(), (Object)now.plus((TemporalAmount)chatSettings.worldChatInterval()));
            }
        }
    }
    
    public ChatType[] getChatTypeList() {
        return ChatWorld.CHAT_TYPES;
    }
    
    static {
        REUSE = (IntMap)new CHashIntMap();
        CHAT_TYPES = new ChatType[] { ChatType.WORLD };
    }
}
