// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import java.util.Objects;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.model.StatsSet;
import org.w3c.dom.Document;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.AdminCommandAccessRight;
import org.l2j.gameserver.model.AccessLevel;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class AdminData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, AccessLevel> _accessLevels;
    private final Map<String, AdminCommandAccessRight> _adminCommandAccessRights;
    private final Map<Player, Boolean> _gmList;
    private int _highestLevel;
    
    private AdminData() {
        this._accessLevels = new HashMap<Integer, AccessLevel>();
        this._adminCommandAccessRights = new HashMap<String, AdminCommandAccessRight>();
        this._gmList = new ConcurrentHashMap<Player, Boolean>();
        this._highestLevel = 0;
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return Path.of("config/xsd/AccessLevels.xsd", new String[0]);
    }
    
    public synchronized void load() {
        this._accessLevels.clear();
        this._adminCommandAccessRights.clear();
        this.parseFile(new File("config/AccessLevels.xml"));
        AdminData.LOGGER.info("Loaded: {} Access Levels.", (Object)this._accessLevels.size());
        AdminData.LOGGER.info("Loaded: {} Access Commands.", (Object)this._adminCommandAccessRights.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("access".equalsIgnoreCase(d.getNodeName())) {
                        final StatsSet set = new StatsSet();
                        final NamedNodeMap attrs = d.getAttributes();
                        for (int i = 0; i < attrs.getLength(); ++i) {
                            final Node attr = attrs.item(i);
                            set.set(attr.getNodeName(), attr.getNodeValue());
                        }
                        final AccessLevel level = new AccessLevel(set);
                        if (level.getLevel() > this._highestLevel) {
                            this._highestLevel = level.getLevel();
                        }
                        this._accessLevels.put(level.getLevel(), level);
                    }
                    else if ("admin".equalsIgnoreCase(d.getNodeName())) {
                        final StatsSet set = new StatsSet();
                        final NamedNodeMap attrs = d.getAttributes();
                        for (int i = 0; i < attrs.getLength(); ++i) {
                            final Node attr = attrs.item(i);
                            set.set(attr.getNodeName(), attr.getNodeValue());
                        }
                        final AdminCommandAccessRight command = new AdminCommandAccessRight(set);
                        this._adminCommandAccessRights.put(command.getAdminCommand(), command);
                    }
                }
            }
        }
    }
    
    public AccessLevel getAccessLevel(final int accessLevelNum) {
        if (accessLevelNum < 0) {
            return this._accessLevels.get(-1);
        }
        return this._accessLevels.get(accessLevelNum);
    }
    
    public AccessLevel getAccessLevelOrDefault(final int level) {
        AccessLevel accessLevel = this.getAccessLevel(level);
        if (Objects.isNull(accessLevel)) {
            AdminData.LOGGER.warn("Can't find access level {}", (Object)level);
            accessLevel = getInstance().getAccessLevel(0);
        }
        final GeneralSettings generalSettings = (GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class);
        final int defaultAccessLevel = generalSettings.defaultAccessLevel();
        if (accessLevel.getLevel() == 0 && defaultAccessLevel > 0) {
            accessLevel = getInstance().getAccessLevel(defaultAccessLevel);
            if (Objects.isNull(accessLevel)) {
                AdminData.LOGGER.warn("Config's default access level ({}) is not defined, defaulting to 0!", (Object)defaultAccessLevel);
                accessLevel = getInstance().getAccessLevel(0);
                generalSettings.setDefaultAccessLevel(0);
            }
        }
        return accessLevel;
    }
    
    public AccessLevel getMasterAccessLevel() {
        return this._accessLevels.get(this._highestLevel);
    }
    
    public boolean hasAccessLevel(final int id) {
        return this._accessLevels.containsKey(id);
    }
    
    public boolean hasAccess(final String adminCommand, final AccessLevel accessLevel) {
        AdminCommandAccessRight acar = this._adminCommandAccessRights.get(adminCommand);
        if (Objects.isNull(acar)) {
            if (accessLevel.getLevel() < this._highestLevel) {
                AdminData.LOGGER.info("No rights defined for admin command {}!", (Object)adminCommand);
                return false;
            }
            acar = new AdminCommandAccessRight(adminCommand, true, accessLevel.getLevel());
            this._adminCommandAccessRights.put(adminCommand, acar);
            AdminData.LOGGER.info("No rights defined for admin command '{}' auto setting accesslevel: '{}'!", (Object)adminCommand, (Object)accessLevel.getLevel());
        }
        return acar.hasAccess(accessLevel);
    }
    
    public boolean requireConfirm(final String command) {
        final AdminCommandAccessRight acar = this._adminCommandAccessRights.get(command);
        if (acar == null) {
            AdminData.LOGGER.info("No rights defined for admin command '{}'.", (Object)command);
            return false;
        }
        return acar.getRequireConfirm();
    }
    
    public List<Player> getAllGms(final boolean includeHidden) {
        final List<Player> tmpGmList = new ArrayList<Player>();
        for (final Map.Entry<Player, Boolean> entry : this._gmList.entrySet()) {
            if (includeHidden || !entry.getValue()) {
                tmpGmList.add(entry.getKey());
            }
        }
        return tmpGmList;
    }
    
    public List<String> getAllGmNames(final boolean includeHidden) {
        final List<String> tmpGmList = new ArrayList<String>();
        for (final Map.Entry<Player, Boolean> entry : this._gmList.entrySet()) {
            if (!entry.getValue()) {
                tmpGmList.add(entry.getKey().getName());
            }
            else {
                if (!includeHidden) {
                    continue;
                }
                tmpGmList.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, entry.getKey().getName()));
            }
        }
        return tmpGmList;
    }
    
    public void addGm(final Player player, final boolean hidden) {
        this._gmList.put(player, hidden);
    }
    
    public void deleteGm(final Player player) {
        this._gmList.remove(player);
    }
    
    public void showGm(final Player player) {
        this._gmList.putIfAbsent(player, false);
    }
    
    public void hideGm(final Player player) {
        this._gmList.putIfAbsent(player, true);
    }
    
    public boolean isGmOnline(final boolean includeHidden) {
        for (final Map.Entry<Player, Boolean> entry : this._gmList.entrySet()) {
            if (includeHidden || !entry.getValue()) {
                return true;
            }
        }
        return false;
    }
    
    public void sendListToPlayer(final Player player) {
        if (this.isGmOnline(player.isGM())) {
            player.sendPacket(SystemMessageId.GM_LIST);
            for (final String name : this.getAllGmNames(player.isGM())) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.GM_C1);
                sm.addString(name);
                player.sendPacket(sm);
            }
        }
        else {
            player.sendPacket(SystemMessageId.THERE_ARE_NO_GMS_CURRENTLY_VISIBLE_IN_THE_PUBLIC_LIST_AS_THEY_MAY_BE_PERFORMING_OTHER_FUNCTIONS_AT_THE_MOMENT);
        }
    }
    
    public void broadcastToGMs(final ServerPacket packet) {
        for (final Player gm : this.getAllGms(true)) {
            gm.sendPacket(packet);
        }
    }
    
    public String broadcastMessageToGMs(final String message) {
        for (final Player gm : this.getAllGms(true)) {
            gm.sendMessage(message);
        }
        return message;
    }
    
    public static AdminData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminData.class);
    }
    
    private static class Singleton
    {
        private static final AdminData INSTANCE;
        
        static {
            INSTANCE = new AdminData();
        }
    }
}
