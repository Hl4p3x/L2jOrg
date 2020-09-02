// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.data.xml.impl.AdminData;

public class AdminCommandAccessRight
{
    private final String _adminCommand;
    private final int _accessLevel;
    private final boolean _requireConfirm;
    
    public AdminCommandAccessRight(final StatsSet set) {
        this._adminCommand = set.getString("command");
        this._requireConfirm = set.getBoolean("confirmDlg", false);
        this._accessLevel = set.getInt("accessLevel", 7);
    }
    
    public AdminCommandAccessRight(final String command, final boolean confirm, final int level) {
        this._adminCommand = command;
        this._requireConfirm = confirm;
        this._accessLevel = level;
    }
    
    public String getAdminCommand() {
        return this._adminCommand;
    }
    
    public boolean hasAccess(final AccessLevel characterAccessLevel) {
        final AccessLevel accessLevel = AdminData.getInstance().getAccessLevel(this._accessLevel);
        return accessLevel != null && (accessLevel.getLevel() == characterAccessLevel.getLevel() || characterAccessLevel.hasChildAccess(accessLevel));
    }
    
    public boolean getRequireConfirm() {
        return this._requireConfirm;
    }
}
