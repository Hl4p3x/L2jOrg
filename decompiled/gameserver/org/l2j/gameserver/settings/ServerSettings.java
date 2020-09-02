// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.settings;

import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;
import org.l2j.gameserver.ServerType;
import org.l2j.commons.configuration.SettingsFile;
import java.util.function.Predicate;
import java.nio.file.Path;
import org.l2j.commons.configuration.Settings;

public class ServerSettings implements Settings
{
    private int serverId;
    private boolean acceptAlternativeId;
    private String authServerAddress;
    private short authServerPort;
    private byte ageLimit;
    private boolean showBrackets;
    private boolean isPvP;
    private int type;
    private short port;
    private int maximumOnlineUsers;
    private Path dataPackDirectory;
    private int scheduledPoolSize;
    private int threadPoolSize;
    private int[] acceptedProtocols;
    private boolean scheduleRestart;
    private boolean useDeadLockDetector;
    private int deadLockDetectorInterval;
    private boolean restartOnDeadLock;
    private int maxPlayers;
    private Predicate<String> playerNameTemplate;
    
    public void load(final SettingsFile settingsFile) {
        this.serverId = settingsFile.getInteger("RequestServerID", 1);
        this.acceptAlternativeId = settingsFile.getBoolean("AcceptAlternateID", true);
        this.authServerAddress = settingsFile.getString("LoginHost", "127.0.0.1");
        this.authServerPort = settingsFile.getShort("LoginPort", (short)9014);
        this.port = settingsFile.getShort("GameserverPort", (short)7777);
        this.type = ServerType.maskOf(settingsFile.getStringArray("ServerListType"));
        this.maximumOnlineUsers = Math.max(1, settingsFile.getInteger("MaximumOnlineUsers", 20));
        this.ageLimit = settingsFile.getByte("ServerListAge", (byte)0);
        this.showBrackets = settingsFile.getBoolean("ServerListBrackets", false);
        this.isPvP = settingsFile.getBoolean("PvPServer", false);
        this.dataPackDirectory = Path.of(settingsFile.getString("DatapackRoot", "."), new String[0]);
        final int processors = Runtime.getRuntime().availableProcessors();
        this.scheduledPoolSize = this.determinePoolSize(settingsFile, "ScheduledThreadPoolSize", processors);
        this.threadPoolSize = this.determinePoolSize(settingsFile, "ThreadPoolSize", processors);
        this.acceptedProtocols = settingsFile.getIntegerArray("AllowedProtocolRevisions", ";");
        this.scheduleRestart = settingsFile.getBoolean("ServerRestartScheduleEnabled", false);
        this.useDeadLockDetector = settingsFile.getBoolean("DeadLockDetector", true);
        this.deadLockDetectorInterval = settingsFile.getInteger("DeadLockCheckInterval", 1800);
        this.restartOnDeadLock = settingsFile.getBoolean("RestartOnDeadlock", false);
        this.determinePlayerNamePattern(settingsFile);
    }
    
    private void determinePlayerNamePattern(final SettingsFile settingsFile) {
        try {
            this.playerNameTemplate = Pattern.compile(settingsFile.getString("CnameTemplate", ".*")).asMatchPredicate();
        }
        catch (PatternSyntaxException e) {
            this.playerNameTemplate = Pattern.compile(".*").asMatchPredicate();
        }
    }
    
    private int determinePoolSize(final SettingsFile settingsFile, final String property, final int processors) {
        final int size = settingsFile.getInteger(property, processors);
        if (size < 2) {
            return processors;
        }
        return size;
    }
    
    public int serverId() {
        return this.serverId;
    }
    
    public void setServerId(final int serverId) {
        this.serverId = serverId;
    }
    
    public short port() {
        return this.port;
    }
    
    public String authServerAddress() {
        return this.authServerAddress;
    }
    
    public int authServerPort() {
        return this.authServerPort;
    }
    
    public byte ageLimit() {
        return this.ageLimit;
    }
    
    public boolean isShowingBrackets() {
        return this.showBrackets;
    }
    
    public boolean isPvP() {
        return this.isPvP;
    }
    
    public int type() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int maximumOnlineUsers() {
        return this.maximumOnlineUsers;
    }
    
    public boolean acceptAlternativeId() {
        return this.acceptAlternativeId;
    }
    
    public Path dataPackDirectory() {
        return this.dataPackDirectory;
    }
    
    public int scheduledPoolSize() {
        return this.scheduledPoolSize;
    }
    
    public int threadPoolSize() {
        return this.threadPoolSize;
    }
    
    public int[] acceptedProtocols() {
        return this.acceptedProtocols;
    }
    
    public boolean scheduleRestart() {
        return this.scheduleRestart;
    }
    
    public boolean useDeadLockDetector() {
        return this.useDeadLockDetector;
    }
    
    public int deadLockDetectorInterval() {
        return this.deadLockDetectorInterval;
    }
    
    public boolean restartOnDeadLock() {
        return this.restartOnDeadLock;
    }
    
    public boolean acceptPlayerName(final String name) {
        return this.playerNameTemplate.test(name);
    }
}
