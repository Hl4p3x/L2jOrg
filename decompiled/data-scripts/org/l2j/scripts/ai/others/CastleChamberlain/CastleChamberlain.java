// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.CastleChamberlain;

import org.l2j.gameserver.model.events.annotations.Id;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.network.serverpackets.ExShowCropSetting;
import org.l2j.gameserver.network.serverpackets.ExShowSeedSetting;
import org.l2j.gameserver.network.serverpackets.ExShowManorDefaultInfo;
import org.l2j.gameserver.network.serverpackets.ExShowCropInfo;
import org.l2j.gameserver.network.serverpackets.ExShowSeedInfo;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcManorBypass;
import org.l2j.gameserver.model.teleporter.TeleportHolder;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.util.Util;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.data.xml.impl.TeleportersData;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.Config;
import java.util.Calendar;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class CastleChamberlain extends AbstractNpcAI
{
    private static final int[] NPC;
    private static final int CROWN = 6841;
    private static final int LORD_CLOAK_OF_LIGHT = 34925;
    private static final int LORD_CLOAK_OF_DARK = 34926;
    private static final SkillHolder[] BUFFS;
    
    private CastleChamberlain() {
        this.addStartNpc(CastleChamberlain.NPC);
        this.addTalkId(CastleChamberlain.NPC);
        this.addFirstTalkId(CastleChamberlain.NPC);
    }
    
    private NpcHtmlMessage getHtmlPacket(final Player player, final Npc npc, final String htmlFile) {
        final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
        packet.setHtml(this.getHtml(player, htmlFile));
        return packet;
    }
    
    private final String funcConfirmHtml(final Player player, final Npc npc, final Castle castle, final int func, final int level) {
        if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
            final String fstring = (func == 1) ? "9" : "10";
            NpcHtmlMessage html;
            if (level == 0) {
                html = this.getHtmlPacket(player, npc, "castleresetdeco.html");
                html.replace("%AgitDecoSubmit%", Integer.toString(func));
            }
            else if (castle.getCastleFunction(func) != null && castle.getCastleFunction(func).getLevel() == level) {
                html = this.getHtmlPacket(player, npc, "castledecoalreadyset.html");
                html.replace("%AgitDecoEffect%", invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, level, fstring));
            }
            else {
                html = this.getHtmlPacket(player, npc, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, func));
                html.replace("%AgitDecoCost%", invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, this.getFunctionFee(func, level), this.getFunctionRatio(func) / 86400000L));
                html.replace("%AgitDecoEffect%", invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, level, fstring));
                html.replace("%AgitDecoSubmit%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, func, level));
            }
            player.sendPacket(new ServerPacket[] { (ServerPacket)html });
            return null;
        }
        return "chamberlain-21.html";
    }
    
    private final void funcReplace(final Castle castle, final NpcHtmlMessage html, final int func, final String str) {
        final Castle.CastleFunction function = castle.getCastleFunction(func);
        if (function == null) {
            html.replace(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, str), "<fstring>4</fstring>");
            html.replace(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, str), "");
            html.replace(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, str), "<fstring>4</fstring>");
            html.replace(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, str), "");
        }
        else {
            final String fstring = (func == 5 || func == 1) ? "9" : "10";
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(function.getEndTime());
            html.replace(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, str), invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, function.getLevel(), fstring));
            html.replace(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, str), invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, function.getLease(), function.getRate() / 86400000L));
            html.replace(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, str), invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, calendar.get(5), calendar.get(2) + 1, calendar.get(1)));
            html.replace(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, str), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, str));
        }
    }
    
    private final int getFunctionFee(final int func, final int level) {
        int fee = 0;
        switch (func) {
            case 4: {
                fee = ((level == 45) ? Config.CS_EXPREG1_FEE : Config.CS_EXPREG2_FEE);
                break;
            }
            case 2: {
                fee = ((level == 300) ? Config.CS_HPREG1_FEE : Config.CS_HPREG2_FEE);
                break;
            }
            case 3: {
                fee = ((level == 40) ? Config.CS_MPREG1_FEE : Config.CS_MPREG2_FEE);
                break;
            }
            case 5: {
                fee = ((level == 5) ? Config.CS_SUPPORT1_FEE : Config.CS_SUPPORT2_FEE);
                break;
            }
            case 1: {
                fee = ((level == 1) ? Config.CS_TELE1_FEE : Config.CS_TELE2_FEE);
                break;
            }
        }
        return fee;
    }
    
    private final long getFunctionRatio(final int func) {
        long ratio = 0L;
        switch (func) {
            case 4: {
                ratio = Config.CS_EXPREG_FEE_RATIO;
                break;
            }
            case 2: {
                ratio = Config.CS_HPREG_FEE_RATIO;
                break;
            }
            case 3: {
                ratio = Config.CS_MPREG_FEE_RATIO;
                break;
            }
            case 5: {
                ratio = Config.CS_SUPPORT_FEE_RATIO;
                break;
            }
            case 1: {
                ratio = Config.CS_TELE_FEE_RATIO;
                break;
            }
        }
        return ratio;
    }
    
    private final int getDoorUpgradePrice(final int type, final int level) {
        int price = 0;
        Label_0182: {
            switch (type) {
                case 1: {
                    switch (level) {
                        case 2: {
                            price = Config.OUTER_DOOR_UPGRADE_PRICE2;
                            break;
                        }
                        case 3: {
                            price = Config.OUTER_DOOR_UPGRADE_PRICE3;
                            break;
                        }
                        case 5: {
                            price = Config.OUTER_DOOR_UPGRADE_PRICE5;
                            break;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (level) {
                        case 2: {
                            price = Config.INNER_DOOR_UPGRADE_PRICE2;
                            break;
                        }
                        case 3: {
                            price = Config.INNER_DOOR_UPGRADE_PRICE3;
                            break;
                        }
                        case 5: {
                            price = Config.INNER_DOOR_UPGRADE_PRICE5;
                            break;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (level) {
                        case 2: {
                            price = Config.WALL_UPGRADE_PRICE2;
                            break Label_0182;
                        }
                        case 3: {
                            price = Config.WALL_UPGRADE_PRICE3;
                            break Label_0182;
                        }
                        case 5: {
                            price = Config.WALL_UPGRADE_PRICE5;
                            break Label_0182;
                        }
                    }
                    break;
                }
            }
        }
        return price;
    }
    
    private final int getTrapUpgradePrice(final int level) {
        int price = 0;
        switch (level) {
            case 1: {
                price = Config.TRAP_UPGRADE_PRICE1;
                break;
            }
            case 2: {
                price = Config.TRAP_UPGRADE_PRICE2;
                break;
            }
            case 3: {
                price = Config.TRAP_UPGRADE_PRICE3;
                break;
            }
            case 4: {
                price = Config.TRAP_UPGRADE_PRICE4;
                break;
            }
        }
        return price;
    }
    
    private final boolean isOwner(final Player player, final Npc npc) {
        return player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || (player.getClan() != null && player.getClanId() == npc.getCastle().getOwnerId());
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final Castle castle = npc.getCastle();
        final StringTokenizer st = new StringTokenizer(event, " ");
        String htmltext = null;
        final boolean isMyLord = player.isClanLeader() && player.getClan().getCastleId() == ((npc.getCastle() != null) ? npc.getCastle().getId() : -1);
        final String nextToken = st.nextToken();
        switch (nextToken) {
            case "chamberlain-01.html":
            case "manor-help-01.html":
            case "manor-help-02.html":
            case "manor-help-03.html":
            case "manor-help-04.html": {
                htmltext = event;
                break;
            }
            case "siege_functions": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getSiege().isInProgress()) {
                    htmltext = "chamberlain-08.html";
                    break;
                }
                htmltext = "chamberlain-12.html";
                break;
            }
            case "manage_doors": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (st.hasMoreTokens()) {
                    final StringBuilder sb = new StringBuilder();
                    final NpcHtmlMessage html = this.getHtmlPacket(player, npc, "chamberlain-13.html");
                    html.replace("%type%", st.nextToken());
                    while (st.hasMoreTokens()) {
                        sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, st.nextToken()));
                    }
                    html.replace("%doors%", sb.toString());
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html });
                    break;
                }
                htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, npc.getCastle().getName());
                break;
            }
            case "upgrade_doors": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
                    final int type = Integer.parseInt(st.nextToken());
                    final int level = Integer.parseInt(st.nextToken());
                    final NpcHtmlMessage html2 = this.getHtmlPacket(player, npc, "chamberlain-14.html");
                    html2.replace("%gate_price%", Integer.toString(this.getDoorUpgradePrice(type, level)));
                    html2.replace("%event%", event.substring("upgrade_doors".length() + 1));
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html2 });
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "upgrade_doors_confirm": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getSiege().isInProgress()) {
                    htmltext = "chamberlain-08.html";
                    break;
                }
                final int type = Integer.parseInt(st.nextToken());
                final int level = Integer.parseInt(st.nextToken());
                final int price = this.getDoorUpgradePrice(type, level);
                final int[] doors = new int[2];
                for (int i = 0; i <= st.countTokens(); ++i) {
                    doors[i] = Integer.parseInt(st.nextToken());
                }
                final Door door = castle.getDoor(doors[0]);
                if (door != null) {
                    final int currentLevel = door.getStats().getUpgradeHpRatio();
                    if (currentLevel >= level) {
                        final NpcHtmlMessage html3 = this.getHtmlPacket(player, npc, "chamberlain-15.html");
                        html3.replace("%doorlevel%", Integer.toString(currentLevel));
                        player.sendPacket(new ServerPacket[] { (ServerPacket)html3 });
                    }
                    else if (player.getAdena() >= price) {
                        takeItems(player, 57, (long)price);
                        for (final int doorId : doors) {
                            castle.setDoorUpgrade(doorId, level, true);
                        }
                        htmltext = "chamberlain-16.html";
                    }
                    else {
                        htmltext = "chamberlain-09.html";
                    }
                }
                break;
            }
            case "manage_trap": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (st.hasMoreTokens()) {
                    NpcHtmlMessage html4;
                    if (castle.getName().equalsIgnoreCase("aden")) {
                        html4 = this.getHtmlPacket(player, npc, "chamberlain-17a.html");
                    }
                    else {
                        html4 = this.getHtmlPacket(player, npc, "chamberlain-17.html");
                    }
                    html4.replace("%trapIndex%", st.nextToken());
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html4 });
                    break;
                }
                htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, npc.getCastle().getName());
                break;
            }
            case "upgrade_trap": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
                    final String trapIndex = st.nextToken();
                    final int level = Integer.parseInt(st.nextToken());
                    final NpcHtmlMessage html2 = this.getHtmlPacket(player, npc, "chamberlain-18.html");
                    html2.replace("%trapIndex%", trapIndex);
                    html2.replace("%level%", Integer.toString(level));
                    html2.replace("%dmgzone_price%", Integer.toString(this.getTrapUpgradePrice(level)));
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html2 });
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "upgrade_trap_confirm": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getSiege().isInProgress()) {
                    htmltext = "chamberlain-08.html";
                    break;
                }
                final int trapIndex2 = Integer.parseInt(st.nextToken());
                final int level = Integer.parseInt(st.nextToken());
                final int price = this.getTrapUpgradePrice(level);
                final int currentLevel2 = castle.getTrapUpgradeLevel(trapIndex2);
                if (currentLevel2 >= level) {
                    final NpcHtmlMessage html5 = this.getHtmlPacket(player, npc, "chamberlain-19.html");
                    html5.replace("%dmglevel%", Integer.toString(currentLevel2));
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html5 });
                }
                else if (player.getAdena() >= price) {
                    takeItems(player, 57, (long)price);
                    castle.setTrapUpgrade(trapIndex2, level, true);
                    htmltext = "chamberlain-20.html";
                }
                else {
                    htmltext = "chamberlain-09.html";
                }
                break;
            }
            case "receive_report": {
                if (!isMyLord) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getSiege().isInProgress()) {
                    htmltext = "chamberlain-07.html";
                    break;
                }
                final Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
                final NpcHtmlMessage html = this.getHtmlPacket(player, npc, "chamberlain-02.html");
                html.replace("%clanleadername%", clan.getLeaderName());
                html.replace("%clanname%", clan.getName());
                html.replace("%castlename%", String.valueOf(1001000 + castle.getId()));
                player.sendPacket(new ServerPacket[] { (ServerPacket)html });
                break;
            }
            case "manage_vault": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES)) {
                    final NpcHtmlMessage html4 = this.getHtmlPacket(player, npc, "castlemanagevault.html");
                    html4.replace("%tax_income%", GameUtils.formatAdena(castle.getTreasury()));
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html4 });
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "manage_vault_deposit": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES)) {
                    final NpcHtmlMessage html4 = this.getHtmlPacket(player, npc, "castlemanagevault_deposit.html");
                    html4.replace("%tax_income%", GameUtils.formatAdena(castle.getTreasury()));
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html4 });
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "manage_vault_withdraw": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES)) {
                    final NpcHtmlMessage html4 = this.getHtmlPacket(player, npc, "castlemanagevault_withdraw.html");
                    html4.replace("%tax_income%", GameUtils.formatAdena(castle.getTreasury()));
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html4 });
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "deposit": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES)) {
                    final long amount = st.hasMoreTokens() ? Long.parseLong(st.nextToken()) : 0L;
                    if (amount > 0L && amount < Inventory.MAX_ADENA) {
                        if (player.getAdena() >= amount) {
                            takeItems(player, 57, amount);
                            castle.addToTreasuryNoTax(amount);
                        }
                        else {
                            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
                        }
                    }
                    htmltext = "chamberlain-01.html";
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "withdraw": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_TAXES)) {
                    final long amount = st.hasMoreTokens() ? Long.parseLong(st.nextToken()) : 0L;
                    if (amount <= castle.getTreasury()) {
                        castle.addToTreasuryNoTax(-1L * amount);
                        this.giveAdena(player, amount, false);
                        htmltext = "chamberlain-01.html";
                    }
                    else {
                        final NpcHtmlMessage html2 = this.getHtmlPacket(player, npc, "castlenotenoughbalance.html");
                        html2.replace("%tax_income%", GameUtils.formatAdena(castle.getTreasury()));
                        html2.replace("%withdraw_amount%", GameUtils.formatAdena(amount));
                        player.sendPacket(new ServerPacket[] { (ServerPacket)html2 });
                    }
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "manage_functions": {
                if (!this.isOwner(player, npc)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getSiege().isInProgress()) {
                    htmltext = "chamberlain-08.html";
                    break;
                }
                htmltext = "chamberlain-23.html";
                break;
            }
            case "banish_foreigner_show": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_DISMISS)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getSiege().isInProgress()) {
                    htmltext = "chamberlain-08.html";
                    break;
                }
                htmltext = "chamberlain-10.html";
                break;
            }
            case "banish_foreigner": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_DISMISS)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getSiege().isInProgress()) {
                    htmltext = "chamberlain-08.html";
                    break;
                }
                castle.banishForeigners();
                htmltext = "chamberlain-11.html";
                break;
            }
            case "doors": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_OPEN_DOOR)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getSiege().isInProgress()) {
                    htmltext = "chamberlain-08.html";
                    break;
                }
                htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, npc.getCastle().getName());
                break;
            }
            case "operate_door": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_OPEN_DOOR)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getSiege().isInProgress()) {
                    htmltext = "chamberlain-08.html";
                    break;
                }
                final boolean open = Integer.parseInt(st.nextToken()) == 1;
                while (st.hasMoreTokens()) {
                    castle.openCloseDoor(player, Integer.parseInt(st.nextToken()), open);
                }
                htmltext = (open ? "chamberlain-05.html" : "chamberlain-06.html");
                break;
            }
            case "additional_functions": {
                htmltext = ((this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) ? "castletdecomanage.html" : "chamberlain-21.html");
                break;
            }
            case "recovery": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
                    final NpcHtmlMessage html4 = this.getHtmlPacket(player, npc, "castledeco-AR01.html");
                    this.funcReplace(castle, html4, 2, "HP");
                    this.funcReplace(castle, html4, 3, "MP");
                    this.funcReplace(castle, html4, 4, "XP");
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html4 });
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "other": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
                    final NpcHtmlMessage html4 = this.getHtmlPacket(player, npc, "castledeco-AE01.html");
                    this.funcReplace(castle, html4, 1, "TP");
                    this.funcReplace(castle, html4, 5, "BF");
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html4 });
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "HP": {
                final int level2 = Integer.parseInt(st.nextToken());
                htmltext = this.funcConfirmHtml(player, npc, castle, 2, level2);
                break;
            }
            case "MP": {
                final int level2 = Integer.parseInt(st.nextToken());
                htmltext = this.funcConfirmHtml(player, npc, castle, 3, level2);
                break;
            }
            case "XP": {
                final int level2 = Integer.parseInt(st.nextToken());
                htmltext = this.funcConfirmHtml(player, npc, castle, 4, level2);
                break;
            }
            case "TP": {
                final int level2 = Integer.parseInt(st.nextToken());
                htmltext = this.funcConfirmHtml(player, npc, castle, 1, level2);
                break;
            }
            case "BF": {
                final int level2 = Integer.parseInt(st.nextToken());
                htmltext = this.funcConfirmHtml(player, npc, castle, 5, level2);
                break;
            }
            case "set_func": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_SET_FUNCTIONS)) {
                    final int func = Integer.parseInt(st.nextToken());
                    final int level = Integer.parseInt(st.nextToken());
                    if (level == 0) {
                        castle.updateFunctions(player, func, level, 0, 0L, false);
                    }
                    else if (!castle.updateFunctions(player, func, level, this.getFunctionFee(func, level), this.getFunctionRatio(func), castle.getCastleFunction(func) == null)) {
                        htmltext = "chamberlain-09.html";
                    }
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "functions": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
                    final Castle.CastleFunction HP = castle.getCastleFunction(2);
                    final Castle.CastleFunction MP = castle.getCastleFunction(3);
                    final Castle.CastleFunction XP = castle.getCastleFunction(4);
                    final NpcHtmlMessage html6 = this.getHtmlPacket(player, npc, "castledecofunction.html");
                    html6.replace("%HPDepth%", (HP == null) ? "0" : Integer.toString(HP.getLevel()));
                    html6.replace("%MPDepth%", (MP == null) ? "0" : Integer.toString(MP.getLevel()));
                    html6.replace("%XPDepth%", (XP == null) ? "0" : Integer.toString(XP.getLevel()));
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html6 });
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "teleport": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getCastleFunction(1) == null) {
                    htmltext = "castlefuncdisabled.html";
                    break;
                }
                final String listName = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, castle.getCastleFunction(1).getLevel());
                final TeleportHolder holder = TeleportersData.getInstance().getHolder(npc.getId(), listName);
                if (holder != null) {
                    holder.showTeleportList(player, npc, "Quest CastleChamberlain goto");
                }
                break;
            }
            case "goto": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS) || st.countTokens() < 2) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                final Castle.CastleFunction func2 = castle.getCastleFunction(1);
                if (func2 == null) {
                    return "castlefuncdisabled.html";
                }
                final String listId = st.nextToken();
                final int funcLvl = (listId.length() >= 4) ? CommonUtil.parseInt(listId.substring(3), -1) : -1;
                if (func2.getLevel() == funcLvl) {
                    final TeleportHolder holder2 = TeleportersData.getInstance().getHolder(npc.getId(), listId);
                    if (holder2 != null) {
                        holder2.doTeleport(player, npc, Util.parseNextInt(st, -1));
                    }
                }
                break;
            }
            case "buffer": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getCastleFunction(5) == null) {
                    htmltext = "castlefuncdisabled.html";
                    break;
                }
                final NpcHtmlMessage html4 = this.getHtmlPacket(player, npc, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, castle.getCastleFunction(5).getLevel()));
                html4.replace("%MPLeft%", Integer.toString((int)npc.getCurrentMp()));
                player.sendPacket(new ServerPacket[] { (ServerPacket)html4 });
                break;
            }
            case "cast_buff": {
                if (!this.isOwner(player, npc) || !player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (castle.getCastleFunction(5) == null) {
                    htmltext = "castlefuncdisabled.html";
                    break;
                }
                final int index = Integer.parseInt(st.nextToken());
                if (CastleChamberlain.BUFFS.length > index) {
                    final SkillHolder holder3 = CastleChamberlain.BUFFS[index];
                    NpcHtmlMessage html;
                    if (holder3.getSkill().getMpConsume() < npc.getCurrentMp()) {
                        npc.setTarget((WorldObject)player);
                        npc.doCast(holder3.getSkill());
                        html = this.getHtmlPacket(player, npc, "castleafterbuff.html");
                    }
                    else {
                        html = this.getHtmlPacket(player, npc, "castlenotenoughmp.html");
                    }
                    html.replace("%MPLeft%", Integer.toString((int)npc.getCurrentMp()));
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html });
                }
                break;
            }
            case "list_siege_clans": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_MANAGE_SIEGE)) {
                    castle.getSiege().listRegisterClan(player);
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "manor": {
                if (Config.ALLOW_MANOR) {
                    htmltext = ((this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_MANOR_ADMIN)) ? "manor.html" : "chamberlain-21.html");
                    break;
                }
                player.sendMessage("Manor system is deactivated.");
                break;
            }
            case "products": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
                    final NpcHtmlMessage html4 = this.getHtmlPacket(player, npc, "chamberlain-22.html");
                    html4.replace("%npcId%", Integer.toString(npc.getId()));
                    player.sendPacket(new ServerPacket[] { (ServerPacket)html4 });
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "buy": {
                if (this.isOwner(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_USE_FUNCTIONS)) {
                    ((Merchant)npc).showBuyWindow(player, Integer.parseInt(st.nextToken()));
                    break;
                }
                htmltext = "chamberlain-21.html";
                break;
            }
            case "give_cloak": {
                if (castle.getSiege().isInProgress()) {
                    htmltext = "chamberlain-08.html";
                    break;
                }
                if (!isMyLord) {
                    htmltext = "chamberlain-29.html";
                    break;
                }
                final int cloakId = (npc.getCastle().getSide() == CastleSide.DARK) ? 34926 : 34925;
                if (hasQuestItems(player, cloakId)) {
                    htmltext = "chamberlain-03.html";
                    break;
                }
                giveItems(player, cloakId, 1L);
                break;
            }
            case "give_crown": {
                if (castle.getSiege().isInProgress()) {
                    htmltext = "chamberlain-08.html";
                    break;
                }
                if (!isMyLord) {
                    htmltext = "chamberlain-21.html";
                    break;
                }
                if (hasQuestItems(player, 6841)) {
                    htmltext = "chamberlain-24.html";
                    break;
                }
                final NpcHtmlMessage html4 = this.getHtmlPacket(player, npc, "chamberlain-25.html");
                html4.replace("%owner_name%", player.getName());
                html4.replace("%feud_name%", String.valueOf(1001000 + castle.getId()));
                player.sendPacket(new ServerPacket[] { (ServerPacket)html4 });
                giveItems(player, 6841, 1L);
                break;
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return this.isOwner(player, npc) ? "chamberlain-01.html" : "chamberlain-04.html";
    }
    
    @RegisterEvent(EventType.ON_NPC_MANOR_BYPASS)
    @RegisterType(ListenerRegisterType.NPC)
    @Id({ 35100, 35142, 35184, 35226, 35274, 35316, 35363, 35509, 35555, 36653, 36654, 36655, 36656, 36657, 36658, 36659, 36660, 36661 })
    public final void onNpcManorBypass(final OnNpcManorBypass evt) {
        final Player player = evt.getActiveChar();
        final Npc npc = evt.getTarget();
        if (this.isOwner(player, npc)) {
            final CastleManorManager manor = CastleManorManager.getInstance();
            if (manor.isUnderMaintenance()) {
                player.sendPacket(SystemMessageId.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
                return;
            }
            final int castleId = (evt.getManorId() == -1) ? npc.getCastle().getId() : evt.getManorId();
            switch (evt.getRequest()) {
                case 3: {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowSeedInfo(castleId, evt.isNextPeriod(), true) });
                    break;
                }
                case 4: {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowCropInfo(castleId, evt.isNextPeriod(), true) });
                    break;
                }
                case 5: {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowManorDefaultInfo(true) });
                    break;
                }
                case 7: {
                    if (manor.isManorApproved()) {
                        player.sendPacket(SystemMessageId.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_4_30_AM_AND_8_PM);
                        return;
                    }
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowSeedSetting(castleId) });
                    break;
                }
                case 8: {
                    if (manor.isManorApproved()) {
                        player.sendPacket(SystemMessageId.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_4_30_AM_AND_8_PM);
                        return;
                    }
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowCropSetting(castleId) });
                    break;
                }
                default: {
                    this.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, player.getName(), player.getObjectId(), evt.getRequest()));
                    break;
                }
            }
        }
    }
    
    public static AbstractNpcAI provider() {
        return new CastleChamberlain();
    }
    
    static {
        NPC = new int[] { 35100, 36653, 35142, 36654, 35184, 36655, 35226, 36656, 35274, 36657 };
        BUFFS = new SkillHolder[] { new SkillHolder(4342, 2), new SkillHolder(4343, 3), new SkillHolder(4344, 3), new SkillHolder(4346, 4), new SkillHolder(4345, 3), new SkillHolder(4347, 2), new SkillHolder(4349, 1), new SkillHolder(4350, 1), new SkillHolder(4348, 2), new SkillHolder(4351, 2), new SkillHolder(4352, 1), new SkillHolder(4353, 2), new SkillHolder(4358, 1), new SkillHolder(4354, 1), new SkillHolder(4347, 6), new SkillHolder(4349, 2), new SkillHolder(4350, 4), new SkillHolder(4348, 6), new SkillHolder(4351, 6), new SkillHolder(4352, 2), new SkillHolder(4353, 6), new SkillHolder(4358, 3), new SkillHolder(4354, 4), new SkillHolder(4355, 1), new SkillHolder(4356, 1), new SkillHolder(4357, 1), new SkillHolder(4359, 1), new SkillHolder(4360, 1) };
    }
}
