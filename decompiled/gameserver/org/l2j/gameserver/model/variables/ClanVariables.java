// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.variables;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.l2j.commons.database.DatabaseFactory;
import org.slf4j.Logger;

public class ClanVariables extends AbstractVariables
{
    private static final Logger LOGGER;
    private static final String SELECT_QUERY = "SELECT * FROM clan_variables WHERE clanId = ?";
    private static final String DELETE_QUERY = "DELETE FROM clan_variables WHERE clanId = ?";
    private static final String INSERT_QUERY = "INSERT INTO clan_variables (clanId, var, val) VALUES (?, ?, ?)";
    private final int _objectId;
    
    public ClanVariables(final int objectId) {
        this._objectId = objectId;
        this.restoreMe();
    }
    
    @Override
    public boolean restoreMe() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement st = con.prepareStatement("SELECT * FROM clan_variables WHERE clanId = ?");
                try {
                    st.setInt(1, this._objectId);
                    final ResultSet rset = st.executeQuery();
                    try {
                        while (rset.next()) {
                            this.set(rset.getString("var"), rset.getString("val"), false);
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
                    if (st != null) {
                        st.close();
                    }
                }
                catch (Throwable t2) {
                    if (st != null) {
                        try {
                            st.close();
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
        catch (SQLException e) {
            ClanVariables.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), this._objectId), (Throwable)e);
            return false;
        }
        finally {
            this.compareAndSetChanges(true, false);
        }
        return true;
    }
    
    @Override
    public boolean storeMe() {
        if (!this.hasChanges()) {
            return false;
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                PreparedStatement st = con.prepareStatement("DELETE FROM clan_variables WHERE clanId = ?");
                try {
                    st.setInt(1, this._objectId);
                    st.execute();
                    if (st != null) {
                        st.close();
                    }
                }
                catch (Throwable t) {
                    if (st != null) {
                        try {
                            st.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                st = con.prepareStatement("INSERT INTO clan_variables (clanId, var, val) VALUES (?, ?, ?)");
                try {
                    st.setInt(1, this._objectId);
                    for (final Map.Entry<String, Object> entry : this.getSet().entrySet()) {
                        st.setString(2, entry.getKey());
                        st.setString(3, String.valueOf(entry.getValue()));
                        st.addBatch();
                    }
                    st.executeBatch();
                    if (st != null) {
                        st.close();
                    }
                }
                catch (Throwable t2) {
                    if (st != null) {
                        try {
                            st.close();
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
        catch (SQLException e) {
            ClanVariables.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), this._objectId), (Throwable)e);
            return false;
        }
        finally {
            this.compareAndSetChanges(true, false);
        }
        return true;
    }
    
    @Override
    public boolean deleteMe() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement st = con.prepareStatement("DELETE FROM clan_variables WHERE clanId = ?");
                try {
                    st.setInt(1, this._objectId);
                    st.execute();
                    if (st != null) {
                        st.close();
                    }
                }
                catch (Throwable t) {
                    if (st != null) {
                        try {
                            st.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                this.getSet().clear();
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
            ClanVariables.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), this._objectId), (Throwable)e);
            return false;
        }
        return true;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClanVariables.class);
    }
}
