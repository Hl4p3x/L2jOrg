// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.Iterator;
import org.l2j.gameserver.model.Location;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.commons.util.PropertiesParser;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.HashMap;
import org.l2j.gameserver.model.TowerSpawn;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public final class SiegeManager
{
    private static final Logger LOGGER;
    private final Map<Integer, List<TowerSpawn>> _controlTowers;
    private final Map<Integer, List<TowerSpawn>> _flameTowers;
    private int _attackerMaxClans;
    private int _attackerRespawnDelay;
    private int _defenderMaxClans;
    private int _flagMaxCount;
    private int _siegeClanMinLevel;
    private int _siegeLength;
    private int _bloodAllianceReward;
    
    private SiegeManager() {
        this._controlTowers = new HashMap<Integer, List<TowerSpawn>>();
        this._flameTowers = new HashMap<Integer, List<TowerSpawn>>();
        this._attackerMaxClans = 500;
        this._attackerRespawnDelay = 0;
        this._defenderMaxClans = 500;
        this._flagMaxCount = 1;
        this._siegeClanMinLevel = 5;
        this._siegeLength = 120;
        this._bloodAllianceReward = 0;
        this.load();
    }
    
    public final void addSiegeSkills(final Player player) {
        SkillEngine.getInstance().addSiegeSkills(player);
    }
    
    public final boolean checkIsRegistered(final Clan clan, final int castleid) {
        if (clan == null) {
            return false;
        }
        if (clan.getCastleId() > 0) {
            return true;
        }
        boolean register = false;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT clan_id FROM siege_clans where clan_id=? and castle_id=?");
                try {
                    statement.setInt(1, clan.getId());
                    statement.setInt(2, castleid);
                    final ResultSet rs = statement.executeQuery();
                    try {
                        if (rs.next()) {
                            register = true;
                        }
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            SiegeManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), e.getMessage()), (Throwable)e);
        }
        return register;
    }
    
    public final void removeSiegeSkills(final Player player) {
        SkillEngine.getInstance().removeSiegeSkills(player);
    }
    
    private void load() {
        final PropertiesParser siegeSettings = new PropertiesParser("./config/Siege.ini");
        this._attackerMaxClans = siegeSettings.getInt("AttackerMaxClans", 500);
        this._attackerRespawnDelay = siegeSettings.getInt("AttackerRespawn", 0);
        this._defenderMaxClans = siegeSettings.getInt("DefenderMaxClans", 500);
        this._flagMaxCount = siegeSettings.getInt("MaxFlags", 1);
        this._siegeClanMinLevel = siegeSettings.getInt("SiegeClanMinLevel", 5);
        this._siegeLength = siegeSettings.getInt("SiegeLength", 120);
        this._bloodAllianceReward = siegeSettings.getInt("BloodAllianceReward", 1);
        for (final Castle castle : CastleManager.getInstance().getCastles()) {
            final List<TowerSpawn> controlTowers = new ArrayList<TowerSpawn>();
            for (int i = 1; i < 255; ++i) {
                final String settingsKeyName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, castle.getName(), i);
                if (!siegeSettings.containskey(settingsKeyName)) {
                    break;
                }
                final StringTokenizer st = new StringTokenizer(siegeSettings.getString(settingsKeyName, ""), ",");
                try {
                    final int x = Integer.parseInt(st.nextToken());
                    final int y = Integer.parseInt(st.nextToken());
                    final int z = Integer.parseInt(st.nextToken());
                    final int npcId = Integer.parseInt(st.nextToken());
                    controlTowers.add(new TowerSpawn(npcId, new Location(x, y, z)));
                }
                catch (Exception e) {
                    SiegeManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, castle.getName()));
                }
            }
            final List<TowerSpawn> flameTowers = new ArrayList<TowerSpawn>();
            for (int j = 1; j < 255; ++j) {
                final String settingsKeyName2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, castle.getName(), j);
                if (!siegeSettings.containskey(settingsKeyName2)) {
                    break;
                }
                final StringTokenizer st2 = new StringTokenizer(siegeSettings.getString(settingsKeyName2, ""), ",");
                try {
                    final int x2 = Integer.parseInt(st2.nextToken());
                    final int y2 = Integer.parseInt(st2.nextToken());
                    final int z2 = Integer.parseInt(st2.nextToken());
                    final int npcId2 = Integer.parseInt(st2.nextToken());
                    final List<Integer> zoneList = new ArrayList<Integer>();
                    while (st2.hasMoreTokens()) {
                        zoneList.add(Integer.parseInt(st2.nextToken()));
                    }
                    flameTowers.add(new TowerSpawn(npcId2, new Location(x2, y2, z2), zoneList));
                }
                catch (Exception e2) {
                    SiegeManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, castle.getName()));
                }
            }
            this._controlTowers.put(castle.getId(), controlTowers);
            this._flameTowers.put(castle.getId(), flameTowers);
            if (castle.getOwnerId() != 0) {
                this.loadTrapUpgrade(castle.getId());
            }
        }
    }
    
    public final List<TowerSpawn> getControlTowers(final int castleId) {
        return this._controlTowers.get(castleId);
    }
    
    public final List<TowerSpawn> getFlameTowers(final int castleId) {
        return this._flameTowers.get(castleId);
    }
    
    public final int getAttackerMaxClans() {
        return this._attackerMaxClans;
    }
    
    public final int getAttackerRespawnDelay() {
        return this._attackerRespawnDelay;
    }
    
    public final int getDefenderMaxClans() {
        return this._defenderMaxClans;
    }
    
    public final int getFlagMaxCount() {
        return this._flagMaxCount;
    }
    
    public final Siege getSiege(final ILocational loc) {
        return CastleManager.getInstance().getSiegeOnLocation(loc);
    }
    
    public final int getSiegeClanMinLevel() {
        return this._siegeClanMinLevel;
    }
    
    public final int getSiegeLength() {
        return this._siegeLength;
    }
    
    public final int getBloodAllianceReward() {
        return this._bloodAllianceReward;
    }
    
    public final List<Siege> getSieges() {
        final List<Siege> sieges = new LinkedList<Siege>();
        for (final Castle castle : CastleManager.getInstance().getCastles()) {
            sieges.add(castle.getSiege());
        }
        return sieges;
    }
    
    private void loadTrapUpgrade(final int castleId) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT * FROM castle_trap_upgrade WHERE castle_id=?");
                try {
                    ps.setInt(1, castleId);
                    final ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            this._flameTowers.get(castleId).get(rs.getInt("towerIndex")).setUpgradeLevel(rs.getInt("level"));
                        }
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t2) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            SiegeManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public static SiegeManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SiegeManager.class);
    }
    
    private static class Singleton
    {
        private static final SiegeManager INSTANCE;
        
        static {
            INSTANCE = new SiegeManager();
        }
    }
}
