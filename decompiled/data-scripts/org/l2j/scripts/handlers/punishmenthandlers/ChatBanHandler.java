// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.punishmenthandlers;

import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Iterator;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.handler.IPunishmentHandler;

public class ChatBanHandler implements IPunishmentHandler
{
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
        final long delay = (task.getExpirationTime() - System.currentTimeMillis()) / 1000L;
        if (delay > 0L) {
            player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (delay > 60L) ? invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, delay / 60L) : invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, delay)));
        }
        else {
            player.sendMessage("You've been chat banned forever.");
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)new EtcStatusUpdate(player) });
    }
    
    private static void removeFromPlayer(final Player player) {
        player.sendMessage("Your Chat ban has been lifted");
        player.sendPacket(new ServerPacket[] { (ServerPacket)new EtcStatusUpdate(player) });
    }
    
    public PunishmentType getType() {
        return PunishmentType.CHAT_BAN;
    }
}
