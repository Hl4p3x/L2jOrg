// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.custom.events.Race;

import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import java.util.Iterator;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.util.Broadcast;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import org.l2j.gameserver.Config;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Collection;
import org.l2j.gameserver.model.actor.Npc;
import java.util.List;
import org.l2j.gameserver.model.quest.Event;

public final class Race extends Event
{
    private List<Npc> _npclist;
    private Npc _npc;
    private Collection<Player> _players;
    ScheduledFuture<?> _eventTask;
    private static boolean _isactive;
    private static boolean _isRaceStarted;
    private static final int _time_register = 5;
    private static final int _time_race = 10;
    private static final int _start_npc = 900103;
    private static final int _stop_npc = 900104;
    private static int _skill;
    private static int[] _randspawn;
    private static final String[] _locations;
    private static final int[][] _coords;
    private static final int[][] _rewards;
    
    private Race() {
        this._eventTask = null;
        this.addStartNpc(900103);
        this.addFirstTalkId(900103);
        this.addTalkId(900103);
        this.addStartNpc(900104);
        this.addFirstTalkId(900104);
        this.addTalkId(900104);
    }
    
    public boolean eventStart(final Player eventMaker) {
        if (Race._isactive) {
            return false;
        }
        if (!Config.CUSTOM_NPC_DATA) {
            eventMaker.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getName()));
            return false;
        }
        this._npclist = new ArrayList<Npc>();
        this._players = (Collection<Player>)ConcurrentHashMap.newKeySet();
        Race._isactive = true;
        this._npc = this.recordSpawn(900103, 18429, 145861, -3090, 0, false, 0L);
        Broadcast.toAllOnlinePlayers("* Race Event started! *");
        Broadcast.toAllOnlinePlayers("Visit Event Manager in Dion village and signup, you have 5 min before Race Start...");
        this._eventTask = (ScheduledFuture<?>)ThreadPool.schedule(this::StartRace, 300000L);
        return true;
    }
    
    protected void StartRace() {
        if (this._players.isEmpty()) {
            Broadcast.toAllOnlinePlayers("Race aborted, nobody signup.");
            this.eventStop();
            return;
        }
        Race._isRaceStarted = true;
        Broadcast.toAllOnlinePlayers("Race started!");
        final int location = Rnd.get(Race._locations.length);
        Race._randspawn = Race._coords[location];
        this.recordSpawn(900104, Race._randspawn[0], Race._randspawn[1], Race._randspawn[2], Race._randspawn[3], false, 0L);
        for (final Player player : this._players) {
            if (player != null && player.isOnline()) {
                if (MathUtil.isInsideRadius2D((ILocational)player, (ILocational)this._npc, 500)) {
                    this.sendMessage(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Race._locations[location]));
                    this.transformPlayer(player);
                    player.getRadar().addMarker(Race._randspawn[0], Race._randspawn[1], Race._randspawn[2]);
                }
                else {
                    this.sendMessage(player, "I told you stay near me right? Distance was too high, you are excluded from race");
                    this._players.remove(player);
                }
            }
        }
        this._eventTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> this.timeUp(), 600000L);
    }
    
    public boolean eventStop() {
        if (!Race._isactive) {
            return false;
        }
        Race._isactive = false;
        Race._isRaceStarted = false;
        if (this._eventTask != null) {
            this._eventTask.cancel(true);
            this._eventTask = null;
        }
        for (final Player player : this._players) {
            if (player != null && player.isOnline()) {
                player.untransform();
                player.teleToLocation((ILocational)this._npc, true);
            }
        }
        for (final Npc _npc : this._npclist) {
            if (_npc != null) {
                _npc.deleteMe();
            }
        }
        this._npclist.clear();
        this._players.clear();
        Broadcast.toAllOnlinePlayers("* Race Event finished *");
        return true;
    }
    
    public boolean eventBypass(final Player activeChar, final String bypass) {
        if (bypass.startsWith("skill")) {
            if (Race._isRaceStarted) {
                activeChar.sendMessage("Race already started, you cannot change transform skill now");
            }
            else {
                final int _number = Integer.parseInt(bypass.substring(5));
                final Skill _sk = SkillEngine.getInstance().getSkill(_number, 1);
                if (_sk != null) {
                    Race._skill = _number;
                    activeChar.sendMessage("Transform skill set to:");
                    activeChar.sendMessage(_sk.getName());
                }
                else {
                    activeChar.sendMessage("Error while changing transform skill");
                }
            }
        }
        else if (bypass.startsWith("tele")) {
            if (Integer.parseInt(bypass.substring(4)) > 0 && Race._randspawn != null) {
                activeChar.teleToLocation(Race._randspawn[0], Race._randspawn[1], Race._randspawn[2]);
            }
            else {
                activeChar.teleToLocation(18429, 145861, -3090);
            }
        }
        this.showMenu(activeChar);
        return true;
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final String htmltext = event;
        final QuestState st = this.getQuestState(player, false);
        if (st == null) {
            return null;
        }
        if (event.equalsIgnoreCase("transform")) {
            this.transformPlayer(player);
            return null;
        }
        if (event.equalsIgnoreCase("untransform")) {
            player.untransform();
            return null;
        }
        if (event.equalsIgnoreCase("showfinish")) {
            player.getRadar().addMarker(Race._randspawn[0], Race._randspawn[1], Race._randspawn[2]);
            return null;
        }
        if (event.equalsIgnoreCase("signup")) {
            if (this._players.contains(player)) {
                return "900103-onlist.htm";
            }
            this._players.add(player);
            return "900103-signup.htm";
        }
        else {
            if (event.equalsIgnoreCase("quit")) {
                player.untransform();
                if (this._players.contains(player)) {
                    this._players.remove(player);
                }
                return "900103-quit.htm";
            }
            if (!event.equalsIgnoreCase("finish")) {
                return htmltext;
            }
            if (player.isAffectedBySkill(Race._skill)) {
                this.winRace(player);
                return "900104-winner.htm";
            }
            return "900104-notrans.htm";
        }
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        this.getQuestState(player, true);
        if (npc.getId() == 900103) {
            if (Race._isRaceStarted) {
                return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.isRacing(player));
            }
            return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.isRacing(player));
        }
        else {
            if (npc.getId() == 900104 && Race._isRaceStarted) {
                return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.isRacing(player));
            }
            return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
    }
    
    private int isRacing(final Player player) {
        return this._players.contains(player) ? 1 : 0;
    }
    
    private Npc recordSpawn(final int npcId, final int x, final int y, final int z, final int heading, final boolean randomOffSet, final long despawnDelay) {
        final Npc npc = addSpawn(npcId, x, y, z, heading, randomOffSet, despawnDelay);
        if (npc != null) {
            this._npclist.add(npc);
        }
        return npc;
    }
    
    private void transformPlayer(final Player player) {
        if (player.isTransformed()) {
            player.untransform();
        }
        if (player.isSitting()) {
            player.standUp();
        }
        player.getEffectList().stopEffects(AbnormalType.SPEED_UP);
        player.stopSkillEffects(true, 268);
        player.stopSkillEffects(true, 298);
        SkillEngine.getInstance().getSkill(Race._skill, 1).applyEffects((Creature)player, (Creature)player);
    }
    
    private void sendMessage(final Player player, final String text) {
        player.sendPacket(new ServerPacket[] { (ServerPacket)new CreatureSay(this._npc.getObjectId(), ChatType.MPCC_ROOM, this._npc.getName(), text) });
    }
    
    private void showMenu(final Player activeChar) {
        final NpcHtmlMessage html = new NpcHtmlMessage();
        final String content = this.getHtml(activeChar, "admin_menu.htm");
        html.setHtml(content);
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    protected void timeUp() {
        Broadcast.toAllOnlinePlayers("Time up, nobody wins!");
        this.eventStop();
    }
    
    private void winRace(final Player player) {
        final int[] _reward = Race._rewards[Rnd.get(Race._rewards.length - 1)];
        player.addItem("eventModRace", _reward[0], (long)_reward[1], (WorldObject)this._npc, true);
        Broadcast.toAllOnlinePlayers(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
        this.eventStop();
    }
    
    public static AbstractScript provider() {
        return (AbstractScript)new Race();
    }
    
    static {
        Race._isactive = false;
        Race._isRaceStarted = false;
        Race._skill = 6201;
        Race._randspawn = null;
        _locations = new String[] { "Heretic catacomb enterance", "Dion castle bridge", "Floran village enterance", "Floran fort gate" };
        _coords = new int[][] { { 39177, 144345, -3650, 0 }, { 22294, 155892, -2950, 0 }, { 16537, 169937, -3500, 0 }, { 7644, 150898, -2890, 0 } };
        _rewards = new int[][] { { 6622, 2 }, { 9625, 2 }, { 9626, 2 }, { 9627, 2 }, { 9546, 5 }, { 9547, 5 }, { 9548, 5 }, { 9549, 5 }, { 9550, 5 }, { 9551, 5 }, { 9574, 3 }, { 9575, 2 }, { 9576, 1 }, { 20034, 1 } };
    }
}
