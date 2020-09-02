// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcManorBypass;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMenuSelect;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.model.entity.Hero;
import java.util.StringTokenizer;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public final class RequestBypassToServer extends ClientPacket
{
    private static final Logger LOGGER;
    private static final String[] nonHtmBypasses;
    private String bypass;
    
    private static void comeHere(final Player player) {
        final WorldObject obj = player.getTarget();
        if (GameUtils.isNpc(obj)) {
            final Npc temp = (Npc)obj;
            temp.setTarget(player);
            temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, player.getLocation());
        }
    }
    
    public void readImpl() {
        this.bypass = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (this.bypass.isEmpty()) {
            RequestBypassToServer.LOGGER.warn("Player {} sent empty bypass!", (Object)player);
            Disconnection.of((GameClient)this.client, player).defaultSequence(false);
            return;
        }
        boolean requiresBypassValidation = true;
        for (final String possibleNonHtmlCommand : RequestBypassToServer.nonHtmBypasses) {
            if (this.bypass.startsWith(possibleNonHtmlCommand)) {
                requiresBypassValidation = false;
                break;
            }
        }
        int bypassOriginId = 0;
        if (requiresBypassValidation) {
            bypassOriginId = player.validateHtmlAction(this.bypass);
            if (bypassOriginId == -1 && !player.isGM()) {
                RequestBypassToServer.LOGGER.warn("Player {} sent non cached bypass: '{}'", (Object)player.getName(), (Object)this.bypass);
                return;
            }
            if (bypassOriginId > 0 && !GameUtils.isInsideRangeOfObjectId(player, bypassOriginId, 250)) {
                return;
            }
        }
        if (!((GameClient)this.client).getFloodProtectors().getServerBypass().tryPerformAction(this.bypass)) {
            return;
        }
        final TerminateReturn terminateReturn = EventDispatcher.getInstance().notifyEvent(new OnPlayerBypass(player, this.bypass), player, TerminateReturn.class);
        if (terminateReturn != null && terminateReturn.terminate()) {
            return;
        }
        try {
            if (this.bypass.startsWith("admin_")) {
                AdminCommandHandler.getInstance().useAdminCommand(player, this.bypass, true);
            }
            else if (CommunityBoardHandler.getInstance().isCommunityBoardCommand(this.bypass)) {
                CommunityBoardHandler.getInstance().handleParseCommand(this.bypass, player);
            }
            else if (this.bypass.equals("come_here") && player.isGM()) {
                comeHere(player);
            }
            else if (this.bypass.startsWith("npc_")) {
                final int endOfId = this.bypass.indexOf(95, 5);
                String id;
                if (endOfId > 0) {
                    id = this.bypass.substring(4, endOfId);
                }
                else {
                    id = this.bypass.substring(4);
                }
                if (Util.isInteger(id)) {
                    final WorldObject object = World.getInstance().findObject(Integer.parseInt(id));
                    if (GameUtils.isNpc(object) && endOfId > 0 && MathUtil.isInsideRadius2D(player, object, 250)) {
                        ((Npc)object).onBypassFeedback(player, this.bypass.substring(endOfId + 1));
                    }
                }
                player.sendPacket(ActionFailed.STATIC_PACKET);
            }
            else if (this.bypass.startsWith("item_")) {
                final int endOfId = this.bypass.indexOf(95, 5);
                String id;
                if (endOfId > 0) {
                    id = this.bypass.substring(5, endOfId);
                }
                else {
                    id = this.bypass.substring(5);
                }
                try {
                    final Item item = player.getInventory().getItemByObjectId(Integer.parseInt(id));
                    if (item != null && endOfId > 0) {
                        item.onBypassFeedback(player, this.bypass.substring(endOfId + 1));
                    }
                    player.sendPacket(ActionFailed.STATIC_PACKET);
                }
                catch (NumberFormatException nfe) {
                    RequestBypassToServer.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.bypass), (Throwable)nfe);
                }
            }
            else if (this.bypass.startsWith("_match")) {
                final String params = this.bypass.substring(this.bypass.indexOf("?") + 1);
                final StringTokenizer st = new StringTokenizer(params, "&");
                final int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heroid = Hero.getInstance().getHeroByClass(heroclass);
                if (heroid > 0) {
                    Hero.getInstance().showHeroFights(player, heroclass, heroid, heropage);
                }
            }
            else if (this.bypass.startsWith("_diary")) {
                final String params = this.bypass.substring(this.bypass.indexOf("?") + 1);
                final StringTokenizer st = new StringTokenizer(params, "&");
                final int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heroid = Hero.getInstance().getHeroByClass(heroclass);
                if (heroid > 0) {
                    Hero.getInstance().showHeroDiary(player, heroclass, heroid, heropage);
                }
            }
            else if (this.bypass.startsWith("_olympiad?command")) {
                final int arenaId = Integer.parseInt(this.bypass.split("=")[2]);
                final IBypassHandler handler = BypassHandler.getInstance().getHandler("arenachange");
                if (handler != null) {
                    handler.useBypass(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, arenaId - 1), player, (Creature)null);
                }
            }
            else if (this.bypass.startsWith("menu_select")) {
                final Npc lastNpc = player.getLastFolkNPC();
                if (lastNpc != null && lastNpc.canInteract(player)) {
                    final String[] split = this.bypass.substring(this.bypass.indexOf("?") + 1).split("&");
                    final int ask = Integer.parseInt(split[0].split("=")[1]);
                    final int reply = Integer.parseInt(split[1].split("=")[1]);
                    EventDispatcher.getInstance().notifyEventAsync(new OnNpcMenuSelect(player, lastNpc, ask, reply), lastNpc);
                }
            }
            else if (this.bypass.startsWith("manor_menu_select")) {
                final Npc lastNpc = player.getLastFolkNPC();
                if (Config.ALLOW_MANOR && lastNpc != null && lastNpc.canInteract(player)) {
                    final String[] split = this.bypass.substring(this.bypass.indexOf("?") + 1).split("&");
                    final int ask = Integer.parseInt(split[0].split("=")[1]);
                    final int state = Integer.parseInt(split[1].split("=")[1]);
                    final boolean time = split[2].split("=")[1].equals("1");
                    EventDispatcher.getInstance().notifyEventAsync(new OnNpcManorBypass(player, lastNpc, ask, state, time), lastNpc);
                }
            }
            else if (this.bypass.startsWith("pccafe")) {
                if (!Config.PC_CAFE_ENABLED) {
                    return;
                }
                final int multisellId = Integer.parseInt(this.bypass.substring(10).trim());
                MultisellData.getInstance().separateAndSend(multisellId, player, null, false);
            }
            else {
                final IBypassHandler handler2 = BypassHandler.getInstance().getHandler(this.bypass);
                if (handler2 != null) {
                    if (bypassOriginId > 0) {
                        final WorldObject bypassOrigin = World.getInstance().findObject(bypassOriginId);
                        if (GameUtils.isCreature(bypassOrigin)) {
                            handler2.useBypass(this.bypass, player, (Creature)bypassOrigin);
                        }
                        else {
                            handler2.useBypass(this.bypass, player, null);
                        }
                    }
                    else {
                        handler2.useBypass(this.bypass, player, null);
                    }
                }
                else {
                    RequestBypassToServer.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lio/github/joealisson/mmocore/Client;Ljava/lang/String;)Ljava/lang/String;, this.client, this.bypass));
                }
            }
        }
        catch (Exception e) {
            RequestBypassToServer.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), this.bypass), (Throwable)e);
            if (player.isGM()) {
                final StringBuilder sb = new StringBuilder(200);
                sb.append("<html><body>").append("Bypass error: ").append(e).append("<br1>").append("Bypass command: ").append(this.bypass).append("<br1>").append("StackTrace:<br1>");
                for (final StackTraceElement ste : e.getStackTrace()) {
                    sb.append(ste).append("<br1>");
                }
                sb.append("</body></html>");
                final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1, sb.toString());
                msg.disableValidation();
                player.sendPacket(msg);
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestBypassToServer.class);
        nonHtmBypasses = new String[] { "admin", "_bbs", "bbs", "_mail", "_friend", "_match", "_diary", "_olympiad?command", "menu_select", "manor_menu_select", "pccafe" };
    }
}
