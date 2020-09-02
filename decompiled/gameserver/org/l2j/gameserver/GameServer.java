// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver;

import java.util.concurrent.TimeUnit;
import java.lang.management.ManagementFactory;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.LoggerFactory;
import org.l2j.commons.util.Util;
import org.l2j.commons.cache.CacheFactory;
import org.l2j.commons.util.DeadLockDetector;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.Broadcast;
import java.time.Duration;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import io.github.joealisson.mmocore.PacketHandler;
import io.github.joealisson.mmocore.ConnectionBuilder;
import org.l2j.gameserver.network.ClientPacketHandler;
import java.net.InetSocketAddress;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import java.time.temporal.TemporalUnit;
import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;
import org.l2j.gameserver.instancemanager.ServerRestartManager;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.data.sql.impl.OfflineTradersTable;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.taskmanager.TaskManager;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.data.xml.impl.SiegeScheduleData;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.votereward.VoteSystem;
import org.l2j.gameserver.engine.events.EventEngine;
import org.l2j.gameserver.instancemanager.SellBuffsManager;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.data.xml.impl.TransformData;
import org.l2j.gameserver.data.xml.impl.TeleportersData;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.data.xml.impl.StaticObjectData;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.instancemanager.ClanHallAuctionManager;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.data.xml.impl.ResidenceFunctionsData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.data.sql.impl.PlayerSummonTable;
import org.l2j.gameserver.data.xml.impl.CubicData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.xml.impl.PlayerTemplateData;
import org.l2j.gameserver.data.xml.impl.HitConditionBonusData;
import org.l2j.gameserver.data.xml.impl.KarmaData;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.data.xml.impl.InitialShortcutData;
import org.l2j.gameserver.data.xml.impl.InitialEquipmentData;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.datatables.SchemeBufferTable;
import org.l2j.gameserver.instancemanager.ItemAuctionManager;
import org.l2j.gameserver.data.xml.impl.ExtendDropData;
import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.engine.rank.RankEngine;
import org.l2j.gameserver.data.xml.CombinationItemsManager;
import org.l2j.gameserver.engine.upgrade.UpgradeItemEngine;
import org.l2j.gameserver.engine.costume.CostumeEngine;
import org.l2j.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.data.xml.impl.LuckyGameData;
import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.data.xml.impl.LCoinShopData;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.data.xml.impl.TeleportEngine;
import org.l2j.gameserver.engine.elemental.ElementalSpiritEngine;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.engine.mission.MissionEngine;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.data.xml.SecondaryAuthManager;
import org.l2j.gameserver.data.database.announce.manager.AnnouncementsManager;
import org.l2j.gameserver.instancemanager.GraciaSeedsManager;
import org.l2j.gameserver.data.xml.impl.ShuttleData;
import org.l2j.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.data.xml.impl.FishingData;
import org.l2j.gameserver.data.xml.impl.ArmorSetsData;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.data.xml.ActionManager;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.commons.threading.ThreadPool;
import java.util.Objects;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.engine.scripting.ScriptEngineManager;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.world.World;
import java.time.Instant;
import org.l2j.gameserver.network.GameClient;
import io.github.joealisson.mmocore.ConnectionHandler;
import org.slf4j.Logger;

public class GameServer
{
    private static final String LOG4J_CONFIGURATION_FILE = "log4j.configurationFile";
    private static Logger LOGGER;
    private static GameServer INSTANCE;
    public static String fullVersion;
    private final ConnectionHandler<GameClient> connectionHandler;
    
    public GameServer() throws Exception {
        final Instant serverLoadStart = Instant.now();
        printSection("World");
        World.init();
        printSection("Skills");
        SkillEngine.init();
        printSection("Items");
        ItemEngine.init();
        printSection("NPCs");
        NpcData.init();
        printSection("Castle Data");
        CastleManager.init();
        printSection("Class Categories");
        CategoryManager.init();
        printSection("Extensions Loaders");
        ExtensionBoot.loaders();
        ScriptEngineManager.getInstance().executeScriptLoader();
        printSection("Spawns");
        SpawnsData.init();
        DBSpawnManager.init();
        final SpawnsData instance = SpawnsData.getInstance();
        Objects.requireNonNull(instance);
        ThreadPool.executeForked(instance::spawnAll);
        printSection("Server Data");
        GlobalVariablesManager.init();
        ActionManager.init();
        BuyListData.init();
        MultisellData.getInstance();
        RecipeData.getInstance();
        ArmorSetsData.getInstance();
        FishingData.getInstance();
        HennaData.getInstance();
        ShuttleData.getInstance();
        GraciaSeedsManager.getInstance();
        printSection("Features");
        AnnouncementsManager.init();
        SecondaryAuthManager.init();
        ClanRewardManager.init();
        MissionEngine.init();
        VipEngine.init();
        ElementalSpiritEngine.init();
        TeleportEngine.init();
        PrimeShopData.getInstance();
        LCoinShopData.getInstance();
        CommissionManager.getInstance();
        LuckyGameData.getInstance();
        AttendanceRewardData.getInstance();
        CostumeEngine.init();
        UpgradeItemEngine.init();
        CombinationItemsManager.init();
        RankEngine.init();
        BeautyShopData.getInstance();
        ExtendDropData.getInstance();
        ItemAuctionManager.getInstance();
        SchemeBufferTable.getInstance();
        GrandBossManager.getInstance();
        printSection("Characters");
        ClassListData.getInstance();
        InitialEquipmentData.getInstance();
        InitialShortcutData.getInstance();
        LevelData.init();
        KarmaData.getInstance();
        HitConditionBonusData.getInstance();
        PlayerTemplateData.getInstance();
        PlayerNameTable.getInstance();
        AdminData.getInstance();
        PetDataTable.getInstance();
        CubicData.getInstance();
        PlayerSummonTable.getInstance().init();
        MentorManager.getInstance();
        printSection("Clans");
        ClanTable.init();
        ResidenceFunctionsData.getInstance();
        ClanHallManager.init();
        ClanHallAuctionManager.getInstance();
        ClanEntryManager.getInstance();
        WalkingManager.getInstance();
        StaticObjectData.getInstance();
        printSection("Instance");
        InstanceManager.getInstance();
        printSection("Cache");
        HtmCache.getInstance();
        CrestTable.getInstance();
        TeleportersData.getInstance();
        TransformData.getInstance();
        ReportTable.getInstance();
        if (Config.SELLBUFF_ENABLED) {
            SellBuffsManager.getInstance();
        }
        printSection("Event Engine");
        EventEngine.init();
        VoteSystem.initialize();
        printSection("Siege");
        SiegeManager.getInstance().getSieges();
        CastleManager.getInstance().activateInstances();
        SiegeScheduleData.getInstance();
        CastleManorManager.getInstance();
        SiegeGuardManager.getInstance();
        QuestManager.getInstance().report();
        final GeneralSettings generalSettings = (GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class);
        if (generalSettings.saveDroppedItems()) {
            ItemsOnGroundManager.init();
        }
        if (generalSettings.autoDestroyItemTime() > 0 || generalSettings.autoDestroyHerbTime() > 0) {
            ItemsAutoDestroy.getInstance();
        }
        TaskManager.getInstance();
        AntiFeedManager.getInstance().registerEvent(0);
        if (generalSettings.allowMail()) {
            MailEngine.init();
        }
        PunishmentManager.getInstance();
        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
        GameServer.LOGGER.info(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, IdFactory.getInstance().size()));
        if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS) {
            OfflineTradersTable.getInstance().restoreOfflineTraders();
        }
        final ServerSettings serverSettings = (ServerSettings)Configurator.getSettings((Class)ServerSettings.class);
        if (serverSettings.scheduleRestart()) {
            ServerRestartManager.getInstance();
        }
        GameServer.LOGGER.info("Maximum number of connected players is configured to {}", (Object)serverSettings.maximumOnlineUsers());
        GameServer.LOGGER.info("Server loaded in {} seconds", (Object)serverLoadStart.until(Instant.now(), ChronoUnit.SECONDS));
        printSection("Setting All characters to offline status!");
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).setAllCharactersOffline();
        (this.connectionHandler = (ConnectionHandler<GameClient>)ConnectionBuilder.create(new InetSocketAddress(serverSettings.port()), GameClient::new, (PacketHandler)new ClientPacketHandler(), ThreadPool::execute).build()).start();
    }
    
    public static void main(final String[] args) throws Exception {
        configureLogger();
        configureCache();
        logVersionInfo();
        configureDatabase();
        configureNetworkPackets();
        printSection("Server Configuration");
        Config.load();
        final ServerSettings settings = (ServerSettings)Configurator.getSettings((Class)ServerSettings.class);
        printSection("Thread Pools");
        ThreadPool.init(settings.threadPoolSize(), settings.scheduledPoolSize());
        printSection("Identity Factory");
        if (!IdFactory.getInstance().isInitialized()) {
            GameServer.LOGGER.error("Could not read object IDs from database. Please check your configuration.");
            throw new Exception("Could not initialize the Identity factory!");
        }
        printSection("Extensions Initializers");
        ScriptEngineManager.init();
        ExtensionBoot.initializers();
        GameServer.INSTANCE = new GameServer();
        ThreadPool.execute((Runnable)AuthServerCommunication.getInstance());
        if (settings.useDeadLockDetector()) {
            final DeadLockDetector deadLockDetector = new DeadLockDetector(Duration.ofSeconds(settings.deadLockDetectorInterval()), () -> {
                if (settings.restartOnDeadLock()) {
                    Broadcast.toAllOnlinePlayers("Server has stability issues - restarting now.");
                    GameServer.LOGGER.warn("Deadlock detected restarting the server");
                    Shutdown.getInstance().startShutdown(null, 60, true);
                }
                return;
            });
            deadLockDetector.start();
        }
    }
    
    private static void configureCache() {
        CacheFactory.getInstance().initialize("config/ehcache.xml");
    }
    
    private static void configureNetworkPackets() {
        System.setProperty("async-mmocore.configurationFile", "config/async-mmocore.properties");
    }
    
    private static void configureLogger() {
        final String logConfigurationFile = System.getProperty("log4j.configurationFile");
        if (Util.isNullOrEmpty((CharSequence)logConfigurationFile)) {
            System.setProperty("log4j.configurationFile", "log4j.xml");
        }
        GameServer.LOGGER = LoggerFactory.getLogger((Class)GameServer.class);
    }
    
    private static void configureDatabase() throws Exception {
        printSection("Datasource Settings");
        System.setProperty("hikaricp.configurationFile", "config/database.properties");
        if (!DatabaseAccess.initialize()) {
            throw new Exception("Database Access could not be initialized");
        }
    }
    
    private static void logVersionInfo() {
        try {
            final InputStream versionFile = ClassLoader.getSystemResourceAsStream("version.properties");
            try {
                if (Objects.nonNull(versionFile)) {
                    final Properties versionProperties = new Properties();
                    versionProperties.load(versionFile);
                    final String version = versionProperties.getProperty("version");
                    final String updateName = versionProperties.getProperty("update");
                    GameServer.fullVersion = String.format("%s: %s-%s (%s)", updateName, version, versionProperties.getProperty("revision"), versionProperties.getProperty("buildDate"));
                    final int[] protocol = ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).acceptedProtocols();
                    printSection("Server Info Version");
                    GameServer.LOGGER.info("Update: .................. {}", (Object)updateName);
                    GameServer.LOGGER.info("Protocol: ................ {}", (Object)protocol);
                    GameServer.LOGGER.info("Build Version: ........... {}", (Object)version);
                    GameServer.LOGGER.info("Build Revision: .......... {}", (Object)versionProperties.getProperty("revision"));
                    GameServer.LOGGER.info("Build date: .............. {}", (Object)versionProperties.getProperty("buildDate"));
                    GameServer.LOGGER.info("Compiler JDK version: .... {}", (Object)versionProperties.getProperty("compilerVersion"));
                }
                if (versionFile != null) {
                    versionFile.close();
                }
            }
            catch (Throwable t) {
                if (versionFile != null) {
                    try {
                        versionFile.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (IOException e) {
            GameServer.LOGGER.warn(e.getLocalizedMessage(), (Throwable)e);
        }
    }
    
    public ConnectionHandler<GameClient> getConnectionHandler() {
        return this.connectionHandler;
    }
    
    public String getUptime() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000L;
        final long days = TimeUnit.MILLISECONDS.toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);
        final long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        return String.format("%d Days, %d Hours, %d Minutes", days, hours, TimeUnit.MILLISECONDS.toMinutes(uptime));
    }
    
    private static void printSection(final String s) {
        GameServer.LOGGER.info("{}=[ {} ]", (Object)"-".repeat(64 - s.length()), (Object)s);
    }
    
    public static GameServer getInstance() {
        return GameServer.INSTANCE;
    }
}
