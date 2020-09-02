// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.sql.impl;

import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.l2j.gameserver.data.database.data.PlayerData;
import java.util.Iterator;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.commons.util.Util;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import io.github.joealisson.primitive.CHashIntIntMap;
import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntIntMap;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public class PlayerNameTable
{
    private static final Logger LOGGER;
    private final IntMap<String> playerData;
    private final IntIntMap accessLevels;
    
    private PlayerNameTable() {
        this.playerData = (IntMap<String>)new CHashIntMap();
        this.accessLevels = (IntIntMap)new CHashIntIntMap();
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).cachePlayersName()) {
            this.loadAll();
        }
    }
    
    public final void addName(final Player player) {
        if (Objects.nonNull(player)) {
            this.addName(player.getObjectId(), player.getName());
            this.accessLevels.put(player.getObjectId(), player.getAccessLevel().getLevel());
        }
    }
    
    private void addName(final int objectId, final String name) {
        if (Objects.nonNull(name) && !name.equals(this.playerData.get(objectId))) {
            this.playerData.put(objectId, (Object)name);
        }
    }
    
    public final void removeName(final int objId) {
        this.playerData.remove(objId);
        this.accessLevels.remove(objId);
    }
    
    public final int getIdByName(final String name) {
        if (Util.isNullOrEmpty((CharSequence)name)) {
            return -1;
        }
        for (final IntMap.Entry<String> entry : this.playerData.entrySet()) {
            if (((String)entry.getValue()).equalsIgnoreCase(name)) {
                return entry.getKey();
            }
        }
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).cachePlayersName()) {
            return -1;
        }
        final PlayerData characterData = ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findIdAndAccessLevelByName(name);
        if (Objects.nonNull(characterData)) {
            this.playerData.put(characterData.getCharId(), (Object)name);
            this.accessLevels.put(characterData.getCharId(), characterData.getAccessLevel());
            return characterData.getCharId();
        }
        return -1;
    }
    
    public final String getNameById(final int id) {
        if (id <= 0) {
            return null;
        }
        final String name = (String)this.playerData.get(id);
        if (Objects.nonNull(name)) {
            return name;
        }
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).cachePlayersName()) {
            return null;
        }
        final PlayerData data = ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findNameAndAccessLevelById(id);
        if (Objects.nonNull(data)) {
            this.playerData.put(id, (Object)data.getName());
            this.accessLevels.put(id, data.getAccessLevel());
        }
        return null;
    }
    
    public final int getAccessLevelById(final int objectId) {
        return Util.zeroIfNullOrElse((Object)this.getNameById(objectId), name -> this.accessLevels.get(objectId));
    }
    
    public synchronized boolean doesCharNameExist(final String name) {
        return ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).existsByName(name);
    }
    
    public int getAccountCharacterCount(final String account) {
        return ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).playerCountByAccount(account);
    }
    
    public int getClassIdById(final int objectId) {
        return ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findClassIdById(objectId);
    }
    
    private void loadAll() {
        int id;
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).withPlayersDataDo(resultSet -> {
            try {
                while (resultSet.next()) {
                    id = resultSet.getInt("charId");
                    this.playerData.put(id, (Object)resultSet.getString("char_name"));
                    this.accessLevels.put(id, resultSet.getInt("accesslevel"));
                }
            }
            catch (SQLException e) {
                PlayerNameTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), e.getMessage()), (Throwable)e);
            }
            return;
        });
        PlayerNameTable.LOGGER.info("Loaded {} player names.", (Object)this.playerData.size());
    }
    
    public static PlayerNameTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PlayerNameTable.class);
    }
    
    private static class Singleton
    {
        private static final PlayerNameTable INSTANCE;
        
        static {
            INSTANCE = new PlayerNameTable();
        }
    }
}
