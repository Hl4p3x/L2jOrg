// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.punishmenthandlers;

import org.l2j.gameserver.model.punishment.PunishmentType;
import java.util.Iterator;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.handler.IPunishmentHandler;

public class BanHandler implements IPunishmentHandler
{
    public void onStart(final PunishmentTask task) {
        switch (task.getAffect()) {
            case CHARACTER: {
                final int objectId = Integer.parseInt(String.valueOf(task.getKey()));
                final Player player = World.getInstance().findPlayer(objectId);
                if (player != null) {
                    applyToPlayer(player);
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
                        applyToPlayer(player2);
                    }
                    else {
                        Disconnection.of(client).defaultSequence(false);
                    }
                    break;
                }
                break;
            }
            case IP: {
                final String ip = String.valueOf(task.getKey());
                for (final Player player2 : World.getInstance().getPlayers()) {
                    if (player2.getIPAddress().equals(ip)) {
                        applyToPlayer(player2);
                    }
                }
                break;
            }
        }
    }
    
    public void onEnd(final PunishmentTask task) {
    }
    
    private static void applyToPlayer(final Player player) {
        Disconnection.of(player).defaultSequence(false);
    }
    
    public PunishmentType getType() {
        return PunishmentType.BAN;
    }
}
