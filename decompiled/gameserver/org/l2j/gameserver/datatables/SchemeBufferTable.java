// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.datatables;

import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import io.github.joealisson.primitive.Containers;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.PrimitiveIterator;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.commons.database.DatabaseFactory;
import io.github.joealisson.primitive.ArrayIntList;
import java.sql.ResultSet;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import org.l2j.gameserver.Config;
import javax.xml.parsers.DocumentBuilderFactory;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.SchemeBufferDAO;
import io.github.joealisson.primitive.LinkedHashIntMap;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.model.holders.BuffSkillHolder;
import io.github.joealisson.primitive.IntList;
import java.util.Map;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public class SchemeBufferTable
{
    private static final Logger LOGGER;
    private static final String INSERT_SCHEME = "INSERT INTO buffer_schemes (object_id, scheme_name, skills) VALUES (?,?,?)";
    private final IntMap<Map<String, IntList>> schemes;
    private final IntMap<BuffSkillHolder> availableBuffs;
    
    private SchemeBufferTable() {
        this.schemes = (IntMap<Map<String, IntList>>)new CHashIntMap();
        this.availableBuffs = (IntMap<BuffSkillHolder>)new LinkedHashIntMap();
        this.load();
    }
    
    private void load() {
        ((SchemeBufferDAO)DatabaseAccess.getDAO((Class)SchemeBufferDAO.class)).loadAll(this::loadBufferSchema);
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
            dbf.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
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
                            this.availableBuffs.put(skillId, (Object)new BuffSkillHolder(skillId, Integer.parseInt(attrs.getNamedItem("price").getNodeValue()), category, attrs.getNamedItem("desc").getNodeValue()));
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            SchemeBufferTable.LOGGER.warn("SchemeBufferTable: Failed to load buff info", (Throwable)e);
        }
        SchemeBufferTable.LOGGER.info("Loaded {} players schemes and {} available buffs.", (Object)this.schemes.size(), (Object)this.availableBuffs.size());
    }
    
    private void loadBufferSchema(final ResultSet resultSet) {
        try {
            while (resultSet.next()) {
                final int objectId = resultSet.getInt("object_id");
                final String schemeName = resultSet.getString("scheme_name");
                final String[] skills = resultSet.getString("skills").split(",");
                final IntList schemeList = (IntList)new ArrayIntList();
                for (final String skill : skills) {
                    if (skill.isEmpty()) {
                        break;
                    }
                    schemeList.add(Integer.parseInt(skill));
                }
                this.setScheme(objectId, schemeName, schemeList, false);
            }
        }
        catch (Exception e) {
            SchemeBufferTable.LOGGER.warn(e.getMessage(), (Throwable)e);
        }
    }
    
    public void saveSchemes() {
        ((SchemeBufferDAO)DatabaseAccess.getDAO((Class)SchemeBufferDAO.class)).deleteAll();
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement stInsert = con.prepareStatement("INSERT INTO buffer_schemes (object_id, scheme_name, skills) VALUES (?,?,?)");
                try {
                    for (final IntMap.Entry<Map<String, IntList>> player : this.schemes.entrySet()) {
                        for (final Map.Entry<String, IntList> scheme : ((Map)player.getValue()).entrySet()) {
                            final StringBuilder sb = new StringBuilder();
                            final PrimitiveIterator.OfInt it = scheme.getValue().iterator();
                            while (it.hasNext()) {
                                sb.append(it.nextInt()).append(",");
                            }
                            if (sb.length() > 0) {
                                sb.setLength(sb.length() - 1);
                            }
                            stInsert.setInt(1, player.getKey());
                            stInsert.setString(2, scheme.getKey());
                            stInsert.setString(3, sb.toString());
                            stInsert.addBatch();
                        }
                    }
                    stInsert.executeBatch();
                    if (stInsert != null) {
                        stInsert.close();
                    }
                }
                catch (Throwable t) {
                    if (stInsert != null) {
                        try {
                            stInsert.close();
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
            SchemeBufferTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e));
        }
    }
    
    public void setScheme(final int playerId, final String schemeName, final IntList list, final boolean save) {
        if (!this.schemes.containsKey(playerId)) {
            this.schemes.put(playerId, (Object)new TreeMap((Comparator<? super Object>)String.CASE_INSENSITIVE_ORDER));
        }
        else if (((Map)this.schemes.get(playerId)).size() >= Config.BUFFER_MAX_SCHEMES) {
            return;
        }
        ((Map)this.schemes.get(playerId)).put(schemeName, list);
        if (save) {
            this.saveSchemes();
        }
    }
    
    public Map<String, IntList> getPlayerSchemes(final int playerId) {
        return (Map<String, IntList>)this.schemes.get(playerId);
    }
    
    public IntList getScheme(final int playerId, final String schemeName) {
        if (this.schemes.get(playerId) == null || ((Map)this.schemes.get(playerId)).get(schemeName) == null) {
            return Containers.emptyList();
        }
        return ((Map)this.schemes.get(playerId)).get(schemeName);
    }
    
    public boolean getSchemeContainsSkill(final int playerId, final String schemeName, final int skillId) {
        final IntList skills = this.getScheme(playerId, schemeName);
        return skills.contains(skillId);
    }
    
    public List<Integer> getSkillsIdsByType(final String groupType) {
        final List<Integer> skills = new ArrayList<Integer>();
        for (final BuffSkillHolder skill : this.availableBuffs.values()) {
            if (skill.getType().equalsIgnoreCase(groupType)) {
                skills.add(skill.getId());
            }
        }
        return skills;
    }
    
    public List<String> getSkillTypes() {
        final List<String> skillTypes = new ArrayList<String>();
        for (final BuffSkillHolder skill : this.availableBuffs.values()) {
            if (!skillTypes.contains(skill.getType())) {
                skillTypes.add(skill.getType());
            }
        }
        return skillTypes;
    }
    
    public BuffSkillHolder getAvailableBuff(final int skillId) {
        return (BuffSkillHolder)this.availableBuffs.get(skillId);
    }
    
    public static void init() {
        getInstance().load();
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
