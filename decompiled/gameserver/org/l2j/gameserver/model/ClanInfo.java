// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public class ClanInfo
{
    private final Clan _clan;
    private final int _total;
    private final int _online;
    
    public ClanInfo(final Clan clan) {
        this._clan = clan;
        this._total = clan.getMembersCount();
        this._online = clan.getOnlineMembersCount();
    }
    
    public Clan getClan() {
        return this._clan;
    }
    
    public int getTotal() {
        return this._total;
    }
    
    public int getOnline() {
        return this._online;
    }
}
