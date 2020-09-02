// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.entity;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import java.util.Objects;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Collections;
import java.util.Collection;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Iterator;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import java.util.Calendar;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.data.xml.impl.NpcData;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import org.l2j.commons.database.DatabaseFactory;
import java.util.List;
import org.l2j.gameserver.model.StatsSet;
import java.util.Map;
import org.slf4j.Logger;

public class Hero
{
    public static final String COUNT = "count";
    public static final String PLAYED = "played";
    public static final String CLAIMED = "claimed";
    public static final String CLAN_NAME = "clan_name";
    public static final String CLAN_CREST = "clan_crest";
    public static final String ALLY_NAME = "ally_name";
    public static final String ALLY_CREST = "ally_crest";
    public static final int ACTION_RAID_KILLED = 1;
    public static final int ACTION_HERO_GAINED = 2;
    public static final int ACTION_CASTLE_TAKEN = 3;
    private static final Logger LOGGER;
    private static final String GET_HEROES = "SELECT heroes.charId, characters.char_name, heroes.class_id, heroes.count, heroes.played, heroes.claimed FROM heroes, characters WHERE characters.charId = heroes.charId AND heroes.played = 1";
    private static final String GET_ALL_HEROES = "SELECT heroes.charId, characters.char_name, heroes.class_id, heroes.count, heroes.played, heroes.claimed FROM heroes, characters WHERE characters.charId = heroes.charId";
    private static final String UPDATE_ALL = "UPDATE heroes SET played = 0";
    private static final String INSERT_HERO = "INSERT INTO heroes (charId, class_id, count, played, claimed) VALUES (?,?,?,?,?)";
    private static final String UPDATE_HERO = "UPDATE heroes SET count = ?, played = ?, claimed = ? WHERE charId = ?";
    private static final String GET_CLAN_ALLY = "SELECT characters.clanid AS clanid, coalesce(clan_data.ally_Id, 0) AS allyId FROM characters LEFT JOIN clan_data ON clan_data.clan_id = characters.clanid WHERE characters.charId = ?";
    private static final String DELETE_ITEMS = "DELETE FROM items WHERE item_id IN (30392, 30393, 30394, 30395, 30396, 30397, 30398, 30399, 30400, 30401, 30402, 30403, 30404, 30405, 30372, 30373, 6842, 6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621, 9388, 9389, 9390) AND owner_id NOT IN (SELECT charId FROM characters WHERE accesslevel > 0)";
    private static final Map<Integer, StatsSet> HEROES;
    private static final Map<Integer, StatsSet> COMPLETE_HEROS;
    private static final Map<Integer, StatsSet> HERO_COUNTS;
    private static final Map<Integer, List<StatsSet>> HERO_FIGHTS;
    private static final Map<Integer, List<StatsSet>> HERO_DIARY;
    private static final Map<Integer, String> HERO_MESSAGE;
    public static final String CHAR_ID = "charId";
    public static final String CLASS_ID = "class_id";
    public static final String CHAR_NAME = "char_name";
    
    private Hero() {
        this.init();
    }
    
    private void init() {
        Hero.HEROES.clear();
        Hero.COMPLETE_HEROS.clear();
        Hero.HERO_COUNTS.clear();
        Hero.HERO_FIGHTS.clear();
        Hero.HERO_DIARY.clear();
        Hero.HERO_MESSAGE.clear();
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement s1 = con.createStatement();
                try {
                    final ResultSet rset = s1.executeQuery("SELECT heroes.charId, characters.char_name, heroes.class_id, heroes.count, heroes.played, heroes.claimed FROM heroes, characters WHERE characters.charId = heroes.charId AND heroes.played = 1");
                    try {
                        final PreparedStatement ps = con.prepareStatement("SELECT characters.clanid AS clanid, coalesce(clan_data.ally_Id, 0) AS allyId FROM characters LEFT JOIN clan_data ON clan_data.clan_id = characters.clanid WHERE characters.charId = ?");
                        try {
                            final Statement s2 = con.createStatement();
                            try {
                                final ResultSet rset2 = s2.executeQuery("SELECT heroes.charId, characters.char_name, heroes.class_id, heroes.count, heroes.played, heroes.claimed FROM heroes, characters WHERE characters.charId = heroes.charId");
                                try {
                                    while (rset.next()) {
                                        final StatsSet hero = new StatsSet();
                                        final int charId = rset.getInt("charId");
                                        hero.set("char_name", rset.getString("char_name"));
                                        hero.set("class_id", rset.getInt("class_id"));
                                        hero.set("count", rset.getInt("count"));
                                        hero.set("played", rset.getInt("played"));
                                        hero.set("claimed", Boolean.parseBoolean(rset.getString("claimed")));
                                        this.loadFights(charId);
                                        this.loadDiary(charId);
                                        this.loadMessage(charId);
                                        this.processHeros(ps, charId, hero);
                                        Hero.HEROES.put(charId, hero);
                                    }
                                    while (rset2.next()) {
                                        final StatsSet hero = new StatsSet();
                                        final int charId = rset2.getInt("charId");
                                        hero.set("char_name", rset2.getString("char_name"));
                                        hero.set("class_id", rset2.getInt("class_id"));
                                        hero.set("count", rset2.getInt("count"));
                                        hero.set("played", rset2.getInt("played"));
                                        hero.set("claimed", Boolean.parseBoolean(rset2.getString("claimed")));
                                        this.processHeros(ps, charId, hero);
                                        Hero.COMPLETE_HEROS.put(charId, hero);
                                    }
                                    if (rset2 != null) {
                                        rset2.close();
                                    }
                                }
                                catch (Throwable t) {
                                    if (rset2 != null) {
                                        try {
                                            rset2.close();
                                        }
                                        catch (Throwable exception) {
                                            t.addSuppressed(exception);
                                        }
                                    }
                                    throw t;
                                }
                                if (s2 != null) {
                                    s2.close();
                                }
                            }
                            catch (Throwable t2) {
                                if (s2 != null) {
                                    try {
                                        s2.close();
                                    }
                                    catch (Throwable exception2) {
                                        t2.addSuppressed(exception2);
                                    }
                                }
                                throw t2;
                            }
                            if (ps != null) {
                                ps.close();
                            }
                        }
                        catch (Throwable t3) {
                            if (ps != null) {
                                try {
                                    ps.close();
                                }
                                catch (Throwable exception3) {
                                    t3.addSuppressed(exception3);
                                }
                            }
                            throw t3;
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t4) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception4) {
                                t4.addSuppressed(exception4);
                            }
                        }
                        throw t4;
                    }
                    if (s1 != null) {
                        s1.close();
                    }
                }
                catch (Throwable t5) {
                    if (s1 != null) {
                        try {
                            s1.close();
                        }
                        catch (Throwable exception5) {
                            t5.addSuppressed(exception5);
                        }
                    }
                    throw t5;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t6) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception6) {
                        t6.addSuppressed(exception6);
                    }
                }
                throw t6;
            }
        }
        catch (SQLException e) {
            Hero.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
        }
        Hero.LOGGER.info(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Hero.HEROES.size()));
        Hero.LOGGER.info(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Hero.COMPLETE_HEROS.size()));
    }
    
    private void processHeros(final PreparedStatement ps, final int charId, final StatsSet hero) throws SQLException {
        ps.setInt(1, charId);
        final ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                final int clanId = rs.getInt("clanid");
                final int allyId = rs.getInt("allyId");
                String clanName = "";
                String allyName = "";
                int clanCrest = 0;
                int allyCrest = 0;
                if (clanId > 0) {
                    clanName = ClanTable.getInstance().getClan(clanId).getName();
                    clanCrest = ClanTable.getInstance().getClan(clanId).getCrestId();
                    if (allyId > 0) {
                        allyName = ClanTable.getInstance().getClan(clanId).getAllyName();
                        allyCrest = ClanTable.getInstance().getClan(clanId).getAllyCrestId();
                    }
                }
                hero.set("clan_crest", clanCrest);
                hero.set("clan_name", clanName);
                hero.set("ally_crest", allyCrest);
                hero.set("ally_name", allyName);
            }
            ps.clearParameters();
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
    }
    
    private String calcFightTime(long FightTime) {
        final String format = String.format("%%0%dd", 2);
        FightTime /= 1000L;
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, String.format(format, FightTime % 3600L / 60L), String.format(format, FightTime % 60L));
    }
    
    public void loadMessage(final int charId) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT message FROM heroes WHERE charId=?");
                try {
                    ps.setInt(1, charId);
                    final ResultSet rset = ps.executeQuery();
                    try {
                        if (rset.next()) {
                            Hero.HERO_MESSAGE.put(charId, rset.getString("message"));
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
        catch (SQLException e) {
            Hero.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, charId, e.getMessage()));
        }
    }
    
    public void loadDiary(final int charId) {
        final List<StatsSet> diary = new ArrayList<StatsSet>();
        int diaryentries = 0;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT * FROM  heroes_diary WHERE charId=? ORDER BY time ASC");
                try {
                    ps.setInt(1, charId);
                    final ResultSet rset = ps.executeQuery();
                    try {
                        while (rset.next()) {
                            final StatsSet _diaryentry = new StatsSet();
                            final long time = rset.getLong("time");
                            final int action = rset.getInt("action");
                            final int param = rset.getInt("param");
                            final String date = new SimpleDateFormat("yyyy-MM-dd HH").format(new Date(time));
                            _diaryentry.set("date", date);
                            if (action == 1) {
                                final NpcTemplate template = NpcData.getInstance().getTemplate(param);
                                if (template != null) {
                                    _diaryentry.set("action", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, template.getName()));
                                }
                            }
                            else if (action == 2) {
                                _diaryentry.set("action", "Gained Hero status");
                            }
                            else if (action == 3) {
                                final Castle castle = CastleManager.getInstance().getCastleById(param);
                                if (castle != null) {
                                    _diaryentry.set("action", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, castle.getName()));
                                }
                            }
                            diary.add(_diaryentry);
                            ++diaryentries;
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
                    Hero.HERO_DIARY.put(charId, diary);
                    Hero.LOGGER.info(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, diaryentries, PlayerNameTable.getInstance().getNameById(charId)));
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
        catch (SQLException e) {
            Hero.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, charId, e.getMessage()));
        }
    }
    
    public void loadFights(final int charId) {
        final List<StatsSet> fights = new ArrayList<StatsSet>();
        final StatsSet heroCountData = new StatsSet();
        final Calendar data = Calendar.getInstance();
        data.set(5, 1);
        data.set(11, 0);
        data.set(12, 0);
        data.set(14, 0);
        final long from = data.getTimeInMillis();
        int numberoffights = 0;
        int _victorys = 0;
        int _losses = 0;
        int _draws = 0;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("SELECT * FROM olympiad_fights WHERE (charOneId=? OR charTwoId=?) AND start<? ORDER BY start ASC");
                try {
                    ps.setInt(1, charId);
                    ps.setInt(2, charId);
                    ps.setLong(3, from);
                    final ResultSet rset = ps.executeQuery();
                    try {
                        while (rset.next()) {
                            final int charOneId = rset.getInt("charOneId");
                            final int charOneClass = rset.getInt("charOneClass");
                            final int charTwoId = rset.getInt("charTwoId");
                            final int charTwoClass = rset.getInt("charTwoClass");
                            final int winner = rset.getInt("winner");
                            final long start = rset.getLong("start");
                            final long time = rset.getLong("time");
                            final int classed = rset.getInt("classed");
                            if (charId == charOneId) {
                                final String name = PlayerNameTable.getInstance().getNameById(charTwoId);
                                final String cls = ClassListData.getInstance().getClass(charTwoClass).getClientCode();
                                if (name == null || cls == null) {
                                    continue;
                                }
                                final StatsSet fight = new StatsSet();
                                fight.set("oponent", name);
                                fight.set("oponentclass", cls);
                                fight.set("time", this.calcFightTime(time));
                                final String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(start));
                                fight.set("start", date);
                                fight.set("classed", classed);
                                if (winner == 1) {
                                    fight.set("result", "<font color=\"00ff00\">victory</font>");
                                    ++_victorys;
                                }
                                else if (winner == 2) {
                                    fight.set("result", "<font color=\"ff0000\">loss</font>");
                                    ++_losses;
                                }
                                else if (winner == 0) {
                                    fight.set("result", "<font color=\"ffff00\">draw</font>");
                                    ++_draws;
                                }
                                fights.add(fight);
                                ++numberoffights;
                            }
                            else {
                                if (charId != charTwoId) {
                                    continue;
                                }
                                final String name = PlayerNameTable.getInstance().getNameById(charOneId);
                                final String cls = ClassListData.getInstance().getClass(charOneClass).getClientCode();
                                if (name == null || cls == null) {
                                    continue;
                                }
                                final StatsSet fight = new StatsSet();
                                fight.set("oponent", name);
                                fight.set("oponentclass", cls);
                                fight.set("time", this.calcFightTime(time));
                                final String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(start));
                                fight.set("start", date);
                                fight.set("classed", classed);
                                if (winner == 1) {
                                    fight.set("result", "<font color=\"ff0000\">loss</font>");
                                    ++_losses;
                                }
                                else if (winner == 2) {
                                    fight.set("result", "<font color=\"00ff00\">victory</font>");
                                    ++_victorys;
                                }
                                else if (winner == 0) {
                                    fight.set("result", "<font color=\"ffff00\">draw</font>");
                                    ++_draws;
                                }
                                fights.add(fight);
                                ++numberoffights;
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
                    heroCountData.set("victory", _victorys);
                    heroCountData.set("draw", _draws);
                    heroCountData.set("loss", _losses);
                    Hero.HERO_COUNTS.put(charId, heroCountData);
                    Hero.HERO_FIGHTS.put(charId, fights);
                    Hero.LOGGER.info(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, numberoffights, PlayerNameTable.getInstance().getNameById(charId)));
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
        catch (SQLException e) {
            Hero.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/sql/SQLException;)Ljava/lang/String;, charId, e));
        }
    }
    
    public Map<Integer, StatsSet> getHeroes() {
        return Hero.HEROES;
    }
    
    public int getHeroByClass(final int classid) {
        for (final Map.Entry<Integer, StatsSet> e : Hero.HEROES.entrySet()) {
            if (e.getValue().getInt("class_id") == classid) {
                return e.getKey();
            }
        }
        return 0;
    }
    
    public void resetData() {
        Hero.HERO_DIARY.clear();
        Hero.HERO_FIGHTS.clear();
        Hero.HERO_COUNTS.clear();
        Hero.HERO_MESSAGE.clear();
    }
    
    public void showHeroDiary(final Player activeChar, final int heroclass, final int charid, final int page) {
        final int perpage = 10;
        final List<StatsSet> mainList = Hero.HERO_DIARY.get(charid);
        if (mainList != null) {
            final NpcHtmlMessage diaryReply = new NpcHtmlMessage();
            final String htmContent = HtmCache.getInstance().getHtm(activeChar, "data/html/olympiad/herodiary.htm");
            final String heroMessage = Hero.HERO_MESSAGE.get(charid);
            if (htmContent != null && heroMessage != null) {
                diaryReply.setHtml(htmContent);
                diaryReply.replace("%heroname%", PlayerNameTable.getInstance().getNameById(charid));
                diaryReply.replace("%message%", heroMessage);
                diaryReply.disableValidation();
                if (!mainList.isEmpty()) {
                    final List<StatsSet> list = new ArrayList<StatsSet>(mainList);
                    Collections.reverse(list);
                    boolean color = true;
                    final StringBuilder fList = new StringBuilder(500);
                    int counter = 0;
                    int breakat = 0;
                    for (int i = (page - 1) * 10; i < list.size(); ++i) {
                        breakat = i;
                        final StatsSet diaryEntry = list.get(i);
                        fList.append("<tr><td>");
                        if (color) {
                            fList.append("<table width=270 bgcolor=\"131210\">");
                        }
                        else {
                            fList.append("<table width=270>");
                        }
                        fList.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, diaryEntry.getString("date")));
                        fList.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, diaryEntry.getString("action")));
                        fList.append("<tr><td>&nbsp;</td></tr></table>");
                        fList.append("</td></tr>");
                        color = !color;
                        if (++counter >= 10) {
                            break;
                        }
                    }
                    if (breakat < list.size() - 1) {
                        diaryReply.replace("%buttprev%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, heroclass, page + 1));
                    }
                    else {
                        diaryReply.replace("%buttprev%", "");
                    }
                    if (page > 1) {
                        diaryReply.replace("%buttnext%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, heroclass, page - 1));
                    }
                    else {
                        diaryReply.replace("%buttnext%", "");
                    }
                    diaryReply.replace("%list%", fList.toString());
                }
                else {
                    diaryReply.replace("%list%", "");
                    diaryReply.replace("%buttprev%", "");
                    diaryReply.replace("%buttnext%", "");
                }
                activeChar.sendPacket(diaryReply);
            }
        }
    }
    
    public void showHeroFights(final Player activeChar, final int heroclass, final int charid, final int page) {
        final int perpage = 20;
        int _win = 0;
        int _loss = 0;
        int _draw = 0;
        final List<StatsSet> heroFights = Hero.HERO_FIGHTS.get(charid);
        if (heroFights != null) {
            final NpcHtmlMessage FightReply = new NpcHtmlMessage();
            final String htmContent = HtmCache.getInstance().getHtm(activeChar, "data/html/olympiad/herohistory.htm");
            if (htmContent != null) {
                FightReply.setHtml(htmContent);
                FightReply.replace("%heroname%", PlayerNameTable.getInstance().getNameById(charid));
                if (!heroFights.isEmpty()) {
                    final StatsSet heroCount = Hero.HERO_COUNTS.get(charid);
                    if (heroCount != null) {
                        _win = heroCount.getInt("victory");
                        _loss = heroCount.getInt("loss");
                        _draw = heroCount.getInt("draw");
                    }
                    boolean color = true;
                    final StringBuilder fList = new StringBuilder(500);
                    int counter = 0;
                    int breakat = 0;
                    for (int i = (page - 1) * 20; i < heroFights.size(); ++i) {
                        breakat = i;
                        final StatsSet fight = heroFights.get(i);
                        fList.append("<tr><td>");
                        if (color) {
                            fList.append("<table width=270 bgcolor=\"131210\">");
                        }
                        else {
                            fList.append("<table width=270>");
                        }
                        fList.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, fight.getString("start"), fight.getString("result"), (fight.getInt("classed") > 0) ? "<font color=\"FFFF99\">cls</font>" : "<font color=\"999999\">non-cls<font>"));
                        fList.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, fight.getString("oponent"), fight.getString("oponentclass"), fight.getString("time")));
                        fList.append("<tr><td colspan=2>&nbsp;</td></tr></table>");
                        fList.append("</td></tr>");
                        color = !color;
                        if (++counter >= 20) {
                            break;
                        }
                    }
                    if (breakat < heroFights.size() - 1) {
                        FightReply.replace("%buttprev%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, heroclass, page + 1));
                    }
                    else {
                        FightReply.replace("%buttprev%", "");
                    }
                    if (page > 1) {
                        FightReply.replace("%buttnext%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, heroclass, page - 1));
                    }
                    else {
                        FightReply.replace("%buttnext%", "");
                    }
                    FightReply.replace("%list%", fList.toString());
                }
                else {
                    FightReply.replace("%list%", "");
                    FightReply.replace("%buttprev%", "");
                    FightReply.replace("%buttnext%", "");
                }
                FightReply.replace("%win%", String.valueOf(_win));
                FightReply.replace("%draw%", String.valueOf(_draw));
                FightReply.replace("%loos%", String.valueOf(_loss));
                activeChar.sendPacket(FightReply);
            }
        }
    }
    
    public synchronized void computeNewHeroes(final List<StatsSet> newHeroes) {
        this.updateHeroes(true);
        for (final Integer objectId : Hero.HEROES.keySet()) {
            final Player player = World.getInstance().findPlayer(objectId);
            if (player == null) {
                continue;
            }
            player.setHero(false);
            for (final InventorySlot slot : InventorySlot.values()) {
                final Item item = player.getInventory().getPaperdollItem(slot);
                if (Objects.nonNull(item) && item.isHeroItem()) {
                    player.getInventory().unEquipItemInSlot(slot);
                }
            }
            final InventoryUpdate iu = new InventoryUpdate();
            for (final Item item2 : player.getInventory().getAvailableItems(false, false, false)) {
                if (item2 != null && item2.isHeroItem()) {
                    player.destroyItem("Hero", item2, null, true);
                    iu.addRemovedItem(item2);
                }
            }
            if (!iu.isEmpty()) {
                player.sendInventoryUpdate(iu);
            }
            player.broadcastUserInfo();
        }
        this.deleteItemsInDb();
        Hero.HEROES.clear();
        if (newHeroes.isEmpty()) {
            return;
        }
        for (final StatsSet hero : newHeroes) {
            final int charId = hero.getInt("charId");
            if (Hero.COMPLETE_HEROS.containsKey(charId)) {
                final StatsSet oldHero = Hero.COMPLETE_HEROS.get(charId);
                final int count = oldHero.getInt("count");
                oldHero.set("count", count + 1);
                oldHero.set("played", 1);
                oldHero.set("claimed", false);
                Hero.HEROES.put(charId, oldHero);
            }
            else {
                final StatsSet newHero = new StatsSet();
                newHero.set("char_name", hero.getString("char_name"));
                newHero.set("class_id", hero.getInt("class_id"));
                newHero.set("count", 1);
                newHero.set("played", 1);
                newHero.set("claimed", false);
                Hero.HEROES.put(charId, newHero);
            }
        }
        this.updateHeroes(false);
    }
    
    public void updateHeroes(final boolean setDefault) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                if (setDefault) {
                    final Statement s = con.createStatement();
                    try {
                        s.executeUpdate("UPDATE heroes SET played = 0");
                        if (s != null) {
                            s.close();
                        }
                    }
                    catch (Throwable t) {
                        if (s != null) {
                            try {
                                s.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                }
                else {
                    for (final Map.Entry<Integer, StatsSet> entry : Hero.HEROES.entrySet()) {
                        final StatsSet hero = entry.getValue();
                        final int heroId = entry.getKey();
                        if (!Hero.COMPLETE_HEROS.containsKey(heroId)) {
                            final PreparedStatement insert = con.prepareStatement("INSERT INTO heroes (charId, class_id, count, played, claimed) VALUES (?,?,?,?,?)");
                            try {
                                insert.setInt(1, heroId);
                                insert.setInt(2, hero.getInt("class_id"));
                                insert.setInt(3, hero.getInt("count"));
                                insert.setInt(4, hero.getInt("played"));
                                insert.setString(5, String.valueOf(hero.getBoolean("claimed")));
                                insert.execute();
                                if (insert != null) {
                                    insert.close();
                                }
                            }
                            catch (Throwable t2) {
                                if (insert != null) {
                                    try {
                                        insert.close();
                                    }
                                    catch (Throwable exception2) {
                                        t2.addSuppressed(exception2);
                                    }
                                }
                                throw t2;
                            }
                            final PreparedStatement statement = con.prepareStatement("SELECT characters.clanid AS clanid, coalesce(clan_data.ally_Id, 0) AS allyId FROM characters LEFT JOIN clan_data ON clan_data.clan_id = characters.clanid WHERE characters.charId = ?");
                            try {
                                statement.setInt(1, heroId);
                                final ResultSet rset = statement.executeQuery();
                                try {
                                    if (rset.next()) {
                                        final int clanId = rset.getInt("clanid");
                                        final int allyId = rset.getInt("allyId");
                                        String clanName = "";
                                        String allyName = "";
                                        int clanCrest = 0;
                                        int allyCrest = 0;
                                        if (clanId > 0) {
                                            clanName = ClanTable.getInstance().getClan(clanId).getName();
                                            clanCrest = ClanTable.getInstance().getClan(clanId).getCrestId();
                                            if (allyId > 0) {
                                                allyName = ClanTable.getInstance().getClan(clanId).getAllyName();
                                                allyCrest = ClanTable.getInstance().getClan(clanId).getAllyCrestId();
                                            }
                                        }
                                        hero.set("clan_crest", clanCrest);
                                        hero.set("clan_name", clanName);
                                        hero.set("ally_crest", allyCrest);
                                        hero.set("ally_name", allyName);
                                    }
                                    if (rset != null) {
                                        rset.close();
                                    }
                                }
                                catch (Throwable t3) {
                                    if (rset != null) {
                                        try {
                                            rset.close();
                                        }
                                        catch (Throwable exception3) {
                                            t3.addSuppressed(exception3);
                                        }
                                    }
                                    throw t3;
                                }
                                if (statement != null) {
                                    statement.close();
                                }
                            }
                            catch (Throwable t4) {
                                if (statement != null) {
                                    try {
                                        statement.close();
                                    }
                                    catch (Throwable exception4) {
                                        t4.addSuppressed(exception4);
                                    }
                                }
                                throw t4;
                            }
                            Hero.HEROES.put(heroId, hero);
                            Hero.COMPLETE_HEROS.put(heroId, hero);
                        }
                        else {
                            final PreparedStatement statement = con.prepareStatement("UPDATE heroes SET count = ?, played = ?, claimed = ? WHERE charId = ?");
                            try {
                                statement.setInt(1, hero.getInt("count"));
                                statement.setInt(2, hero.getInt("played"));
                                statement.setString(3, String.valueOf(hero.getBoolean("claimed")));
                                statement.setInt(4, heroId);
                                statement.execute();
                                if (statement == null) {
                                    continue;
                                }
                                statement.close();
                            }
                            catch (Throwable t5) {
                                if (statement != null) {
                                    try {
                                        statement.close();
                                    }
                                    catch (Throwable exception5) {
                                        t5.addSuppressed(exception5);
                                    }
                                }
                                throw t5;
                            }
                        }
                    }
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t6) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception6) {
                        t6.addSuppressed(exception6);
                    }
                }
                throw t6;
            }
        }
        catch (SQLException e) {
            Hero.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
        }
    }
    
    public void setHeroGained(final int charId) {
        this.setDiaryData(charId, 2, 0);
    }
    
    public void setRBkilled(final int charId, final int npcId) {
        this.setDiaryData(charId, 1, npcId);
        final NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
        final List<StatsSet> list = Hero.HERO_DIARY.get(charId);
        if (list == null || template == null) {
            return;
        }
        final StatsSet diaryEntry = new StatsSet();
        final String date = new SimpleDateFormat("yyyy-MM-dd HH").format(new Date(System.currentTimeMillis()));
        diaryEntry.set("date", date);
        diaryEntry.set("action", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, template.getName()));
        list.add(diaryEntry);
    }
    
    public void setCastleTaken(final int charId, final int castleId) {
        this.setDiaryData(charId, 3, castleId);
        final Castle castle = CastleManager.getInstance().getCastleById(castleId);
        final List<StatsSet> list = Hero.HERO_DIARY.get(charId);
        if (list == null || castle == null) {
            return;
        }
        final StatsSet diaryEntry = new StatsSet();
        final String date = new SimpleDateFormat("yyyy-MM-dd HH").format(new Date(System.currentTimeMillis()));
        diaryEntry.set("date", date);
        diaryEntry.set("action", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, castle.getName()));
        list.add(diaryEntry);
    }
    
    public void setDiaryData(final int charId, final int action, final int param) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("INSERT INTO heroes_diary (charId, time, action, param) values(?,?,?,?)");
                try {
                    ps.setInt(1, charId);
                    ps.setLong(2, System.currentTimeMillis());
                    ps.setInt(3, action);
                    ps.setInt(4, param);
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
        catch (SQLException e) {
            Hero.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
        }
    }
    
    public void setHeroMessage(final Player player, final String message) {
        Hero.HERO_MESSAGE.put(player.getObjectId(), message);
    }
    
    public void saveHeroMessage(final int charId) {
        if (Hero.HERO_MESSAGE.containsKey(charId)) {
            return;
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("UPDATE heroes SET message=? WHERE charId=?;");
                try {
                    ps.setString(1, Hero.HERO_MESSAGE.get(charId));
                    ps.setInt(2, charId);
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
        catch (SQLException e) {
            Hero.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
        }
    }
    
    private void deleteItemsInDb() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final Statement s = con.createStatement();
                try {
                    s.executeUpdate("DELETE FROM items WHERE item_id IN (30392, 30393, 30394, 30395, 30396, 30397, 30398, 30399, 30400, 30401, 30402, 30403, 30404, 30405, 30372, 30373, 6842, 6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621, 9388, 9389, 9390) AND owner_id NOT IN (SELECT charId FROM characters WHERE accesslevel > 0)");
                    if (s != null) {
                        s.close();
                    }
                }
                catch (Throwable t) {
                    if (s != null) {
                        try {
                            s.close();
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
        catch (SQLException e) {
            Hero.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
        }
    }
    
    public void shutdown() {
        for (final int charId : Hero.HERO_MESSAGE.keySet()) {
            this.saveHeroMessage(charId);
        }
    }
    
    public boolean isHero(final int objectId) {
        return Hero.HEROES.containsKey(objectId) && Hero.HEROES.get(objectId).getBoolean("claimed");
    }
    
    public boolean isUnclaimedHero(final int objectId) {
        return Hero.HEROES.containsKey(objectId) && !Hero.HEROES.get(objectId).getBoolean("claimed");
    }
    
    public void claimHero(final Player player) {
        StatsSet hero = Hero.HEROES.get(player.getObjectId());
        if (hero == null) {
            hero = new StatsSet();
            Hero.HEROES.put(player.getObjectId(), hero);
        }
        hero.set("claimed", true);
        final Clan clan = player.getClan();
        if (clan != null && clan.getLevel() >= 5) {
            clan.addReputationScore(Config.HERO_POINTS, true);
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_C1_WAS_NAMED_A_HERO_S2_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION);
            sm.addString(PlayerNameTable.getInstance().getNameById(player.getObjectId()));
            sm.addInt(Config.HERO_POINTS);
            clan.broadcastToOnlineMembers(sm);
        }
        player.setHero(true);
        player.broadcastPacket(new SocialAction(player.getObjectId(), 20016));
        player.sendPacket(new UserInfo(player));
        player.broadcastUserInfo();
        this.setHeroGained(player.getObjectId());
        this.loadFights(player.getObjectId());
        this.loadDiary(player.getObjectId());
        Hero.HERO_MESSAGE.put(player.getObjectId(), "");
        this.updateHeroes(false);
    }
    
    public static Hero getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Hero.class);
        HEROES = new ConcurrentHashMap<Integer, StatsSet>();
        COMPLETE_HEROS = new ConcurrentHashMap<Integer, StatsSet>();
        HERO_COUNTS = new ConcurrentHashMap<Integer, StatsSet>();
        HERO_FIGHTS = new ConcurrentHashMap<Integer, List<StatsSet>>();
        HERO_DIARY = new ConcurrentHashMap<Integer, List<StatsSet>>();
        HERO_MESSAGE = new ConcurrentHashMap<Integer, String>();
    }
    
    private static class Singleton
    {
        private static final Hero INSTANCE;
        
        static {
            INSTANCE = new Hero();
        }
    }
}
