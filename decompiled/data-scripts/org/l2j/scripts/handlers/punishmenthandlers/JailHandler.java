// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.punishmenthandlers;

import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.tasks.player.TeleportTask;
import org.l2j.gameserver.world.zone.type.JailZone;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.Iterator;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.handler.IPunishmentHandler;

public class JailHandler implements IPunishmentHandler
{
    public JailHandler() {
        Listeners.Global().addListener((AbstractEventListener)new ConsumerEventListener(Listeners.Global(), EventType.ON_PLAYER_LOGIN, event -> this.onPlayerLogin(event), (Object)this));
    }
    
    private void onPlayerLogin(final OnPlayerLogin event) {
        final Player activeChar = event.getPlayer();
        if (activeChar.isJailed() && !activeChar.isInsideZone(ZoneType.JAIL)) {
            applyToPlayer(null, activeChar);
        }
        else if (!activeChar.isJailed() && activeChar.isInsideZone(ZoneType.JAIL) && !activeChar.isGM()) {
            removeFromPlayer(activeChar);
        }
    }
    
    public void onStart(final PunishmentTask task) {
        switch (task.getAffect()) {
            case CHARACTER: {
                final int objectId = Integer.parseInt(String.valueOf(task.getKey()));
                final Player player = World.getInstance().findPlayer(objectId);
                if (player != null) {
                    applyToPlayer(task, player);
                    break;
                }
                break;
            }
            case ACCOUNT: {
                final String account = String.valueOf(task.getKey());
                final GameClient client = AuthServerCommunication.getInstance().getAuthedClient(account);
                if (client != null) {
                    final Player player2 = client.getPlayer();
                    if (player2 != null) {
                        applyToPlayer(task, player2);
                    }
                    break;
                }
                break;
            }
            case IP: {
                final String ip = String.valueOf(task.getKey());
                for (final Player player2 : World.getInstance().getPlayers()) {
                    if (player2.getIPAddress().equals(ip)) {
                        applyToPlayer(task, player2);
                    }
                }
                break;
            }
        }
    }
    
    public void onEnd(final PunishmentTask task) {
        switch (task.getAffect()) {
            case CHARACTER: {
                final int objectId = Integer.parseInt(String.valueOf(task.getKey()));
                final Player player = World.getInstance().findPlayer(objectId);
                if (player != null) {
                    removeFromPlayer(player);
                    break;
                }
                break;
            }
            case ACCOUNT: {
                final String account = String.valueOf(task.getKey());
                final GameClient client = AuthServerCommunication.getInstance().getAuthedClient(account);
                if (client != null) {
                    final Player player2 = client.getPlayer();
                    if (player2 != null) {
                        removeFromPlayer(player2);
                    }
                    break;
                }
                break;
            }
            case IP: {
                final String ip = String.valueOf(task.getKey());
                for (final Player player2 : World.getInstance().getPlayers()) {
                    if (player2.getIPAddress().equals(ip)) {
                        removeFromPlayer(player2);
                    }
                }
                break;
            }
        }
    }
    
    private static void applyToPlayer(final PunishmentTask task, final Player player) {
        player.setInstance((Instance)null);
        if (OlympiadManager.getInstance().isRegisteredInComp(player)) {
            OlympiadManager.getInstance().removeDisconnectedCompetitor(player);
        }
        ThreadPool.schedule((Runnable)new TeleportTask(player, JailZone.getLocationIn()), 2000L);
        final NpcHtmlMessage msg = new NpcHtmlMessage();
        String content = HtmCache.getInstance().getHtm(player, "data/html/jail_in.htm");
        if (content != null) {
            content = content.replaceAll("%reason%", (task != null) ? task.getReason() : "");
            content = content.replaceAll("%punishedBy%", (task != null) ? task.getPunishedBy() : "");
            msg.setHtml(content);
        }
        else {
            msg.setHtml("<html><body>You have been put in jail by an admin.</body></html>");
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)msg });
        if (task != null) {
            final long delay = (task.getExpirationTime() - System.currentTimeMillis()) / 1000L;
            if (delay > 0L) {
                player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (delay > 60L) ? invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, delay / 60L) : invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, delay)));
            }
            else {
                player.sendMessage("You've been jailed forever.");
            }
        }
    }
    
    private static void removeFromPlayer(final Player player) {
        ThreadPool.schedule((Runnable)new TeleportTask(player, JailZone.getLocationOut()), 2000L);
        final NpcHtmlMessage msg = new NpcHtmlMessage();
        final String content = HtmCache.getInstance().getHtm(player, "data/html/jail_out.htm");
        if (content != null) {
            msg.setHtml(content);
        }
        else {
            msg.setHtml("<html><body>You are free for now, respect server rules!</body></html>");
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)msg });
    }
    
    public PunishmentType getType() {
        return PunishmentType.JAIL;
    }
}
