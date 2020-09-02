// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.SettingsFile;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.configuration.Settings;

public class CharacterSettings implements Settings
{
    private int partyRange;
    private IntSet autoLootItems;
    private boolean autoLootRaid;
    private int raidLootPrivilegeTime;
    private boolean autoLoot;
    private boolean initialEquipEvent;
    private boolean delevel;
    private float weightLimitMultiplier;
    private boolean removeCastleCirclets;
    private boolean restoreSummonOnReconnect;
    private int minimumEnchantAnnounceWeapon;
    private int minimumEnchantAnnounceArmor;
    
    public void load(final SettingsFile settingsFile) {
        this.partyRange = settingsFile.getInteger("AltPartyRange", 1600);
        this.autoLoot = settingsFile.getBoolean("AutoLoot", false);
        this.autoLootItems = settingsFile.getIntSet("AutoLootItemIds", ",");
        this.autoLootRaid = settingsFile.getBoolean("AutoLootRaids", false);
        this.raidLootPrivilegeTime = settingsFile.getInteger("RaidLootRightsInterval", 900) * 1000;
        this.initialEquipEvent = settingsFile.getBoolean("InitialEquipmentEvent", false);
        this.delevel = settingsFile.getBoolean("Delevel", true);
        this.weightLimitMultiplier = settingsFile.getFloat("AltWeightLimit", 1.0f);
        this.removeCastleCirclets = settingsFile.getBoolean("RemoveCastleCirclets", true);
        this.restoreSummonOnReconnect = settingsFile.getBoolean("RestoreSummonOnReconnect", true);
        this.minimumEnchantAnnounceWeapon = settingsFile.getInteger("MinimumEnchantAnnounceWeapon", 7);
        this.minimumEnchantAnnounceArmor = settingsFile.getInteger("MinimumEnchantAnnounceArmor", 6);
    }
    
    public int partyRange() {
        return this.partyRange;
    }
    
    public boolean autoLoot() {
        return this.autoLoot;
    }
    
    public boolean isAutoLoot(final int item) {
        return this.autoLootItems.contains(item);
    }
    
    public boolean autoLootRaid() {
        return this.autoLootRaid;
    }
    
    public int raidLootPrivilegeTime() {
        return this.raidLootPrivilegeTime;
    }
    
    public boolean initialEquipEvent() {
        return this.initialEquipEvent;
    }
    
    public boolean delevel() {
        return this.delevel;
    }
    
    public float weightLimitMultiplier() {
        return this.weightLimitMultiplier;
    }
    
    public boolean removeCastleCirclets() {
        return this.removeCastleCirclets;
    }
    
    public boolean restoreSummonOnReconnect() {
        return this.restoreSummonOnReconnect;
    }
    
    public int minimumEnchantAnnounceWeapon() {
        return this.minimumEnchantAnnounceWeapon;
    }
    
    public int minimumEnchantAnnounceArmor() {
        return this.minimumEnchantAnnounceArmor;
    }
}
