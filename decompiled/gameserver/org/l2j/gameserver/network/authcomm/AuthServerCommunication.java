// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.authcomm.gs2as.ServerStatus;
import org.l2j.gameserver.network.authcomm.gs2as.ChangePassword;
import java.util.concurrent.TimeUnit;
import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.commons.threading.ThreadPool;
import io.github.joealisson.mmocore.ReadablePacket;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.util.Objects;
import java.net.InetSocketAddress;
import org.l2j.commons.util.Util;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.util.concurrent.ConcurrentHashMap;
import io.github.joealisson.mmocore.Connector;
import org.l2j.gameserver.network.GameClient;
import java.util.Map;
import org.slf4j.Logger;
import io.github.joealisson.mmocore.PacketExecutor;

public class AuthServerCommunication implements Runnable, PacketExecutor<AuthServerClient>
{
    private static final Logger LOGGER;
    private final Map<String, GameClient> waitingClients;
    private final Map<String, GameClient> authedClients;
    private AuthServerClient client;
    private final Connector<AuthServerClient> connector;
    private volatile boolean shutdown;
    
    private AuthServerCommunication() {
        this.waitingClients = new ConcurrentHashMap<String, GameClient>();
        this.authedClients = new ConcurrentHashMap<String, GameClient>();
        this.shutdown = false;
        this.connector = (Connector<AuthServerClient>)Connector.create(AuthServerClient::new, (io.github.joealisson.mmocore.PacketHandler)new PacketHandler(), (PacketExecutor)this);
    }
    
    public void connect() throws IOException, ExecutionException, InterruptedException {
        final ServerSettings serverSettings = (ServerSettings)Configurator.getSettings((Class)ServerSettings.class);
        InetSocketAddress address;
        if (Util.isNullOrEmpty((CharSequence)serverSettings.authServerAddress())) {
            AuthServerCommunication.LOGGER.warn("Auth server address not configured trying to connect to localhost");
            address = new InetSocketAddress(serverSettings.authServerPort());
        }
        else {
            address = new InetSocketAddress(serverSettings.authServerAddress(), serverSettings.authServerPort());
        }
        if (Objects.nonNull(this.client)) {
            this.client.close();
            this.client = null;
        }
        AuthServerCommunication.LOGGER.info("Connecting to auth server on {}", (Object)address);
        this.client = (AuthServerClient)this.connector.connect(address);
    }
    
    @Override
    public void run() {
        try {
            if (!this.shutdown && (Objects.isNull(this.client) || !this.client.isConnected())) {
                this.connect();
            }
        }
        catch (IOException | ExecutionException | InterruptedException ex2) {
            final Exception ex;
            final Exception e = ex;
            AuthServerCommunication.LOGGER.debug(e.getMessage(), (Throwable)e);
            this.restart();
        }
    }
    
    public GameClient addWaitingClient(final GameClient client) {
        return this.waitingClients.put(client.getAccountName(), client);
    }
    
    public GameClient removeWaitingClient(final String account) {
        return this.waitingClients.remove(account);
    }
    
    public GameClient addAuthedClient(final GameClient client) {
        return this.authedClients.put(client.getAccountName(), client);
    }
    
    public GameClient removeAuthedClient(final String login) {
        return this.authedClients.remove(login);
    }
    
    public GameClient getAuthedClient(final String login) {
        return this.authedClients.get(login);
    }
    
    public String[] getAccounts() {
        return this.authedClients.keySet().toArray(String[]::new);
    }
    
    public void execute(final ReadablePacket<AuthServerClient> packet) {
        ThreadPool.execute((Runnable)packet);
    }
    
    public void shutdown() {
        this.shutdown = true;
        if (Objects.nonNull(this.client) && this.client.isConnected()) {
            this.client.close();
        }
    }
    
    public void sendPacket(final SendablePacket packet) {
        if (Objects.nonNull(this.client) && this.client.isConnected()) {
            this.client.sendPacket(packet);
        }
    }
    
    public boolean isShutdown() {
        return this.shutdown;
    }
    
    public void restart() {
        this.restart(5);
    }
    
    public void restart(final int waitSeconds) {
        if (Objects.nonNull(this.client)) {
            this.client.close((WritablePacket)null);
        }
        this.client = null;
        ThreadPool.schedule((Runnable)this, (long)waitSeconds, TimeUnit.SECONDS);
    }
    
    public void sendChangePassword(final String accountName, final String oldPass, final String curpass) {
        this.sendPacket(new ChangePassword(accountName, oldPass, curpass, ""));
    }
    
    public void sendServerType(final int type) {
        this.sendPacket(new ServerStatus().add(6, type));
    }
    
    public static AuthServerCommunication getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AuthServerCommunication.class);
    }
    
    private static class Singleton
    {
        private static final AuthServerCommunication INSTANCE;
        
        static {
            INSTANCE = new AuthServerCommunication();
        }
    }
}
