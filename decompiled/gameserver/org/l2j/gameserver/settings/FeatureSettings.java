// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.SettingsFile;
import org.l2j.commons.configuration.Settings;

public class FeatureSettings implements Settings
{
    private int[] siegeHours;
    
    public void load(final SettingsFile settingsFile) {
        this.siegeHours = settingsFile.getIntegerArray("SiegeHourList", ",");
    }
    
    public int[] siegeHours() {
        return this.siegeHours;
    }
}
