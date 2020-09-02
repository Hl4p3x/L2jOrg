// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.SettingsFile;
import org.l2j.commons.configuration.Settings;

public class AttendanceSettings implements Settings
{
    private boolean enabled;
    private boolean vipOnly;
    private int delay;
    private boolean popUpWindow;
    
    public void load(final SettingsFile settingsFile) {
        this.enabled = settingsFile.getBoolean("EnableAttendanceRewards", false);
        this.vipOnly = settingsFile.getBoolean("VipOnlyAttendanceRewards", false);
        this.delay = settingsFile.getInteger("AttendanceRewardDelay", 30);
        this.popUpWindow = settingsFile.getBoolean("AttendancePopupWindow", false);
    }
    
    public boolean enabled() {
        return this.enabled;
    }
    
    public boolean vipOnly() {
        return this.vipOnly;
    }
    
    public int delay() {
        return this.delay;
    }
    
    public boolean popUpWindow() {
        return this.popUpWindow;
    }
}
