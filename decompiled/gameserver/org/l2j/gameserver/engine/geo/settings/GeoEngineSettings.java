// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo.settings;

import org.l2j.commons.configuration.SettingsFile;
import org.l2j.gameserver.engine.geo.SyncMode;
import org.l2j.commons.configuration.Settings;

public class GeoEngineSettings implements Settings
{
    private SyncMode syncMode;
    private boolean enabledPathFinding;
    
    public void load(final SettingsFile settingsFile) {
        this.syncMode = (SyncMode)settingsFile.getEnum("SyncMode", (Class)SyncMode.class, (Enum)SyncMode.Z_ONLY);
        this.enabledPathFinding = settingsFile.getBoolean("EnablePathFinding", true);
    }
    
    public boolean isEnabledPathFinding() {
        return this.enabledPathFinding;
    }
    
    public void setEnabledPathFinding(final boolean enabledPathFinding) {
        this.enabledPathFinding = enabledPathFinding;
    }
    
    public void setSyncMode(final SyncMode syncMode) {
        this.syncMode = syncMode;
    }
    
    public boolean isSyncMode(final SyncMode mode) {
        return this.syncMode == mode;
    }
}
