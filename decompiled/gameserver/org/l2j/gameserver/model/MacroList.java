// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.database.data.Shortcut;
import java.util.List;
import java.sql.ResultSet;
import org.l2j.gameserver.enums.MacroType;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.commons.database.DatabaseFactory;
import java.util.Iterator;
import java.util.Collection;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.network.serverpackets.SendMacroList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.function.Consumer;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.MacroUpdateType;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.IRestorable;

public class MacroList implements IRestorable
{
    private static final Logger LOGGER;
    private final Player owner;
    private final IntMap<Macro> macros;
    private int macroId;
    
    public MacroList(final Player owner) {
        this.macros = (IntMap<Macro>)new HashIntMap();
        this.owner = owner;
        this.macroId = 1000;
    }
    
    public int size() {
        return this.macros.size();
    }
    
    public void registerMacro(final Macro macro) {
        MacroUpdateType updateType = MacroUpdateType.ADD;
        if (macro.getId() == 0) {
            macro.setId(this.macroId++);
            while (this.macros.containsKey(macro.getId())) {
                macro.setId(this.macroId++);
            }
            this.macros.put(macro.getId(), (Object)macro);
        }
        else {
            updateType = MacroUpdateType.MODIFY;
            Util.doIfNonNull((Object)this.macros.put(macro.getId(), (Object)macro), (Consumer)this::deleteMacroFromDb);
        }
        this.registerMacroInDb(macro);
        this.owner.sendPacket(new SendMacroList(1, macro, updateType));
    }
    
    public void deleteMacro(final int id) {
        Util.doIfNonNull((Object)this.macros.remove(id), removed -> {
            this.deleteMacroFromDb(removed);
            this.owner.deleteShortcuts(s -> s.getShortcutId() == id && s.getType() == ShortcutType.MACRO);
            this.owner.sendPacket(new SendMacroList(0, removed, MacroUpdateType.DELETE));
        });
    }
    
    public void sendAllMacros() {
        final Collection<Macro> allMacros = (Collection<Macro>)this.macros.values();
        final int count = allMacros.size();
        synchronized (this.macros) {
            if (allMacros.isEmpty()) {
                this.owner.sendPacket(new SendMacroList(0, null, MacroUpdateType.LIST));
            }
            else {
                for (final Macro m : allMacros) {
                    this.owner.sendPacket(new SendMacroList(count, m, MacroUpdateType.LIST));
                }
            }
        }
    }
    
    private void registerMacroInDb(final Macro macro) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("INSERT INTO character_macroses (charId,id,icon,name,descr,acronym,commands) values(?,?,?,?,?,?,?)");
                try {
                    ps.setInt(1, this.owner.getObjectId());
                    ps.setInt(2, macro.getId());
                    ps.setInt(3, macro.getIcon());
                    ps.setString(4, macro.getName());
                    ps.setString(5, macro.getDescr());
                    ps.setString(6, macro.getAcronym());
                    final StringBuilder sb = new StringBuilder(300);
                    for (final MacroCmd cmd : macro.getCommands()) {
                        sb.append(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, cmd.getType().ordinal(), cmd.getD1(), cmd.getD2()));
                        if (cmd.getCmd() != null && cmd.getCmd().length() > 0) {
                            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, cmd.getCmd()));
                        }
                        sb.append(';');
                    }
                    if (sb.length() > 255) {
                        sb.setLength(255);
                    }
                    ps.setString(7, sb.toString());
                    ps.execute();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            MacroList.LOGGER.warn("could not store macro:", (Throwable)e);
        }
    }
    
    private void deleteMacroFromDb(final Macro macro) {
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).deleteMacro(this.owner.getObjectId(), macro.getId());
    }
    
    @Override
    public boolean restoreMe() {
        this.macros.clear();
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT charId, id, icon, name, descr, acronym, commands FROM character_macroses WHERE charId=?");
                try {
                    ps.setInt(1, this.owner.getObjectId());
                    final ResultSet rset = ps.executeQuery();
                    try {
                        while (rset.next()) {
                            final int id = rset.getInt("id");
                            final int icon = rset.getInt("icon");
                            final String name = rset.getString("name");
                            final String descr = rset.getString("descr");
                            final String acronym = rset.getString("acronym");
                            final List<MacroCmd> commands = new ArrayList<MacroCmd>();
                            final StringTokenizer st1 = new StringTokenizer(rset.getString("commands"), ";");
                            while (st1.hasMoreTokens()) {
                                final StringTokenizer st2 = new StringTokenizer(st1.nextToken(), ",");
                                if (st2.countTokens() < 3) {
                                    continue;
                                }
                                final MacroType type = MacroType.values()[Integer.parseInt(st2.nextToken())];
                                final int d1 = Integer.parseInt(st2.nextToken());
                                final int d2 = Integer.parseInt(st2.nextToken());
                                String cmd = "";
                                if (st2.hasMoreTokens()) {
                                    cmd = st2.nextToken();
                                }
                                commands.add(new MacroCmd(commands.size(), type, d1, d2, cmd));
                            }
                            this.macros.put(id, (Object)new Macro(id, icon, name, descr, acronym, commands));
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
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
            MacroList.LOGGER.warn("could not store shortcuts:", (Throwable)e);
            return false;
        }
        return true;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)MacroList.class);
    }
}
