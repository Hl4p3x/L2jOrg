// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.ClanHallAuctioneer;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Optional;
import org.l2j.gameserver.model.clanhallauction.Bidder;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.html.PageResult;
import java.util.List;
import org.l2j.gameserver.model.html.IHtmlStyle;
import org.l2j.gameserver.model.html.styles.ButtonsStyle;
import org.l2j.gameserver.model.html.IBypassFormatter;
import org.l2j.gameserver.model.html.formatters.BypassParserFormatter;
import org.l2j.gameserver.model.html.IPageHandler;
import org.l2j.gameserver.model.html.pagehandlers.NextPrevPageHandler;
import java.util.Collection;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.clanhallauction.ClanHallAuction;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.util.BypassParser;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import java.time.temporal.TemporalAccessor;
import java.time.ZoneId;
import java.time.Instant;
import java.time.format.DateTimeFormatterBuilder;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.instancemanager.ClanHallAuctionManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class ClanHallAuctioneer extends AbstractNpcAI
{
    private static final int AUCTIONEER = 30767;
    
    public ClanHallAuctioneer() {
        this.addStartNpc(30767);
        this.addTalkId(30767);
        this.addFirstTalkId(30767);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "ClanHallAuctioneer.html": {
                htmltext = event;
                break;
            }
            case "map": {
                htmltext = this.getHtml(player, "ClanHallAuctioneer-map.html");
                htmltext = htmltext.replace("%MAP%", npc.getParameters().getString("fnAgitMap", "gludio"));
                htmltext = htmltext.replace("%TOWN_NAME%", npc.getCastle().getName());
                break;
            }
            case "cancelBid": {
                final Clan clan = player.getClan();
                if (clan == null) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_PARTICIPATE_IN_AN_AUCTION);
                    return htmltext;
                }
                if (!player.isClanLeader() || clan.getLevel() < 2) {
                    player.sendPacket(SystemMessageId.TO_PARTICIPATE_IN_THE_32_CLAN_HALL_AUCTION_THE_CLAN_LEVEL_MUST_BE_2_OR_HIGHER_AND_THE_CHARACTER_MUST_BE_A_CLAN_LEADER_OR_HAVE_THE_RIGHT_TO_BID_AND_SELL);
                    return htmltext;
                }
                final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionByClan(clan);
                if (clanHallAuction == null) {
                    player.sendPacket(SystemMessageId.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
                    return htmltext;
                }
                htmltext = this.getHtml(player, "ClanHallAuctioneer-cancelBid.html");
                htmltext = htmltext.replaceAll("%myBid%", String.valueOf(clanHallAuction.getClanBid(clan)));
                htmltext = htmltext.replaceAll("%myBidRemain%", String.valueOf(clanHallAuction.getClanBid(clan) * 9L));
                break;
            }
            case "cancel": {
                final Clan clan = player.getClan();
                if (clan == null) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_PARTICIPATE_IN_AN_AUCTION);
                    return htmltext;
                }
                if (!player.isClanLeader() || clan.getLevel() < 2) {
                    player.sendPacket(SystemMessageId.TO_PARTICIPATE_IN_THE_32_CLAN_HALL_AUCTION_THE_CLAN_LEVEL_MUST_BE_2_OR_HIGHER_AND_THE_CHARACTER_MUST_BE_A_CLAN_LEADER_OR_HAVE_THE_RIGHT_TO_BID_AND_SELL);
                    return htmltext;
                }
                final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionByClan(clan);
                if (clanHallAuction == null) {
                    player.sendPacket(SystemMessageId.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
                    return htmltext;
                }
                clanHallAuction.removeBid(clan);
                player.sendPacket(SystemMessageId.YOU_HAVE_CANCELED_YOUR_BID);
                break;
            }
            case "rebid": {
                if (player.hasClanPrivilege(ClanPrivilege.CH_AUCTION)) {
                    final Clan clan = player.getClan();
                    final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionByClan(clan);
                    if (clanHallAuction != null) {
                        final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
                        htmltext = this.getHtml(player, "ClanHallAuctioneer-bid2.html");
                        htmltext = htmltext.replaceAll("%id%", String.valueOf(clanHallAuction.getClanHallId()));
                        htmltext = htmltext.replaceAll("%minBid%", String.valueOf(clanHallAuction.getHighestBid()));
                        htmltext = htmltext.replaceAll("%myBid%", String.valueOf(clanHallAuction.getClanBid(clan)));
                        htmltext = htmltext.replace("%auctionEnd%", builder.appendPattern("dd/MM/yyyy HH").appendLiteral(" hour ").appendPattern("mm").appendLiteral(" minutes").toFormatter().format(Instant.ofEpochMilli(System.currentTimeMillis() + clanHallAuction.getRemaingTime()).atZone(ZoneId.systemDefault())));
                    }
                    break;
                }
                player.sendPacket(SystemMessageId.YOU_MUST_HAVE_RIGHTS_TO_A_CLAN_HALL_AUCTION_IN_ORDER_TO_MAKE_A_BID_FOR_PROVISIONAL_CLAN_HALL);
                break;
            }
            case "my_auction": {
                final Clan clan = player.getClan();
                if (clan == null) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_PARTICIPATE_IN_AN_AUCTION);
                    return htmltext;
                }
                if (!player.isClanLeader() || clan.getLevel() < 2) {
                    player.sendPacket(SystemMessageId.TO_PARTICIPATE_IN_THE_32_CLAN_HALL_AUCTION_THE_CLAN_LEVEL_MUST_BE_2_OR_HIGHER_AND_THE_CHARACTER_MUST_BE_A_CLAN_LEADER_OR_HAVE_THE_RIGHT_TO_BID_AND_SELL);
                    return htmltext;
                }
                final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionByClan(clan);
                if (clanHallAuction == null) {
                    player.sendPacket(SystemMessageId.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
                    return htmltext;
                }
                final ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(clanHallAuction.getClanHallId());
                final Clan owner = clanHall.getOwner();
                final long remainingTime = clanHallAuction.getRemaingTime();
                final Instant endTime = Instant.ofEpochMilli(System.currentTimeMillis() + remainingTime);
                final DateTimeFormatterBuilder builder2 = new DateTimeFormatterBuilder();
                htmltext = this.getHtml(player, "ClanHallAuctioneer-bidInfo.html");
                htmltext = htmltext.replaceAll("%id%", String.valueOf(clanHall.getId()));
                htmltext = htmltext.replace("%owner%", (owner != null) ? owner.getName() : "");
                htmltext = htmltext.replace("%clanLeader%", (owner != null) ? owner.getLeaderName() : "");
                htmltext = htmltext.replace("%rent%", String.valueOf(clanHall.getLease()));
                htmltext = htmltext.replace("%grade%", String.valueOf(clanHall.getGrade().getGradeValue()));
                htmltext = htmltext.replace("%minBid%", String.valueOf(clanHallAuction.getHighestBid()));
                htmltext = htmltext.replace("%myBid%", String.valueOf(clanHallAuction.getClanBid(clan)));
                htmltext = htmltext.replace("%bidNumber%", String.valueOf(clanHallAuction.getBidCount()));
                htmltext = htmltext.replace("%auctionEnd%", builder2.appendPattern("dd/MM/yyyy HH").appendLiteral(" hour ").appendPattern("mm").appendLiteral(" minutes").toFormatter().format(endTime.atZone(ZoneId.systemDefault())));
                htmltext = htmltext.replace("%hours%", String.valueOf(TimeUnit.MILLISECONDS.toHours(remainingTime)));
                htmltext = htmltext.replace("%minutes%", String.valueOf(TimeUnit.MILLISECONDS.toMinutes(remainingTime % 3600000L)));
                break;
            }
            default: {
                if (event.startsWith("auctionList")) {
                    this.processClanHallBypass(player, npc, new BypassParser(event));
                    return htmltext;
                }
                if (event.startsWith("bid")) {
                    this.processBidBypass(player, npc, new BypassParser(event));
                    return htmltext;
                }
                if (event.startsWith("listBidder")) {
                    this.processBiddersBypass(player, npc, new BypassParser(event));
                    return htmltext;
                }
                break;
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return "ClanHallAuctioneer.html";
    }
    
    private void processClanHallBypass(final Player player, final Npc npc, final BypassParser parser) {
        final int page = parser.getInt("page", 0);
        final int clanHallId = parser.getInt("id", 0);
        if (clanHallId > 0) {
            final ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(clanHallId);
            if (clanHall != null) {
                final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionById(clanHallId);
                final Clan owner = clanHall.getOwner();
                final long remainingTime = clanHallAuction.getRemaingTime();
                final Instant endTime = Instant.ofEpochMilli(System.currentTimeMillis() + remainingTime);
                final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
                final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
                html.setHtml(this.getHtml(player, "ClanHallAuctioneer-info.html"));
                html.replace("%id%", clanHall.getId());
                html.replace("%owner%", (owner != null) ? owner.getName() : "");
                html.replace("%clanLeader%", (owner != null) ? owner.getLeaderName() : "");
                html.replace("%rent%", clanHall.getLease());
                html.replace("%grade%", clanHall.getGrade().getGradeValue());
                html.replace("%minBid%", clanHallAuction.getHighestBid());
                html.replace("%bidNumber%", clanHallAuction.getBidCount());
                html.replace("%auctionEnd%", builder.appendPattern("dd/MM/yyyy HH").appendLiteral(" hour ").appendPattern("mm").appendLiteral(" minutes").toFormatter().format(endTime.atZone(ZoneId.systemDefault())));
                html.replace("%hours%", TimeUnit.MILLISECONDS.toHours(remainingTime));
                html.replace("%minutes%", TimeUnit.MILLISECONDS.toMinutes(remainingTime % 3600000L));
                player.sendPacket(new ServerPacket[] { (ServerPacket)html });
            }
        }
        else {
            final List<ClanHall> clanHalls = (List<ClanHall>)ClanHallManager.getInstance().getFreeAuctionableHall();
            if (clanHalls.isEmpty()) {
                player.sendPacket(SystemMessageId.THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION);
            }
            else {
                final NpcHtmlMessage html2 = new NpcHtmlMessage(npc.getObjectId(), this.getHtml(player, "ClanHallAuctioneer-list.html"));
                final PageResult result = PageBuilder.newBuilder((Collection)clanHalls, 8, "bypass -h Quest ClanHallAuctioneer auctionList").currentPage(page).pageHandler((IPageHandler)NextPrevPageHandler.INSTANCE).formatter((IBypassFormatter)BypassParserFormatter.INSTANCE).style((IHtmlStyle)ButtonsStyle.INSTANCE).bodyHandler((pages, clanHall, sb) -> {
                    final ClanHallAuction auction = ClanHallAuctionManager.getInstance().getClanHallAuctionById(clanHall.getId());
                    if (auction == null) {
                        System.out.println(clanHall.getId());
                        return;
                    }
                    sb.append("<tr><td width=50><font color=\"aaaaff\">&^");
                    sb.append(clanHall.getId());
                    sb.append(";</font></td><td width=100><a action=\"bypass -h Quest ClanHallAuctioneer auctionList id=");
                    sb.append(clanHall.getId());
                    sb.append("\"><font color=\"ffffaa\">&%");
                    sb.append(clanHall.getId());
                    sb.append(";[0]</font></a></td><td width=50>");
                    sb.append(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(Instant.ofEpochMilli(System.currentTimeMillis() + auction.getRemaingTime()).atZone(ZoneId.systemDefault())));
                    sb.append("</td><td width=70 align=right><font color=\"aaffff\">");
                    sb.append(auction.getHighestBid());
                    sb.append("</font></td></tr>");
                }).build();
                html2.replace("%pages%", (CharSequence)((result.getPages() > 0) ? result.getPagerTemplate() : ""));
                html2.replace("%agitList%", result.getBodyTemplate().toString());
                player.sendPacket(new ServerPacket[] { (ServerPacket)html2 });
            }
        }
    }
    
    private void processBidBypass(final Player player, final Npc npc, final BypassParser parser) {
        final int clanHallId = parser.getInt("id", 0);
        final long bid = parser.getLong("bid", 0L);
        if (clanHallId > 0) {
            final ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(clanHallId);
            if (clanHall == null) {
                return;
            }
            final Clan clan = player.getClan();
            if (clan == null) {
                player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_PARTICIPATE_IN_AN_AUCTION);
                return;
            }
            if (!player.isClanLeader() || clan.getLevel() < 2) {
                player.sendPacket(SystemMessageId.TO_PARTICIPATE_IN_THE_32_CLAN_HALL_AUCTION_THE_CLAN_LEVEL_MUST_BE_2_OR_HIGHER_AND_THE_CHARACTER_MUST_BE_A_CLAN_LEADER_OR_HAVE_THE_RIGHT_TO_BID_AND_SELL);
                return;
            }
            final ClanHall playerClanHall = ClanHallManager.getInstance().getClanHallByClan(clan);
            if (playerClanHall != null) {
                player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_PARTICIPATE_IN_AN_AUCTION);
                return;
            }
            if (ClanHallAuctionManager.getInstance().checkForClanBid(clanHallId, clan)) {
                player.sendPacket(SystemMessageId.SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME);
                return;
            }
            if (bid == 0L) {
                final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
                html.setHtml(this.getHtml(player, "ClanHallAuctioneer-bid1.html"));
                html.replace("%clanAdena%", clan.getWarehouse().getAdena());
                html.replace("%minBid%", ClanHallAuctionManager.getInstance().getClanHallAuctionById(clanHallId).getHighestBid());
                html.replace("%id%", clanHall.getId());
                player.sendPacket(new ServerPacket[] { (ServerPacket)html });
            }
            else {
                player.sendPacket(SystemMessageId.YOU_HAVE_REGISTERED_FOR_A_CLAN_HALL_AUCTION);
                if (bid > Inventory.MAX_ADENA) {
                    player.sendPacket(SystemMessageId.THE_HIGHEST_BID_IS_OVER_999_9_BILLION_THEREFORE_YOU_CANNOT_PLACE_A_BID);
                    return;
                }
                final ClanHallAuction auction = ClanHallAuctionManager.getInstance().getClanHallAuctionById(clanHallId);
                if (bid < auction.getHighestBid()) {
                    player.sendPacket(SystemMessageId.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID);
                    return;
                }
                if (clan.getWarehouse().destroyItemByItemId("Clan Hall Auction", 57, bid, player, (Object)null) == null) {
                    player.sendPacket(SystemMessageId.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
                    return;
                }
                final Optional<Bidder> bidder = (Optional<Bidder>)auction.getHighestBidder();
                if (bidder.isPresent()) {
                    auction.returnAdenas((Bidder)bidder.get());
                    final Player leader = bidder.get().getClan().getLeader().getPlayerInstance();
                    if (leader != null && leader.isOnline()) {
                        leader.sendPacket(SystemMessageId.YOU_WERE_OUTBID_THE_NEW_HIGHEST_BID_IS_S1_ADENA);
                    }
                }
                auction.addBid(player.getClan(), bid);
                player.sendPacket(SystemMessageId.YOUR_BID_HAS_BEEN_SUCCESSFULLY_PLACED);
            }
        }
    }
    
    private void processBiddersBypass(final Player player, final Npc npc, final BypassParser parser) {
        final int page = parser.getInt("page", 0);
        final int clanHallId = parser.getInt("id", 0);
        if (clanHallId > 0) {
            final ClanHallAuction clanHallAuction = ClanHallAuctionManager.getInstance().getClanHallAuctionById(clanHallId);
            if (clanHallAuction == null) {
                return;
            }
            final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId(), this.getHtml(player, "ClanHallAuctioneer-bidderList.html"));
            final PageResult result = PageBuilder.newBuilder((Collection)clanHallAuction.getBids().values().stream().sorted(Comparator.comparingLong(Bidder::getTime).reversed()).collect((Collector<? super Object, ?, List<Object>>)Collectors.toList()), 10, "bypass -h Quest ClanHallAuctioneer auctionList").currentPage(page).pageHandler((IPageHandler)NextPrevPageHandler.INSTANCE).formatter((IBypassFormatter)BypassParserFormatter.INSTANCE).style((IHtmlStyle)ButtonsStyle.INSTANCE).bodyHandler((pages, bidder, sb) -> {
                sb.append("<tr><td width=100>");
                sb.append(bidder.getClanName());
                sb.append("</td><td width=100>");
                sb.append(bidder.getBid());
                sb.append("</td><td width=70>");
                sb.append(bidder.getFormattedTime());
                sb.append("</td></tr>");
            }).build();
            html.replace("%pages%", (CharSequence)((result.getPages() > 0) ? result.getPagerTemplate() : ""));
            html.replace("%bidderList%", result.getBodyTemplate().toString());
            html.replace("%id%", clanHallAuction.getClanHallId());
            player.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
    }
    
    public static AbstractNpcAI provider() {
        return new ClanHallAuctioneer();
    }
}
