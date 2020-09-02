// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.commons.util.Util;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public final class AdminCastle implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        if (actualCommand.equals("admin_castlemanage")) {
            if (st.hasMoreTokens()) {
                final String param = st.nextToken();
                Castle castle;
                if (Util.isDigit(param)) {
                    castle = CastleManager.getInstance().getCastleById(Integer.parseInt(param));
                }
                else {
                    castle = CastleManager.getInstance().getCastle(param);
                }
                if (castle == null) {
                    BuilderUtil.sendSysMessage(activeChar, "Invalid parameters! Usage: //castlemanage <castleId[1-9] / castleName>");
                    return false;
                }
                if (!st.hasMoreTokens()) {
                    this.showCastleMenu(activeChar, castle.getId());
                }
                else {
                    final String action = st.nextToken();
                    final Player target = this.checkTarget(activeChar) ? activeChar.getTarget().getActingPlayer() : null;
                    final String s = action;
                    switch (s) {
                        case "showRegWindow": {
                            castle.getSiege().listRegisterClan(activeChar);
                            break;
                        }
                        case "addAttacker": {
                            if (this.checkTarget(activeChar)) {
                                castle.getSiege().registerAttacker(target, true);
                                break;
                            }
                            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                            break;
                        }
                        case "removeAttacker": {
                            if (this.checkTarget(activeChar)) {
                                castle.getSiege().removeSiegeClan(activeChar);
                                break;
                            }
                            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                            break;
                        }
                        case "addDeffender": {
                            if (this.checkTarget(activeChar)) {
                                castle.getSiege().registerDefender(target, true);
                                break;
                            }
                            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                            break;
                        }
                        case "removeDeffender": {
                            if (this.checkTarget(activeChar)) {
                                castle.getSiege().removeSiegeClan(target);
                                break;
                            }
                            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                            break;
                        }
                        case "startSiege": {
                            if (!castle.getSiege().getAttackerClans().isEmpty()) {
                                castle.getSiege().startSiege();
                                break;
                            }
                            BuilderUtil.sendSysMessage(activeChar, "There is currently not registered any clan for castle siege!");
                            break;
                        }
                        case "stopSiege": {
                            if (castle.getSiege().isInProgress()) {
                                castle.getSiege().endSiege();
                            }
                            else {
                                BuilderUtil.sendSysMessage(activeChar, "Castle siege is not currently in progress!");
                            }
                            this.showCastleMenu(activeChar, castle.getId());
                            break;
                        }
                        case "setOwner": {
                            if (target == null || !this.checkTarget(activeChar)) {
                                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                            }
                            else if (target.getClan().getCastleId() > 0) {
                                BuilderUtil.sendSysMessage(activeChar, "This clan already have castle!");
                            }
                            else if (castle.getOwner() != null) {
                                BuilderUtil.sendSysMessage(activeChar, "This castle is already taken by another clan!");
                            }
                            else if (!st.hasMoreTokens()) {
                                BuilderUtil.sendSysMessage(activeChar, "Invalid parameters!!");
                            }
                            else {
                                final CastleSide side = Enum.valueOf(CastleSide.class, st.nextToken().toUpperCase());
                                if (side != null) {
                                    castle.setSide(side);
                                    castle.setOwner(target.getClan());
                                }
                            }
                            this.showCastleMenu(activeChar, castle.getId());
                            break;
                        }
                        case "takeCastle": {
                            final Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
                            if (clan != null) {
                                castle.removeOwner(clan);
                            }
                            else {
                                BuilderUtil.sendSysMessage(activeChar, "Error during removing castle!");
                            }
                            this.showCastleMenu(activeChar, castle.getId());
                            break;
                        }
                        case "switchSide": {
                            if (castle.getSide() == CastleSide.DARK) {
                                castle.setSide(CastleSide.LIGHT);
                            }
                            else if (castle.getSide() == CastleSide.LIGHT) {
                                castle.setSide(CastleSide.DARK);
                            }
                            else {
                                BuilderUtil.sendSysMessage(activeChar, "You can't switch sides when is castle neutral!");
                            }
                            this.showCastleMenu(activeChar, castle.getId());
                            break;
                        }
                    }
                }
            }
            else {
                final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
                html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/castlemanage.htm"));
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
            }
        }
        return true;
    }
    
    private void showCastleMenu(final Player player, final int castleId) {
        final Castle castle = CastleManager.getInstance().getCastleById(castleId);
        if (castle != null) {
            final Clan ownerClan = castle.getOwner();
            final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
            html.setHtml(HtmCache.getInstance().getHtm(player, "data/html/admin/castlemanage_selected.htm"));
            html.replace("%castleId%", castle.getId());
            html.replace("%castleName%", castle.getName());
            html.replace("%ownerName%", (ownerClan != null) ? ownerClan.getLeaderName() : "NPC");
            html.replace("%ownerClan%", (ownerClan != null) ? ownerClan.getName() : "NPC");
            html.replace("%castleSide%", CommonUtil.capitalizeFirst(castle.getSide().toString().toLowerCase()));
            player.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
    }
    
    private boolean checkTarget(final Player player) {
        return GameUtils.isPlayer(player.getTarget()) && ((Player)player.getTarget()).getClan() != null;
    }
    
    public String[] getAdminCommandList() {
        return AdminCastle.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_castlemanage" };
    }
}
