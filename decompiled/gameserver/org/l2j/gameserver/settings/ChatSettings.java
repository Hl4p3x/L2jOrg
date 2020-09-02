// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.settings;

import org.l2j.commons.util.Util;
import java.util.Collections;
import org.l2j.commons.configuration.SettingsFile;
import java.time.Duration;
import org.l2j.gameserver.enums.ChatType;
import java.util.Set;
import org.l2j.commons.configuration.Settings;

public class ChatSettings implements Settings
{
    private int generalChatLevel;
    private int whisperChatLevel;
    private int shoutChatLevel;
    private int tradeChatLevel;
    private boolean l2WalkerProtectionEnabled;
    private String[] l2WalkerCommandList;
    private Set<ChatType> bannableChannels;
    private boolean logChat;
    private boolean enableChatFilter;
    private String[] filterList;
    private String filterChars;
    private String defaultGlobalChat;
    private String defaultTradeChat;
    private boolean silenceModeExclude;
    private boolean worldChatEnabled;
    private int worldChatMinLevel;
    private Duration worldChatInterval;
    
    public void load(final SettingsFile settingsFile) {
        this.generalChatLevel = settingsFile.getInteger("MinimumGeneralChatLevel", 2);
        this.whisperChatLevel = settingsFile.getInteger("MinimumWhisperChatLevel", 2);
        this.shoutChatLevel = settingsFile.getInteger("MinimumShoutChatLevel", 10);
        this.tradeChatLevel = settingsFile.getInteger("MinimumTradeChatLevel", 15);
        this.defaultGlobalChat = settingsFile.getString("GlobalChat", "ON");
        this.defaultTradeChat = settingsFile.getString("TradeChat", "ON");
        this.worldChatEnabled = settingsFile.getBoolean("WorldChatEnabled", true);
        this.worldChatMinLevel = settingsFile.getInteger("WorldChatMinLevel", 80);
        this.worldChatInterval = settingsFile.getDuration("WorldChatInterval", 20L);
        this.silenceModeExclude = settingsFile.getBoolean("SilenceModeExclude", false);
        this.logChat = settingsFile.getBoolean("LogChat", false);
        this.enableChatFilter = settingsFile.getBoolean("EnableChatFilter", false);
        this.filterList = settingsFile.getStringArray("FilterList");
        this.filterChars = settingsFile.getString("ChatFilterChars", "");
        this.bannableChannels = (Set<ChatType>)settingsFile.getEnumSet("BanChatChannels", (Class)ChatType.class, (Set)Collections.emptySet());
        this.l2WalkerProtectionEnabled = settingsFile.getBoolean("L2WalkerProtection", false);
        this.l2WalkerCommandList = (this.l2WalkerProtectionEnabled ? settingsFile.getStringArray("L2WalkerCommands") : Util.STRING_ARRAY_EMPTY);
    }
    
    public int generalChatLevel() {
        return this.generalChatLevel;
    }
    
    public int whisperChatLevel() {
        return this.whisperChatLevel;
    }
    
    public int shoutChatLevel() {
        return this.shoutChatLevel;
    }
    
    public int tradeChatLevel() {
        return this.tradeChatLevel;
    }
    
    public boolean l2WalkerProtectionEnabled() {
        return this.l2WalkerProtectionEnabled;
    }
    
    public Set<ChatType> bannableChannels() {
        return this.bannableChannels;
    }
    
    public boolean logChat() {
        return this.logChat;
    }
    
    public boolean enableChatFilter() {
        return this.enableChatFilter;
    }
    
    public boolean isL2WalkerCommand(final String text) {
        for (final String command : this.l2WalkerCommandList) {
            if (text.startsWith(command)) {
                return true;
            }
        }
        return false;
    }
    
    public String filterText(final String text) {
        String filteredText = text;
        for (final String pattern : this.filterList) {
            filteredText = filteredText.replaceAll(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pattern), this.filterChars);
        }
        return filteredText;
    }
    
    public String defaultGlobalChat() {
        return this.defaultGlobalChat;
    }
    
    public String defaultTradeChat() {
        return this.defaultTradeChat;
    }
    
    public boolean silenceModeExclude() {
        return this.silenceModeExclude;
    }
    
    public boolean worldChatEnabled() {
        return this.worldChatEnabled;
    }
    
    public int worldChatMinLevel() {
        return this.worldChatMinLevel;
    }
    
    public Duration worldChatInterval() {
        return this.worldChatInterval;
    }
}
