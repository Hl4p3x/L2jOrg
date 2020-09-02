// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ShortCutRegister;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.enums.MacroType;
import org.l2j.gameserver.model.MacroCmd;
import org.w3c.dom.NamedNodeMap;
import java.util.Collection;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import org.l2j.gameserver.model.Macro;
import org.l2j.gameserver.data.database.data.Shortcut;
import java.util.List;
import org.l2j.gameserver.model.base.ClassId;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class InitialShortcutData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<ClassId, List<Shortcut>> _initialShortcutData;
    private final List<Shortcut> _initialGlobalShortcutList;
    private final Map<Integer, Macro> _macroPresets;
    
    private InitialShortcutData() {
        this._initialShortcutData = new HashMap<ClassId, List<Shortcut>>();
        this._initialGlobalShortcutList = new ArrayList<Shortcut>();
        this._macroPresets = new HashMap<Integer, Macro>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/initialShortcuts.xsd");
    }
    
    public void load() {
        this._initialShortcutData.clear();
        this._initialGlobalShortcutList.clear();
        this.parseDatapackFile("data/stats/initialShortcuts.xml");
        InitialShortcutData.LOGGER.info("Loaded {} Initial Global Shortcuts data.", (Object)this._initialGlobalShortcutList.size());
        InitialShortcutData.LOGGER.info("Loaded {} Initial Shortcuts data.", (Object)this._initialShortcutData.size());
        InitialShortcutData.LOGGER.info("Loaded {} Macros presets.", (Object)this._macroPresets.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equals(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    final String nodeName = d.getNodeName();
                    switch (nodeName) {
                        case "shortcuts": {
                            this.parseShortcuts(d);
                            break;
                        }
                        case "macros": {
                            this.parseMacros(d);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private void parseShortcuts(final Node d) {
        NamedNodeMap attrs = d.getAttributes();
        final Node classIdNode = attrs.getNamedItem("classId");
        final List<Shortcut> list = new ArrayList<Shortcut>();
        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
            if ("page".equals(c.getNodeName())) {
                attrs = c.getAttributes();
                final int pageId = this.parseInteger(attrs, "pageId");
                for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling()) {
                    if ("slot".equals(b.getNodeName())) {
                        list.add(this.createShortcut(pageId, b));
                    }
                }
            }
        }
        if (classIdNode != null) {
            this._initialShortcutData.put(ClassId.getClassId(Integer.parseInt(classIdNode.getNodeValue())), list);
        }
        else {
            this._initialGlobalShortcutList.addAll(list);
        }
    }
    
    private void parseMacros(final Node d) {
        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
            if ("macro".equals(c.getNodeName())) {
                NamedNodeMap attrs = c.getAttributes();
                if (this.parseBoolean(attrs, "enabled", Boolean.valueOf(true))) {
                    final int macroId = this.parseInteger(attrs, "macroId");
                    final int icon = this.parseInteger(attrs, "icon");
                    final String name = this.parseString(attrs, "name");
                    final String description = this.parseString(attrs, "description");
                    final String acronym = this.parseString(attrs, "acronym");
                    final List<MacroCmd> commands = new ArrayList<MacroCmd>(1);
                    int entry = 0;
                    for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling()) {
                        if ("command".equals(b.getNodeName())) {
                            attrs = b.getAttributes();
                            final MacroType type = (MacroType)this.parseEnum(attrs, (Class)MacroType.class, "type");
                            int d2 = 0;
                            int d3 = 0;
                            final String cmd = b.getTextContent();
                            switch (type) {
                                case SKILL: {
                                    d2 = this.parseInteger(attrs, "skillId");
                                    d3 = this.parseInteger(attrs, "skillLvl", Integer.valueOf(0));
                                    break;
                                }
                                case ACTION: {
                                    d2 = this.parseInteger(attrs, "actionId");
                                }
                                case SHORTCUT: {
                                    d2 = this.parseInteger(attrs, "page");
                                    d3 = this.parseInteger(attrs, "slot", Integer.valueOf(0));
                                    break;
                                }
                                case ITEM: {
                                    d2 = this.parseInteger(attrs, "itemId");
                                    break;
                                }
                                case DELAY: {
                                    d2 = this.parseInteger(attrs, "delay");
                                    break;
                                }
                            }
                            commands.add(new MacroCmd(entry++, type, d2, d3, cmd));
                        }
                    }
                    this._macroPresets.put(macroId, new Macro(macroId, icon, name, description, acronym, commands));
                }
            }
        }
    }
    
    private Shortcut createShortcut(final int pageId, final Node b) {
        final NamedNodeMap attrs = b.getAttributes();
        final int slotId = this.parseInteger(attrs, "slotId");
        final ShortcutType shortcutType = (ShortcutType)this.parseEnum(attrs, (Class)ShortcutType.class, "shortcutType");
        final int shortcutId = this.parseInteger(attrs, "shortcutId");
        final int shortcutLevel = this.parseInteger(attrs, "shortcutLevel", Integer.valueOf(0));
        final int characterType = this.parseInteger(attrs, "characterType", Integer.valueOf(0));
        return new Shortcut(Shortcut.pageAndSlotToClientId(pageId, slotId), shortcutType, shortcutId, shortcutLevel, 0, characterType);
    }
    
    public List<Shortcut> getShortcutList(final ClassId cId) {
        return this._initialShortcutData.get(cId);
    }
    
    public List<Shortcut> getShortcutList(final int cId) {
        return this._initialShortcutData.get(ClassId.getClassId(cId));
    }
    
    public List<Shortcut> getGlobalMacroList() {
        return this._initialGlobalShortcutList;
    }
    
    public void registerAllShortcuts(final Player player) {
        if (Objects.isNull(player)) {
            return;
        }
        for (final Shortcut shortcut : this._initialGlobalShortcutList) {
            int shortcutId = shortcut.getShortcutId();
            switch (shortcut.getType()) {
                case ITEM: {
                    final Item item = player.getInventory().getItemByItemId(shortcutId);
                    if (item == null) {
                        continue;
                    }
                    shortcutId = item.getObjectId();
                    break;
                }
                case SKILL: {
                    if (!player.getSkills().containsKey(shortcutId)) {
                        continue;
                    }
                    break;
                }
                case MACRO: {
                    final Macro macro = this._macroPresets.get(shortcutId);
                    if (macro == null) {
                        continue;
                    }
                    player.registerMacro(macro);
                    break;
                }
            }
            final Shortcut newShortcut = new Shortcut(shortcut.getClientId(), shortcut.getType(), shortcutId, shortcut.getLevel(), shortcut.getSubLevel(), shortcut.getCharacterType());
            player.sendPacket(new ShortCutRegister(newShortcut));
            player.registerShortCut(newShortcut);
        }
        if (this._initialShortcutData.containsKey(player.getClassId())) {
            for (final Shortcut shortcut : this._initialShortcutData.get(player.getClassId())) {
                int shortcutId = shortcut.getShortcutId();
                switch (shortcut.getType()) {
                    case ITEM: {
                        final Item item = player.getInventory().getItemByItemId(shortcutId);
                        if (item == null) {
                            continue;
                        }
                        shortcutId = item.getObjectId();
                        break;
                    }
                    case SKILL: {
                        if (!player.getSkills().containsKey(shortcut.getShortcutId())) {
                            continue;
                        }
                        break;
                    }
                    case MACRO: {
                        final Macro macro = this._macroPresets.get(shortcutId);
                        if (macro == null) {
                            continue;
                        }
                        player.registerMacro(macro);
                        break;
                    }
                }
                final Shortcut newShortcut = new Shortcut(shortcut.getClientId(), shortcut.getType(), shortcutId, shortcut.getLevel(), shortcut.getSubLevel(), shortcut.getCharacterType());
                player.sendPacket(new ShortCutRegister(newShortcut));
                player.registerShortCut(newShortcut);
            }
        }
    }
    
    public static InitialShortcutData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)InitialShortcutData.class);
    }
    
    private static class Singleton
    {
        private static final InitialShortcutData INSTANCE;
        
        static {
            INSTANCE = new InitialShortcutData();
        }
    }
}
