// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.slf4j.LoggerFactory;
import java.util.stream.Stream;
import org.l2j.gameserver.network.ConnectionState;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.Arrays;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.world.WorldTimeController;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class FloodProtectorAction
{
    private static final Logger LOGGER;
    private final GameClient _client;
    private final FloodProtectorConfig _config;
    private final AtomicInteger _count;
    private volatile int _nextGameTick;
    private boolean _logged;
    private volatile boolean _punishmentInProgress;
    
    public FloodProtectorAction(final GameClient client, final FloodProtectorConfig config) {
        this._count = new AtomicInteger(0);
        this._nextGameTick = WorldTimeController.getInstance().getGameTicks();
        this._client = client;
        this._config = config;
    }
    
    public boolean tryPerformAction(final String command) {
        final int curTick = WorldTimeController.getInstance().getGameTicks();
        if (this._client.getPlayer() != null && this._client.getPlayer().canOverrideCond(PcCondOverride.FLOOD_CONDITIONS)) {
            return true;
        }
        if (curTick < this._nextGameTick || this._punishmentInProgress) {
            if (this._config.LOG_FLOODING && !this._logged) {
                this.log(" called command ", command, " ~", String.valueOf((this._config.FLOOD_PROTECTION_INTERVAL - (this._nextGameTick - curTick)) * 100), " ms after previous command");
                this._logged = true;
            }
            this._count.incrementAndGet();
            if (!this._punishmentInProgress && this._config.PUNISHMENT_LIMIT > 0 && this._count.get() >= this._config.PUNISHMENT_LIMIT && this._config.PUNISHMENT_TYPE != null) {
                this._punishmentInProgress = true;
                if ("kick".equals(this._config.PUNISHMENT_TYPE)) {
                    this.kickPlayer();
                }
                else if ("ban".equals(this._config.PUNISHMENT_TYPE)) {
                    this.banAccount();
                }
                else if ("jail".equals(this._config.PUNISHMENT_TYPE)) {
                    this.jailChar();
                }
                this._punishmentInProgress = false;
            }
            return false;
        }
        if (this._count.get() > 0 && this._config.LOG_FLOODING) {
            this.log(" issued ", String.valueOf(this._count), " extra requests within ~", String.valueOf(this._config.FLOOD_PROTECTION_INTERVAL * 100), " ms");
        }
        this._nextGameTick = curTick + this._config.FLOOD_PROTECTION_INTERVAL;
        this._logged = false;
        this._count.set(0);
        return true;
    }
    
    private void kickPlayer() {
        Disconnection.of(this._client).defaultSequence(false);
        this.log("kicked for flooding");
    }
    
    private void banAccount() {
        PunishmentManager.getInstance().startPunishment(new PunishmentTask(this._client.getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.BAN, System.currentTimeMillis() + this._config.PUNISHMENT_TIME, "", this.getClass().getSimpleName()));
        this.log(new String[] { " banned for flooding ", (this._config.PUNISHMENT_TIME <= 0L) ? "forever" : invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, this._config.PUNISHMENT_TIME / 60000L) });
    }
    
    private void jailChar() {
        if (this._client.getPlayer() == null) {
            return;
        }
        final int charId = this._client.getPlayer().getObjectId();
        if (charId > 0) {
            PunishmentManager.getInstance().startPunishment(new PunishmentTask(charId, PunishmentAffect.CHARACTER, PunishmentType.JAIL, System.currentTimeMillis() + this._config.PUNISHMENT_TIME, "", this.getClass().getSimpleName()));
        }
        this.log(new String[] { " jailed for flooding ", (this._config.PUNISHMENT_TIME <= 0L) ? "forever" : invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, this._config.PUNISHMENT_TIME / 60000L) });
    }
    
    private void log(final String... lines) {
        final StringBuilder output = new StringBuilder(100);
        output.append(this._config.FLOOD_PROTECTOR_TYPE);
        output.append(": ");
        String address = null;
        try {
            address = this._client.getHostAddress();
        }
        catch (Exception e) {
            FloodProtectorAction.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
        final ConnectionState state = this._client.getConnectionState();
        switch (state) {
            case IN_GAME: {
                if (this._client.getPlayer() != null) {
                    output.append(this._client.getPlayer().getName());
                    output.append("(");
                    output.append(this._client.getPlayer().getObjectId());
                    output.append(") ");
                    break;
                }
                break;
            }
            case AUTHENTICATED: {
                if (this._client.getAccountName() != null) {
                    output.append(this._client.getAccountName());
                    output.append(" ");
                    break;
                }
                break;
            }
            case CONNECTED: {
                if (address != null) {
                    output.append(address);
                    break;
                }
                break;
            }
            default: {
                throw new IllegalStateException("Missing state on switch");
            }
        }
        final Stream<String> stream = Arrays.stream(lines);
        final StringBuilder obj = output;
        Objects.requireNonNull(obj);
        stream.forEach(obj::append);
        FloodProtectorAction.LOGGER.warn(output.toString());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)FloodProtectorAction.class);
    }
}
