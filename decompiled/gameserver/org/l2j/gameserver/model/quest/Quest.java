// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.quest;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSkillLearn;
import org.l2j.gameserver.model.events.impl.item.OnItemBypassEvent;
import org.l2j.gameserver.model.events.impl.item.OnItemTalk;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAttack;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcTeleport;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSpawn;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcDespawn;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillSee;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillFinished;
import org.l2j.gameserver.model.events.impl.character.player.OnTrapAction;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableFactionCall;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAggroRangeEnter;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcCreatureSee;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneExit;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcEventReceived;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMoveFinished;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMoveRouteFinished;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSummonTalk;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSummonSpawn;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableHate;
import org.l2j.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceCreated;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceDestroy;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceEnter;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceLeave;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.KeyValuePair;
import java.util.function.Consumer;
import org.l2j.gameserver.network.serverpackets.ExQuestNpcLogList;
import java.util.Collections;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.engine.scripting.ScriptEngineManager;
import java.nio.file.Path;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.network.serverpackets.html.NpcQuestHtmlMessage;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.Party;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.commons.util.Util;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.olympiad.CompetitionType;
import org.l2j.gameserver.model.olympiad.Participant;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.base.AcquireSkillType;
import java.util.stream.Stream;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.function.Function;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.enums.TrapAction;
import org.l2j.gameserver.model.actor.instance.Trap;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.cache.HtmCache;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.network.serverpackets.QuestList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.Config;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.network.NpcStringId;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.events.AbstractScript;

public class Quest extends AbstractScript implements IIdentifiable
{
    public static final Logger LOGGER;
    private static final String DEFAULT_NO_QUEST_MSG = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
    private static final String QUEST_DELETE_FROM_CHAR_QUERY = "DELETE FROM character_quests WHERE charId=? AND name=?";
    private static final String QUEST_DELETE_FROM_CHAR_QUERY_NON_REPEATABLE_QUERY = "DELETE FROM character_quests WHERE charId=? AND name=? AND var!=?";
    private static final int RESET_HOUR = 6;
    private static final int RESET_MINUTES = 30;
    private static final int STEEL_DOOR_COIN = 37045;
    private static final SkillHolder STORY_QUEST_REWARD;
    private final ReentrantReadWriteLock _rwLock;
    private final ReentrantReadWriteLock.WriteLock _writeLock;
    private final ReentrantReadWriteLock.ReadLock _readLock;
    private final int _questId;
    private final byte _initialState = 0;
    private volatile Map<String, List<QuestTimer>> _questTimers;
    private volatile Set<QuestCondition> _startCondition;
    private boolean _isCustom;
    private NpcStringId _questNameNpcStringId;
    private int[] _questItemIds;
    
    public Quest(final int questId) {
        this._rwLock = new ReentrantReadWriteLock();
        this._writeLock = this._rwLock.writeLock();
        this._readLock = this._rwLock.readLock();
        this._questTimers = null;
        this._startCondition = null;
        this._isCustom = false;
        this._questItemIds = null;
        this._questId = questId;
        if (questId > 0) {
            QuestManager.getInstance().addQuest(this);
        }
        else {
            QuestManager.getInstance().addScript(this);
        }
        this.onLoad();
    }
    
    public static void playerEnter(final Player player) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement invalidQuestData = con.prepareStatement("DELETE FROM character_quests WHERE charId = ? AND name = ?");
                try {
                    final PreparedStatement invalidQuestDataVar = con.prepareStatement("DELETE FROM character_quests WHERE charId = ? AND name = ? AND var = ?");
                    try {
                        final PreparedStatement ps1 = con.prepareStatement("SELECT name, value FROM character_quests WHERE charId = ? AND var = ?");
                        try {
                            ps1.setInt(1, player.getObjectId());
                            ps1.setString(2, "<state>");
                            final ResultSet rs = ps1.executeQuery();
                            try {
                                while (rs.next()) {
                                    final String questId = rs.getString("name");
                                    final String statename = rs.getString("value");
                                    final Quest q = QuestManager.getInstance().getQuest(questId);
                                    if (q == null) {
                                        Quest.LOGGER.debug(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, questId, player.getName()));
                                        if (!Config.AUTODELETE_INVALID_QUEST_DATA) {
                                            continue;
                                        }
                                        invalidQuestData.setInt(1, player.getObjectId());
                                        invalidQuestData.setString(2, questId);
                                        invalidQuestData.executeUpdate();
                                    }
                                    else {
                                        new QuestState(q, player, State.getStateId(statename));
                                    }
                                }
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
                            final PreparedStatement ps2 = con.prepareStatement("SELECT name, var, value FROM character_quests WHERE charId = ? AND var <> ?");
                            try {
                                ps2.setInt(1, player.getObjectId());
                                ps2.setString(2, "<state>");
                                final ResultSet rs2 = ps2.executeQuery();
                                try {
                                    while (rs2.next()) {
                                        final String questId2 = rs2.getString("name");
                                        final String var = rs2.getString("var");
                                        final String value = rs2.getString("value");
                                        final QuestState qs = player.getQuestState(questId2);
                                        if (qs == null) {
                                            Quest.LOGGER.debug(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, var, questId2, player.getName()));
                                            if (!Config.AUTODELETE_INVALID_QUEST_DATA) {
                                                continue;
                                            }
                                            invalidQuestDataVar.setInt(1, player.getObjectId());
                                            invalidQuestDataVar.setString(2, questId2);
                                            invalidQuestDataVar.setString(3, var);
                                            invalidQuestDataVar.executeUpdate();
                                        }
                                        else {
                                            qs.setInternal(var, value);
                                        }
                                    }
                                    if (rs2 != null) {
                                        rs2.close();
                                    }
                                }
                                catch (Throwable t2) {
                                    if (rs2 != null) {
                                        try {
                                            rs2.close();
                                        }
                                        catch (Throwable exception2) {
                                            t2.addSuppressed(exception2);
                                        }
                                    }
                                    throw t2;
                                }
                                if (ps2 != null) {
                                    ps2.close();
                                }
                            }
                            catch (Throwable t3) {
                                if (ps2 != null) {
                                    try {
                                        ps2.close();
                                    }
                                    catch (Throwable exception3) {
                                        t3.addSuppressed(exception3);
                                    }
                                }
                                throw t3;
                            }
                            if (ps1 != null) {
                                ps1.close();
                            }
                        }
                        catch (Throwable t4) {
                            if (ps1 != null) {
                                try {
                                    ps1.close();
                                }
                                catch (Throwable exception4) {
                                    t4.addSuppressed(exception4);
                                }
                            }
                            throw t4;
                        }
                        if (invalidQuestDataVar != null) {
                            invalidQuestDataVar.close();
                        }
                    }
                    catch (Throwable t5) {
                        if (invalidQuestDataVar != null) {
                            try {
                                invalidQuestDataVar.close();
                            }
                            catch (Throwable exception5) {
                                t5.addSuppressed(exception5);
                            }
                        }
                        throw t5;
                    }
                    if (invalidQuestData != null) {
                        invalidQuestData.close();
                    }
                }
                catch (Throwable t6) {
                    if (invalidQuestData != null) {
                        try {
                            invalidQuestData.close();
                        }
                        catch (Throwable exception6) {
                            t6.addSuppressed(exception6);
                        }
                    }
                    throw t6;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t7) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception7) {
                        t7.addSuppressed(exception7);
                    }
                }
                throw t7;
            }
        }
        catch (Exception e) {
            Quest.LOGGER.warn("could not insert char quest:", (Throwable)e);
        }
        player.sendPacket(new QuestList(player));
    }
    
    public static void createQuestVarInDb(final QuestState qs, final String var, final String value) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("INSERT INTO character_quests (charId,name,var,value) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE value=?");
                try {
                    statement.setInt(1, qs.getPlayer().getObjectId());
                    statement.setString(2, qs.getQuestName());
                    statement.setString(3, var);
                    statement.setString(4, value);
                    statement.setString(5, value);
                    statement.executeUpdate();
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
            Quest.LOGGER.warn("could not insert char quest:", (Throwable)e);
        }
    }
    
    public static void updateQuestVarInDb(final QuestState qs, final String var, final String value) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("UPDATE character_quests SET value=? WHERE charId=? AND name=? AND var = ?");
                try {
                    statement.setString(1, value);
                    statement.setInt(2, qs.getPlayer().getObjectId());
                    statement.setString(3, qs.getQuestName());
                    statement.setString(4, var);
                    statement.executeUpdate();
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
            Quest.LOGGER.warn("could not update char quest:", (Throwable)e);
        }
    }
    
    public static void deleteQuestVarInDb(final QuestState qs, final String var) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("DELETE FROM character_quests WHERE charId=? AND name=? AND var=?");
                try {
                    statement.setInt(1, qs.getPlayer().getObjectId());
                    statement.setString(2, qs.getQuestName());
                    statement.setString(3, var);
                    statement.executeUpdate();
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
            Quest.LOGGER.warn("Unable to delete char quest!", (Throwable)e);
        }
    }
    
    public static void deleteQuestInDb(final QuestState qs, final boolean repeatable) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement(repeatable ? "DELETE FROM character_quests WHERE charId=? AND name=?" : "DELETE FROM character_quests WHERE charId=? AND name=? AND var!=?");
                try {
                    ps.setInt(1, qs.getPlayer().getObjectId());
                    ps.setString(2, qs.getQuestName());
                    if (!repeatable) {
                        ps.setString(3, "<state>");
                    }
                    ps.executeUpdate();
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
            Quest.LOGGER.warn("could not delete char quest:", (Throwable)e);
        }
    }
    
    public static void createQuestInDb(final QuestState qs) {
        createQuestVarInDb(qs, "<state>", State.getStateName(qs.getState()));
    }
    
    public static void updateQuestInDb(final QuestState qs) {
        updateQuestVarInDb(qs, "<state>", State.getStateName(qs.getState()));
    }
    
    public static String getNoQuestMsg(final Player player) {
        final String result = HtmCache.getInstance().getHtm(player, "data/html/noquest.htm");
        if (result != null && result.length() > 0) {
            return result;
        }
        return "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
    }
    
    public static String getNoQuestLevelRewardMsg(final Player player) {
        return HtmCache.getInstance().getHtm(player, "data/html/noquestlevelreward.html");
    }
    
    public static String getAlreadyCompletedMsg(final Player player) {
        return getAlreadyCompletedMsg(player, QuestType.ONE_TIME);
    }
    
    public static String getAlreadyCompletedMsg(final Player player, final QuestType type) {
        return HtmCache.getInstance().getHtm(player, (type == QuestType.ONE_TIME) ? "data/html/alreadyCompleted.html" : "data/html/alreadyCompletedDaily.html");
    }
    
    private static boolean checkDistanceToTarget(final Player player, final Npc target) {
        return target == null || GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, player, target, true);
    }
    
    public int getResetHour() {
        return 6;
    }
    
    public int getResetMinutes() {
        return 30;
    }
    
    protected void onLoad() {
    }
    
    public void onSave() {
    }
    
    @Override
    public int getId() {
        return this._questId;
    }
    
    public int getNpcStringId() {
        return (this._questNameNpcStringId != null) ? (this._questNameNpcStringId.getId() / 100) : ((this._questId > 10000) ? (this._questId - 5000) : this._questId);
    }
    
    public NpcStringId getQuestNameNpcStringId() {
        return this._questNameNpcStringId;
    }
    
    public void setQuestNameNpcStringId(final NpcStringId npcStringId) {
        this._questNameNpcStringId = npcStringId;
    }
    
    public QuestState newQuestState(final Player player) {
        return new QuestState(this, player, (byte)0);
    }
    
    public QuestState getQuestState(final Player player, final boolean initIfNone) {
        final QuestState qs = player.getQuestState(this.getName());
        if (qs != null || !initIfNone) {
            return qs;
        }
        return this.canStartQuest(player) ? this.newQuestState(player) : null;
    }
    
    public byte getInitialState() {
        return 0;
    }
    
    public String getName() {
        return this.getClass().getSimpleName();
    }
    
    public String getPath() {
        final String path = this.getClass().getName().replace("org.l2j.scripts.", "").replace('.', '/');
        return path.substring(0, path.lastIndexOf(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName())));
    }
    
    public void startQuestTimer(final String name, final long time, final Npc npc, final Player player) {
        this.startQuestTimer(name, time, npc, player, false);
    }
    
    public final Map<String, List<QuestTimer>> getQuestTimers() {
        if (this._questTimers == null) {
            synchronized (this) {
                if (this._questTimers == null) {
                    this._questTimers = new ConcurrentHashMap<String, List<QuestTimer>>(1);
                }
            }
        }
        return this._questTimers;
    }
    
    public void startQuestTimer(final String name, final long time, final Npc npc, final Player player, final boolean repeating) {
        final List<QuestTimer> timers = this.getQuestTimers().computeIfAbsent(name, k -> new ArrayList(1));
        if (this.getQuestTimer(name, npc, player) == null) {
            this._writeLock.lock();
            try {
                timers.add(new QuestTimer(this, name, time, npc, player, repeating));
            }
            finally {
                this._writeLock.unlock();
            }
        }
    }
    
    public QuestTimer getQuestTimer(final String name, final Npc npc, final Player player) {
        if (this._questTimers == null) {
            return null;
        }
        final List<QuestTimer> timers = this.getQuestTimers().get(name);
        if (timers != null) {
            this._readLock.lock();
            try {
                for (final QuestTimer timer : timers) {
                    if (timer != null && timer.isMatch(this, name, npc, player)) {
                        return timer;
                    }
                }
            }
            finally {
                this._readLock.unlock();
            }
        }
        return null;
    }
    
    public void cancelQuestTimers(final String name) {
        if (this._questTimers == null) {
            return;
        }
        final List<QuestTimer> timers = this.getQuestTimers().get(name);
        if (timers != null) {
            this._writeLock.lock();
            try {
                for (final QuestTimer timer : timers) {
                    if (timer != null) {
                        timer.cancel();
                    }
                }
                timers.clear();
            }
            finally {
                this._writeLock.unlock();
            }
        }
    }
    
    public void cancelQuestTimer(final String name, final Npc npc, final Player player) {
        final QuestTimer timer = this.getQuestTimer(name, npc, player);
        if (timer != null) {
            timer.cancelAndRemove();
        }
    }
    
    public void removeQuestTimer(final QuestTimer timer) {
        if (timer != null && this._questTimers != null) {
            final List<QuestTimer> timers = this.getQuestTimers().get(timer.getName());
            if (timers != null) {
                this._writeLock.lock();
                try {
                    timers.remove(timer);
                }
                finally {
                    this._writeLock.unlock();
                }
            }
        }
    }
    
    public final void notifyAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon, final Skill skill) {
        String res = null;
        try {
            res = this.onAttack(npc, attacker, damage, isSummon, skill);
        }
        catch (Exception e) {
            this.showError(attacker, e);
            return;
        }
        this.showResult(attacker, res);
    }
    
    public final void notifyDeath(final Creature killer, final Creature victim, final QuestState qs) {
        String res = null;
        try {
            res = this.onDeath(killer, victim, qs);
        }
        catch (Exception e) {
            this.showError(qs.getPlayer(), e);
            return;
        }
        this.showResult(qs.getPlayer(), res);
    }
    
    public final void notifyItemUse(final ItemTemplate item, final Player player) {
        String res = null;
        try {
            res = this.onItemUse(item, player);
        }
        catch (Exception e) {
            this.showError(player, e);
            return;
        }
        this.showResult(player, res);
    }
    
    public final void notifySpellFinished(final Npc instance, final Player player, final Skill skill) {
        String res = null;
        try {
            res = this.onSpellFinished(instance, player, skill);
        }
        catch (Exception e) {
            this.showError(player, e);
            return;
        }
        this.showResult(player, res);
    }
    
    public final void notifyTrapAction(final Trap trap, final Creature trigger, final TrapAction action) {
        String res = null;
        try {
            res = this.onTrapAction(trap, trigger, action);
        }
        catch (Exception e) {
            if (trigger.getActingPlayer() != null) {
                this.showError(trigger.getActingPlayer(), e);
            }
            Quest.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
            return;
        }
        if (trigger.getActingPlayer() != null) {
            this.showResult(trigger.getActingPlayer(), res);
        }
    }
    
    public final void notifySpawn(final Npc npc) {
        try {
            this.onSpawn(npc);
        }
        catch (Exception e) {
            Quest.LOGGER.warn("Exception on onSpawn() in notifySpawn(): {}", (Object)e.getMessage(), (Object)e);
        }
    }
    
    public final void notifyTeleport(final Npc npc) {
        try {
            this.onTeleport(npc);
        }
        catch (Exception e) {
            Quest.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public final void notifyEvent(final String event, final Npc npc, final Player player) {
        String res = null;
        try {
            res = this.onAdvEvent(event, npc, player);
        }
        catch (Exception e) {
            this.showError(player, e);
            return;
        }
        this.showResult(player, res, npc);
    }
    
    public final void notifyEnterWorld(final Player player) {
        String res = null;
        try {
            res = this.onEnterWorld(player);
        }
        catch (Exception e) {
            this.showError(player, e);
            return;
        }
        this.showResult(player, res);
    }
    
    public final void notifyKill(final Npc npc, final Player killer, final boolean isSummon) {
        this.notifyKill(npc, killer, isSummon, null);
    }
    
    public final void notifyKill(final Npc npc, final Player killer, final boolean isSummon, final Object payload) {
        String res = null;
        try {
            if (payload == null) {
                res = this.onKill(npc, killer, isSummon);
            }
            else {
                res = this.onKill(npc, killer, isSummon, payload);
            }
        }
        catch (Exception e) {
            this.showError(killer, e);
            return;
        }
        this.showResult(killer, res);
    }
    
    public final void notifyTalk(final Npc npc, final Player player) {
        String res = null;
        try {
            final Stream<Object> map = npc.getListeners(EventType.ON_NPC_QUEST_START).stream().map((Function<? super Object, ?>)AbstractEventListener::getOwner);
            final Class<Quest> obj = Quest.class;
            Objects.requireNonNull(obj);
            final Stream<Object> filter = map.filter(obj::isInstance);
            final Class<Quest> obj2 = Quest.class;
            Objects.requireNonNull(obj2);
            final Set<Quest> startingQuests = filter.map((Function<? super Object, ?>)obj2::cast).collect((Collector<? super Object, ?, Set<Quest>>)Collectors.toSet());
            final String startConditionHtml = this.getStartConditionHtml(player, npc);
            if (startingQuests.contains(this) && startConditionHtml != null) {
                res = startConditionHtml;
            }
            else {
                res = this.onTalk(npc, player, false);
            }
        }
        catch (Exception e) {
            this.showError(player, e);
            return;
        }
        player.setLastQuestNpcObject(npc.getObjectId());
        this.showResult(player, res, npc);
    }
    
    public final void notifyFirstTalk(final Npc npc, final Player player) {
        try {
            final String res = this.onFirstTalk(npc, player);
            this.showResult(player, res, npc);
        }
        catch (Exception e) {
            this.showError(player, e);
        }
    }
    
    public final void notifyAcquireSkill(final Npc npc, final Player player, final Skill skill, final AcquireSkillType type) {
        String res = null;
        try {
            res = this.onAcquireSkill(npc, player, skill, type);
        }
        catch (Exception e) {
            this.showError(player, e);
            return;
        }
        this.showResult(player, res);
    }
    
    public final void notifyItemTalk(final Item item, final Player player) {
        String res = null;
        try {
            res = this.onItemTalk(item, player);
        }
        catch (Exception e) {
            this.showError(player, e);
            return;
        }
        this.showResult(player, res);
    }
    
    public String onItemTalk(final Item item, final Player player) {
        return null;
    }
    
    public final void notifyItemEvent(final Item item, final Player player, final String event) {
        String res = null;
        try {
            res = this.onItemEvent(item, player, event);
            if (res != null && (res.equalsIgnoreCase("true") || res.equalsIgnoreCase("false"))) {
                return;
            }
        }
        catch (Exception e) {
            this.showError(player, e);
            return;
        }
        this.showResult(player, res);
    }
    
    public final void notifySkillSee(final Npc npc, final Player caster, final Skill skill, final WorldObject[] targets, final boolean isSummon) {
        String res = null;
        try {
            res = this.onSkillSee(npc, caster, skill, targets, isSummon);
        }
        catch (Exception e) {
            this.showError(caster, e);
            return;
        }
        this.showResult(caster, res);
    }
    
    public final void notifyFactionCall(final Npc npc, final Npc caller, final Player attacker, final boolean isSummon) {
        String res = null;
        try {
            res = this.onFactionCall(npc, caller, attacker, isSummon);
        }
        catch (Exception e) {
            this.showError(attacker, e);
            return;
        }
        this.showResult(attacker, res);
    }
    
    public final void notifyAggroRangeEnter(final Npc npc, final Player player, final boolean isSummon) {
        String res = null;
        try {
            res = this.onAggroRangeEnter(npc, player, isSummon);
        }
        catch (Exception e) {
            this.showError(player, e);
            return;
        }
        this.showResult(player, res);
    }
    
    public final void notifySeeCreature(final Npc npc, final Creature creature, final boolean isSummon) {
        Player player = null;
        if (isSummon || GameUtils.isPlayer(creature)) {
            player = creature.getActingPlayer();
        }
        String res = null;
        try {
            res = this.onSeeCreature(npc, creature, isSummon);
        }
        catch (Exception e) {
            if (player != null) {
                this.showError(player, e);
            }
            return;
        }
        if (player != null) {
            this.showResult(player, res);
        }
    }
    
    public final void notifyEventReceived(final String eventName, final Npc sender, final Npc receiver, final WorldObject reference) {
        try {
            this.onEventReceived(eventName, sender, receiver, reference);
        }
        catch (Exception e) {
            Quest.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public final void notifyEnterZone(final Creature character, final Zone zone) {
        final Player player = character.getActingPlayer();
        String res = null;
        try {
            res = this.onEnterZone(character, zone);
        }
        catch (Exception e) {
            if (player != null) {
                this.showError(player, e);
            }
            return;
        }
        if (player != null) {
            this.showResult(player, res);
        }
    }
    
    public final void notifyExitZone(final Creature character, final Zone zone) {
        final Player player = character.getActingPlayer();
        String res = null;
        try {
            res = this.onExitZone(character, zone);
        }
        catch (Exception e) {
            if (player != null) {
                this.showError(player, e);
            }
            return;
        }
        if (player != null) {
            this.showResult(player, res);
        }
    }
    
    public final void notifyOlympiadMatch(final Participant winner, final Participant looser, final CompetitionType type) {
        try {
            this.onOlympiadMatchFinish(winner, looser, type);
        }
        catch (Exception e) {
            Quest.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public final void notifyMoveFinished(final Npc npc) {
        try {
            this.onMoveFinished(npc);
        }
        catch (Exception e) {
            Quest.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public final void notifyRouteFinished(final Npc npc) {
        try {
            this.onRouteFinished(npc);
        }
        catch (Exception e) {
            Quest.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public final boolean notifyOnCanSeeMe(final Npc npc, final Player player) {
        try {
            return this.onCanSeeMe(npc, player);
        }
        catch (Exception e) {
            Quest.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
            return false;
        }
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon) {
        return null;
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon, final Skill skill) {
        return this.onAttack(npc, attacker, damage, isSummon);
    }
    
    public String onDeath(final Creature killer, final Creature victim, final QuestState qs) {
        return this.onAdvEvent("", (killer instanceof Npc) ? ((Npc)killer) : null, qs.getPlayer());
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (player != null) {
            final QuestState qs = player.getQuestState(this.getName());
            if (qs != null) {
                return this.onEvent(event, qs);
            }
        }
        return null;
    }
    
    public String onEvent(final String event, final QuestState qs) {
        return null;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        return this.onKill(npc, killer, isSummon, null);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon, final Object payload) {
        if (!this.getNpcLogList(killer).isEmpty()) {
            this.sendNpcLogList(killer);
        }
        return null;
    }
    
    public void onCreatureKill(final Creature creature, final Creature killer) {
    }
    
    public String onTalk(final Npc npc, final Player talker, final boolean simulated) {
        final QuestState qs = talker.getQuestState(this.getName());
        if (qs != null) {
            qs.setSimulated(simulated);
        }
        talker.setSimulatedTalking(simulated);
        return this.onTalk(npc, talker);
    }
    
    public String onTalk(final Npc npc, final Player talker) {
        return null;
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        return null;
    }
    
    public String onItemEvent(final Item item, final Player player, final String event) {
        return null;
    }
    
    public String onAcquireSkillList(final Npc npc, final Player player) {
        return null;
    }
    
    public String onAcquireSkillInfo(final Npc npc, final Player player, final Skill skill) {
        return null;
    }
    
    public String onAcquireSkill(final Npc npc, final Player player, final Skill skill, final AcquireSkillType type) {
        return null;
    }
    
    public String onItemUse(final ItemTemplate item, final Player player) {
        return null;
    }
    
    public String onSkillSee(final Npc npc, final Player caster, final Skill skill, final WorldObject[] targets, final boolean isSummon) {
        return null;
    }
    
    public String onSpellFinished(final Npc npc, final Player player, final Skill skill) {
        return null;
    }
    
    public String onTrapAction(final Trap trap, final Creature trigger, final TrapAction action) {
        return null;
    }
    
    public String onSpawn(final Npc npc) {
        return null;
    }
    
    protected void onTeleport(final Npc npc) {
    }
    
    public String onFactionCall(final Npc npc, final Npc caller, final Player attacker, final boolean isSummon) {
        return null;
    }
    
    public String onAggroRangeEnter(final Npc npc, final Player player, final boolean isSummon) {
        return null;
    }
    
    public String onSeeCreature(final Npc npc, final Creature creature, final boolean isSummon) {
        return null;
    }
    
    public String onEnterWorld(final Player player) {
        return null;
    }
    
    public String onEnterZone(final Creature character, final Zone zone) {
        return null;
    }
    
    public String onExitZone(final Creature character, final Zone zone) {
        return null;
    }
    
    public String onEventReceived(final String eventName, final Npc sender, final Npc receiver, final WorldObject reference) {
        return null;
    }
    
    public void onOlympiadMatchFinish(final Participant winner, final Participant looser, final CompetitionType type) {
    }
    
    public void onOlympiadLose(final Player loser, final CompetitionType type) {
    }
    
    public void onMoveFinished(final Npc npc) {
    }
    
    public void onRouteFinished(final Npc npc) {
    }
    
    public boolean onNpcHate(final Attackable mob, final Player player, final boolean isSummon) {
        return true;
    }
    
    public void onSummonSpawn(final Summon summon) {
    }
    
    public void onSummonTalk(final Summon summon) {
    }
    
    public void onInstanceCreated(final Instance instance, final Player player) {
    }
    
    public void onInstanceDestroy(final Instance instance) {
    }
    
    public void onInstanceEnter(final Player player, final Instance instance) {
    }
    
    public void onInstanceLeave(final Player player, final Instance instance) {
    }
    
    public void onNpcDespawn(final Npc npc) {
    }
    
    public boolean onCanSeeMe(final Npc npc, final Player player) {
        return false;
    }
    
    public boolean showError(final Player player, final Throwable t) {
        Quest.LOGGER.warn(this.getScriptFile().toAbsolutePath().toString(), t);
        if (t.getMessage() == null) {
            Quest.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, t.getMessage()));
        }
        if (player != null && player.getAccessLevel().isGm()) {
            final String res = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CommonUtil.getStackTrace(t));
            return this.showResult(player, res);
        }
        return false;
    }
    
    public boolean showResult(final Player player, final String res) {
        return this.showResult(player, res, null);
    }
    
    public boolean showResult(final Player player, final String res, final Npc npc) {
        if (Util.isNullOrEmpty((CharSequence)res) || Objects.isNull(player)) {
            return true;
        }
        if (res.endsWith(".htm") || res.endsWith(".html")) {
            this.showHtmlFile(player, res, npc);
        }
        else if (res.startsWith("<html>")) {
            final NpcHtmlMessage npcReply = new NpcHtmlMessage((npc != null) ? npc.getObjectId() : 0, res);
            if (npc != null) {
                npcReply.replace("%objectId%", npc.getObjectId());
            }
            npcReply.replace("%playername%", player.getName());
            player.sendPacket(npcReply);
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
        else {
            player.sendMessage(res);
        }
        return false;
    }
    
    public void addStartNpc(final int npcId) {
        this.setNpcQuestStartId(npcId);
    }
    
    public void addFirstTalkId(final int npcId) {
        this.setNpcFirstTalkId(event -> this.notifyFirstTalk(event.getNpc(), event.getActiveChar()), npcId);
    }
    
    public void addTalkId(final int npcId) {
        this.setNpcTalkId(npcId);
    }
    
    public void addKillId(final int npcId) {
        this.setAttackableKillId(kill -> this.notifyKill(kill.getTarget(), kill.getAttacker(), kill.isSummon(), kill.getPayload()), npcId);
    }
    
    public void addDeathId(final int npcId) {
        this.setCreatureKillId(kill -> this.onCreatureKill(kill.getTarget(), kill.getAttacker()), npcId);
    }
    
    public void addAttackId(final int npcId) {
        this.setAttackableAttackId(attack -> this.notifyAttack(attack.getTarget(), attack.getAttacker(), attack.getDamage(), attack.isSummon(), attack.getSkill()), npcId);
    }
    
    public void addStartNpc(final int... npcIds) {
        this.setNpcQuestStartId(npcIds);
    }
    
    public void addStartNpc(final IntCollection npcIds) {
        this.setNpcQuestStartId(npcIds);
    }
    
    public void addFirstTalkId(final int... npcIds) {
        this.setNpcFirstTalkId(event -> this.notifyFirstTalk(event.getNpc(), event.getActiveChar()), npcIds);
    }
    
    public void addFirstTalkId(final IntCollection npcIds) {
        this.setNpcFirstTalkId(event -> this.notifyFirstTalk(event.getNpc(), event.getActiveChar()), npcIds);
    }
    
    public void addAcquireSkillId(final int... npcIds) {
        this.setPlayerSkillLearnId(event -> this.notifyAcquireSkill(event.getTrainer(), event.getActiveChar(), event.getSkill(), event.getAcquireType()), npcIds);
    }
    
    public void addAcquireSkillId(final IntCollection npcIds) {
        this.setPlayerSkillLearnId(event -> this.notifyAcquireSkill(event.getTrainer(), event.getActiveChar(), event.getSkill(), event.getAcquireType()), npcIds);
    }
    
    public void addItemBypassEventId(final int... itemIds) {
        this.setItemBypassEvenId(event -> this.notifyItemEvent(event.getItem(), event.getActiveChar(), event.getEvent()), itemIds);
    }
    
    public void addItemBypassEventId(final IntCollection itemIds) {
        this.setItemBypassEvenId(event -> this.notifyItemEvent(event.getItem(), event.getActiveChar(), event.getEvent()), itemIds);
    }
    
    public void addItemTalkId(final int... itemIds) {
        this.setItemTalkId(event -> this.notifyItemTalk(event.getItem(), event.getActiveChar()), itemIds);
    }
    
    public void addItemTalkId(final IntCollection itemIds) {
        this.setItemTalkId(event -> this.notifyItemTalk(event.getItem(), event.getActiveChar()), itemIds);
    }
    
    public void addAttackId(final int... npcIds) {
        this.setAttackableAttackId(attack -> this.notifyAttack(attack.getTarget(), attack.getAttacker(), attack.getDamage(), attack.isSummon(), attack.getSkill()), npcIds);
    }
    
    public void addAttackId(final IntCollection npcIds) {
        this.setAttackableAttackId(attack -> this.notifyAttack(attack.getTarget(), attack.getAttacker(), attack.getDamage(), attack.isSummon(), attack.getSkill()), npcIds);
    }
    
    public void addKillId(final int... npcIds) {
        this.setAttackableKillId(kill -> this.notifyKill(kill.getTarget(), kill.getAttacker(), kill.isSummon(), kill.getPayload()), npcIds);
    }
    
    public void addKillId(final IntCollection npcIds) {
        this.setAttackableKillId(kill -> this.notifyKill(kill.getTarget(), kill.getAttacker(), kill.isSummon(), kill.getPayload()), npcIds);
    }
    
    public void addTalkId(final int... npcIds) {
        this.setNpcTalkId(npcIds);
    }
    
    public void addTalkId(final IntCollection npcIds) {
        this.setNpcTalkId(npcIds);
    }
    
    public void addTeleportId(final int... npcIds) {
        this.setNpcTeleportId(event -> this.notifyTeleport(event.getNpc()), npcIds);
    }
    
    public void addTeleportId(final IntCollection npcIds) {
        this.setNpcTeleportId(event -> this.notifyTeleport(event.getNpc()), npcIds);
    }
    
    public void addSpawnId(final int... npcIds) {
        this.setNpcSpawnId(event -> this.notifySpawn(event.getNpc()), npcIds);
    }
    
    public void addSpawnId(final IntCollection npcIds) {
        this.setNpcSpawnId(event -> this.notifySpawn(event.getNpc()), npcIds);
    }
    
    public void addDespawnId(final int... npcIds) {
        this.setNpcDespawnId(event -> this.onNpcDespawn(event.getNpc()), npcIds);
    }
    
    public void addDespawnId(final IntCollection npcIds) {
        this.setNpcDespawnId(event -> this.onNpcDespawn(event.getNpc()), npcIds);
    }
    
    public void addSkillSeeId(final int... npcIds) {
        this.setNpcSkillSeeId(event -> this.notifySkillSee(event.getTarget(), event.getCaster(), event.getSkill(), event.getTargets(), event.isSummon()), npcIds);
    }
    
    public void addSkillSeeId(final IntCollection npcIds) {
        this.setNpcSkillSeeId(event -> this.notifySkillSee(event.getTarget(), event.getCaster(), event.getSkill(), event.getTargets(), event.isSummon()), npcIds);
    }
    
    public void addSpellFinishedId(final int... npcIds) {
        this.setNpcSkillFinishedId(event -> this.notifySpellFinished(event.getCaster(), event.getTarget(), event.getSkill()), npcIds);
    }
    
    public void addSpellFinishedId(final IntCollection npcIds) {
        this.setNpcSkillFinishedId(event -> this.notifySpellFinished(event.getCaster(), event.getTarget(), event.getSkill()), npcIds);
    }
    
    public void addTrapActionId(final int... npcIds) {
        this.setTrapActionId(event -> this.notifyTrapAction(event.getTrap(), event.getTrigger(), event.getAction()), npcIds);
    }
    
    public void addTrapActionId(final IntCollection npcIds) {
        this.setTrapActionId(event -> this.notifyTrapAction(event.getTrap(), event.getTrigger(), event.getAction()), npcIds);
    }
    
    public void addFactionCallId(final int... npcIds) {
        this.setAttackableFactionIdId(event -> this.notifyFactionCall(event.getNpc(), event.getCaller(), event.getAttacker(), event.isSummon()), npcIds);
    }
    
    public void addFactionCallId(final IntCollection npcIds) {
        this.setAttackableFactionIdId(event -> this.notifyFactionCall(event.getNpc(), event.getCaller(), event.getAttacker(), event.isSummon()), npcIds);
    }
    
    public void addAggroRangeEnterId(final int... npcIds) {
        this.setAttackableAggroRangeEnterId(event -> this.notifyAggroRangeEnter(event.getNpc(), event.getActiveChar(), event.isSummon()), npcIds);
    }
    
    public void addAggroRangeEnterId(final IntCollection npcIds) {
        this.setAttackableAggroRangeEnterId(event -> this.notifyAggroRangeEnter(event.getNpc(), event.getActiveChar(), event.isSummon()), npcIds);
    }
    
    public void addSeeCreatureId(final int... npcIds) {
        this.setNpcCreatureSeeId(event -> this.notifySeeCreature(event.getNpc(), event.getCreature(), event.isSummon()), npcIds);
    }
    
    public void addSeeCreatureId(final IntCollection npcIds) {
        this.setNpcCreatureSeeId(event -> this.notifySeeCreature(event.getNpc(), event.getCreature(), event.isSummon()), npcIds);
    }
    
    public void addEnterZoneId(final int zoneId) {
        this.setCreatureZoneEnterId(event -> this.notifyEnterZone(event.getCreature(), event.getZone()), zoneId);
    }
    
    public void addEnterZoneId(final int... zoneIds) {
        this.setCreatureZoneEnterId(event -> this.notifyEnterZone(event.getCreature(), event.getZone()), zoneIds);
    }
    
    public void addEnterZoneId(final IntCollection zoneIds) {
        this.setCreatureZoneEnterId(event -> this.notifyEnterZone(event.getCreature(), event.getZone()), zoneIds);
    }
    
    public void addExitZoneId(final int zoneId) {
        this.setCreatureZoneExitId(event -> this.notifyExitZone(event.getCreature(), event.getZone()), zoneId);
    }
    
    public void addExitZoneId(final int... zoneIds) {
        this.setCreatureZoneExitId(event -> this.notifyExitZone(event.getCreature(), event.getZone()), zoneIds);
    }
    
    public void addExitZoneId(final IntCollection zoneIds) {
        this.setCreatureZoneExitId(event -> this.notifyExitZone(event.getCreature(), event.getZone()), zoneIds);
    }
    
    public void addEventReceivedId(final int... npcIds) {
        this.setNpcEventReceivedId(event -> this.notifyEventReceived(event.getEventName(), event.getSender(), event.getReceiver(), event.getReference()), npcIds);
    }
    
    public void addEventReceivedId(final IntCollection npcIds) {
        this.setNpcEventReceivedId(event -> this.notifyEventReceived(event.getEventName(), event.getSender(), event.getReceiver(), event.getReference()), npcIds);
    }
    
    public void addMoveFinishedId(final int... npcIds) {
        this.setNpcMoveFinishedId(event -> this.notifyMoveFinished(event.getNpc()), npcIds);
    }
    
    public void addMoveFinishedId(final IntCollection npcIds) {
        this.setNpcMoveFinishedId(event -> this.notifyMoveFinished(event.getNpc()), npcIds);
    }
    
    public void addRouteFinishedId(final int... npcIds) {
        this.setNpcMoveRouteFinishedId(event -> this.notifyRouteFinished(event.getNpc()), npcIds);
    }
    
    public void addRouteFinishedId(final IntCollection npcIds) {
        this.setNpcMoveRouteFinishedId(event -> this.notifyRouteFinished(event.getNpc()), npcIds);
    }
    
    public void addNpcHateId(final int... npcIds) {
        final TerminateReturn terminateReturn;
        this.addNpcHateId(event -> {
            new TerminateReturn(!this.onNpcHate(event.getNpc(), event.getActiveChar(), event.isSummon()), false, false);
            return terminateReturn;
        }, npcIds);
    }
    
    public void addNpcHateId(final IntCollection npcIds) {
        final TerminateReturn terminateReturn;
        this.addNpcHateId(event -> {
            new TerminateReturn(!this.onNpcHate(event.getNpc(), event.getActiveChar(), event.isSummon()), false, false);
            return terminateReturn;
        }, npcIds);
    }
    
    public void addSummonSpawnId(final int... npcIds) {
        this.setPlayerSummonSpawnId(event -> this.onSummonSpawn(event.getSummon()), npcIds);
    }
    
    public void addSummonSpawnId(final IntCollection npcIds) {
        this.setPlayerSummonSpawnId(event -> this.onSummonSpawn(event.getSummon()), npcIds);
    }
    
    public void addSummonTalkId(final int... npcIds) {
        this.setPlayerSummonTalkId(event -> this.onSummonTalk(event.getSummon()), npcIds);
    }
    
    public void addSummonTalkId(final IntCollection npcIds) {
        this.setPlayerSummonTalkId(event -> this.onSummonTalk(event.getSummon()), npcIds);
    }
    
    public void addCanSeeMeId(final int... npcIds) {
        final TerminateReturn terminateReturn;
        this.addNpcHateId(event -> {
            new TerminateReturn(!this.notifyOnCanSeeMe(event.getNpc(), event.getActiveChar()), false, false);
            return terminateReturn;
        }, npcIds);
    }
    
    public void addCanSeeMeId(final IntCollection npcIds) {
        final TerminateReturn terminateReturn;
        this.addNpcHateId(event -> {
            new TerminateReturn(!this.notifyOnCanSeeMe(event.getNpc(), event.getActiveChar()), false, false);
            return terminateReturn;
        }, npcIds);
    }
    
    public void addOlympiadMatchFinishId() {
        this.setOlympiadMatchResult(event -> this.notifyOlympiadMatch(event.getWinner(), event.getLoser(), event.getCompetitionType()));
    }
    
    public void addInstanceCreatedId(final int... templateIds) {
        this.setInstanceCreatedId(event -> this.onInstanceCreated(event.getInstanceWorld(), event.getCreator()), templateIds);
    }
    
    public void addInstanceCreatedId(final IntCollection templateIds) {
        this.setInstanceCreatedId(event -> this.onInstanceCreated(event.getInstanceWorld(), event.getCreator()), templateIds);
    }
    
    public void addInstanceDestroyId(final int... templateIds) {
        this.setInstanceDestroyId(event -> this.onInstanceDestroy(event.getInstanceWorld()), templateIds);
    }
    
    public void addInstanceDestroyId(final IntCollection templateIds) {
        this.setInstanceDestroyId(event -> this.onInstanceDestroy(event.getInstanceWorld()), templateIds);
    }
    
    public void addInstanceEnterId(final int... templateIds) {
        this.setInstanceEnterId(event -> this.onInstanceEnter(event.getPlayer(), event.getInstanceWorld()), templateIds);
    }
    
    public void addInstanceEnterId(final IntCollection templateIds) {
        this.setInstanceEnterId(event -> this.onInstanceEnter(event.getPlayer(), event.getInstanceWorld()), templateIds);
    }
    
    public void addInstanceLeaveId(final int... templateIds) {
        this.setInstanceLeaveId(event -> this.onInstanceLeave(event.getPlayer(), event.getInstanceWorld()), templateIds);
    }
    
    public void addInstanceLeaveId(final IntCollection templateIds) {
        this.setInstanceLeaveId(event -> this.onInstanceLeave(event.getPlayer(), event.getInstanceWorld()), templateIds);
    }
    
    public Player getRandomPartyMember(final Player player) {
        if (player == null) {
            return null;
        }
        final Party party = player.getParty();
        if (party == null || party.getMembers().isEmpty()) {
            return player;
        }
        return party.getMembers().get(Rnd.get(party.getMembers().size()));
    }
    
    public Player getRandomPartyMember(final Player player, final int cond) {
        return this.getRandomPartyMember(player, "cond", String.valueOf(cond));
    }
    
    public Player getRandomPartyMember(final Player player, final String var, final String value) {
        if (player == null) {
            return null;
        }
        if (var == null) {
            return this.getRandomPartyMember(player);
        }
        QuestState temp = null;
        final Party party = player.getParty();
        if (party == null || party.getMembers().isEmpty()) {
            temp = player.getQuestState(this.getName());
            if (temp != null && temp.isSet(var) && temp.get(var).equalsIgnoreCase(value)) {
                return player;
            }
            return null;
        }
        else {
            final List<Player> candidates = new ArrayList<Player>();
            WorldObject target = player.getTarget();
            if (target == null) {
                target = player;
            }
            for (final Player partyMember : party.getMembers()) {
                if (partyMember == null) {
                    continue;
                }
                temp = partyMember.getQuestState(this.getName());
                if (temp == null || temp.get(var) == null || !temp.get(var).equalsIgnoreCase(value) || !MathUtil.isInsideRadius3D(partyMember, target, Config.ALT_PARTY_RANGE)) {
                    continue;
                }
                candidates.add(partyMember);
            }
            if (candidates.isEmpty()) {
                return null;
            }
            return candidates.get(Rnd.get(candidates.size()));
        }
    }
    
    public Player getRandomPartyMemberState(final Player player, final byte state) {
        if (player == null) {
            return null;
        }
        QuestState temp = null;
        final Party party = player.getParty();
        if (party == null || party.getMembers().isEmpty()) {
            temp = player.getQuestState(this.getName());
            if (temp != null && temp.getState() == state) {
                return player;
            }
            return null;
        }
        else {
            final List<Player> candidates = new ArrayList<Player>();
            WorldObject target = player.getTarget();
            if (target == null) {
                target = player;
            }
            for (final Player partyMember : party.getMembers()) {
                if (partyMember == null) {
                    continue;
                }
                temp = partyMember.getQuestState(this.getName());
                if (temp == null || temp.getState() != state || !MathUtil.isInsideRadius3D(partyMember, target, Config.ALT_PARTY_RANGE)) {
                    continue;
                }
                candidates.add(partyMember);
            }
            if (candidates.isEmpty()) {
                return null;
            }
            return candidates.get(Rnd.get(candidates.size()));
        }
    }
    
    public Player getRandomPartyMember(final Player player, final Npc npc) {
        if (player == null || !checkDistanceToTarget(player, npc)) {
            return null;
        }
        final Party party = player.getParty();
        Player luckyPlayer = null;
        if (party == null) {
            if (this.checkPartyMember(player, npc)) {
                luckyPlayer = player;
            }
        }
        else {
            int highestRoll = 0;
            for (final Player member : party.getMembers()) {
                final int rnd = AbstractScript.getRandom(1000);
                if (rnd > highestRoll && this.checkPartyMember(member, npc)) {
                    highestRoll = rnd;
                    luckyPlayer = member;
                }
            }
        }
        return (luckyPlayer != null && checkDistanceToTarget(luckyPlayer, npc)) ? luckyPlayer : null;
    }
    
    public boolean checkPartyMember(final Player player, final Npc npc) {
        return true;
    }
    
    public QuestState getRandomPartyMemberState(final Player player, final int condition, final int playerChance, final Npc target) {
        if (player == null || playerChance < 1) {
            return null;
        }
        QuestState qs = player.getQuestState(this.getName());
        if (!player.isInParty()) {
            return (!this.checkPartyMemberConditions(qs, condition, target) || !checkDistanceToTarget(player, target)) ? null : qs;
        }
        final List<QuestState> candidates = new ArrayList<QuestState>();
        if (this.checkPartyMemberConditions(qs, condition, target) && playerChance > 0) {
            for (int i = 0; i < playerChance; ++i) {
                candidates.add(qs);
            }
        }
        for (final Player member : player.getParty().getMembers()) {
            if (member == player) {
                continue;
            }
            qs = member.getQuestState(this.getName());
            if (!this.checkPartyMemberConditions(qs, condition, target)) {
                continue;
            }
            candidates.add(qs);
        }
        if (candidates.isEmpty()) {
            return null;
        }
        qs = candidates.get(AbstractScript.getRandom(candidates.size()));
        return checkDistanceToTarget(qs.getPlayer(), target) ? qs : null;
    }
    
    private boolean checkPartyMemberConditions(final QuestState qs, final int condition, final Npc npc) {
        if (qs != null) {
            if (condition == -1) {
                if (!qs.isStarted()) {
                    return false;
                }
            }
            else if (!qs.isCond(condition)) {
                return false;
            }
            if (this.checkPartyMember(qs, npc)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean checkPartyMember(final QuestState qs, final Npc npc) {
        return true;
    }
    
    public String showHtmlFile(final Player player, final String filename) {
        return this.showHtmlFile(player, filename, null);
    }
    
    public String showHtmlFile(final Player player, final String filename, final Npc npc) {
        final boolean questwindow = !filename.endsWith(".html");
        String content = this.getHtml(player, filename);
        if (content != null) {
            if (npc != null) {
                content = content.replaceAll("%objectId%", String.valueOf(npc.getObjectId()));
            }
            if (questwindow && this._questId > 0 && this._questId < 20000 && this._questId != 999) {
                final NpcQuestHtmlMessage npcReply = new NpcQuestHtmlMessage((npc != null) ? npc.getObjectId() : 0, this._questId);
                npcReply.setHtml(content);
                npcReply.replace("%playername%", player.getName());
                player.sendPacket(npcReply);
            }
            else {
                final NpcHtmlMessage npcReply2 = new NpcHtmlMessage((npc != null) ? npc.getObjectId() : 0, content);
                npcReply2.replace("%playername%", player.getName());
                player.sendPacket(npcReply2);
            }
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
        return content;
    }
    
    public String getHtml(final Player player, final String fileName) {
        String path;
        if (fileName.startsWith("data/")) {
            path = fileName;
        }
        else if (fileName.startsWith("./")) {
            path = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getPath(), fileName.substring(1));
        }
        else {
            path = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getPath(), fileName);
        }
        return HtmCache.getInstance().getHtm(player, path);
    }
    
    public int[] getRegisteredItemIds() {
        return this._questItemIds;
    }
    
    public void registerQuestItems(final int... items) {
        for (final int id : items) {
            if (id != 0 && ItemEngine.getInstance().getTemplate(id) == null) {
                Quest.LOGGER.error("Found registerQuestItems for non existing item: {}!", (Object)id);
            }
        }
        this._questItemIds = items;
    }
    
    public void removeRegisteredQuestItems(final Player player) {
        AbstractScript.takeItems(player, -1, this._questItemIds);
    }
    
    @Override
    public String getScriptName() {
        return this.getName();
    }
    
    @Override
    public Path getScriptPath() {
        return ScriptEngineManager.getInstance().getCurrentLoadingScript();
    }
    
    @Override
    public void setActive(final boolean status) {
    }
    
    @Override
    public boolean reload() {
        this.unload();
        return super.reload();
    }
    
    @Override
    public boolean unload() {
        return this.unload(true);
    }
    
    public boolean unload(final boolean removeFromList) {
        this.onSave();
        if (this._questTimers != null) {
            for (final List<QuestTimer> timers : this.getQuestTimers().values()) {
                this._readLock.lock();
                try {
                    for (final QuestTimer timer : timers) {
                        timer.cancel();
                    }
                }
                finally {
                    this._readLock.unlock();
                }
                timers.clear();
            }
            this.getQuestTimers().clear();
        }
        if (removeFromList) {
            return QuestManager.getInstance().removeScript(this) && super.unload();
        }
        return super.unload();
    }
    
    public void setOnEnterWorld(final boolean state) {
        if (state) {
            this.setPlayerLoginId(event -> this.notifyEnterWorld(event.getPlayer()));
        }
        else {
            this.getListeners().stream().filter(listener -> listener.getType() == EventType.ON_PLAYER_LOGIN).forEach(AbstractEventListener::unregisterMe);
        }
    }
    
    public void setIsCustom(final boolean val) {
        this._isCustom = val;
    }
    
    public boolean isCustomQuest() {
        return this._isCustom;
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player activeChar) {
        return Collections.emptySet();
    }
    
    public void sendNpcLogList(final Player activeChar) {
        if (activeChar.getQuestState(this.getName()) != null) {
            final ExQuestNpcLogList packet = new ExQuestNpcLogList(this._questId);
            final Set<NpcLogListHolder> npcLogList = this.getNpcLogList(activeChar);
            final ExQuestNpcLogList obj = packet;
            Objects.requireNonNull(obj);
            npcLogList.forEach(obj::add);
            activeChar.sendPacket(packet);
        }
    }
    
    private Set<QuestCondition> getStartConditions() {
        if (this._startCondition == null) {
            synchronized (this) {
                if (this._startCondition == null) {
                    this._startCondition = (Set<QuestCondition>)ConcurrentHashMap.newKeySet(1);
                }
            }
        }
        return this._startCondition;
    }
    
    public boolean canStartQuest(final Player player) {
        if (this._startCondition == null) {
            return true;
        }
        for (final QuestCondition cond : this._startCondition) {
            if (!cond.test(player)) {
                return false;
            }
        }
        return true;
    }
    
    public String getStartConditionHtml(final Player player, final Npc npc) {
        final QuestState st = this.getQuestState(player, false);
        if (this._startCondition == null || (st != null && !st.isCreated())) {
            return null;
        }
        for (final QuestCondition cond : this._startCondition) {
            if (!cond.test(player)) {
                return cond.getHtml(npc);
            }
        }
        return null;
    }
    
    public void addCondStart(final Predicate<Player> questStartRequirement, final String html) {
        this.getStartConditions().add(new QuestCondition(questStartRequirement, html));
    }
    
    @SafeVarargs
    public final void addCondStart(final Predicate<Player> questStartRequirement, final KeyValuePair<Integer, String>... pairs) {
        this.getStartConditions().add(new QuestCondition(questStartRequirement, pairs));
    }
    
    public void addCondLevel(final int minLevel, final int maxLevel, final String html) {
        this.addCondStart(p -> p.getLevel() >= minLevel && p.getLevel() <= maxLevel, html);
    }
    
    @SafeVarargs
    public final void addCondMinLevel(final int minLevel, final int maxLevel, final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> p.getLevel() >= minLevel && p.getLevel() <= maxLevel, pairs);
    }
    
    public void addCondMinLevel(final int minLevel, final String html) {
        this.addCondStart(p -> p.getLevel() >= minLevel, html);
    }
    
    @SafeVarargs
    public final void addCondMinLevel(final int minLevel, final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> p.getLevel() >= minLevel, pairs);
    }
    
    public void addCondMaxLevel(final int maxLevel, final String html) {
        this.addCondStart(p -> p.getLevel() <= maxLevel, html);
    }
    
    @SafeVarargs
    public final void addCondMaxLevel(final int maxLevel, final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> p.getLevel() <= maxLevel, pairs);
    }
    
    public void addCondRace(final Race race, final String html) {
        this.addCondStart(p -> p.getRace() == race, html);
    }
    
    @SafeVarargs
    public final void addCondRace(final Race race, final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> p.getRace() == race, pairs);
    }
    
    public void addCondNotRace(final Race race, final String html) {
        this.addCondStart(p -> p.getRace() != race, html);
    }
    
    @SafeVarargs
    public final void addCondNotRace(final Race race, final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> p.getRace() != race, pairs);
    }
    
    public void addCondCompletedQuest(final String name, final String html) {
        this.addCondStart(p -> p.hasQuestState(name) && p.getQuestState(name).isCompleted(), html);
    }
    
    @SafeVarargs
    public final void addCondCompletedQuest(final String name, final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> p.hasQuestState(name) && p.getQuestState(name).isCompleted(), pairs);
    }
    
    public void addCondStartedQuest(final String name, final String html) {
        this.addCondStart(p -> p.hasQuestState(name) && p.getQuestState(name).isStarted(), html);
    }
    
    @SafeVarargs
    public final void addCondStartedQuest(final String name, final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> p.hasQuestState(name) && p.getQuestState(name).isStarted(), pairs);
    }
    
    public void addCondClassId(final ClassId classId, final String html) {
        this.addCondStart(p -> p.getClassId() == classId, html);
    }
    
    public final void addCondClassIds(final ClassId... classIds) {
        this.addCondStart(p -> Util.contains((Object[])classIds, (Object)p.getClassId()), "");
    }
    
    public void addCondNotClassId(final ClassId classId, final String html) {
        this.addCondStart(p -> p.getClassId() != classId, html);
    }
    
    @SafeVarargs
    public final void addCondNotClassId(final ClassId classId, final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> p.getClassId() != classId, pairs);
    }
    
    public void addCondIsSubClassActive(final String html) {
        this.addCondStart(p -> p.isSubClassActive(), html);
    }
    
    @SafeVarargs
    public final void addCondIsSubClassActive(final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> p.isSubClassActive(), pairs);
    }
    
    public void addCondIsNotSubClassActive(final String html) {
        this.addCondStart(p -> !p.isSubClassActive() && !p.isDualClassActive(), html);
    }
    
    @SafeVarargs
    public final void addCondIsNotSubClassActive(final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> !p.isSubClassActive() && !p.isDualClassActive(), pairs);
    }
    
    public void addCondInCategory(final CategoryType categoryType, final String html) {
        this.addCondStart(p -> p.isInCategory(categoryType), html);
    }
    
    @SafeVarargs
    public final void addCondInCategory(final CategoryType categoryType, final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> p.isInCategory(categoryType), pairs);
    }
    
    public void addCondClanLevel(final int clanLevel, final String html) {
        this.addCondStart(p -> p.getClan() != null && p.getClan().getLevel() > clanLevel, html);
    }
    
    @SafeVarargs
    public final void addCondClanLevel(final int clanLevel, final KeyValuePair<Integer, String>... pairs) {
        this.addCondStart(p -> p.getClan() != null && p.getClan().getLevel() > clanLevel, pairs);
    }
    
    public void onQuestAborted(final Player player) {
    }
    
    public void giveStoryQuestReward(final Player player, final int steelDoorCoinCount) {
        AbstractScript.giveItems(player, 37045, steelDoorCoinCount);
        if (Config.ENABLE_STORY_QUEST_BUFF_REWARD) {
            Quest.STORY_QUEST_REWARD.getSkill().applyEffects(player, player);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(Quest.class.getName());
        STORY_QUEST_REWARD = new SkillHolder(27580, 1);
    }
}
