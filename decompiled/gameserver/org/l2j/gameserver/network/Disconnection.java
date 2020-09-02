// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import org.slf4j.LoggerFactory;
import org.l2j.commons.threading.ThreadPool;
import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public final class Disconnection
{
    private static final Logger LOGGER;
    private final GameClient client;
    private final Player player;
    
    private Disconnection(final GameClient client) {
        this(client, client.getPlayer());
    }
    
    private Disconnection(final Player activeChar) {
        this(activeChar.getClient(), activeChar);
    }
    
    private Disconnection(final GameClient client, final Player player) {
        this.client = getClient(client, player);
        this.player = getPlayer(client, player);
        AntiFeedManager.getInstance().onDisconnect(this.client);
    }
    
    public static GameClient getClient(final GameClient client, final Player player) {
        if (Objects.nonNull(client)) {
            return client;
        }
        if (Objects.nonNull(player)) {
            return player.getClient();
        }
        return null;
    }
    
    public static Player getPlayer(final GameClient client, final Player player) {
        if (Objects.nonNull(player)) {
            return player;
        }
        if (Objects.nonNull(client)) {
            return client.getPlayer();
        }
        return null;
    }
    
    public static Disconnection of(final GameClient client) {
        return new Disconnection(client);
    }
    
    public static Disconnection of(final Player player) {
        return new Disconnection(player);
    }
    
    public static Disconnection of(final GameClient client, final Player player) {
        return new Disconnection(client, player);
    }
    
    public Disconnection storeMe() {
        try {
            if (Objects.nonNull(this.player)) {
                this.player.storeMe();
            }
            if (Objects.nonNull(this.client)) {
                this.client.storeAccountData();
            }
        }
        catch (RuntimeException e) {
            Disconnection.LOGGER.error(e.getMessage(), (Throwable)e);
        }
        return this;
    }
    
    public Disconnection deleteMe() {
        try {
            if (this.player != null && this.player.isOnline()) {
                this.player.deleteMe();
            }
            this.detachPlayerFromClient();
        }
        catch (RuntimeException e) {
            Disconnection.LOGGER.warn(e.getMessage());
        }
        return this;
    }
    
    private void detachPlayerFromClient() {
        if (this.client != null) {
            this.client.setPlayer(null);
        }
    }
    
    public Disconnection close(final boolean toLoginScreen) {
        if (this.client != null) {
            this.client.close(toLoginScreen);
        }
        this.detachPlayerFromClient();
        return this;
    }
    
    public Disconnection close(final ServerPacket packet) {
        if (this.client != null) {
            this.client.close((WritablePacket)packet);
        }
        this.detachPlayerFromClient();
        return this;
    }
    
    public void defaultSequence(final boolean toLoginScreen) {
        this.defaultSequence();
        this.close(toLoginScreen);
    }
    
    public void defaultSequence(final ServerPacket packet) {
        this.defaultSequence();
        this.close(packet);
    }
    
    private void defaultSequence() {
        this.storeMe();
        this.deleteMe();
    }
    
    public void onDisconnection() {
        if (this.player != null) {
            ThreadPool.schedule(this::defaultSequence, this.player.canLogout() ? 0L : 15000L);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Disconnection.class);
    }
}
