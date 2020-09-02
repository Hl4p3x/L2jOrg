// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.slf4j.LoggerFactory;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.commons.database.DatabaseFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public class ContactList
{
    private static final Logger LOGGER;
    private static final String QUERY_ADD = "INSERT INTO character_contacts (charId, contactId) VALUES (?, ?)";
    private static final String QUERY_REMOVE = "DELETE FROM character_contacts WHERE charId = ? and contactId = ?";
    private static final String QUERY_LOAD = "SELECT contactId FROM character_contacts WHERE charId = ?";
    private final Player activeChar;
    private final Set<String> _contacts;
    
    public ContactList(final Player player) {
        this._contacts = (Set<String>)ConcurrentHashMap.newKeySet();
        this.activeChar = player;
        this.restore();
    }
    
    public void restore() {
        this._contacts.clear();
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT contactId FROM character_contacts WHERE charId = ?");
                try {
                    statement.setInt(1, this.activeChar.getObjectId());
                    final ResultSet rset = statement.executeQuery();
                    try {
                        while (rset.next()) {
                            final int contactId = rset.getInt(1);
                            final String contactName = PlayerNameTable.getInstance().getNameById(contactId);
                            if (contactName != null && !contactName.equals(this.activeChar.getName())) {
                                if (contactId == this.activeChar.getObjectId()) {
                                    continue;
                                }
                                this._contacts.add(contactName);
                            }
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
            ContactList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.activeChar.getName(), e.getMessage()), (Throwable)e);
        }
    }
    
    public boolean add(final String name) {
        final int contactId = PlayerNameTable.getInstance().getIdByName(name);
        if (this._contacts.contains(name)) {
            this.activeChar.sendPacket(SystemMessageId.THE_NAME_ALREADY_EXISTS_ON_THE_ADDED_LIST);
            return false;
        }
        if (this.activeChar.getName().equals(name)) {
            this.activeChar.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOUR_OWN_NAME);
            return false;
        }
        if (this._contacts.size() >= 100) {
            this.activeChar.sendPacket(SystemMessageId.THE_MAXIMUM_NUMBER_OF_NAMES_100_HAS_BEEN_REACHED_YOU_CANNOT_REGISTER_ANY_MORE);
            return false;
        }
        if (contactId < 1) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_NAME_S1_DOESN_T_EXIST_PLEASE_TRY_ANOTHER_NAME);
            sm.addString(name);
            this.activeChar.sendPacket(sm);
            return false;
        }
        for (final String contactName : this._contacts) {
            if (contactName.equalsIgnoreCase(name)) {
                this.activeChar.sendPacket(SystemMessageId.THE_NAME_ALREADY_EXISTS_ON_THE_ADDED_LIST);
                return false;
            }
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("INSERT INTO character_contacts (charId, contactId) VALUES (?, ?)");
                try {
                    statement.setInt(1, this.activeChar.getObjectId());
                    statement.setInt(2, contactId);
                    statement.execute();
                    this._contacts.add(name);
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_WAS_SUCCESSFULLY_ADDED_TO_YOUR_CONTACT_LIST);
                    sm.addString(name);
                    this.activeChar.sendPacket(sm);
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
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
            ContactList.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.activeChar.getName(), e.getMessage()), (Throwable)e);
        }
        return true;
    }
    
    public void remove(final String name) {
        final int contactId = PlayerNameTable.getInstance().getIdByName(name);
        if (!this._contacts.contains(name)) {
            this.activeChar.sendPacket(SystemMessageId.THE_NAME_IS_NOT_CURRENTLY_REGISTERED);
            return;
        }
        if (contactId < 1) {
            return;
        }
        this._contacts.remove(name);
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).deleteContact(this.activeChar.getObjectId(), contactId);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST);
        sm.addString(name);
        this.activeChar.sendPacket(sm);
    }
    
    public Set<String> getAllContacts() {
        return this._contacts;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ContactList.class);
    }
}
