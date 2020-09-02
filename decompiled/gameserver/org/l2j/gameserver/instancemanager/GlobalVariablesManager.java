// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import org.l2j.commons.database.DatabaseFactory;
import org.slf4j.Logger;
import org.l2j.gameserver.model.variables.AbstractVariables;

public final class GlobalVariablesManager extends AbstractVariables
{
    private static final Logger LOGGER;
    public static final String MONSTER_ARENA_VARIABLE = "MA_C";
    private static final String SELECT_QUERY = "SELECT * FROM global_variables";
    private static final String DELETE_QUERY = "DELETE FROM global_variables";
    private static final String INSERT_QUERY = "INSERT INTO global_variables (var, value) VALUES (?, ?)";
    
    private GlobalVariablesManager() {
    }
    
    @Override
    public boolean restoreMe() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement st = con.createStatement();
                try {
                    final ResultSet rset = st.executeQuery("SELECT * FROM global_variables");
                    try {
                        while (rset.next()) {
                            this.set(rset.getString("var"), rset.getString("value"));
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
            GlobalVariablesManager.LOGGER.warn("Couldn't restore global variables");
            return false;
        }
        finally {
            this.compareAndSetChanges(true, false);
        }
        GlobalVariablesManager.LOGGER.info("Loaded {} variables", (Object)this.getSet().size());
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
                final Statement del = con.createStatement();
                try {
                    final PreparedStatement st = con.prepareStatement("INSERT INTO global_variables (var, value) VALUES (?, ?)");
                    try {
                        del.execute("DELETE FROM global_variables");
                        for (final Map.Entry<String, Object> entry : this.getSet().entrySet()) {
                            st.setString(1, entry.getKey());
                            st.setString(2, String.valueOf(entry.getValue()));
                            st.addBatch();
                        }
                        st.executeBatch();
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
                    if (del != null) {
                        del.close();
                    }
                }
                catch (Throwable t2) {
                    if (del != null) {
                        try {
                            del.close();
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
            GlobalVariablesManager.LOGGER.warn("Couldn't save global variables to database.", (Throwable)e);
            return false;
        }
        finally {
            this.compareAndSetChanges(true, false);
        }
        GlobalVariablesManager.LOGGER.info("Stored {} variables", (Object)this.getSet().size());
        return true;
    }
    
    @Override
    public boolean deleteMe() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement del = con.createStatement();
                try {
                    del.execute("DELETE FROM global_variables");
                    if (del != null) {
                        del.close();
                    }
                }
                catch (Throwable t) {
                    if (del != null) {
                        try {
                            del.close();
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
            GlobalVariablesManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
            return false;
        }
        return true;
    }
    
    public void resetRaidBonus() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement st = con.createStatement();
                try {
                    st.execute("DELETE FROM global_variables WHERE var like 'MA_C%'");
                    if (st != null) {
                        st.close();
                    }
                }
                catch (Throwable t2) {
                    if (st != null) {
                        try {
                            st.close();
                        }
                        catch (Throwable exception) {
                            t2.addSuppressed(exception);
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
                    catch (Throwable exception2) {
                        t3.addSuppressed(exception2);
                    }
                }
                throw t3;
            }
        }
        catch (SQLException t) {
            GlobalVariablesManager.LOGGER.warn(t.getMessage(), (Throwable)t);
        }
    }
    
    public static void init() {
        getInstance().restoreMe();
    }
    
    public static GlobalVariablesManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)GlobalVariablesManager.class);
    }
    
    private static class Singleton
    {
        private static final GlobalVariablesManager INSTANCE;
        
        static {
            INSTANCE = new GlobalVariablesManager();
        }
    }
}
