// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.slf4j.LoggerFactory;
import java.time.ZonedDateTime;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SiegeInfo;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.time.LocalDateTime;
import org.l2j.commons.util.Util;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.FeatureSettings;
import java.time.ZoneId;
import java.time.Instant;
import java.util.Objects;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public class RequestSetCastleSiegeTime extends ClientPacket
{
    private static final Logger LOGGER;
    private int castleId;
    private long time;
    
    public void readImpl() {
        this.castleId = this.readInt();
        this.time = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        final Castle castle = CastleManager.getInstance().getCastleById(this.castleId);
        if (Objects.isNull(castle)) {
            RequestSetCastleSiegeTime.LOGGER.warn("player {} requested Siege time to invalid castle {}", (Object)player, (Object)this.castleId);
            return;
        }
        if (castle.getOwnerId() == 0 || castle.getOwnerId() != player.getClanId()) {
            RequestSetCastleSiegeTime.LOGGER.warn("player {} is trying to change siege date of not his own castle {}!", (Object)player, (Object)castle);
        }
        else if (!player.isClanLeader()) {
            RequestSetCastleSiegeTime.LOGGER.warn("player {} is trying to change siege date of castle {} but is not clan leader!", (Object)player, (Object)castle);
        }
        else if (castle.isSiegeTimeRegistrationSeason()) {
            final ZonedDateTime requestedTime = Instant.ofEpochSecond(this.time).atZone(ZoneId.systemDefault());
            final int requestedHour = requestedTime.getHour();
            if (Util.contains(((FeatureSettings)Configurator.getSettings((Class)FeatureSettings.class)).siegeHours(), requestedHour)) {
                CastleManager.getInstance().registerSiegeDate(castle, castle.getSiegeDate().withHour(requestedHour));
                castle.setSiegeTimeRegistrationEnd(LocalDateTime.now());
                Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME)).addCastleId(this.castleId));
                player.sendPacket(new SiegeInfo(castle, player));
            }
            else {
                RequestSetCastleSiegeTime.LOGGER.warn("player {} is trying to an invalid time {}  !", (Object)player, (Object)requestedTime);
            }
        }
        else {
            RequestSetCastleSiegeTime.LOGGER.warn("player {}  is trying to change siege date of castle {} but currently not possible!", (Object)player, (Object)castle);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestSetCastleSiegeTime.class);
    }
}
