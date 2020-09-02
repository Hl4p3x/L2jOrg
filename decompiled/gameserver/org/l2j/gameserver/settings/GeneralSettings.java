// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.settings;

import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import org.l2j.commons.configuration.SettingsFile;
import io.github.joealisson.primitive.IntSet;
import java.time.Duration;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.l2j.commons.configuration.Settings;

public class GeneralSettings implements Settings
{
    private int banChatAdenaAdsReportCount;
    private boolean auditGM;
    private boolean saveDroppedItems;
    private int autoDestroyItemTime;
    private int autoDestroyHerbTime;
    private boolean allowMail;
    private boolean logItems;
    private boolean smallLogItems;
    private boolean loadCustomBuyList;
    private boolean loadCustomMultisell;
    private boolean cachePlayersName;
    private IllegalActionPunishmentType defaultPunishment;
    private boolean disableChatInJail;
    private int defaultAccessLevel;
    private int autoSavePlayerTime;
    private Duration saveDroppedItemInterval;
    private boolean clearDroppedItems;
    private boolean destroyPlayerDroppedItem;
    private boolean destroyEquipableItem;
    private IntSet protectedItems;
    private boolean clearDroppedItemsAfterLoad;
    
    public void load(final SettingsFile settingsFile) {
        this.banChatAdenaAdsReportCount = settingsFile.getInteger("BanChatAdenaADSReportCount", 10);
        this.auditGM = settingsFile.getBoolean("AuditGM", false);
        this.logItems = settingsFile.getBoolean("LogItems", false);
        this.smallLogItems = settingsFile.getBoolean("LogItemsSmallLog", true);
        this.saveDroppedItems = settingsFile.getBoolean("SaveDroppedItem", false);
        this.autoDestroyItemTime = settingsFile.getInteger("AutoDestroyDroppedItemAfter", 600) * 1000;
        this.autoDestroyHerbTime = settingsFile.getInteger("AutoDestroyHerbTime", 120) * 1000;
        this.saveDroppedItemInterval = settingsFile.getDuration("SaveDroppedItemInterval", (TemporalUnit)ChronoUnit.MINUTES, 60L);
        this.clearDroppedItems = settingsFile.getBoolean("ClearDroppedItemTable", false);
        this.destroyPlayerDroppedItem = settingsFile.getBoolean("DestroyPlayerDroppedItem", false);
        this.destroyEquipableItem = settingsFile.getBoolean("DestroyEquipableItem", false);
        this.protectedItems = settingsFile.getIntSet("ListOfProtectedItems", ",");
        this.clearDroppedItemsAfterLoad = settingsFile.getBoolean("EmptyDroppedItemTableAfterLoad", false);
        this.allowMail = settingsFile.getBoolean("AllowMail", true);
        this.loadCustomBuyList = settingsFile.getBoolean("CustomBuyListLoad", false);
        this.loadCustomMultisell = settingsFile.getBoolean("CustomMultisellLoad", false);
        this.cachePlayersName = settingsFile.getBoolean("CacheCharNames", true);
        this.defaultPunishment = (IllegalActionPunishmentType)settingsFile.getEnum("DefaultPunish", (Class)IllegalActionPunishmentType.class, (Enum)IllegalActionPunishmentType.KICK);
        this.disableChatInJail = settingsFile.getBoolean("JailDisableChat", true);
        this.defaultAccessLevel = settingsFile.getInteger("DefaultAccessLevel", 0);
        this.autoSavePlayerTime = settingsFile.getInteger("PlayerDataStoreInterval", 20);
    }
    
    public int banChatAdenaAdsReportCount() {
        return this.banChatAdenaAdsReportCount;
    }
    
    public boolean auditGM() {
        return this.auditGM;
    }
    
    public boolean saveDroppedItems() {
        return this.saveDroppedItems;
    }
    
    public Duration saveDroppedItemInterval() {
        return this.saveDroppedItemInterval;
    }
    
    public boolean clearDroppedItems() {
        return this.clearDroppedItems;
    }
    
    public boolean destroyPlayerDroppedItem() {
        return this.destroyPlayerDroppedItem;
    }
    
    public boolean destroyEquipableItem() {
        return this.destroyEquipableItem;
    }
    
    public boolean isProtectedItem(final int itemId) {
        return this.protectedItems.contains(itemId);
    }
    
    public boolean clearDroppedItemsAfterLoad() {
        return this.clearDroppedItemsAfterLoad;
    }
    
    public int autoDestroyItemTime() {
        return this.autoDestroyItemTime;
    }
    
    public int autoDestroyHerbTime() {
        return this.autoDestroyHerbTime;
    }
    
    public boolean allowMail() {
        return this.allowMail;
    }
    
    public boolean logItems() {
        return this.logItems;
    }
    
    public boolean smallLogItems() {
        return this.smallLogItems;
    }
    
    public boolean loadCustomBuyList() {
        return this.loadCustomBuyList;
    }
    
    public boolean loadCustomMultisell() {
        return this.loadCustomMultisell;
    }
    
    public boolean cachePlayersName() {
        return this.cachePlayersName;
    }
    
    public IllegalActionPunishmentType defaultPunishment() {
        return this.defaultPunishment;
    }
    
    public boolean disableChatInJail() {
        return this.disableChatInJail;
    }
    
    public int defaultAccessLevel() {
        return this.defaultAccessLevel;
    }
    
    public void setDefaultAccessLevel(final int accessLevel) {
        this.defaultAccessLevel = accessLevel;
    }
    
    public int autoSavePlayerTime() {
        return this.autoSavePlayerTime;
    }
}
