// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.teleporter;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.enums.SpecialItemType;
import org.l2j.gameserver.engine.item.ItemEngine;
import java.time.LocalDateTime;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcTeleportRequest;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.instancemanager.CastleManager;
import java.util.Objects;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.StatsSet;
import java.util.ArrayList;
import java.util.List;
import org.l2j.gameserver.enums.TeleportType;
import java.time.DayOfWeek;
import java.util.EnumSet;
import org.slf4j.Logger;

public final class TeleportHolder
{
    private static final Logger LOGGER;
    private static final String NPC_STRING_ID_FORMAT = "<fstring>%d</fstring>";
    private static final String BUTTON_QUEST_BYPASS = "<button align=left icon=\"quest\" action=\"bypass -h ";
    private static final String BUTTON_TELEPORT_BYPASS = "<button align=left icon=\"teleport\" action=\"bypass -h ";
    private static final String CONFIRM_TELEPORT_MSG = "\" msg=\"811;";
    private static final String ADENA_STRING_ID = "<fstring>1000308</fstring>";
    private static final String ANCIENT_ADENA_STRING_ID = "<fstring>1000309</fstring>";
    private static final String TEMPLATE_TELEPORTER_HTM = "data/html/teleporter/teleports.htm";
    private static final String CASTLE_TELEPORTER_BUSY_HTM = "data/html/teleporter/castleteleporter-busy.htm";
    private static final EnumSet<DayOfWeek> DISCOUNT_DAYS;
    private final String name;
    private final TeleportType type;
    private final List<TeleportLocation> teleportData;
    
    public TeleportHolder(final String name, final TeleportType type) {
        this.teleportData = new ArrayList<TeleportLocation>();
        this.name = name;
        this.type = type;
    }
    
    public void registerLocation(final StatsSet locData) {
        this.teleportData.add(new TeleportLocation(this.teleportData.size(), locData));
    }
    
    public void showTeleportList(final Player player, final Npc npc) {
        this.showTeleportList(player, npc, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getObjectId()));
    }
    
    public void showTeleportList(final Player player, final Npc npc, final String bypass) {
        if (this.isNoblesse() && !player.isNoble()) {
            TeleportHolder.LOGGER.warn("Player {} requested noblesse teleport without being noble!", (Object)player.getObjectId());
            return;
        }
        final int questZoneId = this.isNormalTeleport() ? player.getQuestZoneId() : -1;
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sb_f = new StringBuilder();
        for (final TeleportLocation loc : this.teleportData) {
            String finalName = loc.getName();
            String confirmDesc = loc.getName();
            if (loc.getNpcStringId() != -1) {
                finalName = String.format("<fstring>%d</fstring>", loc.getNpcStringId());
                confirmDesc = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, loc.getNpcStringId());
            }
            if (this.shouldPayFee(player, loc)) {
                final long fee = this.calculateFee(player, loc);
                if (fee != 0L) {
                    finalName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;JLjava/lang/String;)Ljava/lang/String;, finalName, fee, this.getItemName(loc.getFeeId(), true));
                }
            }
            final boolean isQuestTeleport = questZoneId >= 0 && loc.getQuestZoneId() == questZoneId;
            final StringBuilder builder = isQuestTeleport ? sb_f.append("<button align=left icon=\"quest\" action=\"bypass -h ") : sb.append("<button align=left icon=\"teleport\" action=\"bypass -h ");
            builder.append(bypass).append(" ").append(this.name).append(" ").append(loc.getId()).append("\" msg=\"811;").append(confirmDesc).append("\">").append(finalName).append("</button>");
        }
        sb_f.append(sb.toString());
        final NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
        msg.setFile(player, "data/html/teleporter/teleports.htm");
        msg.replace("%locations%", sb_f.toString());
        player.sendPacket(msg);
    }
    
    public void doTeleport(final Player player, final Npc npc, final int locId) {
        if (this.isNoblesse() && !player.isNoble()) {
            TeleportHolder.LOGGER.warn("Player {} requested noblesse teleport without being noble!", (Object)player.getObjectId());
            return;
        }
        final TeleportLocation loc = this.getLocation(locId);
        if (Objects.isNull(loc)) {
            TeleportHolder.LOGGER.warn("Player {} requested unknown teleport location {} within list {}!", new Object[] { player.getObjectId(), locId, this.name });
            return;
        }
        for (final int castleId : loc.getCastleId()) {
            if (CastleManager.getInstance().getCastleById(castleId).getSiege().isInProgress()) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
                return;
            }
        }
        if (this.isNormalTeleport()) {
            if (npc.getCastle().getSiege().isInProgress()) {
                final NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
                msg.setFile(player, "data/html/teleporter/castleteleporter-busy.htm");
                player.sendPacket(msg);
                return;
            }
            if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && player.getReputation() < 0) {
                player.sendMessage("Go away, you're not welcome here.");
                return;
            }
        }
        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnNpcTeleportRequest(player, npc, loc), npc, TerminateReturn.class);
        if (term != null && term.terminate()) {
            return;
        }
        if (this.shouldPayFee(player, loc) && !player.destroyItemByItemId("Teleport", loc.getFeeId(), this.calculateFee(player, loc), npc, true)) {
            if (loc.getFeeId() == 57) {
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            }
            else {
                player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getItemName(loc.getFeeId(), false)));
            }
        }
        else if (!player.isAlikeDead()) {
            player.teleToLocation(loc);
        }
    }
    
    private boolean shouldPayFee(final Player player, final TeleportLocation loc) {
        return !this.isNormalTeleport() || ((player.getLevel() > Config.MAX_FREE_TELEPORT_LEVEL || player.isSubClassActive()) && loc.getFeeId() != 0 && loc.getFeeCount() > 0L);
    }
    
    private long calculateFee(final Player player, final TeleportLocation loc) {
        if (this.isNormalTeleport()) {
            if (!player.isSubClassActive() && player.getLevel() <= Config.MAX_FREE_TELEPORT_LEVEL) {
                return 0L;
            }
            final LocalDateTime now = LocalDateTime.now();
            if (now.getHour() >= 20 && TeleportHolder.DISCOUNT_DAYS.contains(now.getDayOfWeek())) {
                return loc.getFeeCount() / 2L;
            }
        }
        return loc.getFeeCount();
    }
    
    private boolean isNormalTeleport() {
        return this.type == TeleportType.NORMAL || this.type == TeleportType.HUNTING;
    }
    
    private String getItemName(final int itemId, final boolean fstring) {
        if (fstring) {
            if (itemId == 57) {
                return "<fstring>1000308</fstring>";
            }
            if (itemId == 5575) {
                return "<fstring>1000309</fstring>";
            }
        }
        final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
        if (item != null) {
            return item.getName();
        }
        final SpecialItemType specialItem = SpecialItemType.getByClientId(itemId);
        if (specialItem != null) {
            return specialItem.getDescription();
        }
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, itemId);
    }
    
    public TeleportLocation getLocation(final int locationId) {
        return this.teleportData.get(locationId);
    }
    
    public List<TeleportLocation> getLocations() {
        return this.teleportData;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isNoblesse() {
        return this.type == TeleportType.NOBLES_ADENA || this.type == TeleportType.NOBLES_TOKEN;
    }
    
    public TeleportType getType() {
        return this.type;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)TeleportHolder.class);
        DISCOUNT_DAYS = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY);
    }
}
