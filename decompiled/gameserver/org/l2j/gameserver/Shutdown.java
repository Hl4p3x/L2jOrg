// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.datatables.SchemeBufferTable;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.engine.olympiad.OlympiadEngine;
import org.l2j.gameserver.instancemanager.ItemAuctionManager;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.data.sql.impl.OfflineTradersTable;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.OnlineStatus;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.slf4j.Logger;

public class Shutdown extends Thread
{
    private static final Logger LOGGER;
    private static Shutdown counterInstance;
    private int secondsShut;
    private ShutdownMode shutdownMode;
    
    private Shutdown() {
        this.secondsShut = -1;
        this.shutdownMode = ShutdownMode.SIGTERM;
    }
    
    public Shutdown(int seconds, final boolean restart) {
        if (seconds < 0) {
            seconds = 0;
        }
        this.secondsShut = seconds;
        this.shutdownMode = (restart ? ShutdownMode.GM_RESTART : ShutdownMode.GM_SHUTDOWN);
    }
    
    @Override
    public void run() {
        if (this == getInstance()) {
            this.saveDataAndReleaseResources();
        }
        else {
            this.countdown();
            switch (this.shutdownMode) {
                case GM_SHUTDOWN: {
                    getInstance().setMode(ShutdownMode.GM_SHUTDOWN);
                    System.exit(0);
                    break;
                }
                case GM_RESTART: {
                    getInstance().setMode(ShutdownMode.GM_RESTART);
                    System.exit(2);
                    break;
                }
                case ABORT: {
                    AuthServerCommunication.getInstance().sendPacket(new OnlineStatus(true));
                    break;
                }
            }
        }
    }
    
    private void saveDataAndReleaseResources() {
        switch (this.shutdownMode) {
            case SIGTERM: {
                Shutdown.LOGGER.info("SIGTERM received. Shutting down NOW!");
                break;
            }
            case GM_SHUTDOWN: {
                Shutdown.LOGGER.info("GM shutdown received. Shutting down NOW!");
                break;
            }
            case GM_RESTART: {
                Shutdown.LOGGER.info("GM restart received. Restarting NOW!");
                break;
            }
        }
        this.saveData();
        try {
            GameServer.getInstance().getConnectionHandler().shutdown();
            Shutdown.LOGGER.info("Game Server: Networking has been shut down.");
            AutoPlayEngine.getInstance().shutdown();
            Shutdown.LOGGER.info("Auto Play Engine has been shut down.");
            WorldTimeController.getInstance().stopTimer();
            Shutdown.LOGGER.info("Game Time Controller: Timer stopped.");
            AuthServerCommunication.getInstance().shutdown();
            Shutdown.LOGGER.info("Auth server Communication: Thread interrupted.");
            DatabaseAccess.shutdown();
            Shutdown.LOGGER.info("Database connection has been shut down.");
            ThreadPool.getInstance().shutdown();
            Shutdown.LOGGER.info("Thread Pool Manager: Manager has been shut down.");
        }
        catch (Throwable t) {
            Shutdown.LOGGER.warn(t.getMessage(), t);
        }
        Shutdown.LOGGER.info("The server has been successfully shut down .");
        if (getInstance().shutdownMode == ShutdownMode.GM_RESTART) {
            Runtime.getRuntime().halt(2);
        }
        else {
            Runtime.getRuntime().halt(0);
        }
    }
    
    public void startShutdown(final Player player, final int seconds, final boolean restart) {
        this.shutdownMode = (restart ? ShutdownMode.GM_RESTART : ShutdownMode.GM_SHUTDOWN);
        if (player != null) {
            Shutdown.LOGGER.warn("GM: {} issued shutdown command. {} in {} seconds!", new Object[] { player, this.shutdownMode.description, seconds });
        }
        else {
            Shutdown.LOGGER.warn("Server scheduled restart issued shutdown command. Restart in {} seconds!", (Object)seconds);
        }
        if (Shutdown.counterInstance != null) {
            Shutdown.counterInstance._abort();
        }
        (Shutdown.counterInstance = new Shutdown(seconds, restart)).start();
    }
    
    private void SendServerQuit(final int seconds) {
        Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECOND_S_PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT)).addInt(seconds));
    }
    
    public void abort(final Player player) {
        Shutdown.LOGGER.warn("GM: {} issued shutdown ABORT. {} has been stopped!", (Object)player, (Object)this.shutdownMode.description);
        Util.doIfNonNull((Object)Shutdown.counterInstance, counter -> {
            counter._abort();
            Broadcast.toAllOnlinePlayers(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.shutdownMode.description), false);
        });
    }
    
    private void setMode(final ShutdownMode mode) {
        this.shutdownMode = mode;
    }
    
    private void _abort() {
        this.shutdownMode = ShutdownMode.ABORT;
    }
    
    private void countdown() {
        try {
            final int countDelay = 1000;
            while (this.secondsShut > 0) {
                switch (this.secondsShut) {
                    case 1:
                    case 5:
                    case 10:
                    case 30:
                    case 120:
                    case 180:
                    case 240:
                    case 300:
                    case 360:
                    case 420:
                    case 480:
                    case 540: {
                        this.SendServerQuit(this.secondsShut);
                        break;
                    }
                    case 60: {
                        AuthServerCommunication.getInstance().sendPacket(new OnlineStatus(false));
                        this.SendServerQuit(60);
                        break;
                    }
                }
                --this.secondsShut;
                Thread.sleep(countDelay);
                if (this.shutdownMode == ShutdownMode.ABORT) {
                    break;
                }
            }
        }
        catch (InterruptedException ex) {}
    }
    
    private void saveData() {
        try {
            if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS && !Config.STORE_OFFLINE_TRADE_IN_REALTIME) {
                OfflineTradersTable.getInstance().storeOffliners();
            }
        }
        catch (Throwable t) {
            Shutdown.LOGGER.warn("Error saving offline shops.", t);
        }
        this.disconnectAllCharacters();
        Shutdown.LOGGER.info("All players disconnected and saved.");
        DBSpawnManager.getInstance().cleanUp();
        Shutdown.LOGGER.info("RaidBossSpawnManager: All raidboss info saved.");
        GrandBossManager.getInstance().cleanUp();
        Shutdown.LOGGER.info("GrandBossManager: All Grand Boss info saved.");
        ItemAuctionManager.getInstance().shutdown();
        Shutdown.LOGGER.info("Item Auction Manager: All tasks stopped.");
        OlympiadEngine.getInstance().saveOlympiadStatus();
        Shutdown.LOGGER.info("Olympiad System: Data saved.");
        Hero.getInstance().shutdown();
        Shutdown.LOGGER.info("Hero System: Data saved.");
        ClanTable.getInstance().shutdown();
        Shutdown.LOGGER.info("Clan System: Data saved.");
        CastleManorManager.getInstance().storeMe();
        Shutdown.LOGGER.info("Castle Manor Manager: Data saved.");
        QuestManager.getInstance().save();
        Shutdown.LOGGER.info("Quest Manager: Data saved.");
        GlobalVariablesManager.getInstance().storeMe();
        Shutdown.LOGGER.info("Global Variables Manager: Variables saved.");
        SchemeBufferTable.getInstance().saveSchemes();
        Shutdown.LOGGER.info("SchemeBufferTable data has been saved.");
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).saveDroppedItems()) {
            ItemsOnGroundManager.getInstance().saveInDb();
            Shutdown.LOGGER.info("Items On Ground Manager: Data saved.");
            ItemsOnGroundManager.getInstance().cleanUp();
            Shutdown.LOGGER.info("Items On Ground Manager: Cleaned up.");
        }
        if (Config.BOTREPORT_ENABLE) {
            ReportTable.getInstance().saveReportedCharData();
            Shutdown.LOGGER.info("Bot Report Table: Successfully saved reports to database!");
        }
    }
    
    private void disconnectAllCharacters() {
        World.getInstance().forEachPlayer(player -> Disconnection.of(player).defaultSequence(true));
    }
    
    public static Shutdown getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Shutdown.class);
        Shutdown.counterInstance = null;
    }
    
    private enum ShutdownMode
    {
        SIGTERM("SIGTERM"), 
        GM_SHUTDOWN("SHUTTING DOWN"), 
        GM_RESTART("RESTARTING"), 
        ABORT("ABORTING");
        
        private final String description;
        
        private ShutdownMode(final String description) {
            this.description = description;
        }
    }
    
    private static class Singleton
    {
        private static final Shutdown INSTANCE;
        
        static {
            INSTANCE = new Shutdown();
        }
    }
}
