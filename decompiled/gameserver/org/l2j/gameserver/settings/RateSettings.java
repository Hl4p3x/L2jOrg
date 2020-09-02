// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.SettingsFile;
import org.l2j.commons.configuration.Settings;

public class RateSettings implements Settings
{
    private float xp;
    
    public void load(final SettingsFile settingsFile) {
        this.xp = settingsFile.getFloat("RateXp", 1.0f);
    }
    
    public float xp() {
        return this.xp;
    }
    
    public void setXp(final float xp) {
        this.xp = xp;
    }
}
