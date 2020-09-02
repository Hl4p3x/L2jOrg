// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.datatables;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.io.File;
import org.l2j.gameserver.Config;
import javax.xml.parsers.DocumentBuilderFactory;
import org.l2j.commons.database.DatabaseFactory;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.holders.BuffSkillHolder;
import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;

public class SchemeBufferTable
{
    private static final Logger LOGGER;
    private static final String LOAD_SCHEMES = "SELECT * FROM buffer_schemes";
    private static final String DELETE_SCHEMES = "TRUNCATE TABLE buffer_schemes";
    private static final String INSERT_SCHEME = "INSERT INTO buffer_schemes (object_id, scheme_name, skills) VALUES (?,?,?)";
    private final Map<Integer, Map<String, ArrayList<Integer>>> _schemesTable;
    private final Map<Integer, BuffSkillHolder> _availableBuffs;
    
    private SchemeBufferTable() {
        this._schemesTable = new ConcurrentHashMap<Integer, Map<String, ArrayList<Integer>>>();
        this._availableBuffs = new LinkedHashMap<Integer, BuffSkillHolder>();
        int count = 0;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement st = con.prepareStatement("SELECT * FROM buffer_schemes");
                final ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    final int objectId = rs.getInt("object_id");
                    final String schemeName = rs.getString("scheme_name");
                    final String[] skills = rs.getString("skills").split(",");
                    final ArrayList<Integer> schemeList = new ArrayList<Integer>();
                    for (final String skill : skills) {
                        if (skill.isEmpty()) {
                            break;
                        }
                        schemeList.add(Integer.valueOf(skill));
                    }
                    this.setScheme(objectId, schemeName, schemeList);
                    ++count;
                }
                rs.close();
                st.close();
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (Exception e) {
            SchemeBufferTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e));
        }
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(new File(Config.DATAPACK_ROOT, "data/SchemeBufferSkills.xml"));
            final Node n = doc.getFirstChild();
            for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                if (d.getNodeName().equalsIgnoreCase("category")) {
                    final String category = d.getAttributes().getNamedItem("type").getNodeValue();
                    for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                        if (c.getNodeName().equalsIgnoreCase("buff")) {
                            final NamedNodeMap attrs = c.getAttributes();
                            final int skillId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                            this._availableBuffs.put(skillId, new BuffSkillHolder(skillId, Integer.parseInt(attrs.getNamedItem("price").getNodeValue()), category, attrs.getNamedItem("desc").getNodeValue()));
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            SchemeBufferTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e));
        }
        SchemeBufferTable.LOGGER.info(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, count, this._availableBuffs.size()));
    }
    
    public void saveSchemes() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                PreparedStatement st = con.prepareStatement("TRUNCATE TABLE buffer_schemes");
                st.execute();
                st.close();
                st = con.prepareStatement("INSERT INTO buffer_schemes (object_id, scheme_name, skills) VALUES (?,?,?)");
                for (final Map.Entry<Integer, Map<String, ArrayList<Integer>>> player : this._schemesTable.entrySet()) {
                    for (final Map.Entry<String, ArrayList<Integer>> scheme : player.getValue().entrySet()) {
                        final StringBuilder sb = new StringBuilder();
                        for (final int skillId : scheme.getValue()) {
                            sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, skillId));
                        }
                        if (sb.length() > 0) {
                            sb.setLength(sb.length() - 1);
                        }
                        st.setInt(1, player.getKey());
                        st.setString(2, scheme.getKey());
                        st.setString(3, sb.toString());
                        st.addBatch();
                    }
                }
                st.executeBatch();
                st.close();
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (Exception e) {
            SchemeBufferTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e));
        }
    }
    
    public void setScheme(final int playerId, final String schemeName, final ArrayList<Integer> list) {
        if (!this._schemesTable.containsKey(playerId)) {
            this._schemesTable.put(playerId, new TreeMap<String, ArrayList<Integer>>(String.CASE_INSENSITIVE_ORDER));
        }
        else if (this._schemesTable.get(playerId).size() >= Config.BUFFER_MAX_SCHEMES) {
            return;
        }
        this._schemesTable.get(playerId).put(schemeName, list);
        this.saveSchemes();
    }
    
    public Map<String, ArrayList<Integer>> getPlayerSchemes(final int playerId) {
        return this._schemesTable.get(playerId);
    }
    
    public List<Integer> getScheme(final int playerId, final String schemeName) {
        if (this._schemesTable.get(playerId) == null || this._schemesTable.get(playerId).get(schemeName) == null) {
            return Collections.emptyList();
        }
        return this._schemesTable.get(playerId).get(schemeName);
    }
    
    public boolean getSchemeContainsSkill(final int playerId, final String schemeName, final int skillId) {
        final List<Integer> skills = this.getScheme(playerId, schemeName);
        if (skills.isEmpty()) {
            return false;
        }
        for (final int id : skills) {
            if (id == skillId) {
                return true;
            }
        }
        return false;
    }
    
    public List<Integer> getSkillsIdsByType(final String groupType) {
        final List<Integer> skills = new ArrayList<Integer>();
        for (final BuffSkillHolder skill : this._availableBuffs.values()) {
            if (skill.getType().equalsIgnoreCase(groupType)) {
                skills.add(skill.getId());
            }
        }
        return skills;
    }
    
    public List<String> getSkillTypes() {
        final List<String> skillTypes = new ArrayList<String>();
        for (final BuffSkillHolder skill : this._availableBuffs.values()) {
            if (!skillTypes.contains(skill.getType())) {
                skillTypes.add(skill.getType());
            }
        }
        return skillTypes;
    }
    
    public BuffSkillHolder getAvailableBuff(final int skillId) {
        return this._availableBuffs.get(skillId);
    }
    
    public static SchemeBufferTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SchemeBufferTable.class);
    }
    
    private static class Singleton
    {
        private static final SchemeBufferTable INSTANCE;
        
        static {
            INSTANCE = new SchemeBufferTable();
        }
    }
}
