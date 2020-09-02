// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.function.BiFunction;
import java.util.Collections;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.model.WorldObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;

public final class PlayerEventHolder
{
    private final Player _player;
    private final String _name;
    private final String _title;
    private final int _clanId;
    private final Location _loc;
    private final int _pvpKills;
    private final int _pkKills;
    private final int _reputation;
    private final Map<Player, Integer> _kills;
    private boolean _sitForced;
    
    public PlayerEventHolder(final Player player) {
        this(player, false);
    }
    
    public PlayerEventHolder(final Player player, final boolean sitForced) {
        this._kills = new ConcurrentHashMap<Player, Integer>();
        this._player = player;
        this._name = player.getName();
        this._title = player.getTitle();
        this._clanId = player.getClanId();
        this._loc = new Location(player);
        this._pvpKills = player.getPvpKills();
        this._pkKills = player.getPkKills();
        this._reputation = player.getReputation();
        this._sitForced = sitForced;
    }
    
    public void restorePlayerStats() {
        this._player.setName(this._name);
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).cachePlayersName()) {
            PlayerNameTable.getInstance().addName(this._player);
        }
        this._player.setTitle(this._title);
        this._player.setClan(ClanTable.getInstance().getClan(this._clanId));
        this._player.teleToLocation(this._loc, true);
        this._player.setPvpKills(this._pvpKills);
        this._player.setPkKills(this._pkKills);
        this._player.setReputation(this._reputation);
    }
    
    public boolean isSitForced() {
        return this._sitForced;
    }
    
    public void setSitForced(final boolean sitForced) {
        this._sitForced = sitForced;
    }
    
    public Map<Player, Integer> getKills() {
        return Collections.unmodifiableMap((Map<? extends Player, ? extends Integer>)this._kills);
    }
    
    public void addKill(final Player player) {
        this._kills.merge(player, 1, Integer::sum);
    }
}
